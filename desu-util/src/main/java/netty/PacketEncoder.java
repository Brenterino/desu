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
package netty;

import crypto.MapleCrypto;
import crypto.ShandaCrypto;
import net.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Implementation of a Netty encoder pattern so that encryption of MapleStory
 * packets is possible. Follows steps using the special MapleAES as well as
 * ShandaCrypto (which became non-used after v149.2 in GMS).
 *
 * @author Brent
 */
public final class PacketEncoder extends MessageToByteEncoder<Packet> {

    public PacketEncoder() {
        // empty constructor -> nothing required here
    }

    @Override
    protected void encode(ChannelHandlerContext chc, Packet in, ByteBuf bb) throws Exception {
        byte[] data = in.getData();
        NettyClient c = chc.channel().attr(NettyClient.CLIENT_KEY).get();
        MapleCrypto mCr = chc.channel().attr(NettyClient.CRYPTO_KEY).get();

        if (c != null) {
            byte[] iv = c.getSendIV();
            byte[] head = MapleCrypto.getHeader(data.length, iv);

            ShandaCrypto.encrypt(data);

            c.acquireEncoderState();
            try {
                mCr.crypt(data, iv);
                c.setSendIV(MapleCrypto.getNewIv(iv));
            } finally {
                c.releaseEncodeState();
            }
            
            bb.writeBytes(head);
            bb.writeBytes(data);
            
        } else {
            bb.writeBytes(data);
        }
    }
}
