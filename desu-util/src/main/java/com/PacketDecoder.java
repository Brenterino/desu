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
package com;

import netty.*;
import net.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 *
 * @author Brent
 */
public class PacketDecoder extends ByteToMessageDecoder {

    public PacketDecoder() {
        // empty constructor -> nothing required here
    }

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf in, List<Object> out) throws Exception {
        NettyClient c = chc.channel().attr(NettyClient.CLIENT_KEY).get();

        if (c != null) {
            if (c.getStoredLength() == -1) {
                if (in.readableBytes() >= 4) {
                    c.setStoredLength(in.readInt());
                }
            }

            if (c.getStoredLength() != -1) {
                if (in.readableBytes() >= c.getStoredLength()) {
                    byte[] data = new byte[c.getStoredLength()];
                    in.readBytes(data);
                    c.setStoredLength(-1);
                    out.add(new Packet(data));
                }
            }
        }
    }
}
