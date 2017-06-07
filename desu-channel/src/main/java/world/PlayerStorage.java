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

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import player.Player;
import service.Configuration;
import service.GameService;
import service.Service;
import world.packet.PacketCreator;

/**
 *
 * @author Brent
 */
public class PlayerStorage {

    private static final ConcurrentHashMap<Integer, Storage> onlinePlayers
            = new ConcurrentHashMap<>();
    private static final LinkedList<Integer> incomingPlayers
            = new LinkedList<>();

    // XXX may need to add a blocking queue for storage + clean this up
    private static class Storage {

        int channelID = -1;
        String name = null;

        Storage(int id, String pn) {
            channelID = id;
            name = pn;
        }
    }

    public static boolean isOnline(int cid) {
        return onlinePlayers.containsKey(cid);
    }

    private static void addInternal(int cid, String name, int channel) {
        if (onlinePlayers.containsKey(cid)) {
            onlinePlayers.get(cid).channelID = channel;
        } else {
            onlinePlayers.put(cid, new Storage(channel, name));
        }
    }

    private static void removeInternal(int cid) {
        onlinePlayers.remove(cid);
    }

    public static void registerLocalPlayer(Player p) {
        synchronized (incomingPlayers) {
            if (incomingPlayers.contains(p.getId())) {
                incomingPlayers.remove(p.getId());
                return;
            }
        }
        addInternal(p.getId(), p.getName(), Configuration.CHANNEL_ID);

        GameService.getInstance().updateBuddylists(false);

        Service.getInstance().getWorld().write(PacketCreator.getPlayerConnected(p.getId(), p.getName()));
    }

    public static void deregisterLocalPlayer(Player p) {
        removeInternal(p.getId());

        GameService.getInstance().updateBuddylists(true);

        Service.getInstance().getWorld().write(PacketCreator.getPlayerDisconnected(p.getId(), p.getName()));
    }

    public static void registerRemotePlayer(int cid, String name, int channel) {
        addInternal(cid, name, channel);

        GameService.getInstance().updateBuddylists(false);
    }

    public static void deregisterRemotePlayer(int cid) {
        removeInternal(cid);

        GameService.getInstance().updateBuddylists(true);
    }

    public static void transitLocalPlayer(Player p, int target) {
        addInternal(p.getId(), p.getName(), target); // update vs. remove

        GameService.getInstance().updateBuddylists(true);

        Service.getInstance().getWorld().write(PacketCreator.transitPlayer(p, target));
    }

    public static void transitRemotePlayer(int cid, String name, int channelTo) {
        addInternal(cid, name, channelTo);

        if (channelTo == Configuration.CHANNEL_ID) {
            synchronized (incomingPlayers) {
                incomingPlayers.add(cid);
            }
        }

        // XXX we may want to do some magic here to make sure the source of the transit sends all info regarding transit
        GameService.getInstance().updateBuddylists(channelTo != Configuration.CHANNEL_ID);
    }

    public static int getChannelOfPlayer(int id) {
        if (onlinePlayers.containsKey(id)) {
            return onlinePlayers.get(id).channelID;
        }
        return -1;
    }

    private PlayerStorage() {
    }
}
