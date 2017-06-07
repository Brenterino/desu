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
package data.external.wz;

import data.external.factory.FieldFactory;
import field.Field;
import field.Foothold;
import field.FootholdTree;
import field.NPC;
import field.Reactor;
import field.Spawner;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import util.StringUtil;
import wz.WzImage;
import wz.WzObject;
import wz.common.WzDataTool;

/**
 *
 * @author Brent
 */
public class WzFieldFactory implements FieldFactory {

    private WzDataProvider provider;
    
    private WzFieldFactory() {}
    
    public WzFieldFactory(WzDataProvider provider) {
        this.provider = provider;
    }
    
    @Override
    public Field generate(int id, boolean life, boolean reactors) {
        Field ret = new Field(id);
        StringBuilder path = new StringBuilder();
        path.append("Map\\Map");
        path.append(id / 100000000);
        path.append("\\");
        path.append(StringUtil.getLeftPaddedString(String.valueOf(id), '0', 9));
        path.append(".img");
        
        WzImage data = provider.loadImage("Map", path.toString());
        
        if (data == null) {
            System.out.printf("Tried loading invalid field with path: %s%n", path.toString());
            return null;
        }
        
        String link = WzDataTool.getString(data, "info/link", null);
        if (link != null) {
            data.unparse();
            
            Field lField = generate(Integer.parseInt(link), life, reactors);
            lField.forceFieldId(id);
            return lField;
        }
        ret.setMobRate(WzDataTool.getFloat(data, "info/mobRate", 1.0F));
        ret.setReturnField(WzDataTool.getInteger(data, "info/returnMap", 0));
        ret.setFlyEnabled(WzDataTool.getBoolean(data, "info/fly", false));
        ret.setTown(WzDataTool.getBoolean(data, "info/town", false));
        ret.setSwimEnabled(WzDataTool.getBoolean(data, "info/swim", false));
        ret.setOnFirstUserEnter(WzDataTool.getString(data, "info/onFirstUserEnter", null));
        ret.setOnUserEnter(WzDataTool.getString(data, "info/onUserEnter", null));
        ret.setTimeLimit(WzDataTool.getInteger(data, "info/timeLimit", -1));
        ret.setFieldType(WzDataTool.getInteger(data, "info/fieldType", -1));
        ret.setForcedReturn(WzDataTool.getInteger(data, "info/forcedReturn", -1));
        Point lBound = new Point();
        Point uBound = new Point();
        ArrayList<Foothold> footholds = new ArrayList<>();
        WzObject<?, ?> foothold = data.getChild("foothold");       
        for (WzObject<?, ?> layer : foothold) {
            for (WzObject<?, ?> connected : layer) {
                for (WzObject<?, ?> hold : connected) {
                    Foothold fh = new Foothold(hold);
                    if (fh.x1 < lBound.x) {
                        lBound.x = fh.x1;
                    }
                    if (fh.x2 > uBound.x) {
                        uBound.x = fh.x2;
                    }
                    if (fh.y1 < lBound.y) {
                        lBound.y = fh.y1;
                    }
                    if (fh.y2 > uBound.y) {
                        uBound.y = fh.y2;
                    }
                    footholds.add(fh);
                }
            }
        }
        FootholdTree root = new FootholdTree(lBound, uBound);
        for (Foothold fh : footholds) {
            root.insert(fh);
        }
        ret.setFootholdMap(root);
        WzObject<?, ?> area = data.getChild("area");
        if (area != null) {
            for (WzObject<?, ?> info : area) {
                int x1 = WzDataTool.getInteger(info, "x1", 0);
                int y1 = WzDataTool.getInteger(info, "y1", 0);
                int x2 = WzDataTool.getInteger(info, "x2", 0);
                int y2 = WzDataTool.getInteger(info, "y2", 0);
                ret.addArea(new Rectangle(x1, y1, (x2 - x1), (y2 - y1)));
            }
        }
        if (life) {
            for (WzObject o : data.getChild("life")) {
                String t = WzDataTool.getString(o, "type", "");
                switch (t) {
                    case "n":
                        NPC n = new NPC(o);
                        ret.addFieldObject(n);
                        break;
                    case "m":
                        Spawner sp = new Spawner(o);
                        ret.addSpawner(sp);
                        break;
                    default:
                        System.out.printf("New life type discovered: %s%n", t);
                        break;
                }
            }
        }
        ret.parsePortalList(data.getChild("portal"));
        if (reactors) {
            WzObject<?, ?> re = data.getChild("reactor");
            if (re != null) {
                for (WzObject o : re) {
                    ret.addReactor(new Reactor(o));
                }
            }
        }
        ((WzImage) data).unparse();
        return ret;
    }
}
