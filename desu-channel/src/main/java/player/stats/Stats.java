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
package player.stats;

import client.packet.PacketCreator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import player.Player;

/**
 *
 * @author Brent
 */
public class Stats {

    private List<Stat> changed;
    private EnumMap<Stat, AtomicInteger> stats;

    public Stats() {
        changed = new LinkedList<>();
        stats = new EnumMap<>(Stat.class);
        for (Stat k : Stat.values()) {
            stats.put(k, new AtomicInteger(0));
        }
    }
    
    public Map<Stat, Integer> getChangedStats() {
        EnumMap<Stat, Integer> ret = new EnumMap<>(Stat.class);
        
        changed.stream().forEach(change -> {
            ret.put(change, stats.get(change).get());
        });
        
        changed.clear();
        
        return ret;
    }
    
    private void changed(Stat k) {
        if (!changed.contains(k)) {
            changed.add(k);
        }
    }
    
    public void forceUpdateOnChange(Stat k) {
        changed(k);
    }
    
    public void applyChangedStats(Player p) {
        applyChangedStats(p, false);
    }
    
    public void applyChangedStats(Player p, boolean item) {
        Map<Stat, Integer> changes = getChangedStats();
        
        p.getClient().write(PacketCreator.updatePlayerStats(changes, item));
    }
    
    public Integer getStat(Stat k) {
        return stats.get(k).get();
    }
    
    public void setStat(Stat k, int val) {
        stats.get(k).set(val);
    }

    public void changeStat(Stat k, int val) {
        stats.get(k).set(val);
        changed(k);
    }
    
    public void increment(Stat k) {
        stats.get(k).incrementAndGet();
        changed(k);
    }
    
    public void addTo(Stat k, int val) {
        stats.get(k).addAndGet(val);
        changed(k);
    }
    
    public void decrement(Stat k) {
        stats.get(k).decrementAndGet();
        changed(k);
    }
    
    public void subtractFrom(Stat k, int val) {
        stats.get(k).addAndGet(-val);
        changed(k);
    }
}
