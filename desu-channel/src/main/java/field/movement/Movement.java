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
package field.movement;

import java.awt.Point;
import net.PacketWriter;

/**
 * 
 * @author Brent
 */
public abstract class Movement {
    
    private int type;
    private Point pos;
    private int duration;
    private int newState;
    
    private Movement() {
    }
    
    protected Movement(int t, Point xy, int dur, int ns) {
        type = t;
        pos = xy;
        duration = dur;
        newState = ns;
    }
    
    public int getType() {
        return type;
    }
    
    public int getNewState() {
        return newState;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public Point getPosition() {
        return pos;
    }
    
    public abstract void serialize(PacketWriter pw);

    public abstract boolean isLifeMovement();
}
