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

import client.Client;
import field.FieldObject;
import java.awt.Point;
import java.sql.Connection;

/**
 *
 * @author Brent
 */
public class Minigame extends Item implements FieldObject {

    private int type;
    private int pieceType;
    
    private int oid;
    private int livePlayerCount;
    private String liveDescription;
    private boolean privacy;
    private String livePassword;
    
    public Minigame(int uid, int id, String tag, int type, int pieceType, byte slot, long expiration) {
        super(uid, id, tag, Inventory.Type.ETC.getType(), 1, 1, slot, (byte) 0, expiration, false);
        this.type = type;
        this.pieceType = pieceType;
    }

    @Override
    public Type getObjectType() {
        return Type.MINIGAME;
    }

    @Override
    public int getOid() {
        return oid;
    }

    @Override
    public void setOid(int oid) {
        this.oid = oid;
    }

    @Override
    public boolean isHidden() {
        return false; // should never be "hidden"
    }

    @Override
    public void toggleHidden() {
    }

    @Override
    public int getStance() {
        return -1;
    }

    @Override
    public void setStance(int s) {
    }

    @Override
    public Point getPosition() {
        return null;
    }

    @Override
    public void setPosition(Point p) {
    }

    @Override
    public int getFh() {
        return -1;
    }

    @Override
    public void sendSpawnData(Client mc) { // XXX open game
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendDestroyData(Client mc, boolean spc) { // XXX close
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void save(int cid, Connection c) {
        // XXX do dis
    }

    public int getGameType() {
        return type;
    }

    public int getPieceType() {
        return pieceType;
    }

    public String getDescription() {
        return liveDescription;
    }

    public int getPlayerCount() {
        return livePlayerCount;
    }
    
    public boolean isPrivate() {
        return privacy;
    }

    public boolean hasFreeSlot() {
        return livePlayerCount != 2;
    }
    
    public String getPassword() {
        return livePassword;
    }
}
