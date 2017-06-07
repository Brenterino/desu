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
import wz.WzObject;
import wz.common.WzDataTool;

/**
 *
 * @author Brent
 */
public final class Reactor extends FieldLife {

    private int f;
    private int id;
    private Point p;
    private String name;
    private int reactorTime;
    private int state;
    private int stance;
    private boolean hide;
    
    private Reactor() {
    }
    
    // XXX treat this as both the reactor and spawn since reactors are stationary?
    public Reactor(WzObject mdata) {
        f = WzDataTool.getInteger(mdata, "f", 0);
        id = WzDataTool.getInteger(mdata, "id", -1);
        name = WzDataTool.getString(mdata, "name", null);
        reactorTime = WzDataTool.getInteger(mdata, "reactorTime", -1);
        p = new Point(WzDataTool.getInteger(mdata, "x", -1),
                      WzDataTool.getInteger(mdata, "y", -1));
    }
    
    public int getF() {
        return f;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getReactorTime() {
        return reactorTime;
    }
    
    @Override
    public boolean isHidden() {
        return hide;
    }

    @Override
    public void toggleHidden() {
        hide = !hide;
    }
    
    @Override
    public int getFh() {
        return -1;
    }

    @Override
    public Point getPosition() {
        return p;
    }

    @Override
    public void setPosition(Point p) {
        this.p = p;
    }

    // XXX spawn & destroy reactor packets
    @Override
    public void sendSpawnData(Client c) {
        if (state != -1) {
            c.write(PacketCreator.spawnReactor(this));
        }
    }

    @Override
    public void sendDestroyData(Client c, boolean spc) {
        c.write(PacketCreator.removeReactor(this));
    }

    @Override
    public void sendControlGain(Client mc) {
    }

    @Override
    public void sendControlLoss(Client mc) {
    }

    @Override
    public Type getObjectType() {
        return Type.REACTOR;
    }

    @Override
    public int getStance() {
        return stance;
    }

    @Override
    public void setStance(int s) {
        stance = s;
    }

    public int getState() {
        return state;
    }
}
