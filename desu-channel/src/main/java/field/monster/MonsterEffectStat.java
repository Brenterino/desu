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

import util.IntegerValue;

/**
 * 
 * @author Brent
 */
public enum MonsterEffectStat implements IntegerValue {

    WEAPON_ATTACK(0x00000001),
    WEAPON_DEFENSE(0x00000002),
    MAGIC_ATTACK(0x00000004),
    MAGIC_DEFENSE(0x00000008),
    ACCURACY(0x00000010),
    AVOIDABILITY(0x00000020),
    SPEED(0x00000040),
    WEAPON_ATTACK_UP(0x00001000),
    WEAPON_DEFENSE_UP(0x00002000),
    MAGIC_ATTACK_UP(0x00004000),
    MAGIC_DEFENSE_UP(0x00008000),
    WEAPON_IMMUNITY(0x00040000),
    MAGIC_IMMUNITY(0x00080000),
    STUN(0x00000080, true),
    FREEZE(0x00000100, true),
    POISON(0x00000200, true),
    SEAL(0x00000400, true),
    SHOWDOWN(0x00000800, true),
    DOOM(0x00010000, true),
    SHADOW_WEB(0x00020000, true),
    AMBUSH(0x00400000, true), // should be fine
    CONFUSED(0x10000000, true); // XXX check this

    private int value;
    private boolean debuff;

    private MonsterEffectStat(int val) {
        this(val, false);
    }

    private MonsterEffectStat(int val, boolean deb) {
        value = val;
        debuff = deb;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int val) {
        this.value = val;
    }

    public boolean isDebuff() {
        return debuff;
    }

    public static MonsterEffectStat get(int value) {
        for (MonsterEffectStat s : values()) {
            if (s.value == value) {
                return s;
            }
        }
        return null;
    }
}
