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
package data.external;

import data.DropData;
import data.EquipData;
import data.ItemData;
import data.SkillData;
import data.external.wz.WzDataProvider;
import field.Field;
import field.monster.Monster;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.HashMap;
import player.Player;
import player.item.Inventory;
import service.GameService;

/**
 *
 * @author Brent
 */
public class GameDatabase {

    private final static DataProvider provider;
    
    private final static HashMap<Integer, SkillData> SKILLS;
    private final static HashMap<Integer, SkillData> MONSTER_SKILLS;
    private final static HashMap<Integer, Monster> MONSTERS;
    private final static HashMap<Integer, ItemData> ITEMS;
    private final static HashMap<Integer, EquipData> EQUIPS;
    private final static HashMap<Integer, DropData> DROPS;

    static {
        provider = new WzDataProvider();
        provider.init();
        
        SKILLS = new HashMap<>();
        MONSTER_SKILLS = new HashMap<>();
        MONSTERS = new HashMap<>();
        ITEMS = new HashMap<>();
        EQUIPS = new HashMap<>();
        DROPS = new HashMap<>();
    }

    public static EquipData getEquip(int id) {
        synchronized (EQUIPS) {
            if (EQUIPS.containsKey(id)) {
                return EQUIPS.get(id);
            }
            
            EquipData ret = provider.getEquipFactory().generate(id);
            
            if (ret != null) {
                EQUIPS.put(id, ret);
            }
            
            return ret;
        }
    }

    // field manager handles this, so no synchronization required
    public static Field getField(int id, boolean life, boolean reactors) {
        return provider.getFieldFactory().generate(id, life, reactors);
    }

    public static ItemData getItem(int id) {
        synchronized (ITEMS) {
            if (ITEMS.containsKey(id)) {
                return ITEMS.get(id);
            }
            
            List<ItemData> items = provider.getItemFactory().generate(id);
            
            for (ItemData data : items) {
                ITEMS.putIfAbsent(data.getId(), data);
            }
            
            return ITEMS.get(id);
        }
    }
    
    public static Monster getMonster(int id, Field src) {
        synchronized (MONSTERS) {
            if (MONSTERS.containsKey(id)) {
                return new Monster(MONSTERS.get(id));
            }
            
            Monster base = provider.getMonsterFactory().generate(id);
            
            if (base != null) {
                MONSTERS.put(id, base);
                
                return new Monster(base);
            }
        
            return null;
        }
    }
    

    public static SkillData getSkill(int id) {
        synchronized (SKILLS) {
            if (SKILLS.containsKey(id)) {
                return SKILLS.get(id);
            }
            
            SkillData ret = provider.getSkillFactory().generate(id, false);
            
            if (ret != null) {
                SKILLS.put(id, ret);
            }
            
            return ret;
        }
    }

    public static SkillData getMonsterSkill(int id) {
        synchronized (MONSTER_SKILLS) {
            if (MONSTER_SKILLS.containsKey(id)) {
                return MONSTER_SKILLS.get(id);
            }
            
            SkillData ret = provider.getSkillFactory().generate(id, true);
            
            if (ret != null) {
                MONSTER_SKILLS.put(id, ret);
            }
            
            return ret;
        }
    }

    // XXX Refactor
    public static DropData getDropData(int mob) {
        synchronized (DROPS) {
            if (DROPS.containsKey(mob)) {
                return DROPS.get(mob);
            }
            
            Connection c = GameService.getInstance().getDatabase().getConnection();
            try {
                PreparedStatement ps = c.prepareStatement("SELECT * FROM drops WHERE monster = ?");
                ps.setInt(1, mob);
                
                ResultSet rs = ps.executeQuery();
                
                DropData ret = new DropData(rs);
                
                DROPS.put(mob, ret);
                
                rs.close();
                ps.close();
                
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    c.close();
                } catch (Exception e) {
                }
            }
        }
    }
    
    public static boolean isEquip(int id) {
        return id / 1000000 == 1;
    }
    
    public static Inventory getInventoryForItem(int id, Player p) {
        switch (id / 1000000) {
            case 1:
                return p.getEquipInventory();
            case 2:
                return p.getUseInventory();
            case 3:
                return p.getSetupInventory();
            case 4:
                return p.getEtcInventory();
            case 5:
                return p.getCashInventory();
            default:
                break;
        }
        return null; // B^)
    }
    
}
