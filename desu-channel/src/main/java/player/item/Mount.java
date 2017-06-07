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
package player.item;

import java.sql.Connection;

/**
 *
 * @author Brent
 */
public class Mount extends Equip {

    private int fatigue;
    private int mountLevel;
    private int mountEXP;
    
    public Mount(int uid, int id, String name, byte slot, 
            byte slots, byte upgrades, byte flag, short str, short dex, 
            short _int, short luk, short hp, short mp, short attack, 
            short magic, short wdef, short mdef, short acc, short avoid, 
            short speed, short jump, short hands, long locked,
            long expiration, int fatigue, int mountLevel, int mountEXP) {
        super(uid, id, name, 1, slot, slots, upgrades, flag, 
              str, dex, _int, luk, hp, mp, attack, magic, wdef, 
              mdef, acc, avoid, speed, jump, hands, locked, 0, 
              0, 0, 0, 0, expiration, 
              false);
        this.fatigue = fatigue;
        this.mountLevel = mountLevel;
        this.mountEXP = mountEXP;
    }
    
    // XXX coderino
    
    @Override
    public void save(int cid, Connection c) {
        // XXX do dis
    }

    public int getMountLevel() {
        return mountLevel;
    }

    public int getMountExp() {
        return mountEXP;
    }

    public int getFatigue() {
        return fatigue;
    }
}
