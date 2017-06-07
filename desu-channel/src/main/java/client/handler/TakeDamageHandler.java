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
import field.FieldObject;
import field.monster.Monster;
import net.PacketReader;
import netty.PacketHandler;
import player.Player;
import player.Violation;
import player.stats.Stat;

/**
 * 
 * @author Brent
 */
public class TakeDamageHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedin() && c.getPlayer().isAlive();
    }

    @Override
    public void handle(Client c, PacketReader pr) {
        // XXX handle mist, I think it is client-sided to begin with after
        // it is spawned, but who knows?
        Player p = c.getPlayer();
        pr.skip(4);
        int spec = pr.readByte();
        pr.skip(1);
        int damage = pr.readInteger();
        if (damage <= 0) {
            return;
        }
        int oid;
        int direction = 0;
        int x = 0, y = 0;
        int fake = 0;
        Monster src = null;
        if (spec != -2) {
            int id = pr.readInteger(); // monster id
            oid = pr.readInteger();
            direction = pr.read();
            FieldObject obj = p.getField().getFieldObject(oid);
            assert obj != null : Violation.PACKET_EDITTING;
            assert obj.getObjectType().equals(FieldObject.Type.MONSTER) : Violation.PACKET_EDITTING;
            src = (Monster) obj;
            assert src.getId() == id : Violation.PACKET_EDITTING;
        }       
        if (spec != -1 && spec != -2 && src != null) {
            // XXX handle monster attack later           
        }
        // XXX handle damage reflect later
        
        // XXX show damage taken to all players
        int lethalDamage = p.getStat(Stat.HP);
        
        p.subtractFromStat(Stat.HP, Math.min(lethalDamage, damage));
        
        p.applyChangedStats();
    }
}
