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
package player;

import client.packet.PacketCreator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import world.PlayerStorage;

/**
 *
 * @author Brent
 */
public final class Buddylist {

    private int capacity = 20;
    private Player owner;
    private HashMap<String, Group> groups;

    private Buddylist() {
    }

    public Buddylist(int cap, Player p) {
        owner = p;
        capacity = cap;
        groups = new HashMap<>();
        addGroup("Default Group"); // just add default group automatically
    }

    public int getCapacity() {
        return capacity;
    }

    public void forceUpdate(boolean silent) {
        for (Group g : groups.values()) {
            for (Entry e : g.buddies) {
                e.checkOnline();
                if (e.isOnline() && !silent) {
                    owner.getClient().write(PacketCreator.updateBuddyChannel(e.id, e.channel));
                }
            }
        }
    }

    public static class Entry {

        public int id;
        public String name;
        public int channel = -1;

        public void checkOnline() {
            channel = PlayerStorage.getChannelOfPlayer(id);
        }

        public boolean isOnline() {
            return channel > 0;
        }
    }

    private class Group {

        public ArrayList<Entry> buddies = new ArrayList<>();
    }

    public void addGroup(String group) {
        if (!groups.containsKey(group)) {
            groups.put(group, new Group());
        }
    }

    public boolean hasGroup(String name) {
        return groups.containsKey(name);
    }

    private void addBuddyInternal(String group, Entry e) {
        if (hasGroup(group)) {
            groups.get(group).buddies.add(e);
        }
    }

    public void save(Connection c) {
        try {
            PreparedStatement ps = c.prepareStatement("DELETE FROM buddylist WHERE owner = ?");
            ps.setInt(1, owner.getId());
            ps.execute();
            ps.close();
            for (Map.Entry<String, Group> group : groups.entrySet()) {
                String name = group.getKey();
                Group g = group.getValue();
                StringBuilder idB = new StringBuilder();
                StringBuilder nB = new StringBuilder();
                for (Entry e : g.buddies) {
                    idB.append(e.id).append(",");
                    nB.append(e.name).append(",");
                }
                ps = c.prepareStatement("INSERT INTO buddylist (owner, `group`, buddyids, buddynames) VALUES (?, ?, ?, ?)");
                ps.setInt(1, owner.getId());
                ps.setString(2, name); // group name
                ps.setString(3, idB.toString());
                ps.setString(4, nB.toString());
                ps.executeUpdate();
                ps.close();
            }
        } catch (Exception e) {
            System.out.println("[SEVERE] Failed saving buddy list.");
            e.printStackTrace(); // XXX remove later
        }
    }

    public static Buddylist load(Connection c, int cap, Player owner) {
        Buddylist ret = new Buddylist(cap, owner);
        try {
            PreparedStatement ps = c.prepareStatement("SELECT `group`, buddyids, buddynames FROM buddylist WHERE owner = ?");
            ps.setInt(1, owner.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String group = rs.getString("group");
                ret.addGroup(group);
                String buddyIds = rs.getString("buddyids");
                if (buddyIds.length() > 0) {
                    buddyIds = buddyIds.substring(0, buddyIds.length() - 1);
                }
                String buddyNames = rs.getString("buddynames");
                if (buddyNames.length() > 0) {
                    buddyNames = buddyNames.substring(0, buddyIds.length() - 1);
                }
                if (!buddyIds.isEmpty() && !buddyNames.isEmpty()) {
                    String[] idSplit = buddyIds.split(",");
                    String[] nameSplit = buddyNames.split(",");
                    for (int i = 0; i < idSplit.length; i++) {
                        Entry e = new Entry();
                        e.id = Integer.parseInt(idSplit[i]);
                        e.name = nameSplit[i];
                        e.checkOnline();
                        ret.addBuddyInternal(group, e);
                    }
                    ret.addBuddyInternal(group, null);
                }
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            System.out.println("[SEVERE] Failed loading buddy list.");
            e.printStackTrace(); // XXX remove later
        }
        return ret;
    }
}
