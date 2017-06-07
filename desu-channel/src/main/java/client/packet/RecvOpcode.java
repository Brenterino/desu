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
package client.packet;

import util.IntegerValue;

/**
 *
 * @author Brent
 */
public enum RecvOpcode implements IntegerValue {
   
    PLAYER_LOGGEDIN(0x14),
    
    PONG(0x18),
    
    SECURITY_PACKET(0x1A),
    
    CHANGE_FIELD(0x23),
    MOVE_PLAYER(0x26),
    
    MELEE_ATTACK(0x29),
    
    TAKE_DAMAGE(0x2D),
    FIELD_CHAT(0x2E),
    
    FACIAL_EXPRESSION(0x30),
    
    MODIFY_INVENTORY(0x42),
    
    APPLY_AP(0x50),
    HEAL_OVER_TIME(0x51),
    APPLY_SP(0x52),
    
    PORTAL_ENTER(0x5C), // XXX see if this is CHANGE_MAP_SPECIAL (I forgot)
    
    MODIFY_KEY_BINDINGS(0x7B),
    
    MONSTER_ACTION(0x9D),
    MONSTER_AGGRO(0x9E),
    
    NPC_ACTION(0xA6),
    ITEM_LOOT(0xAB),
    
    PLAYER_UPDATE(0xC0), 
    ;
        
    private int value;    
        
    private RecvOpcode(int val) {
        value = val;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int val) {
        value = val;
    }
}
