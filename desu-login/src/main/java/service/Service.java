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

import client.ClientHandler;
import crypto.MapleCrypto;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import netty.PacketDecoder;
import netty.PacketEncoder;

/**
 * Represents the Service entry point for a MapleStory Login Server.
 *
 * @author Brent
 */
public final class Service extends Thread {

    private static Service instance;

    private Database db;
    private ServerBootstrap sb;
    private Channel serverChannel;
    private EventLoopGroup bossGroup, workerGroup;

    private Service() {
    }

    public Database getDatabase() {
        return db;
    }

    @Override
    public void run() {
        db = Database.createDatabase(Configuration.URL, Configuration.USER, Configuration.PASS);

        Connection c = db.getConnection();
        try {
            PreparedStatement ps = c.prepareStatement("UPDATE accounts SET state = 0");
            ps.executeUpdate();
            ps.close();
            
            ps = c.prepareStatement("DELETE FROM transition");
            ps.execute();
            ps.close();
        } catch (Exception e) { // NPE will be caught if c is null AKA no connection
            System.out.println("[Info] Could not connect to database. Check configuration and ensure database is online.");
            System.exit(0);
        } finally {
            try {
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        sb = new ServerBootstrap();

        sb.group(bossGroup, workerGroup);
        sb.channel(NioServerSocketChannel.class);
        sb.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel c) throws Exception {
                c.pipeline().addLast(new PacketDecoder(), new ClientHandler(), new PacketEncoder());
            }
        });

        sb.option(ChannelOption.SO_BACKLOG, Configuration.MAXIMUM_CONNECTIONS);

        sb.childOption(ChannelOption.TCP_NODELAY, true);
        sb.childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            ChannelFuture f = sb.bind(Configuration.PORT).sync();
            serverChannel = f.channel();
            System.out.printf("[Info] Login Service has been bound to port %s.%n", Configuration.PORT);
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.printf("[Info] Login Service has been unbound from port %s.%n", Configuration.PORT);
        }
    }

    public void shutdown(boolean planned) {
        ChannelFuture sf = serverChannel.close();
        sf.awaitUninterruptibly();

        WorldService.getInstance().shutdown(planned);
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
        WorldService.getInstance().start();

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                Service.getInstance().shutdown(false);
            }
        });
    }
}
