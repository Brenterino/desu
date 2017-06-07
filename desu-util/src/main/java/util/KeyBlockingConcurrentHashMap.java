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
package util;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ConcurrentHashMap that has an option of awaiting a key to
 * arrive through usage of a SynchronousQueue.  Interaction is
 * very loose between the underlying ConccurentHashMap and this 
 * implementation. May be better to create a map interface on top of
 * this to make it actually block on get(...) until until a key is available.
 * 
 * @author Brent
 */
public class KeyBlockingConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

    private long duration = 1000;
    private TimeUnit durationUnit = TimeUnit.MILLISECONDS;
    private LinkedList<K> arrived = new LinkedList<>();
    private SynchronousQueue<K> arriving = new SynchronousQueue<>();
    private ReentrantReadWriteLock arrivalLock = new ReentrantReadWriteLock();

    public KeyBlockingConcurrentHashMap() {
        super();
    }

    public KeyBlockingConcurrentHashMap(long duration) {
        super();
        this.duration = duration;
    }

    public KeyBlockingConcurrentHashMap(long duration, TimeUnit durationUnit) {
        super();
        this.duration = duration;
        this.durationUnit = durationUnit;
    }
    
    // other constructors are not included simple because they are not needed
    
    public boolean await(K key) {
        return await(key, duration, durationUnit);
    }

    public boolean await(K key, long timeOut, TimeUnit timeOutUnit) {
        if (key == null) {
            return false; // can throw exception
        }
        arrivalLock.readLock().lock();
        try {
            if (arrived.contains(key)) {
                return arrived.remove(key);
            }
        } finally {
            arrivalLock.readLock().unlock();
        }

        long timeOutMillis = timeOutUnit.toMillis(timeOut);
        
        long start = System.currentTimeMillis();
        long last = System.currentTimeMillis();
        
        while (last - start < timeOutMillis) {
            try {
                K here = arriving.poll(duration, durationUnit);
                if (!here.equals(key)) { // hopefuly not terrible
                    arrivalLock.writeLock().lock();
                    try {
                        arrived.add(here);
                    } finally {
                        arrivalLock.writeLock().unlock();
                    }
                } else {
                    return true;
                }
            } catch (Exception e) {
                break;
            }
            last = System.currentTimeMillis();
        }
        
        arrivalLock.readLock().lock();
        try {
            return arrived.remove(key);
        } finally {
            arrivalLock.readLock().unlock();
        }
    }

    public void arrive(K key) {
        try {
            arriving.offer(key, duration, durationUnit);
        } catch (Exception e) {
            // swallow
        }
    }
}
