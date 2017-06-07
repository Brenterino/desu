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
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Brent
 */
public class Item implements Comparable<Item> {

    private int uid;
    private int id;
    private int type;
    private int quantity;
    private int mquantity;
    private byte slot;
    private byte flag;
    private boolean cash;
    private long expiration;
    private String name = "";

    private Item() {
    }

    public Item(int uid, int id, String name, int type, int quantity, int mquantity, byte slot, byte flag, long expiration, boolean cash) {
        this.uid = uid;
        this.id = id;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.mquantity = mquantity;
        this.slot = slot;
        this.flag = flag;
        this.expiration = expiration;
        this.cash = cash;
    }

    public final int getItemId() {
        return id;
    }

    public final int getInventoryType() {
        return type;
    }

    public final String getName() {
        return name == null ? "" : name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final int getQuantity() {
        return quantity;
    }

    public final void addQuantity(int num) {
        quantity += num;
    }

    public final void removeQuantity(int num) {
        quantity -= num;
    }

    public final void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public final int getMaximumQuantity() {
        return mquantity;
    }

    public final byte getSlot() {
        return slot;
    }

    public final void setSlot(byte slot) {
        this.slot = slot;
    }

    public final int getType() {
        return type;
    }

    public final byte getFlag() {
        return flag;
    }

    public final long getExpiration() {
        return expiration;
    }

    public final void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public final boolean isCash() {
        return cash;
    }

    public final boolean isMergeable() {
        return type != Inventory.Type.EQUIP.getType()
                && !isNonMergeableThrowingObject();
    }

    public int getDatabaseId() {
        return uid;
    }

    // only use this when saving
    public void forceDatabaseId(int db) {
        uid = db;
    }

    public boolean isPet() {
        return false;
    }

    public boolean isRing() {
        return false;
    }

    public final boolean isBullet() {
        return id / 10000 == 233;
    }

    public final boolean isThrowingStar() {
        return id / 10000 == 207;
    }

    public final boolean isArrow(boolean bow) {
        return bow ? id / 1000 == 2060 : id / 1000 == 2061;
    }

    public final boolean isNonMergeableThrowingObject() {
        return isBullet() || isThrowingStar();
    }

    public void save(int cid, Connection c) throws SQLException {
        PreparedStatement ps;
        if (getDatabaseId() == -1) {
            ps = c.prepareStatement("INSERT INTO items (owner, type, uid, pos, quantity, mquantity, expiration, cash) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        } else {
            ps = c.prepareStatement("UPDATE items SET owner = ?, type = ?, uid = ?, pos = ?, quantity = ?, mquantity = ?, expiration = ?, cash = ? WHERE id = ?");
        }
        ps.setInt(1, cid);
        ps.setInt(2, type);
        ps.setInt(3, id);
        ps.setByte(4, slot);
        ps.setInt(5, quantity);
        ps.setInt(6, mquantity);
        ps.setLong(7, expiration);
        ps.setInt(8, cash ? 1 : 0);
        if (getDatabaseId() > 0) {
            ps.setInt(9, uid);
        }
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public final int compareTo(Item o) {
        if (slot < o.getSlot()) {
            return -1;
        } else if (slot == o.getSlot()) {
            return 0;
        } else {
            return 1;
        }
    }
}
