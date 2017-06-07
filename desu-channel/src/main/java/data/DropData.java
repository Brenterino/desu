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
package data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Brent
 */
public class DropData {

    public class Drop {
        
        private int item;
        private int minimumQuantity;
        private int maximumQuantity;
        private int chance;
    
    
        public Drop(int item, int min, int max, int chance) {
            this.item = item;
            this.minimumQuantity = min;
            this.maximumQuantity = max;
            this.chance = chance;
        }

        public int getItemId() {
            return item;
        }
        
        public int getDropVariance() {
            return maximumQuantity - minimumQuantity;
        }

        public int getMinimumQuantity() {
            return minimumQuantity;
        }

        public int getMaximumQuantity() {
            return maximumQuantity;
        }

        public int getChance() {
            return chance;
        }

        public float getProbability() {
            return chance / 1000000;
        }
    }
    
    private List<Drop> drops;
    
    private DropData() {}
    
    public DropData(ResultSet rs) throws SQLException {
        rs.last();
        drops = new ArrayList<>(rs.getRow());
        
        rs.first();
        while (rs.next()) {
            int item = rs.getInt("item");
            int min = rs.getInt("min");
            int max = rs.getInt("max");
            int chance = rs.getInt("chance");
            
            drops.add(new Drop(item, min, max, chance));
        }
    }
    
    public List<Drop> getDrops() {
        return drops;
    }
}
