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
import data.external.GameDatabase;
import field.Field;
import field.FieldManager;
import field.Portal;
import net.PacketReader;
import netty.PacketHandler;
import player.Player;
import player.Violation;
import data.internal.ExperienceTable;
import player.stats.Stat;

/**
 *
 * @author Brent
 */
public class FieldChatHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedin();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        short len = r.readShort();

        assert len <= 70 : Violation.PACKET_EDITTING;

        String msg = r.readString(len);

        char head = msg.charAt(0); // for commands in the future
        // may not implement these just because
        // commands are also client-sided

        Player p = c.getPlayer();

        if (head == '!') { /// XXX remove later
            String rem = msg.substring(1);
            String[] cmd = rem.split(" ");
            if (cmd[0].equalsIgnoreCase("m")) {
                try {
                    int id = Integer.parseInt(cmd[1]);
                    Field to = FieldManager.getField(id);
                    int target = 0;
                    if (cmd.length > 2) {
                        target = Integer.parseInt(cmd[2]);
                    }
                    Portal toP = to.getPortal(target);
                    assert to != null;
                    p.changeField(to, toP);
                    
                    return;
                } catch (Exception e) {
                    c.write(PacketCreator.enableActions());
                    return;
                }
            } else if (cmd[0].equalsIgnoreCase("b")) {
                try {
                    int bId = Integer.parseInt(cmd[1]);
                    int level = Integer.parseInt(cmd[2]);

                    GameDatabase.getSkill(bId).generatePlayerBuff(level).apply(c);

                    c.write(PacketCreator.enableActions());
                    
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    c.write(PacketCreator.enableActions());
                    return;
                }
            } else if (cmd[0].equalsIgnoreCase("j")) {
                try {
                    int tj = Integer.parseInt(cmd[1]);

                    p.changeJob(tj); // to test B^)
                    
//                    c.write(PacketCreator.enableActions());
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    c.write(PacketCreator.enableActions());
                    return;
                }
            } else if (cmd[0].equalsIgnoreCase("d")) {
                c.write(PacketCreator.enableActions());
                return;
            } else if (cmd[0].equalsIgnoreCase("p")) {
                msg = "X: " + p.getPosition().x + " Y: " + p.getPosition().y + " Fh: " + p.getFh();
            } else if (cmd[0].equalsIgnoreCase("l")) {
                int exp = ExperienceTable.forNextLevel(p.getStat(Stat.LEVEL)) - 1;
                p.changeStat(Stat.EXP, exp);
                p.applyChangedStats();
                return;
            } else if (cmd[0].equalsIgnoreCase("t")) {
                try {
                    int id = Integer.parseInt(cmd[1]);
                    
                    c.write(PacketCreator.testEffect(id));
                } catch (Exception e) {
                    e.printStackTrace();
                    c.write(PacketCreator.enableActions());
                }
                return;
            }
        }

        p.getField().broadcast(PacketCreator.getFieldChat(p.getId(), msg, p.isGM(), r.read()), p.isHidden());
    }
}
