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

/**
 * Artifact from Invictus. Documentation is not going to be added since
 * the logic is fairly straightforward.
 * 
 * @author OdinMS (original code)
 * @author Brent (modified code)
 */
public final class HexTool {

    private static final char[] HEX = {
        '0', '1', '2', '3',
        '4', '5', '6', '7',
        '8', '9', 'A', 'B',
        'C', 'D', 'E', 'F'
    };

    private HexTool() {
    }

    public static String toHex(byte b) {
        return new StringBuilder().append(HEX[((b << 8) >> 12) & 0x0F]).
                append(HEX[((b << 8) >> 8) & 0x0F]).
                toString();
    }

    public static String toHex(byte[] arr) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            ret.append(toHex(arr[i]));
            ret.append(' ');
        }
        return ret.substring(0, ret.length() - 1);
    }

    public static byte[] toBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
