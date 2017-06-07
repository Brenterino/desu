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
import data.external.GameDatabase;
import net.PacketReader;
import netty.PacketHandler;
import player.Player;
import player.Violation;

/**
 *
 * @author Brent
 */
public class ModifyKeyBindingsHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedin();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        assert r.available() > 8 : Violation.PACKET_EDITTING;

        r.readInteger(); // 1 and 2 are given here when inventory is opened

        int size = r.readInteger();

        assert r.available() > 9 * size;

        Player p = c.getPlayer();

        for (int i = size; i > 0; i--) {
            int key = r.readInteger();
            int type = r.read(); // location of the action on the keyboard and the actual type of it
            int action = r.readInteger(); // skill, item, generic key
            
            if (GameDatabase.getSkill(action) != null) { // not sure if this will throw an error
                assert p.hasSkill(action) : Violation.PACKET_EDITTING;
            }
            
            p.modifyKeyBinding(key, type, action);
        }
    }
}
