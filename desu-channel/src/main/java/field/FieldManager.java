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

import data.external.GameDatabase;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Brent
 */
public final class FieldManager {

    private static final ReentrantLock fieldLock = new ReentrantLock(true);
    private static final ReentrantLock instanceLock = new ReentrantLock(true);
    private static final LinkedHashMap<Integer, Field> fields = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Field> instancedFields = new LinkedHashMap<>();

    private FieldManager() {
    }

    public static Field getField(int id) {
        return getField(id, false, true, true);
    }

    public static Field getField(int id, boolean reload) {
        return getField(id, reload, true, true);
    }

    public static Field getField(int id, boolean reload, boolean life, boolean reactors) {
        fieldLock.lock();
        try {
            if (reload) {
                Field f = fields.remove(id);
                if (f != null) {
                    f.purge();
                }
                // XXX may want to force respawn for players
                // in the old field if this is to occur, however
                // I doubt that it is going to happen where we require a reload
            } else if (fields.containsKey(id)) {
                return fields.get(id);
            }
            Field ret = createField(id, life, reactors);
            if (ret != null) {
                fields.put(id, ret);
            }
            return ret;
        } finally {
            fieldLock.unlock();
        }
    }

    public static Field createInstancedField(int id, String ik) {
        return createInstancedField(id, ik, true, true);
    }

    public static Field createInstancedField(int id, String ik, boolean life, boolean reactors) {
        instanceLock.lock();
        try {
            if (instancedFields.containsKey(ik)) {
                // may want to implement something different
                // especially if for some reason we can't know
                // what instance key we want to use.
                System.out.println("Tried creating a new instanced field while another one with the same key exists.");
                return null;
            }
            Field ret = createField(id, life, reactors);
            if (ret != null) {
                instancedFields.put(ik, ret);
            }
            return ret;
        } finally {
            instanceLock.unlock();
        }
    }

    public static Field getInstancedField(String ik) {
        return instancedFields.get(ik);
    }

    public static void removeInstancedField(String ik) {
        instancedFields.remove(ik);
    }

    private static Field createField(int id, boolean life, boolean reactors) {
        return GameDatabase.getField(id, life, reactors);
    }
}
