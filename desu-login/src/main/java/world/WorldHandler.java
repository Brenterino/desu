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
package world;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.Packet;
import net.PacketReader;
import netty.PacketHandler;
import service.WorldService;
import world.handler.BroadcastMessageHandler;
import world.handler.ChannelUpdateHandler;
import world.handler.UpdateAccountStateHandler;
import world.handler.WorldInfoHandler;
import world.packet.RecvOpcode;

/**
 *
 * @author Brent
 */
public class WorldHandler extends ChannelInboundHandlerAdapter {    
    
    private final static PacketHandler[] HANDLERS;
     
    static {
        HANDLERS = new PacketHandler[400]; // XXX change this to max value of handler
        
        HANDLERS[RecvOpcode.BROADCAST_MESSAGE.getValue()] = new BroadcastMessageHandler();
        HANDLERS[RecvOpcode.CHANNEL_UPDATE.getValue()] = new ChannelUpdateHandler();
        HANDLERS[RecvOpcode.UPDATE_ACCOUNT_STATE.getValue()] = new UpdateAccountStateHandler();
        HANDLERS[RecvOpcode.WORLD_INFO.getValue()] = new WorldInfoHandler();
    }
    
    public WorldHandler() {
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();
        
        World w = new World(ch);
        
        ch.attr(World.CLIENT_KEY).set(w);
        
        System.out.printf("[Debug] World Server has connected with remote address: %s%n",
                w.getIP());
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {        
        Channel ch = ctx.channel();
        
        World w = (World) ch.attr(World.CLIENT_KEY).get();
        
        if (w.isValid()) {
            WorldService.getInstance().removeWorld(w.getWorldId());
            System.out.printf("[Debug] %s has disconnected from Login Server.%n",
                    w.getWorldName());
        }
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {        
        Channel ch = ctx.channel();
        
        World w = (World) ch.attr(World.CLIENT_KEY).get();
        
        Packet p = (Packet) msg;
        
        PacketReader pr = w.getReader().next(p);
        
        int op = pr.readShort();
        
        PacketHandler h = HANDLERS[op];
        
        if (h != null) {
            if (h.validateState(w)) {
                h.handle(w, pr);
            } else {
                w.close();
            }
        } else {
            w.close();
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        t.printStackTrace(); // XXX remove later
    }
}
