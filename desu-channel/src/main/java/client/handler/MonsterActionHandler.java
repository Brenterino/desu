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
import data.SkillData;
import data.skill.MobSkill;
import field.FieldObject;
import field.monster.Monster;
import field.movement.Movement;
import field.movement.MovementParser;
import java.util.Collection;
import net.PacketReader;
import netty.PacketHandler;
import player.Violation;

/**
 *
 * @author Brent
 */
public class MonsterActionHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedin();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        int oid = r.readInteger();
        int move = r.readShort();
        
        boolean useSkill = r.readBool();
        int skill = r.read();
        int skill1 = r.read();
        int skill2 = r.read();
        int skill3 = r.read();
                
        r.skip(6);
        
        int x = r.readShort();
        int y = r.readShort();
        
        FieldObject obj = c.getPlayer().getField().getFieldObject(oid);
        
        // XXX this object may no longer exist because of lag
        // we can try to remove it (?)
        if (obj == null) {
            return;
        }
        
        assert obj.getObjectType().equals(FieldObject.Type.MONSTER) : Violation.PACKET_EDITTING;
        
        Monster m = (Monster) obj;
        
        if (m.getController() != c.getPlayer().getOid()) {
            return;
        }
        
        Monster.Skill toUseFuture = null;
        if (useSkill && m.hasSkills()) {
            toUseFuture = m.getRandomSkill();
            SkillData toUseInfo = GameDatabase.getMonsterSkill(toUseFuture.id);
            if (toUseInfo == null || !m.canUseSkill(toUseFuture.id, toUseFuture.level, toUseInfo.getProperty(toUseFuture.level, "hp"), toUseInfo.getMPConsumption(toUseFuture.level))) {
                toUseFuture = null;
            }
        }
        
        if (skill1 >= MobSkill.WEAPON_ATTACK && skill1 <= MobSkill.SUMMON &&
                m.hasSkill(skill1, skill2)) {
            SkillData use = GameDatabase.getMonsterSkill(skill1);
            if (use != null && m.canUseSkill(skill1, skill2, use.getProperty(skill2, "hp"), use.getMPConsumption(skill2))) {
                // XXX apply the skill effect & cooldown, etc.
            }
        }
        
        Collection<Movement> mov = MovementParser.parse(r);
        
        assert MovementParser.validateMovement(mov, obj) : Violation.PACKET_EDITTING;
        
        assert m.getPosition().x == x : Violation.PACKET_EDITTING;
        assert m.getPosition().y == y : Violation.PACKET_EDITTING;
        
        boolean isAggro = m.isAggressive();
        
        if (toUseFuture != null) {
            c.write(PacketCreator.sendMonsterActionResponse(oid, move, m.getMP(), toUseFuture.id, toUseFuture.level, isAggro));
        } else {
            c.write(PacketCreator.sendMonsterActionResponse(oid, move, m.getMP(), isAggro));
        }
        
        if (mov != null) {
            assert r.available() == 9;           
        
            c.getPlayer().getField().broadcast(PacketCreator.moveMonster(useSkill, skill, skill1, skill2, skill3, oid, m.getPosition(), mov), c.getPlayer().getId()); // , c.getPlayer().getId()
            
            MovementParser.updatePosition(mov, obj, 0);
        }
        
        // XXX check to see if this is ok
    }
}
