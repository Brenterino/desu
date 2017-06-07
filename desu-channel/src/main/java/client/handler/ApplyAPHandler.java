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

import static player.stats.Stat.*;

import client.Client;
import client.packet.PacketCreator;
import data.internal.Constants;
import net.PacketReader;
import netty.PacketHandler;
import player.Player;
import player.Violation;
import player.stats.Stat;

/**
 *
 * @author Brent
 */
public class ApplyAPHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedin() && c.getPlayer().isAlive();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        Player p = c.getPlayer();

        r.readInteger();
        int stat = r.readInteger();

        assert p.getStat(AP) > 0 : Violation.PACKET_EDITTING;
        
        Stat k = Stat.get(stat);
        
        if (k.equals(MAXHP)) { // special
            p.addHealthAP();
        } else if (k.equals(MAXMP)) {
            p.addManaAP();
        } else {
            int target = p.getStat(k);
            if (target < Constants.STAT_MAX) {
                p.incrementStat(k);
            } else {
                p.incrementStat(AP);
            }
        }
        p.decrementStat(AP);
        
        p.applyChangedStats();

        c.write(PacketCreator.enableActions());
    }
}
