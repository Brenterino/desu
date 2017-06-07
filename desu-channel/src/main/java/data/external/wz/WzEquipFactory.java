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

import data.EquipData;
import data.external.factory.EquipFactory;
import util.StringUtil;
import wz.WzImage;
import wz.WzObject;
import wz.common.WzDataTool;

/**
 *
 * @author Brent
 */
public class WzEquipFactory implements EquipFactory {

    private WzDataProvider provider;
    
    private WzEquipFactory() {}
    
    public WzEquipFactory(WzDataProvider provider) {
        this.provider = provider;
    }
    
    private EquipData generateInternal(int id, WzObject<?, ?> data) {
        return new 
            EquipData(id, 
                      WzDataTool.getBoolean(data, "info/cash", false),
                      WzDataTool.getInteger(data, "info/price", 1),
                      WzDataTool.getBoolean(data, "info/treadeBlock", false), // XXX typo?
                      WzDataTool.getBoolean(data, "info/notSale", false),
                      WzDataTool.getBoolean(data, "info/timeLimited", false),
                      WzDataTool.getBoolean(data, "info/expireOnLogout", false),
                      WzDataTool.getBoolean(data, "info/only", false),
                      WzDataTool.getBoolean(data, "info/quest", false),
                      WzDataTool.getBoolean(data, "info/pachinko", false),
                      WzDataTool.getInteger(data, "info/slotMax", 1),
                      WzDataTool.getInteger(data, "info/fs", 0),
                      WzDataTool.getInteger(data, "info/attack", 0),
                      WzDataTool.getInteger(data, "info/attackSpeed", 0),
                      WzDataTool.getInteger(data, "info/knockback", 0),
                      WzDataTool.getInteger(data, "info/tamingMob", 0),
                      WzDataTool.getInteger(data, "info/tuc", 0),
                      WzDataTool.getString(data, "info/islot", ""),
                      WzDataTool.getString(data, "info/vslot", ""),
                      WzDataTool.getInteger(data, "info/recovery", 0),
                      WzDataTool.getInteger(data, "info/hpRecovery", 0),
                      WzDataTool.getInteger(data, "info/mpRecovery", 0),
                      WzDataTool.getInteger(data, "info/reqSTR", 0),
                      WzDataTool.getInteger(data, "info/reqDEX", 0),
                      WzDataTool.getInteger(data, "info/reqINT", 0),
                      WzDataTool.getInteger(data, "info/reqLUK", 0),
                      WzDataTool.getInteger(data, "info/reqPOP", 0),
                      WzDataTool.getInteger(data, "info/regPOP", 0),
                      WzDataTool.getInteger(data, "info/reqLevel", 0),
                      WzDataTool.getInteger(data, "info/reqJob", 0),
                      WzDataTool.getInteger(data, "info/incSTR", 0),
                      WzDataTool.getInteger(data, "info/incDEX", 0),
                      WzDataTool.getInteger(data, "info/incINT", 0),
                      WzDataTool.getInteger(data, "info/incLUK", 0),
                      WzDataTool.getInteger(data, "info/incLUk", 0),
                      WzDataTool.getInteger(data, "info/incSpeed", 0),
                      WzDataTool.getInteger(data, "info/incJump", 0),
                      WzDataTool.getInteger(data, "info/incMHP", 0),
                      WzDataTool.getInteger(data, "info/incMMP", 0),
                      WzDataTool.getInteger(data, "info/incMDD", 0),
                      WzDataTool.getInteger(data, "info/incPDD", 0),
                      WzDataTool.getInteger(data, "info/incMMD", 0),
                      WzDataTool.getInteger(data, "info/incPAD", 0),
                      WzDataTool.getInteger(data, "info/incMAD", 0),
                      WzDataTool.getInteger(data, "info/incACC", 0),
                      WzDataTool.getInteger(data, "info/acc", 0),
                      WzDataTool.getInteger(data, "info/incEVA", 0),
                      WzDataTool.getInteger(data, "info/incCraft", 0),
                      WzDataTool.getInteger(data, "info/incFatigue", 0),
                      WzDataTool.getInteger(data, "info/incSwim", 0),
                      WzDataTool.getInteger(data, "info/incRMAS", 100),
                      WzDataTool.getInteger(data, "info/incRMAL", 100),
                      WzDataTool.getInteger(data, "info/incRMAI", 100),
                      WzDataTool.getInteger(data, "info/incRMAF", 100),
                      WzDataTool.getInteger(data, "info/elemDefault", 100)
            );
    }
    
    @Override
    public EquipData generate(int id) {
        StringBuilder path = new StringBuilder();

        path.append(StringUtil.getLeftPaddedString(String.valueOf(id), '0', 8));
        path.append(".img");

        try {
            WzImage img = provider.loadImage("Character", 
                    getImageNameByPrefix(id / 10000), 
                    path.toString());
            
            if (img != null) {
                try {
                    return generateInternal(id, img);
                } finally {
                    img.unparse();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // was unable to find the item, probably wrong id
        }
        
        return null;
    }
    
    private static String getImageNameByPrefix(int prefix) {
        switch (prefix) {
            case 101:
            case 102:
            case 103:
            case 112:
                return "Accessory";
                
            case 100:
                return "Cap";
                
            case 110:
                return "Cape";
                
            case 104:
                return "Coat";
                
            case 108:
                return "Glove";
                
            case 105:
                return "Longcoat";
                
            case 106:
                return "Pants";
                
            case 180:
            case 181:
            case 182:
            case 183:
                return "PetEquip";
                
            case 111:
                return "Ring";
                
            case 109:
                return "Shield";
                
            case 107:
                return "Shoes";
                
            case 190:
            case 191:
            case 193:
                return "TamingMob";
                
            default:
                return "Weapon";
        }
    }
}
