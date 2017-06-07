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
package player.item;

import java.sql.Connection;

/**
 *
 * @author Brent
 */
public class Ring extends Equip {
    
    private int partnerId;
    private String partnerName;
    private int partnerDatabaseId;
    private int marriageId;
    
    public Ring(int uid, int id, String name, byte slot, 
            byte slots, byte upgrades, byte flag, short str, short dex, 
            short _int, short luk, short hp, short mp, short attack, 
            short magic, short wdef, short mdef, short acc, short avoid, 
            short speed, short jump, short hands, long locked,
            long expiration, boolean cash, int partnerId, String partnerName,
            int partnerDatabaseId, int marriageId) {
        super(uid, id, name, 1, slot, slots, upgrades, flag, str, dex, 
                _int, luk, hp, mp, attack, magic, wdef, mdef, acc, avoid, 
                speed, jump, hands, locked, 0, 0, 0, 0, 
                0, expiration, cash);
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.partnerDatabaseId = partnerDatabaseId;
        this.marriageId = marriageId;
    }
    
    public boolean isWeddingRing() {
        switch (getItemId()) {
            case 1112803: // Moonstone Wedding Ring
            case 1112806: // Star gem Wedding Ring
            case 1112807: // Golden Heart Wedding Ring
            case 1112809: // Silver Swan Wedding Ring
                return true;
            default:
                return false;
        }
    }
    
    public boolean isCrushRing() {
        switch (getItemId()) {
            case 1112001: // Crush Ring
            case 1112002: // Cloud Ring
            case 1112003: // Cupid Ring
            case 1112005: // Venus Fireworks
            case 1112006: // Crossed Hearts
            case 1112007: // Mistletoe Crush Ring
            case 1112012: // Rose Crush Ring
                return true;
            default:
                return false;
        }
    }
    
    public boolean isFriendshipRing() {
        switch (getItemId()) {
            case 1112800: // Clover
            case 1112801: // Flower Petal
            case 1112802: // Star
            case 1112812: // Shared Umbrella (not sure)
                return true;
            default:
                return false;
        }
    }

    public int getMarriageId() {
        return marriageId;
    }
    
    public int getPartnerRingDatabaseId() {
        return partnerDatabaseId;
    }
    
    public int getPartnerCharacterId() {
        return partnerId;
    }
    
    public String getPartnerName() {
        return partnerName;
    }
    
    @Override
    public boolean isRing() {
        return true;
    }
    
    @Override
    public void save(int cid, Connection c) {
        // XXX do dis
    }
}
