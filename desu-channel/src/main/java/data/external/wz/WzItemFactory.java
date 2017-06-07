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

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import data.ItemData;
import data.external.factory.ItemFactory;
import util.StringUtil;
import wz.WzImage;
import wz.WzObject;
import wz.WzProperty;
import wz.common.WzDataTool;

/**
 *
 * @author Brent
 */
public class WzItemFactory implements ItemFactory {

    private WzDataProvider provider;
    
    private WzItemFactory() {}
    
    public WzItemFactory(WzDataProvider provider) {
        this.provider = provider;
    }
    
    private int[] loadExtended(WzObject<?, WzProperty<?>> data) {
        int[] ret;
        
        if (data != null) {
            int max = 0;
            for (WzProperty<?> idata : data) {
                max = Math.max(max, Integer.parseInt(idata.getName()));
            }
            
            ret = new int[max];
            for (WzProperty<?> idata : data) {
                int i = Integer.parseInt(idata.getName());
                
                ret[i] = WzDataTool.getInteger(idata, 0);
            }
        } else {
            ret = new int[0];
        }
        
        return ret;
    }
    
    private ItemData generateInternal(int id, WzObject<?, ?> data) {
        return new ItemData(id,
                WzDataTool.getInteger(data, "info/price", 1),
                WzDataTool.getInteger(data, "info/unitPrice", 1),
                WzDataTool.getInteger(data, "info/slotMax", 1),
                WzDataTool.getBoolean(data, "info/cash", false),
                WzDataTool.getBoolean(data, "info/pachinko", false),
                WzDataTool.getInteger(data, "info/meso", 0),
                WzDataTool.getInteger(data, "info/life", 0),
                WzDataTool.getInteger(data, "info/rate", 0),
                WzDataTool.getInteger(data, "info/npc", 0),
                WzDataTool.getBoolean(data, "info/soldInform", false),
                WzDataTool.getBoolean(data, "info/monsterBook", false),
                WzDataTool.getInteger(data, "info/type", 0),
                WzDataTool.getInteger(data, "info/mcType", -1),
                WzDataTool.getInteger(data, "info/mob", 0),
                WzDataTool.getInteger(data, "info/mobHP", 0),
                WzDataTool.getInteger(data, "info/bridleProp", 0),
                WzDataTool.getInteger(data, "info/bridlePropChg", 0),
                WzDataTool.getInteger(data, "info/bridlgeMsgType", 0),
                WzDataTool.getInteger(data, "info/useDelay", 0),
                WzDataTool.getInteger(data, "info/pquest", 0),
                WzDataTool.getInteger(data, "info/maxLevel", 200),
                WzDataTool.getInteger(data, "info/create", 0),
                WzDataTool.getBoolean(data, "info/quest", false),
                WzDataTool.getInteger(data, "info/questId", 0),
                WzDataTool.getInteger(data, "info/exp", 0),
                WzDataTool.getString(data, "info/path", ""),
                WzDataTool.getString(data, "info/bgmPath", ""),
                WzDataTool.getString(data, "info/effect", ""),
                WzDataTool.getBoolean(data, "info/isBgmOrEffect", false),
                WzDataTool.getInteger(data, "info/stateChangeItem", 0),
                WzDataTool.getBoolean(data, "info/showMessage", false),
                WzDataTool.getBoolean(data, "info/only", false),
                WzDataTool.getBoolean(data, "info/notSale", false),
                WzDataTool.getBoolean(data, "info/pickUpBlock", false),
                WzDataTool.getBoolean(data, "info/tradBlock", false),
                WzDataTool.getBoolean(data, "info/tradeBlock", false),
                WzDataTool.getBoolean(data, "info/expireOnLogout", false),
                WzDataTool.getBoolean(data, "info/timeLimited", false),
                WzDataTool.getInteger(data, "info/time", Integer.MAX_VALUE),
                WzDataTool.getInteger(data, "info/recoveryHP", 0),
                WzDataTool.getInteger(data, "info/recoveryMP", 0),
                WzDataTool.getInteger(data, "info/reqSkillLevel", 0),
                WzDataTool.getInteger(data, "info/skill/0", 0),
                WzDataTool.getInteger(data, "info/masterLevel", 0),
                WzDataTool.getInteger(data, "info/warmsupport", 0), // these might work as bools V
                WzDataTool.getInteger(data, "info/preventslip", 0),
                WzDataTool.getInteger(data, "info/success", 0),
                WzDataTool.getInteger(data, "info/cursed", 0),
                WzDataTool.getBoolean(data, "info/randstat", false),
                WzDataTool.getBoolean(data, "info/recover", false),
                WzDataTool.getInteger(data, "info/incMHP", 0),
                WzDataTool.getInteger(data, "info/incMMP", 0),
                WzDataTool.getInteger(data, "info/incSTR", 0),
                WzDataTool.getInteger(data, "info/incDEX", 0),
                WzDataTool.getInteger(data, "info/incINT", 0),
                WzDataTool.getInteger(data, "info/incLUK", 0),
                WzDataTool.getInteger(data, "info/incPAD", 0),
                WzDataTool.getInteger(data, "info/incMAD", 0),
                WzDataTool.getInteger(data, "info/incPDD", 0),
                WzDataTool.getInteger(data, "info/incMDD", 0),
                WzDataTool.getInteger(data, "info/incACC", 0),
                WzDataTool.getInteger(data, "info/incEVA", 0),
                WzDataTool.getInteger(data, "info/incSpeed", 0),
                WzDataTool.getInteger(data, "info/incJump", 0),
                WzDataTool.getInteger(data, "spec/time", 0),
                WzDataTool.getBoolean(data, "spec/consumeOnPickup", false),
                loadExtended(data.getChildByPath("spec/con")),
                WzDataTool.getInteger(data, "spec/itemCode", 0),
                WzDataTool.getInteger(data, "spec/itemupbyitem", 0),
                WzDataTool.getInteger(data, "spec/mesoupbyitem", 0),
                WzDataTool.getInteger(data, "spec/itemRange", 0),
                WzDataTool.getInteger(data, "spec/prob", 0),
                WzDataTool.getBoolean(data, "spec/respectFS", false),
                WzDataTool.getBoolean(data, "spec/respectPimmune", false),
                WzDataTool.getBoolean(data, "spec/respectMimmune", false),
                WzDataTool.getInteger(data, "spec/defenseState", 0),
                WzDataTool.getInteger(data, "spec/defenseAtt", 0),
                WzDataTool.getInteger(data, "spec/morph", 0),
                WzDataTool.getBoolean(data, "spec/ghost", false),
                WzDataTool.getInteger(data, "spec/moveTo", 9999999), // XXX forced return (?)
                WzDataTool.getInteger(data, "spec/returnMapQR", 0),
                WzDataTool.getInteger(data, "spec/nuffSkill", 0),
                WzDataTool.getBoolean(data, "spec/party", false),
                WzDataTool.getInteger(data, "spec/cp", 0),
                WzDataTool.getBoolean(data, "spec/barrier", false),
                WzDataTool.getInteger(data, "spec/mob/0", 0), // XXX may need more (zzz)
                WzDataTool.getInteger(data, "spec/thaw", 0),
                WzDataTool.getBoolean(data, "spec/curse", false),
                WzDataTool.getBoolean(data, "spec/darkness", false),
                WzDataTool.getBoolean(data, "spec/poison", false),
                WzDataTool.getBoolean(data, "spec/seal", false),
                WzDataTool.getBoolean(data, "spec/weakness", false),
                loadExtended(data.getChildByPath("spec/pets")),
                WzDataTool.getInteger(data, "spec/inc", 0),
                WzDataTool.getInteger(data, "spec/incFatigue", 0),
                WzDataTool.getInteger(data, "spec/hp", 0),
                WzDataTool.getInteger(data, "spec/mp", 0),
                WzDataTool.getInteger(data, "spec/hpR", 0),
                WzDataTool.getInteger(data, "spec/mpR", 0),
                WzDataTool.getInteger(data, "spec/pad", 0),
                WzDataTool.getInteger(data, "spec/mad", 0),
                WzDataTool.getInteger(data, "spec/pdd", 0),
                WzDataTool.getInteger(data, "spec/mdd", 0),
                WzDataTool.getInteger(data, "spec/acc", 0),
                WzDataTool.getInteger(data, "spec/eva", 0),
                WzDataTool.getInteger(data, "spec/speed", 0),
                WzDataTool.getInteger(data, "spec/jump", 0)
        );
    }
    
    @Override
    public List<ItemData> generate(int id) {
        WzImage data = findImage(id);
        
        if (data != null) {
            if (id / 1000000 == 5) { // pet
                try {
                    return Collections.singletonList(generateInternal(id, data));
                } finally {
                    data.unparse();
                }
            } else {
                // since I'm pretty dank, we're going to load all of the items
                List<ItemData> items = new LinkedList<>();
                
                try {
                    for (WzObject<?, ?> item : data) {
                        int itemId = Integer.parseInt(item.getName());
                        
                        items.add(generateInternal(itemId, item));
                    }

                    return items;
                } finally {
                    data.unparse();
                }
            }
        }
        
        return Collections.EMPTY_LIST;
    }
    
    private WzImage findImage(int id) {
        StringBuilder path = new StringBuilder();
        String inner = "";
        int prefix = id / 10000;
        outer:
        {
            switch (id / 1000000) {
                case 2:
                    inner = "Consume";
                    break;
                case 3:
                    inner = "Install";
                    break;
                case 4:
                    inner = "Etc";
                    break;
                case 5:
                    if (prefix != 500) {
                        inner = "Cash";
                    } else {
                        inner = "Pet";
                        path.append(String.valueOf(id)).append(".img");
                        break outer;
                    }
                    break;
                case 9:
                    inner = "Special";
                    break;
                default:
                    break;
            }
            path.append(StringUtil.getLeftPaddedString(String.valueOf(prefix), '0', 4));
            path.append(".img");
        }
        
        return provider.loadImage("Item", inner, path.toString());
    }
}
