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

import client.packet.PacketCreator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Stream;
import player.Player;
import player.Violation;

/**
 *
 * @author Brent
 */
public final class Inventory<E extends Item> implements Iterable<E> {

    private int slots;
    private final Type type;
    private final HashMap<Byte, E> ITEMS;
    private LinkedList<Modification> modifications;
    private ArrayList<Integer> startingUID;
    private ReentrantReadWriteLock lock;

    public static enum Type {

        EQUIP(0x01),
        USE(0x02),
        SETUP(0x03),
        ETC(0x04),
        CASH(0x05),
        UNKNOWN(0x80);
        private int type;

        private Type(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public boolean isEquip() {
            return type == 0x01;
        }

        public static Type getByType(int type) {
            for (Type t : values()) {
                if (t.type == type) {
                    return t;
                }
            }
            return UNKNOWN;
        }
    }

    public static class Modification {

        public int action;
        public int inventory;
        public byte old_pos;
        public byte new_pos;
        public short count;
        public Item target = null;

        private Modification() {
        }

        public Modification(Item i) {
            action = ADD_ITEM;
            target = i;
            inventory = i.getInventoryType();
            new_pos = i.getSlot();
        }

        public Modification(Item i, byte oslot) {
            this(i);
            new_pos = oslot;
        }

        public Modification(int type, byte pos, short q) {
            action = CHANGE_QUANTITY;
            inventory = type;
            new_pos = pos;
            count = q;
        }

        public Modification(int type, byte src, byte dst) {
            action = MOVE_ITEM;
            inventory = type;
            old_pos = src;
            new_pos = dst;
        }

        public Modification(int type, byte pos) {
            action = REMOVE_ITEM;
            inventory = type;
            new_pos = pos;
        }

        public static final int ADD_ITEM = 0;
        public static final int CHANGE_QUANTITY = 1;
        public static final int MOVE_ITEM = 2;
        public static final int REMOVE_ITEM = 3;
    }

    private Inventory(Type t, int s) {
        type = t;
        slots = s;
        ITEMS = new HashMap<>();
        modifications = new LinkedList<>();
        startingUID = new ArrayList<>();
        lock = new ReentrantReadWriteLock();
    }

    public int getCapacity() {
        return slots;
    }

    public void addModification(Modification m) {
        modifications.add(m);
    }

    public List<Modification> getModifications() {
        ArrayList<Modification> ret = new ArrayList<>(modifications);

        modifications.clear();

        return ret;
    }

    public void showInventoryModifications(Player p, boolean silent) {
        List<Modification> mods = getModifications();
        if (!mods.isEmpty()) {
            p.getClient().write(PacketCreator.modifyInventory(silent, mods));
        }
    }

    public Item get(int id) {
        lock.readLock().lock();
        try {
            for (E i : ITEMS.values()) {
                if (i.getItemId() == id) {
                    return i;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }

    public Item get(byte slot) {
        lock.readLock().lock();
        try {
            return ITEMS.get(slot);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<Byte, E> getItems() {
        return ITEMS;
    }

    // this method is for only initial loading
    private void add(E i) {
        lock.writeLock().lock();
        try {
            ITEMS.put(i.getSlot(), i);
            startingUID.add(i.getDatabaseId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean addItem(E item) {
        lock.writeLock().lock();
        try {
            if (item.isMergeable()) { // first look to see if we merge this item onto a stack of items that exist
                for (E inInv : ITEMS.values()) {
                    if (inInv.getItemId() == item.getItemId()) {
                        int mergeAmount = inInv.getMaximumQuantity() - inInv.getQuantity();
                        if (mergeAmount > 0) {
                            mergeAmount = item.getQuantity() >= mergeAmount ? mergeAmount : item.getQuantity();
                            item.removeQuantity(mergeAmount);
                            inInv.addQuantity(mergeAmount);
                            addModification(new Modification(type.getType(), inInv.getSlot(), (short) inInv.getQuantity()));
                        }
                    }
                    if (item.getQuantity() == 0) {
                        return true;
                    }
                }
            }
            byte nextSlot = nextAvailableSlot();
            if (nextSlot != -1) {
                item.setSlot(nextSlot);
                ITEMS.put(nextSlot, item);
                addModification(new Modification(item));
                return true;
            }
        } finally {
            lock.writeLock().unlock();
        }
        return false;
    }

    public void removeItem(byte slot) {
        lock.writeLock().lock();
        try {
            if (ITEMS.remove(slot) != null) {
                addModification(new Modification(type.getType(), slot));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean moveItem(byte src, byte dst) {
        lock.writeLock().lock();
        try {
            E srcItem = ITEMS.get(src);
            if (dst == -1) { // move to next available slot
                dst = nextAvailableSlot();
            }
            E dstItem = ITEMS.get(dst); 
            // XXX if any of the following fail, return false V
            if (srcItem == null || dst >= slots) {
                // XXX handle packet editting
            } else if (dst == -1) {
                return false; // cannot unequip if the target is next available slot
            }
            assert dst != -1 && srcItem != null && dst <= slots : Violation.PACKET_EDITTING;
            if (dstItem == null) {
                srcItem.setSlot(dst);
                ITEMS.remove(src);
                ITEMS.put(dst, srcItem);
                addModification(new Modification(type.getType(), src, dst));
            } else {
                if (srcItem.getItemId() == dstItem.getItemId()) { // merge & remove if needed
                    assert srcItem.isMergeable() && dstItem.isMergeable() : Violation.PACKET_EDITTING;
                    int mergeAmount = dstItem.getMaximumQuantity() - dstItem.getQuantity();
                    if (mergeAmount > 0) {
                        mergeAmount = srcItem.getQuantity() >= mergeAmount ? mergeAmount : srcItem.getQuantity();
                        srcItem.removeQuantity(mergeAmount);
                        dstItem.addQuantity(mergeAmount);
                        addModification(new Modification(type.getType(), dstItem.getSlot(), (short) dstItem.getQuantity()));
                        if (srcItem.getQuantity() == 0) {
                            addModification(new Modification(type.getType(), src));
                            ITEMS.remove(src);
                        } else {
                            addModification(new Modification(type.getType(), srcItem.getSlot(), (short) srcItem.getQuantity()));
                        }
                    }
                } else { // swap position
                    ITEMS.remove(src);
                    ITEMS.remove(dst);
                    srcItem.setSlot(dst);
                    dstItem.setSlot(src);
                    addModification(new Modification(type.getType(), src, dst));
                    ITEMS.put(dst, srcItem);
                    ITEMS.put(src, dstItem);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
        return true;
    }

    public int getItemCount(int id) {
        lock.readLock().lock();
        try {
            return supplier().get().filter(i -> (i.getItemId() == id)).map(i -> i.getQuantity()).reduce(0, Integer::sum);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean isSlotTaken(int slot) {
        lock.readLock().lock();
        try {
            return ITEMS.containsKey((byte) slot);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void removeFromSlot(byte slot, int count) {
        lock.writeLock().lock();
        try {
            Item target = ITEMS.get(slot);
            if (target != null) {
                if (target.getQuantity() > count) {
                    target.addQuantity(-count);
                    addModification(new Modification(type.getType(), slot, (short) count));
                } else if (target.getQuantity() == count) {
                    ITEMS.remove(slot);
                    addModification(new Modification(type.getType(), slot));
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeById(int id, int quantity) {
        lock.writeLock().lock();
        try {
            boolean done = false;
            ArrayList<Byte> loc = new ArrayList<>();
            ArrayList<Byte> toRemove = new ArrayList<>();
            for (E i : ITEMS.values()) {
                if (i.getItemId() == id) {
                    if (i.getQuantity() > quantity) {
                        i.removeQuantity(quantity);
                        addModification(new Modification(type.getType(), i.getSlot(), (short) i.getQuantity()));
                        done = true;
                        break;
                    } else if (i.getQuantity() == quantity) {
                        toRemove.add(i.getSlot());
                        done = true;
                        break;
                    } else {
                        loc.add(i.getSlot());
                    }
                }
            }
            if (!done && !loc.isEmpty()) {
                int num = 0;
                for (int i = 0; i < loc.size(); i++) {
                    E item = ITEMS.get(loc.get(i));
                    num += item.getQuantity();
                    if (num >= quantity) {
                        for (int x = 0; x < i; x++) {
                            item = ITEMS.get(loc.get(i));
                            if (item.getQuantity() >= quantity) {
                                item.removeQuantity(quantity);
                                addModification(new Modification(type.getType(), item.getSlot(), (short) item.getQuantity()));
                            } else {
                                toRemove.add(item.getSlot());
                            }
                            quantity -= item.getQuantity();
                        }
                        break;
                    }
                }
            }
            for (Byte destroy : toRemove) {
                ITEMS.remove(destroy);
                addModification(new Modification(type.getType(), destroy));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void sortById() { // XXX create inventory modification list
        lock.writeLock().lock();
        try {
            HashMap<Byte, E> items = (HashMap<Byte, E>) ITEMS.clone();
            ArrayList<Integer> ids = new ArrayList<>(items.size());
            ITEMS.clear();
            for (Item i : items.values()) {
                ids.add(i.getItemId());
            }
            Collections.sort(ids);
            for (Integer id : ids) {
                E rem = null;
                int quantity = 0;
                for (E i : items.values()) {
                    if (i.getItemId() == id) {
                        quantity += i.getQuantity();
                        if (quantity >= i.getMaximumQuantity()) {
                            i.setQuantity(i.getMaximumQuantity());
                            quantity -= i.getMaximumQuantity();
                            i.setSlot(nextAvailableSlot());
                            ITEMS.put(i.getSlot(), i);
                        } else {
                            rem = i;
                        }
                    }
                }
                if (quantity > 0 && rem != null) { // anything else shouldn't happen, since we would have separate stacks
                    rem.setQuantity(quantity);
                    rem.setSlot(nextAvailableSlot());
                    ITEMS.put(rem.getSlot(), rem);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private byte nextAvailableSlot() {
        for (byte b = 1; b < slots + 1; b++) {
            if (!ITEMS.containsKey(b)) {
                return b;
            }
        }
        return -1;
    }

    public Type getInventoryType() {
        return type;
    }

    public void dispose() {
        ITEMS.clear();
    }

    @Override
    public Iterator<E> iterator() {
        lock.readLock().lock();
        try {
            return ITEMS.values().iterator();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Supplier<Stream<E>> supplier() {
        lock.readLock().lock();
        try {
            return () -> ITEMS.values().stream();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public Supplier<Stream<E>> equippedSupplier() {
        if (!type.isEquip()) {
            return null;
        }
        lock.readLock().lock();
        try {
            return () -> ITEMS.values().stream().filter(i -> i.getSlot() < 0);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void save(Connection c, int id) throws SQLException {
        lock.readLock().lock();
        try {
            StringBuilder dB = new StringBuilder("DELETE FROM ");
            dB.append(type.equals(Type.EQUIP) ? "equips" : "items");
            dB.append(" WHERE id = ?");
            PreparedStatement ps = c.prepareStatement(dB.toString());
            for (Integer old : startingUID) {
                boolean found = false;
                for (Item item : ITEMS.values()) {
                    if (item.getDatabaseId() == old) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    ps.setInt(1, old);
                    ps.execute();
                }
            }
            ps.close();
            for (Item i : ITEMS.values()) {
                i.save(id, c);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public static Inventory load(Connection c, int id, int slots, Type t) throws SQLException {
        Inventory ret = new Inventory(t, slots);
        if (!t.isEquip()) {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM items WHERE owner = ? && type = ?");
            ps.setInt(1, id);
            ps.setInt(2, t.getType());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Item item = new Item(rs.getInt("id"), rs.getInt("uid"), rs.getString("tag"), t.getType(), rs.getInt("quantity"),
                        rs.getInt("mquantity"), rs.getByte("pos"), rs.getByte("flag"), rs.getLong("expiration"),
                        rs.getByte("cash") == 1);
                ret.add(item);
            }
            rs.close();
            ps.close();
            if (t.equals(Type.ETC)) {
                ps = c.prepareStatement("SELECT * FROM minigames WHERE owner = ?");
                ps.setInt(1, id);

                rs = ps.executeQuery();

                while (rs.next()) {
                    Minigame m = new Minigame(rs.getInt("uid"), rs.getInt("id"), rs.getString("tag"),
                            rs.getInt("type"), rs.getInt("piece"),
                            rs.getByte("pos"), rs.getLong("expiration"));
                    ret.add(m);
                }
                rs.close();
                ps.close();
            } else if (t.equals(Type.CASH)) {
                ps = c.prepareStatement("SELECT * FROM shops WHERE owner = ?");
                ps.setInt(1, id);

                rs = ps.executeQuery();

                while (rs.next()) {
                    Shop s = new Shop(rs.getInt("uid"), rs.getInt("id"), rs.getString("tag"),
                            rs.getByte("pos"), rs.getLong("expiration"));
                    ret.add(s);
                }

                rs.close();
                ps.close();

                ps = c.prepareStatement("SELECT * FROM pets WHERE owner = ?");
                ps.setInt(1, id);

                rs = ps.executeQuery();

                while (rs.next()) {
                    Pet p = new Pet(rs.getInt("uid"), rs.getInt("id"), rs.getString("tag"),
                            rs.getByte("pos"), rs.getLong("expiration"),
                            rs.getInt("level"), rs.getInt("experience"),
                            rs.getInt("closeness"), rs.getInt("fullness"));
                    ret.add(p);
                }

                rs.close();
                ps.close();
            }
        } else {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM equips WHERE owner = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ret.add(Equip.load(rs));
            }
            rs.close();
            ps.close();

            ps = c.prepareStatement("SELECT * FROM mounts WHERE owner = ?");
            ps.setInt(1, id);

            rs = ps.executeQuery();

            while (rs.next()) {
                byte slot = (byte) (rs.getByte("pos") * (rs.getBoolean("equipped") ? -1 : 1));
                Mount m = new Mount(rs.getInt("uid"), rs.getInt("id"), rs.getString("tag"), slot,
                        rs.getByte("slots"), rs.getByte("upgrades"), rs.getByte("flag"),
                        rs.getShort("str"), rs.getShort("dex"), rs.getShort("int"),
                        rs.getShort("luk"), rs.getShort("hp"), rs.getShort("mp"),
                        rs.getShort("attack"), rs.getShort("magic"), rs.getShort("defence"),
                        rs.getShort("mdefence"), rs.getShort("accuracy"), rs.getShort("avoidability"),
                        rs.getShort("speed"), rs.getShort("jump"), rs.getShort("hands"),
                        rs.getLong("locked"), rs.getLong("expiration"), rs.getInt("fatigue"),
                        rs.getInt("level"), rs.getInt("exp"));
                ret.add(m);
            }
            rs.close();
            ps.close();

            ps = c.prepareStatement("SELECT * FROM rings WHERE owner = ?");
            ps.setInt(1, id);
            
            rs = ps.executeQuery();

            while (rs.next()) {
                Ring r = new Ring(rs.getInt("uid"), rs.getInt("id"), rs.getString("tag"), rs.getByte("pos"),
                        rs.getByte("slots"), rs.getByte("upgrades"), rs.getByte("flag"),
                        rs.getShort("str"), rs.getShort("dex"), rs.getShort("int"),
                        rs.getShort("luk"), rs.getShort("hp"), rs.getShort("mp"),
                        rs.getShort("attack"), rs.getShort("magic"), rs.getShort("defence"),
                        rs.getShort("mdefence"), rs.getShort("accuracy"), rs.getShort("avoidability"),
                        rs.getShort("speed"), rs.getShort("jump"), rs.getShort("hands"),
                        rs.getLong("locked"), rs.getLong("expiration"), rs.getByte("cash") == 1,
                        rs.getInt("partner"), rs.getString("partnerName"), rs.getInt("partnerRingUID"),
                        rs.getInt("marriageID"));
                ret.add(r);
            }
            rs.close();
            ps.close();
        }
        return ret;
    }
}
