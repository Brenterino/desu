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
package player.stats;

import client.Client;
import client.packet.PacketCreator;
import java.util.EnumMap;
import java.util.Map;
import net.PacketWriter;

/**
 *
 * @author Brent
 */
public final class Effect { // called it an effect because they have visual effects :D
    
    private int skill;
    private int duration;
    private final EnumMap<EffectStat, Integer> statups;

    public Effect(int skill) {
        this.skill = skill;
        statups = new EnumMap<>(EffectStat.class);
    }

    public void setDuration(int dur) {
        duration = dur;
    }

    public int getDuration() {
        return duration;
    }

    public int getSkill() {
        return skill;
    }

    public int getStatupValue(EffectStat s) {
        return getStatupValue(s, 0);
    }

    public int getStatupValue(EffectStat s, int def) {
        if (!statups.containsKey(s)) {
            return def;
        }
        return statups.get(s);
    }

    public void addStatup(EffectStat s, int val) {
        if (!statups.containsKey(s)) { // this may change so we can stack
            statups.put(s, val);
        }
    }

    public int removeStatup(EffectStat s) {
        if (statups.containsKey(s)) {
            return statups.remove(s);
        }
        return 0;
    }

    public void subtractStatup(EffectStat s, int val) {
        if (statups.containsKey(s)) {
            int oVal = statups.remove(s);
            int nVal = val - oVal;
            if (nVal > 0) {
                statups.put(s, nVal);
            }
        }
    }

    public void sumStatup(EffectStat s, int val) {
        if (statups.containsKey(s)) {
            int oVal = statups.remove(s);
            int nVal = val + oVal;
            if (nVal > 0) {
                statups.put(s, nVal);
            }
        }
    }

    public void multiplyStatup(EffectStat s, int val) {
        if (statups.containsKey(s)) {
            int oVal = statups.remove(s);
            int nVal = val * oVal;
            if (nVal > 0) {
                statups.put(s, nVal);
            }
        }
    }

    public void divideStatup(EffectStat s, int val) {
        if (statups.containsKey(s)) {
            int oVal = statups.remove(s);
            int nVal = val / oVal;
            if (nVal > 0) {
                statups.put(s, nVal);
            }
        }
    }

    public Map<EffectStat, Integer> getStatups() {
        return statups;
    }

    public boolean hasStatup(EffectStat s) {
        return statups.containsKey(s);
    }

    public void clear() {
        statups.clear();
    }
    
    public void serialize(PacketWriter pw) {
        int[] buffer = new int[4];
        
        for (EffectStat stat : statups.keySet()) {
            buffer[stat.getPosition() - 1] |= stat.getValue();
        }
        
        for (int i = 0; i < buffer.length; i++) {
            pw.writeInteger(buffer[i]);
        }
    }

    public void apply(Client c) {
        apply(c, false);
    }

    public void apply(Client c, boolean foreign) {
        if (statups.size() > 0) {
            if (foreign) {
                // XXX handle foreign buffs
            } else {
                c.write(PacketCreator.applyTemporaryStati(this));
                
                c.getPlayer().giveStatusEffect(this);
            }
        }
    }
}
