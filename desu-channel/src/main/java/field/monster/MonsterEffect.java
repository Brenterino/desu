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
package field.monster;

import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author Brent
 */
public class MonsterEffect {
    
    private int skill;
    private int duration;
    private final EnumMap<MonsterEffectStat, Integer> statups;

    public MonsterEffect(int skill) {
        statups = new EnumMap<>(MonsterEffectStat.class);
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

    public int getStatupValue(MonsterEffectStat s) {
        return getStatupValue(s, 0);
    }

    public int getStatupValue(MonsterEffectStat s, int def) {
        if (!statups.containsKey(s)) {
            return def;
        }
        return statups.get(s);
    }

    public void addStatup(MonsterEffectStat s, int val) {
        if (!statups.containsKey(s)) { // this may change so we can stack
            statups.put(s, val);
        }
    }

    public int removeStatup(MonsterEffectStat s) {
        if (statups.containsKey(s)) {
            return statups.remove(s);
        }
        return 0;
    }

    public void subtractStatup(MonsterEffectStat s, int val) {
        if (statups.containsKey(s)) {
            int oVal = statups.remove(s);
            int nVal = val - oVal;
            if (nVal > 0) {
                statups.put(s, nVal);
            }
        }
    }

    public void sumStatup(MonsterEffectStat s, int val) {
        if (statups.containsKey(s)) {
            int oVal = statups.remove(s);
            int nVal = val + oVal;
            if (nVal > 0) {
                statups.put(s, nVal);
            }
        }
    }

    public void multiplyStatup(MonsterEffectStat s, int val) {
        if (statups.containsKey(s)) {
            int oVal = statups.remove(s);
            int nVal = val * oVal;
            if (nVal > 0) {
                statups.put(s, nVal);
            }
        }
    }

    public void divideStatup(MonsterEffectStat s, int val) {
        if (statups.containsKey(s)) {
            int oVal = statups.remove(s);
            int nVal = val / oVal;
            if (nVal > 0) {
                statups.put(s, nVal);
            }
        }
    }

    public Map<MonsterEffectStat, Integer> getStatups() {
        return statups;
    }

    public void clear() {
        statups.clear();
    }

    public void apply(Monster m) {
        // XXX handle buff
    }

    public void apply(Monster m, boolean foreign) {
        // XXX handle foreign buffs
    }
}
