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
import client.Player;
import client.packet.PacketCreator;
import java.util.LinkedList;
import java.util.List;
import net.PacketReader;
import netty.PacketHandler;

/**
 *
 * @author Brent
 */
public class ViewAllCharactersHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedIn() && c.isPinVerified() && !c.isBanned();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        List<Player> players = c.getPlayersInWorld(-1, true);
        int hiWorld = 0;
        c.write(PacketCreator.getAllCharacterCount(players.size()));
        if (!players.isEmpty()) {
            for (Player p : players) {
                hiWorld = Math.max(hiWorld, p.getWorld());
            }
            for (int i = 0; i < hiWorld + 1; i++) {
                int cur = i;
                List<Player> inWorld = new LinkedList<>();
                players.stream().filter(p -> p.getWorld() == cur).forEach(p -> inWorld.add(p));
                c.write(PacketCreator.getAllCharacterList(inWorld, i));
            }
        }
    }
}
