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
import net.PacketReader;
import netty.PacketHandler;

/**
 *
 * @author Brent
 */
public class CreateCharacterHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedIn() && c.isPinVerified() && !c.isBanned() && c.getCharacterSlots() > c.getPlayersInWorld(-1, false).size();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        String name = r.readMapleString();
        if (Player.checkName(name, c.getWorld())) {
            int face = r.readInteger();
            int hair = r.readInteger();
            int hairColor = r.readInteger();
            int skinColor = r.readInteger();
            int top = r.readInteger();
            int bottom = r.readInteger();
            int shoes = r.readInteger();
            int weapon = r.readInteger();
            int gender = r.read();

            r.skip(4); // stats, idrc B^)
//            int str = r.read();
//            int dex = r.read();
//            int _int = r.read();
//            int luk = r.read();

            Player p = new Player(name);
            p.setFace(face);
            p.setHairstyle(hair + hairColor);
            p.setSkinColor((byte) skinColor);
            p.setGender((byte) gender);
            p.setStrength((short) 12);
            p.setDexterity((short) 5);
            p.setIntelligence((short) 4);
            p.setLuck((short) 4);

            p.getEquipment().put((byte) 5, top);
            p.getEquipment().put((byte) 6, bottom);
            p.getEquipment().put((byte) 7, shoes);
            p.getEquipment().put((byte) 11, weapon);

            if (checkCharacter(p)) {
                p.save(c.getAccountId());
                c.addPlayer(p);
                c.write(PacketCreator.getCharacterCreationResponse(p, true));
            } else {
                c.write(PacketCreator.getCharacterCreationResponse(null, false));
            }
        } else {
            c.write(PacketCreator.getCharacterCreationResponse(null, false));
        }
    }

    private static int[] EQUIPMENT = {
        1302000, 1312004, 1322005, // weapons
        1040002, 1040006, 1040010, 1041002, 1041006, 1041010, 1041011, // bottom
        1060002, 1060006, 1061002, 1061008, // top
        1072001, 1072005, 1072037, 1072038, // shoes
    };

    private static boolean checkCharacter(Player p) { // can modify later if desired
        for (Integer id : p.getEquipment().values()) {
            boolean found = false;
            for (int i = 0; i < EQUIPMENT.length; i++) {
                found = EQUIPMENT[i] == id;
                if (found) {
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return !(p.getStrength() + p.getDexterity() + p.getIntelligence() + p.getLuck() != 25
                || p.getStrength() < 4 || p.getDexterity() < 4 || p.getIntelligence() < 4 || p.getLuck() < 4);
    }
}
