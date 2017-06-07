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
package client.handler;

import client.Client;
import client.packet.PacketCreator;
import net.PacketReader;
import netty.PacketHandler;
import player.Player;
import player.Violation;
import world.PlayerStorage;

/**
 *
 * @author Brent
 */
public class PlayerLoggedInHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return !c.isLoggedin();
    }

    @Override
    public void handle(Client c, PacketReader pr) {
        int cid = pr.readInteger();
        
//        assert c.checkAccount(cid) : Violation.PACKET_EDITTING; // ban ip
        
        if (!c.checkAccount(cid)) {
            c.disconnect();
            return;
        } 
        
        Player p = c.loadCharacter(cid);
        
        assert p != null : Violation.PACKET_EDITTING;
        
        if (p.isGM()) { // XXX give them "empty" hide buff maybe?
            p.toggleHidden();
        }
        
        c.write(PacketCreator.playerLogin(p));
        c.write(PacketCreator.sendKeymap(p));
        // XXX silent buff player from old channel
        // XXX sendMacros
        // XXX Guild + Alliance
        // XXX Notes
        // XXX Parties
        // XXX updateBuddylist
        p.getField().spawnPlayer(p);
        
        PlayerStorage.registerLocalPlayer(p);
    }
}
