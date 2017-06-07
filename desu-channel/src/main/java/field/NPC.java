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
import client.packet.PacketCreator;
import java.awt.Point;
import util.StringUtil;
import wz.WzObject;
import wz.common.WzDataTool;

/**
 *
 * @author Brent
 */
public final class NPC extends FieldLife {

    private int f;
    private int fh;
    private int cy;
    private int id;
    private int rx0;
    private int rx1;
    private Point p;
    private boolean hide;
    private String limitedname; // XXX whether or not to hide if certain script is going on?

    private NPC() {
    }
    
    public NPC(WzObject data) {
        cy = WzDataTool.getInteger(data, "cy", -1);
        f = WzDataTool.getInteger(data, "f", 0);
        fh = WzDataTool.getInteger(data, "fh", -1);
        hide = WzDataTool.getBoolean(data, "hide", false);
        id = StringUtil.getIntegerFromLeftPadded(WzDataTool.getString(data, "id", "0"));
        limitedname = WzDataTool.getString(data, "limitedname", null);
        rx0 = WzDataTool.getInteger(data, "rx0", -1);
        rx1 = WzDataTool.getInteger(data, "rx1", -1);
        p = new Point(WzDataTool.getInteger(data, "x", 0),
                      WzDataTool.getInteger(data, "y", 0));
    }
    
    public int getF() {
        return f;
    }
    
    public void setF(int f) {
        this.f = f;
    }
    
    @Override
    public int getFh() {
        return fh;
    }
    
    public void setFh(int fh) {
        this.fh = fh;
    }
    
    public int getCy() {
        return cy;
    }
    
    public void setCy(int cy) {
        this.cy = cy;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) { // lol transform npc?
        this.id = id;
    }
    
    public int getRx0() {
        return rx0;
    }
    
    public void setRx0(int rx0) {
        this.rx0 = rx0;
    }
    
    public int getRx1() {
        return rx1;
    }
    
    public void setRx1(int rx1) {
        this.rx1 = rx1;
    }
    
    @Override
    public boolean isHidden() {
        return hide;
    }
    
    @Override
    public void toggleHidden() {
        hide = !hide;
    }
    
    public String getLimitedName() {
        return limitedname;
    }
    
    public void setLimitedName(String name) {
        limitedname = name;
    }
    
    @Override
    public Point getPosition() {
        return p;
    }

    @Override
    public void setPosition(Point p) {
        this.p = p;
    }

    @Override
    public void sendSpawnData(Client c) {
        c.write(PacketCreator.spawnNPC(this));
    }

    @Override
    public void sendDestroyData(Client c, boolean spc) {
        c.write(PacketCreator.removeNPC(getOid(), spc));
    }

    @Override
    public void sendControlGain(Client c) {
        c.write(PacketCreator.giveNPCControl(this, 
                id <= 9010010 || id >= 9010014));
    }

    @Override
    public void sendControlLoss(Client c) {
        c.write(PacketCreator.removeNPCControl(this));
    }

    @Override
    public Type getObjectType() {
        return Type.NPC;
    }

    @Override
    public int getStance() {
        return -1;
    }

    @Override
    public void setStance(int s) {
    }
}
