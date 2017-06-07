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

import channel.WorldChannel;
import channel.WorldChannelHandler;
import channel.packet.PacketCreator;
import com.PacketDecoder;
import com.PacketEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.Collection;
import java.util.HashMap;

/**
 * Represents the service for maintaining Channel Server connections.
 * 
 * @author Brent
 */
public class ChannelService extends Thread {
    
    private ServerBootstrap sb;
    private Channel serverChannel;
    private static ChannelService instance;
    private HashMap<Integer, WorldChannel> activeChannels;
    private EventLoopGroup bossGroup, workerGroup;
    
    private ChannelService() {
        activeChannels = new HashMap<>();
    }
    
    public void addChannel(WorldChannel wc) {
        activeChannels.put(wc.getChannelId(), wc);
        Service.getInstance().getLoginServer().write(login.packet.PacketCreator.getChannelUpdate(2, wc));
    }
    
    public void removeChannel(int wcid) {
        WorldChannel wc = activeChannels.remove(wcid);
        Service.getInstance().getLoginServer().write(login.packet.PacketCreator.getChannelUpdate(3, wc));
    }
    
    public Collection<WorldChannel> getChannels() {
        return activeChannels.values();
    }
    
    public WorldChannel getChannel(int id) {
        return activeChannels.get(id);
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
                c.pipeline().addLast(new PacketDecoder(), new WorldChannelHandler(), new PacketEncoder());
            }
        });
        
        sb.childOption(ChannelOption.TCP_NODELAY, true);
        sb.childOption(ChannelOption.SO_KEEPALIVE, true);
        
        try {
            ChannelFuture f = sb.bind(Configuration.CHANNEL_SERVICE_PORT).sync();
            serverChannel = f.channel();
            System.out.printf("[Info] Channel Service has been bound to port %s.%n", Configuration.CHANNEL_SERVICE_PORT);
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.printf("[Info] Channel Service has been unbound from port %s.%n", Configuration.CHANNEL_SERVICE_PORT);
        }
    }

    public void shutdown(boolean planned) {
        for (WorldChannel wc : activeChannels.values()) {
            wc.write(PacketCreator.getShutdown(planned));
        }
        
        ChannelFuture f = serverChannel.close();
        f.awaitUninterruptibly();
    }
    
    public static ChannelService getInstance() {
        if (instance == null) instance = new ChannelService();
        return instance;
    }
}
