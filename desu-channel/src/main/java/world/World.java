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
package world;

import io.netty.channel.Channel;
import netty.NettyClient;

/**
 *
 * @author Brent
 */
public class World extends NettyClient {
    
    private int expMod;
    private int dropMod;
    private int worldId;
    private String worldName;
    private String serverMsg;
    
    public World(Channel c) {
        super(c, null, null);
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public void setWorldName(String name) {
        worldName = name;
    }
    
    public int getWorldId() {
        return worldId;
    } 
    
    public void setWorldId(int wid) {
        worldId = wid;
    }

    public String getServerMessage() {
        return serverMsg;
    }
    
    public void setServerMessage(String msg) {
        serverMsg = msg;
    }

    public int getDropModifier() {
        return dropMod;
    }

    public int getExperienceModifier() {
        return expMod;
    }
    
    public void setExperienceModifier(int mod) {
        expMod = mod;
    }
    
    public void setDropModifier(int mod) {
        dropMod = mod;
    }
}
