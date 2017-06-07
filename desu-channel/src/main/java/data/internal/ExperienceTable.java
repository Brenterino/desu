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
public final class ExperienceTable {
    
    private final static int[] TABLE = new int[201];
    
    private ExperienceTable() {
    }
    
    static { // XXX may need to update this shit lata
        int level = 1;
        for (; level < 6; level++) {
            TABLE[level] = level * (level * level / 2 + 15);
        }
        for (; level < 51; level++) {
            TABLE[level] = level * level / 3 * (level * level / 3 + 19);
        }
        int exp = TABLE[level - 1];
        for (; level < 200; level++) {
            exp = (int) (exp * 1.0548);
            
            TABLE[level] = exp;
        }
        TABLE[200] = 0;
    }
    
    public static int forNextLevel(int curL) {
        if (curL >= 200) { // lol long in the future boys
            return Integer.MAX_VALUE;
        }
        return TABLE[curL];
    }
}
