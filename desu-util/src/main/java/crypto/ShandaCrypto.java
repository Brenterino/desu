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
package crypto;

import static crypto.BitTools.rollLeft;
import static crypto.BitTools.rollRight;

/**
 * Cryptography originally from a company called Shanda. Was used in
 * Global MapleStory up until version 149.2. Until recently, the name of this
 * cryptography step was unknown until someone identified the segment of code.
 * Documentation not included since the math is not easily explained.
 * 
 * @author OdinMS (original code)
 * @author Brent (modified version)
 */
public final class ShandaCrypto {

    public static byte[] encrypt(byte data[]) {
        for (int j = 0; j < 6; j++) {
            byte remember = 0;
            byte dataLength = (byte) (data.length & 0xFF);
            if (j % 2 == 0) {
                for (int i = 0; i < data.length; i++) {
                    byte cur = data[i];
                    cur = rollLeft(cur, 3);
                    cur += dataLength;
                    cur ^= remember;
                    remember = cur;
                    cur = rollRight(cur, (int) dataLength & 0xFF);
                    cur = ((byte) ((~cur) & 0xFF));
                    cur += 0x48;
                    dataLength--;
                    data[i] = cur;
                }
            } else {
                for (int i = data.length - 1; i >= 0; i--) {
                    byte cur = data[i];
                    cur = rollLeft(cur, 4);
                    cur += dataLength;
                    cur ^= remember;
                    remember = cur;
                    cur ^= 0x13;
                    cur = rollRight(cur, 3);
                    dataLength--;
                    data[i] = cur;
                }
            }
        }
        return data;
    }

    public static byte[] decrypt(byte data[]) {
        for (int j = 1; j <= 6; j++) {
            byte remember = 0;
            byte dataLength = (byte) (data.length & 0xFF);
            byte nextRemember = 0;
            if (j % 2 == 0) {
                for (int i = 0; i < data.length; i++) {
                    byte cur = data[i];
                    cur -= 0x48;
                    cur = ((byte) ((~cur) & 0xFF));
                    cur = rollLeft(cur, (int) dataLength & 0xFF);
                    nextRemember = cur;
                    cur ^= remember;
                    remember = nextRemember;
                    cur -= dataLength;
                    cur = rollRight(cur, 3);
                    data[i] = cur;
                    dataLength--;
                }
            } else {
                for (int i = data.length - 1; i >= 0; i--) {
                    byte cur = data[i];
                    cur = rollLeft(cur, 3);
                    cur ^= 0x13;
                    nextRemember = cur;
                    cur ^= remember;
                    remember = nextRemember;
                    cur -= dataLength;
                    cur = rollRight(cur, 4);
                    data[i] = cur;
                    dataLength--;
                }
            }
        }
        return data;
    }
}
