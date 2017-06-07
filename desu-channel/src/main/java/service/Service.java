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
import crypto.MapleCrypto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import world.World;
import world.WorldHandler;

/**
 * Represents the Service entry point for a MapleStory Channel Server.
 * 
 * 
 * @author Brent
 */
public class Service extends Thread {
    
    private World w;
    private Channel c;
    private boolean shutdown = false;
    private EventLoopGroup workerGroup;
    
    private static Service instance;
    
    private Service() {
    }
    
    public void setWorld(World w) {
        this.w = w;
    }
    
    public World getWorld() {
        return w;
    }
    
    @Override
    public void run() {
        workerGroup = new NioEventLoopGroup();
        
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new PacketDecoder(), new WorldHandler(), new PacketEncoder());
                }
            });
            
            ChannelFuture f = b.connect(Configuration.CHANNEL_SERVICE, Configuration.CHANNEL_SERVICE_PORT);
            
            c = f.channel();
            
            f.sync();
            c.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            System.out.println("[Info] Disconnected from World Server.");
        }
    }

    public void shutdown(boolean planned) {
        if (!shutdown) {
            shutdown = true;
            GameService.getInstance().shutdown(planned);
        }
    }
    
    public static Service getInstance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }
    
    public static void main(String[] args) {
        MapleCrypto.initialize(Configuration.MAPLE_VERSION);
        Service.getInstance().start();
        GameService.getInstance().start();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
        
            @Override
            public void run() {
                Service.getInstance().shutdown(false);
            }
        });
    }
}
