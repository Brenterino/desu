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
package service;

import com.PacketDecoder;
import com.PacketEncoder;
import db.Database;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import world.World;
import world.WorldHandler;
import world.packet.PacketCreator;

/**
 * Represents the service for maintaining World Server connections and 
 * keeping them informed as well as checking the health of their numerous
 * channels.
 * 
 * @author Brent
 */
public class WorldService extends Thread {
    
    private ServerBootstrap sb;
    private Channel serverChannel;
    private static WorldService instance;
    private HashMap<Integer, World> activeWorlds;
    private EventLoopGroup bossGroup, workerGroup;
    
    private WorldService() {
        activeWorlds = new HashMap<>();
    }
    
    public void addWorld(World w) {
        activeWorlds.put(w.getWorldId(), w);
    }
    
    public void removeWorld(int wid) {
        Database db = activeWorlds.remove(wid).getDatabase();
        if (db != null) {
            try {
                db.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    } 
    
    public Collection<World> getWorlds() {
        return activeWorlds.values();
    }
    
    public World getWorld(int id) {
        return activeWorlds.get(id);
    }
    
    @Override
    public void run() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        
        sb = new ServerBootstrap();
        
        sb.group(bossGroup, workerGroup);
        sb.channel(NioServerSocketChannel.class);
        sb.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel c) throws Exception {
                c.pipeline().addLast(new PacketDecoder(), new WorldHandler(), new PacketEncoder());
            }
        });
        
        sb.childOption(ChannelOption.TCP_NODELAY, true);
        sb.childOption(ChannelOption.SO_KEEPALIVE, true);
        
        try {
            ChannelFuture f = sb.bind(Configuration.WORLD_SERVICE_PORT).sync();
            serverChannel = f.channel();
            System.out.printf("[Info] World Service has been bound to port %s.%n", Configuration.WORLD_SERVICE_PORT);
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.printf("[Info] World Service has been unbound from port %s.%n", Configuration.WORLD_SERVICE_PORT);
        }
    }

    public void shutdown(boolean planned) {
        for (World w : activeWorlds.values()) {
            w.write(PacketCreator.getShutdown(planned));
        }
        
        ChannelFuture f = serverChannel.close();
        f.awaitUninterruptibly();
    }
    
    public static WorldService getInstance() {
        if (instance == null) instance = new WorldService();
        return instance;
    }
}
