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
package util;

import java.nio.charset.Charset;

/**
 * 
 * @author Brent
 */
public abstract class Reader {

    private static  Charset ASCII = Charset.forName("US-ASCII");
    
    public abstract int read();

    public final void read(byte[] in) {
        read(in, 0, in.length);
    }

    public final void read(byte[] in, int off, int len) {
        for (int i = off; i < len; i++) {
            in[i] = readByte();
        }
    }

    public final byte[] read(int num) {
        byte[] ret = new byte[num];
        for (int i = 0; i < num; i++) {
            ret[i] = readByte();
        }
        return ret;
    }

    public final boolean readBool() {
        return read() > 0;
    }

    public final byte readByte() {
        return (byte) read();
    }

    public final short readShort() {
        return (short) (read() + (read() << 8));
    }

    public final char readChar() {
        return (char) (read() + (read() << 8));
    }

    public final int readInteger() {
        return read() + (read() << 8) + (read() << 16)
                + (read() << 24);
    }

    public final float readFloat() {
        return Float.intBitsToFloat(readInteger());
    }

    public final long readLong() {
        return read() + (read() << 8) + (read() << 16)
                + (read() << 24) + (read() << 32)
                + (read() << 40) + (read() << 48)
                + (read() << 56);
    }

    public final double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public final String readString(int len) {
        byte[] sd = new byte[len];
        for (int i = 0; i < len; i++) {
            sd[i] = readByte();
        }
        return new String(sd, ASCII);
    }

    public final String readMapleString() {
        return readString(readShort());
    }

    public final String readNullTerminatedString() {
        StringBuilder sb = new StringBuilder();
        
        char c = 0;
        while ((c = (char) read()) != 0) {
            sb.append(c);
        }
        
        return sb.toString();
    }

    public abstract Reader skip(int num);

    public abstract int available();

    public abstract int getOffset();
    
    public abstract void close();
}
