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
import field.Field;
import field.movement.Movement;
import field.movement.MovementParser;
import java.util.Collection;
import net.PacketReader;
import netty.PacketHandler;
import player.Player;
import player.Violation;

/**
 *
 * @author Brent
 */
public class MovePlayerHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedin();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        Player p = c.getPlayer();
        
        // XXX supposedly, when you die you have a movement? zzz
        
        assert p.isAlive() : Violation.PACKET_EDITTING;

        r.skip(5);

        Collection<Movement> mov = MovementParser.parse(r);
        if (mov != null && mov.size() > 0) {
            MovementParser.validateMovement(mov, p);
            MovementParser.updatePosition(mov, p, 0);
           
            Field f = p.getField();
            
            f.broadcast(PacketCreator.movePlayer(p.getId(), mov), p.isHidden(), p.getId(), c.getPlayer().getId()); // XXX check
            
            f.updatePlayerView(p);
        }
    }
}
