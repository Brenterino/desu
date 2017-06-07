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
package quest;

import player.Player;

/**
 *
 * @author Brent
 */
public enum Action {
    
    // PRE
    
    // POST
    PETSKILL,
    NEXT_QUEST, // force the start of a quest
    PETTAMENESS,
    MESSAGE, // drop down message
    POP,
    SKILL,
    BUFFITEM_ID,
    NPC_ACT,
    PETSPEED,
    MAP, // used in Baby Dragon quest to get Dragon Stone, so may be for buff and message
         // ironically, HT quest has map 0 listed as well
    
    // PRE + POST
    ITEM,
    MONEY,
    EXP,
    ;
    
    @FunctionalInterface
    public static interface Act {
        
        public void act(Player p);
    }
}
