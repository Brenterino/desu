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

import util.IntegerValue;

/**
 *
 * @author Brent
 */
public enum EffectStat implements IntegerValue {

    BEHOLDER(0x00020000, 2), // v83?
    ENERGY_CHARGE(0x00040000, 2),
    WINGS(0x00040000, 2),
    DASH_SPEED(0x00080000, 2),
    DASH_JUMP(0x00100000, 2),
    MONSTER_RIDING(0x00200000, 2),
    SPEED_INFUSION(0x00400000, 2),
    HOMING_BEACON(0x00800000, 2),
    MORPH(0x00000002, 3),
    RECOVERY(0x00000004, 3),
    MAPLE_WARRIOR(0x00000008, 3),
    STANCE(0x00000010, 3),
    SHARP_EYES(0x00000020, 3),
    MANA_REFLECTION(0x00000040, 3),
    SHADOW_CLAW(0x00000100, 3),
    INFINITY(0x00000200, 3),
    HOLY_SHIELD(0x00000400, 3),
    HAMSTRING(0x00000800, 3),
    BLIND(0x00001000, 3),
    CONCENTRATE(0x00002000, 3),
    ECHO_OF_HERO(0x00008000, 3),
    MESO_BONUS(0x00010000, 3),
    GHOST_MORPH(0x00020000, 3),
    BARRIER(0x00040000, 3), // v83?
    ITEM_BONUS(0x00100000, 3), // v83?
    RESPECT_P_IMMUNE(0x00200000, 3), // v83?
    RESPECT_M_IMMUNE(0x00400000, 3), // v83?
    DEFENSE_ATTACK(0x00800000, 3), // v83?
    DEFENSE_STATE(0x01000000, 3), // v83?
    INC_EFFECT_HP_POTION(0x02000000, 3), // v83?
    INC_EFFECT_MP_POTION(0x04000000, 3), // v83?

    WEAPON_ATTACK(0x00000001, 4),
    WEAPON_DEFENSE(0x00000002, 4),
    MAGIC_ATTACK(0x00000004, 4),
    MAGIC_DEFENSE(0x00000008, 4),
    ACCURACY(0x00000010, 4),
    AVOIDABILITY(0x00000020, 4),
    HANDS(0x00000040, 4),
    SPEED(0x00000080, 4),
    JUMP(0x00000100, 4),
    MAGIC_GUARD(0x00000200, 4),
    DARK_SIGHT(0x00000400, 4),
    BOOSTER(0x00000800, 4),
    POWER_GUARD(0x00001000, 4),
    HYPER_BODY_HP(0x00002000, 4),
    HYPER_BODY_MP(0x00004000, 4),
    INVINCIBLE(0x00008000, 4),
    SOUL_ARROW(0x00010000, 4),
    COMBO(0x00200000, 4),
    CHARGE(0x00400000, 4),
    DRAGON_BLOOD(0x00800000, 4),
    HOLY_SYMBOL(0x01000000, 4),
    MESO_UP(0x02000000, 4),
    SHADOW_PARTNER(0x04000000, 4),
    PICK_POCKET(0x08000000, 4),
    MESO_GUARD(0x10000000, 4),
    // Debuffs

    SLOW(0x00000001, 3, true),
    SEDUCE(0x00000080, 3, true),
    BANISH(0x00004000, 3, true), // v83 (?)
    INVERSE_CONTROLS(0x00080000, 3, true),
    STUN(0x00020000, 4, true),
    POISON(0x00040000, 4, true),
    SEAL(0x00080000, 4, true),
    DARKNESS(0x00100000, 4, true),
    THAW(0x20000000, 4, true),
    WEAKEN(0x40000000, 4, true),
    CURSE(0x80000000, 4, true);

    private int value;
    private int position;
    private boolean debuff;

    private EffectStat(int val, int pos) {
        this(val, pos, false);
    }

    private EffectStat(int val, int pos, boolean deb) {
        value = val;
        position = pos;
        debuff = deb;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int val) {
        value = val;
    }

    public boolean isDebuff() {
        return debuff;
    }
}
