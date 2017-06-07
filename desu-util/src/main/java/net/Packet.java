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

import util.HexTool;

/**
 * Represents a packet to be sent over a TCP socket for MapleStory. 
 * Very simply, it is an abstraction of raw data that applies some extra 
 * functionality because it is a MapleStory packet.
 *
 * @author Brent
 */
public class Packet implements Cloneable {

    private byte[] data;

    public Packet() {
    }

    public Packet(byte[] nD) {
        data = nD;
    }

    public int getLength() {
        if (data != null) {
            return data.length;
        }
        return 0;
    }

    public int getHeader() {
        if (data.length < 2) {
            return 0xFFFF;
        }
        return (data[0] + (data[1] << 8));
    }

    public void setData(byte[] nD) {
        data = nD;
    }

    public byte[] getData() {
        return data;
    }
    
    @Override
    public String toString() {
        if (data == null) return "";
        return HexTool.toHex(data);
    }
    
    @Override
    public Packet clone() {
        return new Packet(data);
    }
}
