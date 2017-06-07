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

import field.FieldObject;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import net.PacketReader;

/**
 * 
 * @author Brent
 */
public final class MovementParser {

    public static Collection<Movement> parse(PacketReader pr) {
        ArrayList<Movement> ret = new ArrayList<>();
        int count = pr.read();
        for (int i = 0; i < count; i++) {
            int c = pr.read();
            switch (c) {
                case 0:
                case 5:
                case 17: {
                    short x = pr.readShort();
                    short y = pr.readShort();
                    short xw = pr.readShort();
                    short yw = pr.readShort();
                    short unk = pr.readShort();
                    byte ns = pr.readByte();
                    short dur = pr.readShort();
                    ret.add(new LifeMovement(c, new Point(x, y), dur, ns, new Point(xw, yw), unk));
                    break;
                }
                case 1:
                case 2:
                case 6:
                case 12:
                case 13:
                case 16:
                case 18: 
                case 19: 
                case 20:
                case 22: {
                    short x = pr.readShort();
                    short y = pr.readShort();
                    byte ns = pr.readByte();
                    short dur = pr.readShort();
                    ret.add(new GenericMovement(c, new Point(x, y), dur, ns));
                    break;
                }
                case 3:
                case 4:
                case 7:
                case 8:
                case 9: {
                    short x = pr.readShort();
                    short y = pr.readShort();
                    short xw = pr.readShort();
                    short yw = pr.readShort();
                    byte ns = pr.readByte();
                    ret.add(new TeleportMovement(c, new Point(x, y), ns, new Point(xw, yw)));
                    break;
                }
                case 10:
                    ret.add(new ChangeEquipMovement(pr.readByte()));
                    break;
                case 11: {
                    short x = pr.readShort();
                    short y = pr.readShort();
                    short unk = pr.readShort();
                    byte ns = pr.readByte();
                    short d = pr.readShort();
                    ret.add(new ChairMovement(c, new Point(x, y), d, ns, unk));
                    break;
                }
                case 14: // not exactly sure what this instruction is supposed to be tbh
                         // if handled as teleport, it causes drop to disappear when jump down is used
                    pr.skip(9);
                    break;
                case 15: {
                    short x = pr.readShort();
                    short y = pr.readShort();
                    short xw = pr.readShort();
                    short yw = pr.readShort();
                    short unk = pr.readShort();
                    short fh = pr.readShort();
                    byte ns = pr.readByte();
                    short dur = pr.readShort();
                    ret.add(new JumpDownMovement(c, new Point(x, y), dur, ns, fh, unk, new Point(xw, yw)));
                    break;
                }
                case 21:
                    break;
                default:
                    System.out.printf("[Debug] Unknown movement type found %s%n", c);
                    System.out.println(pr.toString());
                    return null;
            }
        }
        return ret;
    }
    
    public static boolean validateMovement(Collection<Movement> ms, FieldObject src) {
        // XXX anti-hack
        return true;
    }

    public static void updatePosition(Collection<Movement> ms, FieldObject src, int yoff) {
        for (Movement m : ms) {
            if (m.isLifeMovement()) {
                if (m instanceof TeleportMovement || m instanceof LifeMovement) {
                    Point to = m.getPosition();
                    to.y += yoff;
                    src.setPosition(to);
                }
                src.setStance(m.getNewState());
            }
        }
    }
}
