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
public class Shop extends Item implements FieldObject {

    private boolean isHiredMerchant = false;
    
    private int oid;
    private String liveDescription;
    private int livePlayerCount;
    
    private boolean privacy;
    private String password;
    
    // XXX needs to have items as well
    
    public Shop(int uid, int id, String tag, byte slot, long expiration) {
        super(uid, id, tag, Inventory.Type.CASH.getType(), 1, 1, slot, (byte) 0, expiration, true);
        isHiredMerchant = (id / 10000) == 503; // XXX update this later if not the case later on
    }
    
    @Override
    public void save(int cid, Connection c) {
        // XXX do dis
    }

    @Override
    public Type getObjectType() {
        return isHiredMerchant ? Type.HIRED_MERCHANT : Type.SHOP;
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
        return -1;
    }

    @Override
    public void setStance(int s) {
    }

    @Override
    public Point getPosition() {
        return null;
    }

    @Override
    public void setPosition(Point p) {
    }

    @Override
    public int getFh() {
        return -1;
    }

    @Override
    public void sendSpawnData(Client mc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendDestroyData(Client mc, boolean spc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getDescription() {
        return liveDescription;
    }

    public boolean hasAvailableSlot() {
        return livePlayerCount < 3;
    }

    public int getVisitorCount() {
        return livePlayerCount;
    }

    public boolean isPrivate() {
        return privacy;
    }
    
    public String getPassword() {
        return password;
    }
}
