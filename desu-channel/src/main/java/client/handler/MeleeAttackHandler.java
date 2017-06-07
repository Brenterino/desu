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
import data.skill.Buccaneer;
import data.skill.DarkKnight;
import data.skill.Hero;
import data.skill.Paladin;
import java.util.Random;
import net.PacketReader;
import netty.PacketHandler;
import player.Player;
import player.Violation;
import player.attack.AttackData;
import player.attack.AttackParser;
import player.stats.EffectStat;
import player.stats.Stat;

/**
 *
 * @author Brent
 */
public class MeleeAttackHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedin() && c.getPlayer().isAlive();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        AttackData data = AttackParser.parseAttack(r, false);
        Player p = c.getPlayer();
        
        assert p.hasSkill(data.skill) : Violation.PACKET_EDITTING;      
        assert p.isOffCooldown(data.skill) : Violation.PACKET_EDITTING;
        
        p.getField().broadcast(PacketCreator.showAttack(p.getId(), data), p.getId());
    
        if (data.skill >= Hero.PANIC_SWORD && data.skill <= Hero.COMA_AXE) {
            assert p.hasAppliedStatEffect(EffectStat.COMBO) : Violation.PACKET_EDITTING;
            
            p.consumeComboOrbs(data.skill);
        } else if (data.attackCount > 0) {
            if (p.hasAppliedStatEffect(EffectStat.COMBO)) { // apparently shout does not give combo? *check this*
                p.gainComboOrbs(data.attackCount);
            }
            
            if (p.hasSkill(Buccaneer.ENERGY_CHARGE)) {
                p.gainEnergyCharge(data.attackCount);
            }
        }
        
        if (data.attackCount > 0 && data.skill == DarkKnight.SACRIFICE) {
            int damageDealt = data.damage.get(0)[0];
            
            SkillData sacrificeInfo = GameDatabase.getSkill(DarkKnight.SACRIFICE);
            
            int propX = sacrificeInfo.getProperty(p.getSkillLevel(DarkKnight.SACRIFICE), "x");
            
            int hpSacrifice = (damageDealt * propX) / 100;
            
            int remHealth = Math.max(1, p.getStat(Stat.HP) - hpSacrifice);
            
            p.changeStat(Stat.HP, remHealth);
            
            p.applyChangedStats();
        }
        
        if (data.attackCount > 0 && data.skill == Paladin.CHARGED_BLOW) {
            if (p.hasSkill(Paladin.ADVANCED_CHARGE)) {
                SkillData advancedChargeInfo = GameDatabase.getSkill(Paladin.ADVANCED_CHARGE);
                
                int chance = advancedChargeInfo.getProperty(p.getSkillLevel(Paladin.ADVANCED_CHARGE), "x");
                
                boolean retainCharge = chance >= new Random().nextInt(100);
                
                if (!retainCharge) {
                    p.cancelAppliedStatEffect(EffectStat.CHARGE);
                }
            } else {
                p.cancelAppliedStatEffect(EffectStat.CHARGE);
            }
        }
        
        AttackParser.apply(data, p); // check order
        
        // XXX anti-hack segment can be made here to check damage
    }
}
