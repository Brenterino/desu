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

import java.awt.Point;

/**
 * Artifact from Invictus. Original was part of OdinMS code and modified
 * later for Invictus. Not documented further since logic is straightforward.
 * 
 * @author OdinMS (original code)
 * @author Brent
 */
public final class StringUtil {

    private StringUtil() {
    }
    
    public static String getRightPaddedString(String in, char fill, int len) {
        StringBuilder sb = new StringBuilder();
        sb.append(in);
        for (int i = 0; i < len - in.length(); i++) {
            sb.append(fill);
        }
        return sb.toString();
    }
    
    public static String getLeftPaddedString(String in, char fill, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len - in.length(); i++) {
            sb.append(fill);
        }
        sb.append(in);
        return sb.toString();
    }
    
    public static int getIntegerFromLeftPadded(String s) {
        int index = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != '0') {
                index = i;
                break;
            }
        }
        return Integer.parseInt(s.substring(index));
    }
    
    public static String toString(Point p) {
        return new StringBuilder("X: ").append(p.x).append("\nY: ").append(p.y).toString();
    }

    public static String combine(String[] in, int index, char sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = index; i < in.length; i++) {
            sb.append(in[i]).append(i + 1 >= in.length ? "" : sep);
        }
        return sb.toString();
    }
}
