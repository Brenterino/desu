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
import java.util.List;
import net.PacketReader;
import netty.PacketHandler;
import service.WorldService;

/**
 *
 * @author Brent
 */
public class CharacterListRequestHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedIn() && c.isPinVerified() && !c.isBanned();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        int world = r.read();
        int channel = r.read() + 1;

        if (!c.checkAgeRestriction(WorldService.getInstance().getWorld(world).getChannel(channel).AGE_RESTRICTED)) {
            c.write(PacketCreator.getAfterLoginError(11));
            c.write(PacketCreator.getCharacterListFailure());
            return;
        }

        List<Player> ps = c.getPlayersInWorld(world, world != c.getWorld());

        c.setWorldChannelSelection(world, channel);

        c.write(PacketCreator.getCharacterList(ps, c.getCharacterSlots()));
    }
}
