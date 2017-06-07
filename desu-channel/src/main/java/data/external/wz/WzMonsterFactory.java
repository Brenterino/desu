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

import data.external.factory.MonsterFactory;
import field.monster.Monster;
import util.StringUtil;
import wz.WzObject;
import wz.common.WzDataTool;

/**
 *
 * @author Brent
 */
public class WzMonsterFactory implements MonsterFactory {

    private WzDataProvider provider;
    
    private WzMonsterFactory() {}
    
    public WzMonsterFactory(WzDataProvider provider) {
        this.provider = provider;
    }
    
    private Monster generateInternal(int id, WzObject<?, ?> data) {
        Monster ret = new Monster(id,
            WzDataTool.getInteger(data, "info/exp", 0),
            WzDataTool.getInteger(data, "info/level", 1),
            WzDataTool.getInteger(data, "info/link", 0),
            WzDataTool.getInteger(data, "info/maxHP", 0),
            WzDataTool.getInteger(data, "info/maxMP", 0),
            WzDataTool.getInteger(data, "info/mobType", 0),
            WzDataTool.getBoolean(data, "info/undead", false),
            WzDataTool.getBoolean(data, "info/publicReward", false),
            WzDataTool.getBoolean(data, "info/explosiveReward", false)
        );
        
        WzObject<?, ?> skillData = data.getChildByPath("info/skill");
        if (skillData != null) {
            for (WzObject<?, ?> skill : skillData) {
                int skillId = WzDataTool.getInteger(skill, "skill", 0);
                int level = WzDataTool.getInteger(skill, "level", 0);
                
                // XXX validate skill data (?)
                ret.addSkill(skillId, level);
            }
        }
        return ret;
    }
    
    @Override
    public Monster generate(int id) {
        StringBuilder path = new StringBuilder(
                StringUtil.getLeftPaddedString(String.valueOf(id), '0', 7));
        path.append(".img");
        
        return generateInternal(id, 
                provider.loadImage("Mob", path.toString()));
    }

}
