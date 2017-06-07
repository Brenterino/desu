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

import data.SkillData;
import data.external.factory.SkillFactory;
import wz.WzImage;
import wz.WzObject;
import wz.WzProperty;
import wz.common.WzDataTool;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Brent
 */
public class WzSkillFactory implements SkillFactory {
    
    private WzDataProvider provider;
    
    private WzSkillFactory() {}
    
    public WzSkillFactory(WzDataProvider provider) {
        this.provider = provider;
    }
    
    private SkillData generateInternal(int id, WzObject<?, ?> data) {
        if (data != null) {
            int skillType = WzDataTool.getInteger(data, "skillType", 0);
            int masterLevel = WzDataTool.getInteger(data, "masterLevel", 0);
            boolean invisible = WzDataTool.getBoolean(data, "invisible", false);

            WzObject<?, ?> lvlData = data.getChild("level");
            
            // XXX refactor this probs fam
            int count = lvlData.getChildren().size();
            
            HashMap<String, Integer>[] skillLvlData = new HashMap[count];

            for (WzObject<?, ?> level : lvlData) {
                HashMap<String, Integer> prop = new HashMap<>();
                int levelNum = Integer.valueOf(level.getName());

                for (WzObject<?, ?> iProp : level) {
                    if (iProp instanceof WzProperty) { // just a check even though it should be true
                        prop.put(iProp.getName(), WzDataTool.getInteger((WzProperty) iProp, 0));
                    }
                }
                
                skillLvlData[levelNum - 1] = prop; // problematic zzzzzzz
            }
            
            return new SkillData(id, skillType, masterLevel,
                invisible, Arrays.asList(skillLvlData));
        }
        return null;
    }
    
    @Override
    public SkillData generate(int id, boolean monster) {
        WzObject<?, ?> data = null;
        
        if (monster) {
            WzImage img = provider.loadImage("Skill", "MobSkill.img");
            
            if (img != null) {
                data = img.getChild(String.valueOf(id));
            }
            
        } else {
            int job = id / 10000;
            
            StringBuilder path = new StringBuilder(job == 0 ? "000" : String.valueOf(job));
            path.append(".img");
            
            WzImage img = provider.loadImage("Skill", path.toString());
            if (img != null) {
                WzObject<?, ?> skill = img.getChild("skill");
                
                String str_id = String.valueOf(id);
                
                if (job == 0) {
                    str_id = "000" + str_id;
                }
                
                data = skill.getChild(str_id);
            }
        }
        
        return generateInternal(id, data);
    }
}
