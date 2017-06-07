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

import client.Client;
import client.ClientHandler;
import db.Database;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.Packet;
import netty.PacketDecoder;
import netty.PacketEncoder;

/**
 *
 * @author Brent
 */
public class GameService extends Thread {
    
    private static GameService instance;
    
    private Database db;
    private ServerBootstrap sb;
    private Channel serverChannel;
    private ChannelGroup connected;
    private EventLoopGroup bossGroup, workerGroup;
    
    private GameService() {
    }
    
    public Database getDatabase() {
        return db;
    }
    
    @Override
    public void run() 
    {
        db = Database.createDatabase(Configuration.URL, Configuration.USER, Configuration.PASS);
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        connected = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        
        sb = new ServerBootstrap();
        
        sb.group(bossGroup, workerGroup);
        sb.channel(NioServerSocketChannel.class);
        sb.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel c) throws Exception {
                c.pipeline().addLast(new PacketDecoder(),  new ClientHandler(), new PacketEncoder());
            }
        });
        
        sb.option(ChannelOption.SO_BACKLOG, Configuration.MAXIMUM_CONNECTIONS);
        
        sb.childOption(ChannelOption.TCP_NODELAY, true);
        sb.childOption(ChannelOption.SO_KEEPALIVE, true);
        
        try {
            ChannelFuture f = sb.bind(Configuration.PORT).sync();
            serverChannel = f.channel();
            System.out.printf("[Info] Game Service has been bound to port %s.%n", Configuration.PORT);
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.printf("[Info] Game Service has been unbound from port %s.%n", Configuration.PORT);
        }
    }
    
    public void broadcastMessage(Packet p) {
        connected.write(p);
    }
    
    public void addConnected(Channel ch) {
        connected.add(ch);
    }

    public void updateBuddylists(boolean silent) {
        for (Channel c : connected) {
            ((Client) c.attr(Client.CLIENT_KEY).get()).updateBuddylist(silent);
        }
    }
    
    public int getLoad() {
        if (connected == null) {
            return 0;
        }
        return connected.size();
    }
    
    public void shutdown(boolean planned) {
        // XXX do more stuff here
        ChannelFuture sf = serverChannel.close();
        sf.awaitUninterruptibly();
    }
    
    public static GameService getInstance() {
        if (instance == null) instance = new GameService();
        return instance;
    }
}
