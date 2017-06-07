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

import db.Database;
import io.netty.channel.Channel;
import java.util.Collection;
import java.util.HashMap;
import netty.NettyClient;

/**
 *
 * @author Brent
 */
public class World extends NettyClient {
    
    private int worldId;
    private int eventFlag;
    private String eventMsg;
    private String worldName;
    private boolean isValid = false;
    private HashMap<Integer, WorldChannel> channels;
    private Database db;
    
    public World(Channel c) {
        super(c, null, null);
        channels = new HashMap<>();
    }
    
    public void removeChannel(int id) {
        channels.remove(id);
    }

    public void removeChannel(WorldChannel wc) {
        channels.remove(wc.ID);
    }
    
    public void addChannel(WorldChannel w) {
        channels.put(w.ID, w);
    }
    
    public void setValid(boolean v) {
        isValid = v;
    }
    
    public boolean isValid() {
        return isValid;
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

    public String getEventMessage() {
        return eventMsg;
    }
    
    public void setEventMessage(String msg) {
        eventMsg = msg;
    }
    
    public int getEventFlag() {
        return eventFlag;
    }
    
    public void setEventFlag(int f) {
        eventFlag = f;
    }

    public WorldChannel getChannel(int channel) {
        return channels.get(channel);
    }
    
    public Collection<WorldChannel> getChannels() {
        return channels.values();
    }
    
    public Database getDatabase() {
        return db;
    }

    public void setDatabaseInfo(String url, String user, String pass) {
        db = Database.createDatabase(url, user, pass);
    }
}
