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
package data.internal;

/**
 *
 * @author Brent
 */
public class Constants {
    
    public static final int MAX_LEVEL = 200;
    public static final int STAT_MAX = 999;
    public static final int DAMAGE_CAP = 99999;
    
    public static final int MAX_JOB_PREFIX = 5;
    
    // Structure inside of BMS leak seems to suggest
    // Nexon may have considered scaling HP on a primary
    // stat just like MP at one point... (probably STR)
    
    // Min, Max, Null Weight
    public static final int[] HP_GAINS = {
        12, 16, 0, // Beginner Level Gains
         8, 12, 0, // Beginner AP Gains
        
        24, 28, 0, // Warrior Level Gains
        20, 24, 0, // Warrior AP Gains
        
        10, 14, 0, // Mage Level Gains
         6, 10, 0, // Mage AP Gains
         
        20, 24, 0, // Archer Level Gains
        16, 20, 0, // Archer AP Gains
        
        20, 24, 0, // Thief Level Gains
        16, 20, 0, // Thief AP Gains
        
        22, 26, 0, // Pirate Level Gains
        18, 22, 0, // Pirate AP Gains
    };
                
    
    // Min, Max, Intelligence Weight
    public static final int[] MP_GAINS = {
        10, 12, 20, // Beginner Level Gains
         6,  8, 15, // Beginner AP Gains
        
         4,  6, 20, // Warrior Level Gains
         2,  4, 15, // Warrior AP Gains
         
        22, 24, 20, // Mage Level Gains
        18, 20, 15, // Mage AP Gains
        
        14, 16, 20, // Archer Level Gains
        10, 12, 15, // Archer AP Gains
        
        14, 16, 20, // Thief Level Gains
        10, 12, 15, // Thief AP Gains
        
        18, 20, 20, // Pirate Level Gains
         8, 10, 15, // Pirate AP Gains
    };
}
