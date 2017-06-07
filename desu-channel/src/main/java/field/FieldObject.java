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
package field;

import client.Client;
import java.awt.Point;

/**
 *
 * @author Brent
 */
public interface FieldObject {
    
    public Type getObjectType();    
    public int getOid();
    public void setOid(int oid);
    public boolean isHidden();
    public void toggleHidden();
    public int getStance();
    public void setStance(int s);
    public Point getPosition();
    public void setPosition(Point p);
    public int getFh();
    public void sendSpawnData(Client mc);
    public void sendDestroyData(Client mc, boolean spc);
    
    default void sendDestroyData(Client mc) {
        sendDestroyData(mc, false);
    }

    default boolean isNonRanged() {
        switch (getObjectType()) {
            case NPC:
            case PLAYER:
            case PET:
            case SHOP:
            case MINIGAME:
            case HIRED_MERCHANT:
            case PLAYER_NPC:
            case MIST:
                return true;
            default:
                return false;
        }
    }
    
    public enum Type {
        
        // non ranged
        NPC,
        PLAYER,
        PET, 
        SHOP, 
        MINIGAME,
        HIRED_MERCHANT,
        PLAYER_NPC,       
        MIST,
        
        // ranged
        REACTOR,
        MONSTER, 
        PORTAL,
        SUMMON, 
        ITEM,
        ;
    }
}
