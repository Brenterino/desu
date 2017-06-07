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
package channel;

import channel.handler.ChannelInfoHandler;
import channel.handler.PlayerConnectedHandler;
import channel.handler.PlayerDisconnectedHandler;
import channel.handler.UpdateAccountStateHandler;
import channel.packet.RecvOpcode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import login.handler.BroadcastMessageHandler;
import net.Packet;
import net.PacketReader;
import netty.PacketHandler;
import service.ChannelService;

/**
 *
 * @author Brent
 */
public class WorldChannelHandler extends ChannelInboundHandlerAdapter {       
    
    private final static PacketHandler[] HANDLERS;
     
    static {
        HANDLERS = new PacketHandler[400]; // XXX change this to max value of handler
        
        HANDLERS[RecvOpcode.BROADCAST_MESSAGE.getValue()] = new BroadcastMessageHandler();
        HANDLERS[RecvOpcode.CHANNEL_INFO.getValue()] = new ChannelInfoHandler();
        HANDLERS[RecvOpcode.PLAYER_CONNECTED.getValue()] = new PlayerConnectedHandler();
        HANDLERS[RecvOpcode.PLAYER_DISONNECTED.getValue()] = new PlayerDisconnectedHandler();
        HANDLERS[RecvOpcode.UPDATE_ACCOUNT_STATE.getValue()] = new UpdateAccountStateHandler();
    }
    
    public WorldChannelHandler() {
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();
        
        WorldChannel wc = new WorldChannel(ch);
        
        ch.attr(WorldChannel.CLIENT_KEY).set(wc);
        
        System.out.printf("[Debug] Channel Server has connected with remote address: %s%n",
                wc.getIP());
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {              
        Channel ch = ctx.channel();
        
        WorldChannel wc = (WorldChannel) ch.attr(WorldChannel.CLIENT_KEY).get();
        
        if (wc.isValid()) {
            ChannelService.getInstance().removeChannel(wc.getChannelId());
            System.out.printf("[Debug] Channel %s has disconnected from the World Server.%n",
                wc.getChannelId());
        }
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {              
        Channel ch = ctx.channel();
        
        WorldChannel wc = (WorldChannel) ch.attr(WorldChannel.CLIENT_KEY).get();
        
        Packet p = (Packet) msg;
        
        PacketReader pr = wc.getReader().next(p);
        
        int op = pr.readShort();
        
        PacketHandler h = HANDLERS[op];
        
        if (h != null) {
            if (h.validateState(wc)) {
                h.handle(wc, pr);
            } else {
                wc.close(); // goofing not allowed
            }
        } else {
            wc.close(); // goofing not allowed
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        t.printStackTrace(); // XXX remove later
    }
}
