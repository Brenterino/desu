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

import player.Player;
import net.PacketWriter;
import util.IntegerValue;

/**
 *
 * @author Brent
 */
public enum Stat implements IntegerValue, Comparable<Stat> {

    SKIN(0x01),
    FACE(0x02),
    HAIR(0x04),
    PET0(0x08),
    LEVEL(0x10),
    JOB(0x20),
    STR(0x40),
    DEX(0x80),
    INT(0x100),
    LUK(0x200),
    HP(0x400),
    MAXHP(0x800),
    MP(0x1000),
    MAXMP(0x2000),
    AP(0x4000),
    SP(0x8000),
    EXP(0x10000),
    FAME(0x20000),
    MESOS(0x40000),
    PET1(0x80000),
    PET2(0x100000);
    private int value;

    private Stat(int val) {
        value = val;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int val) {
        this.value = val;
    }
    
    public void serialize(PacketWriter pw, Player p) {
        serialize(pw, p.getStat(this));
    }

    public void serialize(PacketWriter pw, Integer i) {
        switch (this) {
            case LEVEL: // 0x10
                pw.write(i);
                break;
            case SKIN: // 0x01
            case JOB:  // 0x20 
            case STR:  // 0x40
            case DEX:  // 0x80
            case INT:  // 0x100
            case LUK:  // 0x200
            case HP:   // 0x400
            case MAXHP:// 0x800
            case MP:   // 0x1000
            case MAXMP:// 0x2000
            case AP: // 0x4000
            case SP: // 0x8000
                pw.writeShort(i);
                break;
            case FACE: // 0x02
            case HAIR: // 0x04
            case PET0: // 0x08
            case EXP:   // 0x10000
            case FAME:  // 0x20000
            case MESOS: // 0x40000
            case PET1:  // 0x80000
            case PET2:  // 0x100000
                pw.writeInteger(i);
                break;
        }
    }

    public static Stat get(int value) {
        for (Stat s : values()) {
            if (s.value == value) {
                return s;
            }
        }
        return null;
    }
}
