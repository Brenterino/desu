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
import service.WorldService;
import world.World;
import world.WorldChannel;

/**
 *
 * @author Brent
 */
public class ChannelConnectionRequestHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedIn() && c.isPinVerified() && !c.isBanned();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        int cid = r.readInteger();
        
        if (c.getChannel() == -1) { // view all
            int worldId = r.readInteger() - 1; // (?)
            c.setWorldChannelSelection(worldId, 1); // default to channel 1
        }
        
        // XXX handle mac address + hwid
        r.readMapleString(); // mac address with hyphens
        
        r.readMapleString(); // mac address without hyphens + extra info

        int target = c.getWorld() * 100 + c.getChannel();

        boolean isOwner = c.getPlayersInWorld(-1, false).stream().anyMatch(p -> p.getId() == cid);

        if (isOwner) {
            World w = WorldService.getInstance().getWorld(c.getWorld());
            WorldChannel wc = w.getChannel(c.getChannel());
            if (wc != null) {

                w.write(world.packet.PacketCreator.updateAccountState(c.getAccountId(),
                                cid, c.getAccountName(), c.isGM(), c.getLastIP(), target));

                if (c.checkAgeRestriction(wc.AGE_RESTRICTED)) {
                    c.setPlayer(cid);
                    c.triggerTransition();
                    c.write(PacketCreator.getChannelConnectionInfo(wc, cid));
                } else {
                    c.write(PacketCreator.getAfterLoginError(11));
                }
                return;
            }
        }

        c.close();
    }
}
