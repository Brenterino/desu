/*
    This file is part of Desu: MapleStory v62 Server Emulator
    Copyright (C) 2017  Brenterino <therealspookster@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package client;

import client.packet.PacketCreator;
import client.packet.RecvOpcode;
import client.handler.*;
import crypto.MapleCrypto;
import net.Packet;
import net.PacketReader;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.PacketHandler;

/**
 * Root handler for all connections to the Login Server service for
 * regular MapleStory clients.
 * 
 * @author Brent
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    
    private final static PacketHandler[] HANDLERS;
     
    static {
        HANDLERS = new PacketHandler[400]; // XXX change this to max value of handler
        
        HANDLERS[RecvOpcode.CHANNEL_CONNECTION_REQUEST.getValue()] = new ChannelConnectionRequestHandler();
        HANDLERS[RecvOpcode.CHARACTER_LIST_REQUEST.getValue()] = new CharacterListRequestHandler();
        HANDLERS[RecvOpcode.ERROR_REPORT.getValue()] = new ErrorReportHandler(); // unhandled
        HANDLERS[RecvOpcode.CREATE_CHARACTER.getValue()] = new CreateCharacterHandler();
        HANDLERS[RecvOpcode.DELETE_CHARACTER.getValue()] = new DeleteCharacterHandler();
        HANDLERS[RecvOpcode.GUEST_LOGIN.getValue()] = new GuestLoginHandler();
        HANDLERS[RecvOpcode.LOGIN_REQUEST.getValue()] = new LoginRequestHandler();
        HANDLERS[RecvOpcode.NAME_CHECK_REQUEST.getValue()] = new CharacterNameCheckHandler();
        HANDLERS[RecvOpcode.PIN_OPERATION.getValue()] = new PinOperationHandler();
        HANDLERS[RecvOpcode.PONG.getValue()] = new PongHandler(); // unhandled
        HANDLERS[RecvOpcode.REGISTER_PIN.getValue()] = new RegisterPinHandler();
        HANDLERS[RecvOpcode.RELOG_REQUEST.getValue()] = new RelogRequestHandler();
        HANDLERS[RecvOpcode.SET_GENDER.getValue()] = new SetGenderHandler();
        HANDLERS[RecvOpcode.VIEW_ALL_CHANNEL_CONNECTION_REQUEST.getValue()] = new ChannelConnectionRequestHandler();
        HANDLERS[RecvOpcode.VIEW_ALL_CHARACTERS.getValue()] = new ViewAllCharactersHandler();
        HANDLERS[RecvOpcode.WORLD_INFO_REQUEST.getValue()] = new WorldInfoRequestHandler();
        HANDLERS[RecvOpcode.WORLD_INFO_REREQUEST.getValue()] = new WorldInfoRequestHandler();
        HANDLERS[RecvOpcode.WORLD_STATUS_REQUEST.getValue()] = new WorldStatusRequestHandler();
    }
    
    public ClientHandler() {
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();
        
        byte[] siv = {82, 48, (byte) (Math.random() * 255), 115};
        byte[] riv = {70, 114, (byte) (Math.random() * 255), 82};
        
        Client c = new Client(ch, siv, riv);
        
        c.write(PacketCreator.getHello(siv, riv));
        
        ch.attr(Client.CLIENT_KEY).set(c);
        ch.attr(Client.CRYPTO_KEY).set(new MapleCrypto());
        
        // remove after debug stage
        System.out.printf("[Debug] Opened session with %s%n", c.getIP());
        
        c.startPing(ch);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();
        
        Client c = (Client) ch.attr(Client.CLIENT_KEY).get();
        
        c.softDisconnect(c.isLoggedIn()); // handle this is we don't soft disconnect through handler
        
        c.cancelPingTask();
        
        // remove after debug stage
        System.out.printf("[Debug] Closed session with %s.%n", c.getIP());
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet p = (Packet) msg;
        Channel ch = ctx.channel();
        
        Client c = (Client) ch.attr(Client.CLIENT_KEY).get();
        PacketReader r = c.getReader().next(p);
        
        int op = r.readShort();
        
        PacketHandler h = HANDLERS[op];
        
        if (h != null) {
            if (h.validateState(c)) {
                h.handle(c, r);
            } else {
                c.close(); // invalid state for client, disconnect
            }
        } else {           
            System.out.printf("[Debug] Received completely unhandled packet %s.%n", p.toString());
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        t.printStackTrace();
        // XXX remove later, disconnect client after this point
    }
}
