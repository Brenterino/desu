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

/**
 *
 * @author Brent
 */
public class SetGenderHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return !c.isBanned() && c.isLoggedIn() && c.getAccountGender() == 0x0A;
    }

    @Override
    public void handle(Client c, PacketReader r) {
        if (r.available() > 1) {
            c.setAccountGender(r.readShort() - 1); // can be packet editted, but there's no benefit (just for the lols)
            c.write(PacketCreator.getAuthSuccess(c));
        } else {
            c.softDisconnect(true); // if only 1 byte was available, then we should soft-disconnect the client with its data (otherwise the client will be stuck)
        }
    }
}
