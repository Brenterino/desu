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

import field.monster.Monster;
import data.external.GameDatabase;
import java.awt.Point;
import wz.WzObject;
import wz.common.WzDataTool;

/**
 *
 * @author Brent
 */
public final class Spawner {

    private int cy;
    private int f;
    private int fh;
    private int id;
    private int mobTime;
    private int rx0;
    private int rx1;
    private Point p;
    private long lastSpawn;
    
    public Spawner(WzObject data) {
        cy = WzDataTool.getInteger(data, "cy", -1);
        f = WzDataTool.getInteger(data, "f", -1);
        fh = WzDataTool.getInteger(data, "fh", -1);
        id = WzDataTool.getIntegerConvert(data, "id", -1);
        mobTime = WzDataTool.getInteger(data, "mobTime", 0);
        rx0 = WzDataTool.getInteger(data, "rx0", -1);
        rx1 = WzDataTool.getInteger(data, "rx1", -1);
        p = new Point(WzDataTool.getInteger(data, "x", -1),
                      WzDataTool.getInteger(data, "y", -1));
    }
    
    public void respawn(Field m, boolean force) {
        if (shouldSpawn() || force) {
            Monster mob = GameDatabase.getMonster(id, m);
            mob.setPosition(m.calcPositionBelow(p)); // XXX get position of foothold lol
            mob.setFh(fh);
            m.spawnMonster(mob);
            lastSpawn = System.currentTimeMillis();
        }
    }
    
    // XXX code for bosses that spawn B^)
    public boolean shouldSpawn() {
        return mobTime != -1;
    }
    
    public Monster getMonster() {
        return GameDatabase.getMonster(id, null);
    }
    
    public int getCy() {
        return cy;
    }
    
    public int getF() {
        return f;
    }
    
    public int getFh() {
        return fh;
    }
    
    public int getMobId() {
        return id;
    }
    
    public int getMobTime() {
        return mobTime;
    }
    
    public int getRx0() {
        return rx0;
    }
    
    public int getRx1() {
        return rx1;
    }
    
    public Point getPosition() {
        return p;
    }

    public void setPosition(Point p) {
        this.p = p;
    }
}
