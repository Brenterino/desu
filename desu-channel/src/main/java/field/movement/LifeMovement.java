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
public final class LifeMovement extends Movement {

    private int unk;
    private Point pps;
    
    private LifeMovement() {
        super(-1, null, -1, -1);
    }
    
    public LifeMovement(int t, Point p, int dur, int ns, Point pix, int uk) {
        super(t, p, dur, ns);
        pps = pix;
        unk = uk;
    }
    
    public Point getPixelsPerSecond() {
        return pps;
    }
    
    public int getUnk() {
        return unk;
    }
    
    @Override
    public void serialize(PacketWriter pw) {
        pw.write(getType());
        pw.writeShort(getPosition().x);
        pw.writeShort(getPosition().y);
        pw.writeShort(pps.x);
        pw.writeShort(pps.y);
        pw.writeShort(unk);
        pw.write(getNewState());
        pw.writeShort(getDuration());
    }

    @Override
    public boolean isLifeMovement() {
        return true;
    }
}
