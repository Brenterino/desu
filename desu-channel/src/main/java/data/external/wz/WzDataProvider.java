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

import java.nio.file.Paths;
import data.external.DataProvider;
import data.external.factory.EquipFactory;
import data.external.factory.FieldFactory;
import data.external.factory.ItemFactory;
import data.external.factory.MonsterFactory;
import data.external.factory.SkillFactory;
import service.Configuration;
import wz.WzImage;
import wz.common.WzTool;
import wz.io.WzMappedInputStream;

/**
 *
 * @author Brent
 */
public class WzDataProvider implements DataProvider {

    private static final byte[] AES_KEY =
            WzTool.generateKey(Configuration.FILE_VERSION);
    
    private WzEquipFactory equipFactory;
    private WzFieldFactory fieldFactory;
    private WzItemFactory itemFactory;
    private WzMonsterFactory monsterFactory;
    private WzSkillFactory skillFactory;
    
    public WzDataProvider() {
    }
    
    @Override
    public void init() {
        equipFactory = new WzEquipFactory(this);
        fieldFactory = new WzFieldFactory(this);
        itemFactory = new WzItemFactory(this);
        monsterFactory = new WzMonsterFactory(this);
        skillFactory = new WzSkillFactory(this);
    }

    @Override
    public EquipFactory getEquipFactory() {
        return equipFactory;
    }

    @Override
    public FieldFactory getFieldFactory() {
        return fieldFactory;
    }

    @Override
    public ItemFactory getItemFactory() {
        return itemFactory;
    }
    
    @Override
    public MonsterFactory getMonsterFactory() {
        return monsterFactory;
    }

    @Override
    public SkillFactory getSkillFactory() {
        return skillFactory;
    }
    
    public WzImage loadImage(String... path) {
        WzMappedInputStream in = new WzMappedInputStream(Paths.get(Configuration.WZ_DIRECTORY, path));
        in.setKey(AES_KEY);
        WzImage img = new WzImage(path[path.length - 1], in); // XXX cache this?
        img.parse(in);
        return img;
    }
}
