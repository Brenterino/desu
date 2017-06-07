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
import net.PacketReader;
import netty.PacketHandler;
import player.Player;
import player.stats.Stat;

/**
 *
 * @author Brent
 */
public class HealOverTimeHandler implements PacketHandler<Client>  {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedin() && c.getPlayer().isAlive();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        Player p = c.getPlayer();
        
        r.skip(4);
        
        int amountHP = r.readShort();
        int amountMP = r.readShort();
        
        int maxHealHP = p.getStat(Stat.MAXHP) - p.getStat(Stat.HP); // HP difference
        int maxHealMP = p.getStat(Stat.MAXMP) - p.getStat(Stat.MP); // MP difference
        
        p.addToStat(Stat.HP, Math.min(amountHP, maxHealHP)); // XXX fix later for hackerino B^)
        p.addToStat(Stat.MP, Math.min(amountMP, maxHealMP)); // XXX fix later for hackerino B^)
        
        p.applyChangedStats();
    }

}
