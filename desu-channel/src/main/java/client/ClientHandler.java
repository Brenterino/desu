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

import client.handler.*;
import client.packet.PacketCreator;
import client.packet.RecvOpcode;
import crypto.MapleCrypto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.Packet;
import net.PacketReader;
import netty.PacketHandler;
import service.GameService;

/**
 * Root handler for all connections to the Channel Server service for
 * regular MapleStory clients.
 * 
 * @author Brent
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    
    private final static PacketHandler[] HANDLERS;
    
    static {
        HANDLERS = new PacketHandler[400]; // XXX change this to max value of handler
        // XXX add handlers
        HANDLERS[RecvOpcode.APPLY_AP.getValue()] = new ApplyAPHandler();
        HANDLERS[RecvOpcode.APPLY_SP.getValue()] = new ApplySPHandler();
        HANDLERS[RecvOpcode.CHANGE_FIELD.getValue()] = new ChangeFieldHandler();
        HANDLERS[RecvOpcode.FACIAL_EXPRESSION.getValue()] = new FacialExpressionHandler();
        HANDLERS[RecvOpcode.FIELD_CHAT.getValue()] = new FieldChatHandler();
        HANDLERS[RecvOpcode.HEAL_OVER_TIME.getValue()] = new HealOverTimeHandler();
        HANDLERS[RecvOpcode.ITEM_LOOT.getValue()] = new ItemLootHandler();
        HANDLERS[RecvOpcode.MELEE_ATTACK.getValue()] = new MeleeAttackHandler();
        HANDLERS[RecvOpcode.MODIFY_INVENTORY.getValue()] = new ModifyInventoryHandler();
        HANDLERS[RecvOpcode.MODIFY_KEY_BINDINGS.getValue()] = new ModifyKeyBindingsHandler();
        HANDLERS[RecvOpcode.MONSTER_ACTION.getValue()] = new MonsterActionHandler();
        HANDLERS[RecvOpcode.MONSTER_AGGRO.getValue()] = new MonsterAggroHandler();
        HANDLERS[RecvOpcode.MOVE_PLAYER.getValue()] = new MovePlayerHandler();
        HANDLERS[RecvOpcode.NPC_ACTION.getValue()] = new NPCActionHandler();
        HANDLERS[RecvOpcode.PLAYER_LOGGEDIN.getValue()] = new PlayerLoggedInHandler();
        HANDLERS[RecvOpcode.PLAYER_UPDATE.getValue()] = new PlayerUpdateHandler();
        HANDLERS[RecvOpcode.PONG.getValue()] = new PongHandler();
        HANDLERS[RecvOpcode.PORTAL_ENTER.getValue()] = new PortalEnterHandler();
        HANDLERS[RecvOpcode.SECURITY_PACKET.getValue()] = new SecurityPacketHandler(); // yeah not important but just want it to shut up
        HANDLERS[RecvOpcode.TAKE_DAMAGE.getValue()] = new TakeDamageHandler();
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
        
        GameService.getInstance().addConnected(ch);
        
        c.startPing();
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();
        
        Client c = (Client) ch.attr(Client.CLIENT_KEY).get();
        
        c.disconnect(false);
        
        System.out.printf("[Debug] Closed session with %s.%n", c.getIP()); // remove after debug stage
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
                System.out.printf("[Debug] Client failed to validate state for packet %s.%n", op);
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
