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
package channel;

import io.netty.channel.Channel;
import netty.NettyClient;

/**
 *
 * @author Brent
 */
public class WorldChannel extends NettyClient {

    private short load = 0;
    private byte[] publicIP;
    private short publicPort;
    private int channelId = -1;
    private boolean valid = false;
    private boolean ageRestricted = false;
    
    public WorldChannel(Channel c) {
        super(c, null, null);
    }
    
    public short getLoad() {
        return load;
    }
    
    public void setLoad(short l) {
        load = l;
    }
    
    public byte[] getPublicIP() {
        return publicIP;
    }
    
    public void setPublicIP(byte[] ip) {
        publicIP = ip;
    }
    
    public short getPublicPort() {
        return publicPort;
    }
    
    public void setPublicPort(short pp) {
        publicPort = pp;
    }
    
    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int cid) {
        channelId = cid;
    }
    
    public void setValid(boolean v) {
        valid = v;
    }

    public boolean isValid() {
        return valid;
    }

    public void setAgeRestricted(boolean ar) {
        ageRestricted = ar;
    }

    public boolean isAgeRestricted() {
        return ageRestricted;
    }
}
