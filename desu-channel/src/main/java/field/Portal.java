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
import player.Player;
import player.Violation;
import wz.WzObject;
import wz.common.WzDataTool;

/**
 *
 * @author Brent
 */
public final class Portal extends FieldEntity {

    private String pn;
    private int pt;
    private String script;
    private int tm;
    private String tn;
    private Point pos;
    private int id;
    private int oid;

    private Portal() {
    }

    public Portal(int id, WzObject data) {
        this.id = id;
        pn = WzDataTool.getString(data, "pn", "");
        pt = WzDataTool.getInteger(data, "pt", 0);
        script = WzDataTool.getString(data, "script", null);
        tm = WzDataTool.getInteger(data, "tm", -1);
        tn = WzDataTool.getString(data, "tn", null);
        pos = new Point(WzDataTool.getInteger(data, "x", -1),
                WzDataTool.getInteger(data, "y", -1));
    }
    
    public int getId() {
        return id;
    }

    public String getPn() {
        return pn;
    }

    public int getPt() {
        return pt;
    }

    public String getScript() {
        return script;
    }

    public int getTm() {
        return tm;
    }

    public String getTn() {
        return tn;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void toggleHidden() {
    }

    @Override
    public Point getPosition() {
        return pos;
    }

    @Override
    public void setPosition(Point p) {
        this.pos = p;
    }
    
    @Override
    public int getFh() {
        return -1;
    }

    @Override
    public void sendSpawnData(Client c) {
        c.write(PacketCreator.spawnPortal(this));
    }

    @Override
    public void sendDestroyData(Client mc, boolean spc) {
    }

    @Override
    public Type getObjectType() {
        return Type.PORTAL;
    }

    @Override
    public int getStance() {
        return -1;
    }

    @Override
    public void setStance(int s) {
    }

    public void enterPortal(Client c) {
        Player p = c.getPlayer();
        double dist = p.getPosition().distance(getPosition());
        assert dist > 150 : Violation.PACKET_EDITTING;
        boolean change = false;
        if (getScript() != null && !getScript().isEmpty()) {
            // XXX handle debug for portal scripts later
            System.out.printf("Create portal script: %s.js%n", getScript());
            // XXX execute portal scripts
        } else if (getTm() != 999999999) {
            // XXX handle instances
            Field to = FieldManager.getField(getTm());
            if (to != null) {
                Portal target = to.getPortalByName(getTn()); // this is portal
                if (target == null) {
                    target = to.getPortal(0);
                }
                p.changeField(to, target);
                change = true;
            } else {
                System.out.printf("[Debug] Player [%s] tried warping to a field that does not exist id = [%s]%n", p.getName(), getTm());
            }
        }
        if (!change) {
            c.write(PacketCreator.enableActions());
        }
    }
}
