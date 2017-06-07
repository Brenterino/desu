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
package player.community;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Brent
 */
public class Guild {

    private String name;
    private int logoBG;
    private int logoBGColor;
    private int logo;
    private int logoColor;
    
    private Map<Integer, String> members;
    
    public Guild(String name, int logoBG, int logoBGColor, int logo, int logoColor) {
        this.name = name;
        this.logoBG = logoBG;
        this.logoBGColor = logoBGColor;
        this.logo = logo;
        this.logoColor = logoColor;
        
        members = new HashMap<>();
    }
    
    // XXX code stuff
    
    public Map<Integer, String> getMembers() {
        return members;
    }
    
    public String getName() {
        return name;
    }

    public int getLogoBG() {
        return logoBG;
    }

    public int getLogoBGColor() {
        return logoBGColor;
    }

    public int getLogo() {
        return logo;
    }

    public int getLogoColor() {
        return logoColor;
    }
}
