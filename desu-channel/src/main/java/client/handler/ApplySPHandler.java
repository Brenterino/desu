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
import data.skill.Beginner;
import net.PacketReader;
import netty.PacketHandler;
import player.Player;
import player.Violation;
import player.stats.Job;
import player.stats.Stat;

/**
 *
 * @author Brent
 */
public class ApplySPHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedin() && c.getPlayer().isAlive();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        Player p = c.getPlayer();

        r.readInteger();
        int id = r.readInteger();

        assert Job.getById(p.getStat(Stat.JOB)).canHaveSkill(id) : Violation.PACKET_EDITTING;
        assert p.getStat(Stat.SP) > 0 || id / 1000000 == 0 : Violation.PACKET_EDITTING;

        boolean canApplyTo = true;
        boolean isBeginnerSkill = false;

        switch (id) {
            case Beginner.THREE_SNAILS:
            case Beginner.RECOVERY:
            case Beginner.NIMBLE_FEET:
                isBeginnerSkill = true;
                break;
            case Beginner.FOLLOW_THE_LEAD:
            case Beginner.LEGENDARY_SPIRIT:
            case Beginner.MONSTER_RIDER:
            case Beginner.ECHO_OF_HERO:
            case Beginner.JUMP_DOWN:
                canApplyTo = false;
                break;
            default:
                break;
        }

        if (isBeginnerSkill) {
            p.applyBeginnerSP(id);
        } else if (canApplyTo) {
            p.applySP(id);
        }

        c.write(PacketCreator.enableActions());
    }
}
