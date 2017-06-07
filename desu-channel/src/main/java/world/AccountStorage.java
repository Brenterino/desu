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

import java.util.concurrent.TimeUnit;
import service.Service;
import util.KeyBlockingConcurrentHashMap;
import world.packet.PacketCreator;

/**
 *
 * @author Brent
 */
public class AccountStorage {

    private static final KeyBlockingConcurrentHashMap<Integer, Storage> accounts
            = new KeyBlockingConcurrentHashMap<>();

    public static class Storage {

        int accountId = -1;
        String name = null;
        String lastIP = null;
        boolean isGM = false;
        long startTime = System.currentTimeMillis();

        Storage(int id, String pn, boolean gm, String ip) {
            accountId = id;
            name = pn;
            lastIP = ip;
        }

        public int getAccountId() {
            return accountId;
        }

        public String getAccountName() {
            return name;
        }

        public String getLastIP() {
            return lastIP;
        }

        public boolean isGM() {
            return isGM;
        }

        public boolean hasExpired() {
            return System.currentTimeMillis() - startTime > 30000L;
        }
    }

    public static boolean inStorage(int cid) {
        return accounts.containsKey(cid);
    }

    public static void addIncoming(int cid, int accId, String name, boolean gm, String lastIP) {
        accounts.put(cid, new Storage(accId, name, gm, lastIP));
        
        accounts.arrive(cid);
    }

    public static Storage getAccount(int cid) {
        if (accounts.containsKey(cid)) {
            return accounts.remove(cid);
        }
        return null;
    }

    public static Storage awaitStorage(int cid) {
        if (accounts.await(cid, 30, TimeUnit.SECONDS)) {
            return getAccount(cid);
        }
        return null;
    }

    // could make this storage instead
    public static void updateAccount(int accountId, int id, boolean isGM, String lastIP, String name, int target) {
        Service.getInstance().getWorld().write(PacketCreator.updateAccountState(accountId, id, isGM, lastIP, name, target));
    }

    private AccountStorage() {
    }
}
