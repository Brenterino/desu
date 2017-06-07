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
package world.packet;

import util.IntegerValue;

/**
 *
 * @author Brent
 */
public enum SendOpcode implements IntegerValue {

    BROADCAST_MESSAGE(0x00),
    CHANNEL_INFO(0x01),
    SHUTDOWN(0x02),
    PLAYER_CONNECTED(0x03),
    PLAYER_DISONNECTED(0x04), 
    PLAYER_TRANSIT(0x05),
    UPDATE_ACCOUNT_STATE(0x06), 
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
