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
 * Time utility used for certain packets dealing with calendar dates and
 * specific times in the Gregorian calendar. Information regarding the logic
 * behind these mutations can be found in the original OdinMS source code.
 * 
 * @author OdinMS (original author)
 */
public final class TimeUtil {

               public static final long ZERO_TIME =  94354848000000000L;
            public static final long FT_UT_OFFSET = 116444592000000000L;
          public static final long PERMANENT_TIME = 150842304000000000L;
    
    private TimeUtil() {
    }

    public static long getTimestamp(long time) {
        if (time == 0) {
            return PERMANENT_TIME;
        }
        return time * 10000 + FT_UT_OFFSET;
    }
}
