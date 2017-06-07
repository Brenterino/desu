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

    LOGIN_STATUS(0x00),
    GUEST_LOGIN_STATUS(0x01),
    WORLD_STATUS(0x03),
    ON_SET_ACCOUNT_RESULT(0x04), // not sure what this does
    CONFIRM_EULA_RESULT(0x05),
    PIN_OPERATION(0x06),
    PIN_REGISTERED(0x07),
    ALL_CHARACTER_LIST(0x08),
    AFTER_LOGIN_ERROR(0x09),
    WORLD_INFO(0x0A),
    CHARACTER_LIST(0x0B),
    CHANNEL_CONNECTION_INFO(0x0C),
    NAME_CHECK_RESPONSE(0x0D),
    CREATE_CHARACTER_RESPONSE(0x0E),
    DELETE_CHARACTER_RESPONSE(0x0F),
    PING(0x11),
    RELOG_RESPONSE(0x16);

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
