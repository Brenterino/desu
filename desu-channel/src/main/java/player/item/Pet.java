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

import client.Client;
import field.FieldObject;
import java.awt.Point;
import java.sql.Connection;

/**
 *
 * @author Brent
 */
public class Pet extends Item implements FieldObject {

    private int level;
    private int experience;
    private int closeness;
    private int fullness;
    
    private int oid;
    private int fh;
    private int stance = 0;
    private Point pos = new Point();
    
    
    public Pet(int uid, int id, String tag, byte slot, long expiration,
            int level, int experience, int closeness, int fullness) {
        super(uid, id, tag, Inventory.Type.CASH.getType(), 1, 1, slot, (byte) 0, expiration, true);
        this.level = level;
        this.experience = experience;
        this.closeness = closeness;
        this.fullness = fullness;
    }

    public int getLevel() {
        return level;
    }
    
    public int getExperience() {
        return experience;
    }

    public int getCloseness() {
        return closeness;
    }

    public int getFullness() {
        return fullness;
    }
    
    @Override
    public boolean isPet() {
        return true;
    }
    
    @Override
    public void save(int cid, Connection c) {
        // XXX do dis
    }

    @Override
    public FieldObject.Type getObjectType() {
        return FieldObject.Type.PET;
    }

    @Override
    public int getOid() {
        return oid;
    }

    @Override
    public void setOid(int oid) {
        this.oid = oid;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void toggleHidden() {
    }

    @Override
    public int getStance() {
        return stance;
    }

    @Override
    public void setStance(int s) {
        stance = s;
    }

    @Override
    public Point getPosition() {
        return pos;
    }

    @Override
    public void setPosition(Point p) {
        pos.x = p.x;
        pos.y = p.y;
    }

    @Override
    public int getFh() {
        return fh;
    }

    @Override
    public void sendSpawnData(Client mc) { // XXX do dis
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendDestroyData(Client mc, boolean spc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
