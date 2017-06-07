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

import java.awt.Point;
import java.nio.charset.Charset;

/**
 * 
 * @author Brent
 */
public abstract class Writer {

    private static final Charset ASCII = Charset.forName("US-ASCII");

    public abstract Writer write(int b);

    private Writer write(long lb) {
        return write((int) lb);
    }

    public final Writer write(byte[] in) {
        return write(in, 0, in.length);
    }

    public final Writer write(byte[] in, int off, int len) {
        for (int i = off; i < len; i++) {
            write(in[i]);
        }
        return this;
    }

    public final Writer write(int... b) {
        for (int i = 0; i < b.length; i++) {
            write(b[i]);
        }
        return this;
    }

    public final Writer writeByte(byte b) {
        return write(b);
    }

    public final Writer writeShort(int s) {
        return write(s & 0xFF).write(s >>> 8);
    }

    public final Writer writeShort(short s) {
        return write(s & 0xFF).write(s >>> 8);
    }

    public final Writer writeChar(char c) {
        return writeShort(c);
    }

    public final Writer writeInteger(int i) {
        return write(i & 0xFF).write(i >>> 8).write(i >>> 16).
                write(i >>> 24);
    }

    public final Writer writeFloat(float f) {
        return writeInteger(Float.floatToIntBits(f));
    }

    public final Writer writeLong(long l) {
        return write(l & 0xFF).write(l >>> 8).write(l >>> 16).
                write(l >>> 24).write(l >>> 32).write(l >>> 40).
                write(l >>> 48).write(l >>> 56);
    }

    public final Writer writeDouble(double d) {
        return writeLong(Double.doubleToLongBits(d));
    }

    public final Writer writeString(String s) {
        return write(s.getBytes(ASCII));
    }

    public final Writer writeMapleString(String s) {
        return writeShort(s.length()).writeString(s);
    }

    public final Writer writeNullTerminatedString(String s) {
        return writeString(s).write(0);
    }

    public final Writer writeHex(String s) {
        return write(HexTool.toBytes(s));
    }

    public final Writer writeBool(boolean b) {
        return write(b ? 1 : 0);
    }

    public final Writer fill(int val, int num) {
        for (int i = 0; i < num; i++) {
            write(val);
        }
        return this;
    }
    
    public final Writer writePosition(Point p) {
        return writeShort(p.x).writeShort(p.y);
    }

    public abstract int getOffset();

    public abstract void close();
}
