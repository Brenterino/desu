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
package data.internal;

/**
 *
 * @author Brent
 */
public enum Slots {

    CAP(100, -1),
    FACE_ACCESSORY(101, -2),
    EYE_ACCESSORY(102, -3),
    EARRINGS(103, -4),
    TOP(104, -5),
    OVERCOAT(105, -5),
    PANTS(106, -6),
    SHOES(107, -7),
    GLOVES(108, -8),
    SHIELD(109, -10),
    CAPE(110, -9),
    RING(111, -12, -13, -15, -16),
    PENDANT(112, -17),
    
    ONE_HANDED_SWORD(130, -11),
    ONE_HANDED_AXE(131, -11),
    ONE_HANDED_BLUNT_WEAPON(132, -11),
    DAGGER(133, -11),
    WAND(137, -11),
    STAFF(138, -11),
    
    FISTS(139, -11), // placeholder item for having no "knuckler"
    TWO_HANDED_SWORD(140, -11),
    TWO_HANDED_AXE(141, -11),
    TWO_HANDED_BLUNT_WEAPON(142, -11),
    SPEAR(143, -11),
    POLEARM(144, -11),
    BOW(145, -11),
    CROSSBOW(146, -11),
    CLAW(147, -11),
    KNUCKLER(148, -11),
    GUN(149, -11),
    
    TAMING_MOB(190, -18),
    SADDLE(191, -19),
    SPECIAL_TAMING_MOB(193, -18),
    
    CASH_ITEM;

    private int prefix;
    private int[] allowed;

    private Slots() {
        prefix = 0;
    }

    private Slots(int pre, int... in) {
        prefix = pre;
        allowed = in;
    }

    public int getPrefix() {
        return prefix;
    }
    
    public boolean isTwoHanded() {
        return prefix >= 139 && prefix <= 149;
    }

    public boolean isAllowed(int slot, boolean cash) {
        if (allowed != null) {
            for (Integer allow : allowed) {
                int condition = cash ? allow - 100 : allow;
                if (slot == condition) {
                    return true;
                }
            }
        }
        return cash;
    }

    public static Slots getFromItemId(int id) {
        int prefix = id / 10000;
        if (prefix != 0) {
            for (Slots c : values()) {
                if (c.getPrefix() == prefix) {
                    return c;
                }
            }
        }
        return CASH_ITEM;
    }
}
