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
import net.Packet;
import net.PacketReader;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstraction for Netty channels that contains some attribute keys
 * for important resources used by the client during encryption, 
 * decryption, and general functions. <B>Note: Some methods cannot be
 * overridden by descendents due to the nature of the functionality they 
 * provide</B>
 * 
 * @author Brent
 */
public class NettyClient {
    
    /**
     * Attribute key for MapleAES related to this Client.
     */
    public static final AttributeKey<MapleCrypto> CRYPTO_KEY = AttributeKey.valueOf("A");
    /**
     * Attribute key for this NettyClient object.
     */
    public static final AttributeKey<NettyClient> CLIENT_KEY = AttributeKey.valueOf("C");
    
    /**
     * Send seed or IV for one of the cryptography stages.
     */
    private byte[] siv;
    /**
     * Receive seed or IV for one of the cryptography stages.
     */
    private byte[] riv;
    /**
     * Stored length used for packet decryption. This is used for
     * storing the packet length for the next packet that is readable.
     * Since TCP sessions ensure that all data arrives to the server in order,
     * we can decode packet data in the correct order.
     */
    private int storedLength = -1;
    /**
     * Channel object associated with this specific client. Used for all
     * I/O operations regarding a MapleStory game session.
     */
    protected final Channel ch;
    
    /**
     * Lock regarding the encoding of packets to be sent to remote 
     * sessions.
     */
    private final ReentrantLock lock;
    
    /**
     * PacketReader object for this specific session since this can help
     * scaling compared to keeping PacketWriter for each session.
     */
    private final PacketReader r;
    
    /**
     * Empty constructor for child class implementation.
     */
    private NettyClient() {
        ch = null;
        lock = null;
        r = null;
    }
    
    /**
     * Construct a new NettyClient with the corresponding Channel that
     * will be used to write to as well as the send and recv seeds or IVs.
     * @param c the channel object associated with this client session.
     * @param alpha the send seed or IV.
     * @param delta the recv seed or IV.
     */
    public NettyClient(Channel c, byte[] alpha, byte[] delta) {
        ch = c;
        siv = alpha;
        riv = delta;
        r = new PacketReader();
        lock = new ReentrantLock(true); // note: lock is fair to ensure logical sequence is maintained server-side
    }
    
    /**
     * Gets the PacketReader object associated with this NettyClient.
     * @return a packet reader.
     */
    public final PacketReader getReader() {
        return r;
    }
    
    /**
     * Gets the stored length for the next packet to be read. Used as
     * a decoding state variable to determine when it is ok to proceed with
     * decoding a packet.
     * @return stored length for next packet.
     */
    public final int getStoredLength() {
        return storedLength;
    }
    
    /**
     * Sets the stored length for the next packet to be read.
     * @param val length of the next packet to be read.
     */
    public final void setStoredLength(int val) {
        storedLength = val;
    }
    
    /**
     * Gets the current send seed or IV.
     * @return send IV.
     */
    public final byte[] getSendIV() {
        return siv;
    }
    
    /**
     * Gets the current recv seed or IV.
     * @return recv IV.
     */
    public final byte[] getRecvIV() {
        return riv;
    }

    /**
     * Sets the send seed or IV for this session.
     * @param alpha the new send IV.
     */
    public final void setSendIV(byte[] alpha) {
        siv = alpha;
    }

    /**
     * Sets the recv seed or IV for this session.
     * @param delta  the new recv IV.
     */
    public final void setRecvIV(byte[] delta) {
        riv = delta;
    }
    
    /**
     * Writes a packet message to the channel. Gets encoded later in the
     * pipeline.
     * @param msg the packet message to be sent. 
     */
    public void write(Packet msg) {
        ch.writeAndFlush(msg);
    }
    
    /**
     * Closes this channel and session.
     */
    public void close() {
        ch.close();
    }
    
    /**
     * Gets the remote IP address for this session.
     * @return the remote IP address.
     */
    public String getIP() {
        return ch.remoteAddress().toString().split(":")[0].substring(1);
    }
    
    /**
     * Acquires the encoding state for this specific send IV. This is to
     * prevent multiple encoding states to be possible at the same time. If 
     * allowed, the send IV would mutate to an unusable IV and the session would
     * be dropped as a result.
     */
    public final void acquireEncoderState() {
        lock.lock();
    }
    
    /**
     * Releases the encoding state for this specific send IV.
     */
    public final void releaseEncodeState() {
        lock.unlock();
    }
}
