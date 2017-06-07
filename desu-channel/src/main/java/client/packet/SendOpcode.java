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
public enum SendOpcode implements IntegerValue {

    PING(0x11), 
     
    MODIFY_INVENTORY(0x1A),
    UPDATE_STATS(0x1C),
    GIVE_BUFF(0x1D),
    CANCEL_BUFF(0x1E),
     
    UPDATE_SKILL(0x21),
    SHOW_STAT_CHANGE(0x24),
    
    BUDDYLIST(0x3C),
    
    SPAWN_PORTAL(0x40),
    
    WARP_PLAYER(0x5C), 
    
    FORCE_EQUIP(0x63),
    
    SPAWN_PLAYER(0x78), 
    REMOVE_PLAYER(0x79),
    FIELD_CHAT(0x7A),
    
    MOVE_PLAYER(0x8D),
    CLOSE_RANGE_ATTACK(0x8E), 
    RANGED_ATTACK(0x8F), 
    
    MAGIC_ATTACK(0x90),
    FACIAL_EXPRESSION(0x95),
     
    SHOW_ITEM_GAIN(0xA1),
    COOLDOWN(0xAD),
    
    SPAWN_MONSTER(0xAF), 
    REMOVE_MONSTER(0xB0),
    MONSTER_CONTROL(0xB1),
    MONSTER_ACTION(0xB2),
    MONSTER_ACTION_RESPONSE(0xB3),
    SHOW_MONSTER_HP(0xBD), 
    
    SPAWN_NPC(0xC2), 
    REMOVE_NPC(0xC3),
    NPC_CONTROL(0xC4), 
    NPC_ACTION(0xC5),
    SPAWN_FIELD_ITEM(0xCD),
    REMOVE_FIELD_ITEM(0xCE),
    
    SPAWN_REACTOR(0xD8), 
    REMOVE_REACTOR(0xD9), 
    
    KEYMAP(0x107), 
    ;

    private int value;

    private SendOpcode(int val) {
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
