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

/**
 *
 * @author Brent
 */
public class MonsterAggroHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedin();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        int oid = r.readInteger();
        
        Player p = c.getPlayer();
        
        FieldObject target = p.getField().getFieldObject(oid);
        
        assert target != null : Violation.PACKET_EDITTING; // latency can cause this
        assert target.getObjectType().equals(FieldObject.Type.MONSTER) : Violation.PACKET_EDITTING;
        
        Monster m = (Monster) target;
        
        System.out.println("Monster Aggro Handler called");
//        m.checkAggroStatus();
    }
}
