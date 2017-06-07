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
import field.FieldObject;
import field.NPC;
import field.movement.Movement;
import field.movement.MovementParser;
import java.awt.Point;
import java.util.Collection;
import net.PacketReader;
import netty.PacketHandler;
import player.Violation;

/**
 *
 * @author Brent
 */
public class NPCActionHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedin();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        int oid = r.readInteger();
        
        FieldObject npc = c.getPlayer().getField().getFieldObject(oid);
        
        assert npc instanceof NPC : Violation.PACKET_EDITTING;
        
        Point origin = npc.getPosition();
       
        short dialog = r.readShort();
        
        Collection<Movement> mov = null;
        
        if (r.available() > 0) {
            r.skip(4); // original position perhaps?
            
            mov = MovementParser.parse(r);
            
            assert MovementParser.validateMovement(mov, npc) : Violation.PACKET_EDITTING;
            
            MovementParser.updatePosition(mov, npc, 0);
        }
        
        c.getPlayer().getField().broadcast(PacketCreator.getNPCAction(dialog, oid, origin, mov));
    }
}
