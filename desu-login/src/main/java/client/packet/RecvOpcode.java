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

    LOGIN_REQUEST(0x01),
    GUEST_LOGIN(0x02),
    WORLD_INFO_REREQUEST(0x04),
    CHARACTER_LIST_REQUEST(0x05),
    WORLD_STATUS_REQUEST(0x06),
    SET_GENDER(0x08),
    PIN_OPERATION(0x09),
    REGISTER_PIN(0x0A),
    WORLD_INFO_REQUEST(0x0B),
    VIEW_ALL_CHARACTERS(0x0D),
    VIEW_ALL_CHANNEL_CONNECTION_REQUEST(0x0E),
    VIEW_ALL_STATE(0x0F),
    CHANNEL_CONNECTION_REQUEST(0x13),
    NAME_CHECK_REQUEST(0x15),
    CREATE_CHARACTER(0x16),
    DELETE_CHARACTER(0x17),
    PONG(0x18),
    ERROR_REPORT(0x19),
    RELOG_REQUEST(0x1C);
        
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
