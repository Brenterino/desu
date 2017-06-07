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
package player.attack;

import data.skill.Bishop;
import data.skill.Marksman;
import data.skill.Buccaneer;
import data.skill.ILArchMage;
import data.skill.Corsair;
import data.skill.BowMaster;
import data.skill.FPArchMage;
import data.internal.Constants;
import data.external.GameDatabase;
import field.Field;
import field.FieldObject;
import field.monster.Monster;
import java.util.Map.Entry;
import net.PacketReader;
import player.Player;
import player.Violation;
import player.stats.Stat;

/**
 * 
 * @author Brent
 */
public final class AttackParser {

    private AttackParser() {
    }

    public static void apply(AttackData attack, Player p) {
        assert p.isAlive() : Violation.PACKET_EDITTING;
        assert p.hasSkill(attack.skill) : Violation.PACKET_EDITTING;
        // XXX add check for hit mismatch
        if (attack.skill != 0) {
            int hpCon = GameDatabase.getSkill(attack.skill).getHPConsumption(p.getSkillLevel(attack.skill));
            int mpCon = GameDatabase.getSkill(attack.skill).getMPConsumption(p.getSkillLevel(attack.skill));
            // XXX calculate MP consumption for elemental amplification
            p.subtractFromStat(Stat.HP, hpCon);
            p.subtractFromStat(Stat.MP, mpCon);
            p.applyChangedStats();
        }
        Field f = p.getField();
        if (attack.isMesoExplosion()) {
            // XXX handle meso explosion
            return;
        }
        for (Entry<Integer, Integer[]> hit : attack.damage.entrySet()) {
            FieldObject target = f.getFieldObject(hit.getKey());
            if (target != null
                    && target.getObjectType().equals(FieldObject.Type.MONSTER)) {
                Monster mob = (Monster) target;
                if (attack.skill == Bishop.HEAL
                        && !mob.isUndead()) {
                    throw new AssertionError(Violation.PACKET_EDITTING);
                }
                int total = 0;
                for (Integer indi : hit.getValue()) {
                    total += indi;
                }
                if (total > 0 && !attack.isHeavensHammer()) { // HH doesn't show HP change
                    mob.damage(p, total);
                } // damage mob with HH another way I guess
                // XXX check mob aggro
                // XXX pick pocket
            }
        }
    }

    public static AttackData parseAttack(PacketReader pr, boolean range) {
        AttackData ret = new AttackData();
        pr.skip(1);
        ret.attackInfo = pr.readByte();
        ret.attackCount = 0x0F & (ret.attackInfo >>> 4);
        ret.attackFreq = 0x0F & ret.attackInfo;
        ret.skill = pr.readInteger();
        switch (ret.skill) {
            case Bishop.BIG_BANG:
            case Buccaneer.CORKSCREW_BLOW:
            case Corsair.GRENADE:
            case ILArchMage.BIG_BANG:
            case FPArchMage.BIG_BANG:
                ret.charge = pr.readInteger();
                break;
            default:
                ret.charge = 0;
                break;
        }
        ret.unk = pr.readByte();
        ret.stance = pr.readByte();
        if (ret.isMesoExplosion()) {
            return parseMesoExplosion(pr, ret);
        }
        pr.skip(1);
        ret.speed = pr.readByte();
        pr.skip(1);
        if (range) {
            ret.direction = pr.readByte();
            pr.skip(7);
        } else {
            pr.skip(3);
        }
        switch (ret.skill) {
            case BowMaster.HURRICANE:
            case Corsair.GRENADE:
            case Corsair.RAPID_FIRE:
            case Marksman.PIERCING_ARROW:
                pr.skip(4);
                break;
            default:
                break;
        }
        for (int x = 0; x < ret.attackCount; x++) {
            int oid = pr.readInteger();
            pr.skip(14); // XXX position info, use for PE check later
            Integer[] damage = new Integer[ret.attackFreq];
            for (int y = 0; y < ret.attackFreq; y++) {
                if (ret.skill == Marksman.SNIPE) { // XXX snipe eventually depends on target
                    damage[y] = Constants.DAMAGE_CAP;
                    continue;
                }
                damage[y] = pr.readInteger();
            }
            if (ret.skill != Corsair.RAPID_FIRE) {
                pr.skip(4);
            }
            ret.damage.put(oid, damage);
        }
        return ret;
    }

    private static AttackData parseMesoExplosion(PacketReader pr, AttackData ret) {
        ret.speed = 4;
        if (ret.attackInfo == 0) {
            pr.skip(10);
            int hits = pr.readByte();
            for (int x = 0; x < hits; x++) {
                int oid = pr.readInteger();
                pr.skip(1);
                ret.damage.put(oid, null);
            }
            return ret;
        } else {
            pr.skip(6);
        }
        for (int x = 0; x < ret.attackCount; x++) {
            int oid = pr.readInteger();
            pr.skip(12);
            int count = pr.read();
            Integer[] iD = new Integer[count];
            for (int y = 0; y < count; y++) {
                iD[y] = pr.readInteger();
            }
            ret.damage.put(oid, iD);
            pr.skip(4);
        }
        int count = pr.read();
        for (int x = 0; x < count; x++) {
            ret.damage.put(pr.readInteger(), null);
            pr.skip(1);
        }
        return ret;
    }
}
