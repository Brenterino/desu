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
package field;

import wz.WzObject;
import wz.common.WzDataTool;

/**
 * 
 * @author Brent
 */
public class Foothold implements Comparable<Foothold> {
    
    public int x1;
    public int x2;
    public int y1;
    public int y2;
    public int next;
    public int prev;
    public int holder;
    
    public Foothold(WzObject data) {
        holder = Integer.parseInt(data.getName());
        x1 = WzDataTool.getInteger(data, "x1", 0);
        x2 = WzDataTool.getInteger(data, "x2", 0);
        y1 = WzDataTool.getInteger(data, "y1", 0);
        y2 = WzDataTool.getInteger(data, "y2", 0);
        next = WzDataTool.getInteger(data, "next", 0);
        prev = WzDataTool.getInteger(data, "prev", 0);
    }
    
    public boolean hasTerminatingEnd() {
        return next == 0 || prev == 0;
    }    
    
    public boolean isWall() {
        return x1 == x2;
    }
    
    public int getY(int x) {
        return y1 + x * (y2 - y1) / (x2 - x1);
    }
        
    @Override
    public int compareTo(Foothold o) {
        if (y2 < o.y1) {
            return -1;
        } else if (y1 > o.y2) {
            return 1;
        } else {
            return 0;
        }
    }
    
    // XXX do calculuserino kappa
}
