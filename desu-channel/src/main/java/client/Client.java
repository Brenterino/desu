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
package client;

import client.packet.PacketCreator;
import field.Field;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import netty.NettyClient;
import player.Player;
import world.AccountStorage;
import world.PlayerStorage;

/**
 * Represents a client within the Channel Server state.
 *
 * @author Brent
 */
public class Client extends NettyClient {

    private Player player;
    private int target = 0;
    private int accountId = -1;
    private String accountName = "";
    private String lastIP = "";
    private boolean gm = false;
    private boolean loggedIn = false;
    private ScheduledFuture<?> ping;
    // XXX store assigned tasks via event loop and cancel them here

    public Client(Channel c, byte[] alpha, byte[] delta) {
        super(c, alpha, delta);
    }

    public Player getPlayer() {
        return player;
    }

    public void startPing() {
        ping = ch.eventLoop().scheduleAtFixedRate(() -> 
                ch.writeAndFlush(PacketCreator.getPing()), 5, 5, TimeUnit.SECONDS);
    }
    
    public ScheduledFuture<?> startFieldPulse(final Field f) {
        return ch.eventLoop().scheduleAtFixedRate(() -> f.pulse(), 
                1, // initial delay
                1, // pulse period
                TimeUnit.SECONDS); 
        // units and delays can be changed if higher accuracy is required
    }
    
    public ScheduledFuture<?> schedule(Runnable r, long millis) {
        return ch.eventLoop().schedule(r, millis, TimeUnit.MILLISECONDS);
    }
    
    public void cancelPing() {
        if (ping != null) {
            ping.cancel(true);
        }
    }

    public void triggerTransition(int target) {
        this.target = target;
    }
    
    public void disconnect() {
        disconnect(true);
    }

    public void disconnect(boolean force) {
        cancelPing();
        loggedIn = false;
        if (player != null) { // if player is null, we don't care
        // XXX todo
            player.save();
            player.getField().removePlayer(player);
            if (target > 0) { // if it is 0, then just drop
                PlayerStorage.transitLocalPlayer(player, target);
            } else {
                PlayerStorage.deregisterLocalPlayer(player);
            }
            AccountStorage.updateAccount(accountId, player.getId(), gm, lastIP, accountName, target);
        }
        player = null;
        if (force) {
            close();
        }
    }

    public boolean isLoggedin() {
        return loggedIn;
    }

    public boolean checkAccount(int cid) {
        AccountStorage.Storage s = AccountStorage.awaitStorage(cid);
        if (s != null) {
            if (!s.hasExpired()) {
                accountId = s.getAccountId();
                accountName = s.getAccountName();
                lastIP = s.getLastIP();
                gm = s.isGM();
                
                return lastIP.equals(getIP()); // hax0rs get shrekt here
            }
        }
        return false;
    }

    public Player loadCharacter(int cid) {
        loggedIn = true;
        
        player = Player.load(cid);
        player.setClient(this);
        
        return player;
    }

    public boolean isGM() {
        return gm;
    }

    public void updateBuddylist(boolean silent) {
        if (player != null) {
            player.getBuddylist().forceUpdate(silent);
        }
    }
}
