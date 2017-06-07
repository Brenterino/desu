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
import field.FieldItem;
import field.FieldObject;
import net.PacketReader;
import netty.PacketHandler;
import player.Player;
import player.Violation;
import player.community.Party;

/**
 *
 * @author Brent
 */
public class ItemLootHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedin() && c.getPlayer().isAlive();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        byte mode = r.readByte();
        r.readInteger(); // ?
        r.readInteger(); // ?
        int oid = r.readInteger();

        Player p = c.getPlayer();
        Field f = p.getField();

        FieldObject target = f.getFieldObject(oid);
        if (target != null) {
            if (target instanceof FieldItem) {
                FieldItem item = (FieldItem) target;

                synchronized (item) { // to prevent double looting
                    if (item.isLooted() || item.isExpired() ||
                            (item.isProtected() && 
                             item.getOwner() != p.getDropOwnershipId())) {
                        // part of these checks are technically PE
                        System.out.println("NICE MEME");
                        c.write(PacketCreator.enableActions());
                        return;
                    }
                    assert item.getPosition().distanceSq(p.getPosition()) < 90000 : Violation.PACKET_EDITTING; // 300 pixels minimum

                    if (item.isMeso()) {
                        Party playerParty = p.getParty();
                        if (playerParty != null) {
                            p.getParty().distributeMeso(item.getAmount(), f.getFieldId());
                        } else {
                            p.gainMeso(item.getAmount());
                        }
                    } else {
                        boolean added = false;
                        if (item.getItem() != null) {
                            added = p.gainItem(item.getItem());
                        } else {
                            added = p.gainItem(item.getItemId(), item.getAmount());
                        }
                        if (!added) { // unable to add, don't remove from map
                            // XXX should display inventory full (?)
                            return;
                        }
                        c.write(PacketCreator.showItemGain(item.getItemId(), item.getAmount()));
                    }
                    item.setLooted(); // just as a switch
                    f.removeFieldItem(item, 2, p.getId());
                }
            } else {
                // XXX trying to loot an object that isn't an item
            }
        } else {
            // XXX trying to pick up an item that doesn't exist
        }
    }
}
