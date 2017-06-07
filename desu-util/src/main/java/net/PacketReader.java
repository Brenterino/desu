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
package net;

import util.Reader;

/**
 * Artifact from Invictus. Unlike PacketWriter, this is able to be used for
 * reading data from a client over and over again without needing a lock. It is
 * still practical to have on per session rather than creating new generations
 * for each new received packet.
 * 
 * @author Brent
 */
public final class PacketReader extends Reader {

    private int offset;
    private byte[] data;

    public PacketReader() {
        offset = -1;
        data = null;
    }

    public PacketReader next(byte[] d) {
        offset = 0;
        data = d;
        return this;
    }

    public PacketReader next(Packet p) {
        return next(p.getData());
    }

    @Override
    public int read() {
        try {
            return 0xFF & data[offset++];
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public PacketReader skip(int num) {
        offset += num;
        return this;
    }

    @Override
    public int available() {
        return data.length - offset;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public void close() {
        offset = -1;
        data = null;
    }
}
