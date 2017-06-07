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
package player.community;

import java.util.function.Supplier;
import java.util.stream.Stream;
import player.Player;
import player.stats.Stat;

/**
 *
 * @author Brent
 */
public class Party {
    
    // XXX party players
    // XXX code parties
    
    public int getId() {
        return -1;
    }
    
    // want to make this as close as possible to O(K) where K = players 
    public Stream<Player> getPlayers(int field) {
        return null;
    }
    
    public int getPartyBonus(int field) {
        int count = (int) (getPlayers(field).count() - 1); // remove 1 from this
        // some maps later on have an additional "party play" bonus
        return (count > 0 ? (110 + (5 * (count - 1))) : 0);
    }
    
    // XXX come back to healing later since it hits undead mobs + party members
    // also may want to use the effective rate as some sort of indication of
    // heal.
    // exp = 20 * (healthAfterHeal - healthBeforeHeal) / (8 * targetLevel + 190)  
    
//    public void applyHeal(int heal, int hs, int field) { 
//        Supplier<Stream<Player>> members = () -> getPlayers(field);
//        
//        int experience = 0;
//        
//        Player src = members.get().filter(p -> p.getId() == hs).findFirst().orElse(null);
//
//        // need range box magic
//        
//        int rangeDist = heal > 150 ? 62500 : 90000; 
//        // hack for range box since it isn't a square
//        // wrong because it will be radial vs. by box.  This increases
//        // Change in Range For Heal:
//        // y: [0, +100] pixels
//        // Bounding by a Rectangle is possible with a bit more code, but for now we can try this
//        
//        // Heal Recovery
//        // MAX = something * Magic * Heal Level * Target Multiplier
//        // MIN = something * Magic * Heal Level * Target Multiplier
//        
//        
//        if (src != null) {
//            Supplier<Stream<Player>> targets = () -> members.get().sequential().filter(p -> p.getId() != hs).
//                filter(p -> p.getPosition().distanceSq(src.getPosition()) < rangeDist);
//            
//            long count = targets.get().count();
//            
//            float multiplier = 1.5f + (5 / (count + 1));
//            
//            // uses total intelligence to calculate heal
//            
//            // multiplier = 1.5 + 5/(number of targets including yourself)
//            
//            // MAX = (1.132682429*10^-7*luk^2 + 3.368846366*10^-6*luk + 1.97124794*10^-3) * magic * int * healSkillLevel * multiplier
//            // MIN = MAX*0.8
//                 
//            
//        }
//        
//        
//    }
    
    public void distributeMeso(int meso, int field) {
        Supplier<Stream<Player>> memberSupply = () -> getPlayers(field);
        final int count = (int) (memberSupply.get().count() & 6); // only split between 6
        memberSupply.get().forEach(p -> p.gainMeso(meso / count));
    }
    
    public void distributeExp(int exp, int field, int killer, boolean boss, int mobLevel, int mobHealth, int mostDamageCid) {
        // only people who get EXP will be on the same channel and field
        Supplier<Stream<Player>> members = () -> getPlayers(field);
        
        final int minLevel = mobLevel - 5;
        
        int leechMinLevel = members.get().mapToInt(p -> p.getStat(Stat.LEVEL)).filter(i -> i >= minLevel).min().orElse(minLevel + 5) - 5;
        
        int partyLevel = members.get().mapToInt(p -> p.getStat(Stat.LEVEL)).filter(l -> l >= leechMinLevel).sum();
        
        // essentially the condition means that leeching without dealing at least 1 damage can only occur if you are 
        // at a minimum below 5 levels of the monster or a party member is within [mobLevel - 5, mobLevel)
        // the theoretical minimum level is (mobLevel - 10): this occurs when a party member is at the minimum base level
        // and causes the actual leech level to decrease by a maximum of 5.
        
        // formula after v80
        // EXP received by the highest damager: TotalEXP * (0.2 + 0.8 * level/party level)
        // EXP received by other party members: TotalEXP * (0.8 * level/party level)
        
        // formula before v80:
        // EXP received when in party = TotalEXP * (0.6 * damage dealt/monster's HP + 0.4 * lvl/party lvl)
//         int xp = (int) (exp * 0.40f * (p.getStat(Stat.LEVEL) / partyLevel)); // 40% from level regardless of damage done
//         if (expDist.containsKey(p.getOid())) {
//             xp += 0.60f * expDist.get(p.getOid()); // 60% from damage dealt (raw expDist)
//         }
//         if (!boss && isKiller) {
//             p.incrementMonsterKills();
//         }
//         p.gainExp(xp, isKiller);
        
        // I'm lazy, this is technically wrong, but I don't care since balance magic
        members.get().filter(p -> p.getStat(Stat.LEVEL) >= leechMinLevel).
                forEach(p -> {
                    boolean isKiller = killer == p.getId();
                    boolean mostDamage = mostDamageCid == p.getId();
                    int xp = (int) (exp * 0.80f * p.getStat(Stat.LEVEL) / partyLevel); // 80% from level
                    if (mostDamage) {
                        xp += (exp * 0.20f);
                    }
                    if (isKiller) {
                        p.incrementMonsterKills();
                    }
                    p.gainExp(xp, isKiller);
                });
    }
}
