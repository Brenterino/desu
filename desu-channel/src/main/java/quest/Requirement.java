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
package quest;

import data.internal.Constants;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import player.Player;
import static player.stats.Stat.*;
import wz.WzObject;
import wz.common.WzDataTool;

/**
 *
 * @author Brent
 */
public enum Requirement {

    // PRE
    LVMIN,
    START,
    TAMINGMOBLEVELMIN,
    WORLDMIN, // useless check
    NORMAL_AUTO_START,
    FIELD_ENTER,
    DAY_BY_DAY,
    POP,
    LVMAX,
    SKILL,
    END,
    INTERVAL,
    WORLD_MAX, // useless check
    JOB,
    STARTSCRIPT,
    
    // POST
    ENDSCRIPT,
    MOB,
    ENDMESO,
    INFO_NUMBER,
    INFO,
    
    // PRE + POST
    NPC,
    ITEM,
    PET_RECALL_LIMIT, // idek
    PET_AUTO_SPEAKING_LIMIT, // idek
    QUEST,
    PETTAMENESSMIN,
    PET,
    ;

    @FunctionalInterface
    public static interface Check {

        public boolean check(Player p);
    }

    public static Check compileRequirement(int qid, Requirement req, WzObject<?, ?> src) {
        Check ret = p -> false; // if something is wrong, then we won't allow it to work
        switch (req) {
            case NPC:
                int npcID = WzDataTool.getInteger(src, "npc", 0);
                ret = p -> p.getField().hasNPC(npcID); // in the future, Nexon trolls me
                break;
            case LVMIN:
                int minLV = WzDataTool.getInteger(src, "lvmin", 1);
                ret = p -> p.getStat(LEVEL) >= minLV;
                break;
            case LVMAX:
                int maxLV = WzDataTool.getInteger(src, "lvmax", Constants.MAX_LEVEL);
                ret = p -> p.getStat(LEVEL) <= maxLV;
                break;
            case ITEM:
            case MOB:
                boolean item = req == ITEM;
                WzObject<?, ?> root = src.getChild(item ? "item" : "mob");
                if (root != null) {
                    Map<Integer, Integer> map = new HashMap<>();
                    for (WzObject<?, ?> child : root) {
                        map.put(WzDataTool.getInteger(child, "id", 0), 
                                WzDataTool.getInteger(child, "count", 1));
                    }
                    ret = p -> {
                        for (Entry<Integer, Integer> child : map.entrySet()) {
                            if (item) {
                                if (p.getItemCount(child.getKey()) < child.getValue()) {
                                    return false;
                                }
                            } else {
                                if (p.getQuestKillCount(child.getKey()) < child.getValue()) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    };
                } else {
                    ret = p -> true;
                }
                break;
            case START:
            case END:
                boolean start = req.equals(START);
                String day = WzDataTool.getString(src,
                        start ? "start" : "end",
                        start ? "2000010100" : "2100010100"); // if not updated, will become invalid in 2100 B^)
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime then = LocalDateTime.parse(day, DateTimeFormatter.ofPattern("yyyyMMddhh"));
                ret = p -> start ? now.isAfter(then) : now.isBefore(then);
                break;
            case QUEST:
                WzObject<?, ?> questRoot = src.getChild("quest");
                if (questRoot != null) {
                    Map<Integer, Integer> map = new HashMap<>();
                    for (WzObject<?, ?> quest : questRoot) {
                        map.put(WzDataTool.getInteger(quest, "id", 0),
                                WzDataTool.getInteger(quest, "state", 1));
                    }
                    ret = p -> {
                        for (Entry<Integer, Integer> quest : map.entrySet()) {
                            if (p.getQuestState(quest.getKey()) != quest.getValue()) {
                                return false;
                            }
                        }
                        return true;
                    };
                } else {
                    ret = p -> true;
                }
                break;
            case SKILL:
                WzObject<?, ?> skillRoot = src.getChild("skill");
                if (skillRoot != null) {
                    Map<Integer, Boolean> smap = new HashMap<>();
                    for (WzObject<?, ?> skill : skillRoot) {
                        smap.put(WzDataTool.getInteger(skill, "id", 0),
                                WzDataTool.getBoolean(skill, "acquire", false));
                    }
                    ret = p -> {
                        for (Entry<Integer, Boolean> skill : smap.entrySet()) {
                            if (p.hasSkill(skill.getKey()) != skill.getValue()) {
                                return false;
                            }
                        }
                        return true;
                    };
                } else {
                    ret = p -> true;
                }
                break;
            case PET:
                WzObject<?, ?> petRoot = src.getChild("pet");
                if (petRoot != null) {
                    List<Integer> pets = new LinkedList<>();
                    for (WzObject<?, ?> pet : petRoot) {
                        pets.add(WzDataTool.getInteger(pet, "id", 0));
                    }
                    // XXX check for active pet vs. owned pet (?)
                } else {
                    ret = p -> true;
                }
                break;
            case PETTAMENESSMIN:
                int petTMin = WzDataTool.getInteger(src, "pettamenessmin", 0);
                // XXX handle this by creating a way to get the required pet
                ret = p -> petTMin == 0;
                break;
            case TAMINGMOBLEVELMIN:
                int tmMinLV = WzDataTool.getInteger(src, "tamingmoblevelmin", 0);
                // XXX handle this by creating a way to get the current tamed mob's minimum level
                ret = p -> tmMinLV == 0;
                break;
            case DAY_BY_DAY:
                boolean daily = WzDataTool.getBoolean(src, "dayByDay", false);
                if (daily) {
                    ret = p -> {
                        if (p.getQuestState(qid) != Quest.QUEST_COMPLETE) { // 2 = quest completed
                            return true;
                        }
                        long lastComplete = p.getCompletedQuests().get(qid).completionTime;

                        LocalDateTime currentTime = LocalDateTime.now();
                        LocalDateTime lastCompleted = LocalDateTime.ofEpochSecond(lastComplete, 0, ZoneOffset.UTC);

                        int yearDifference = currentTime.getYear() - lastCompleted.getYear();
                        int dayDifference = currentTime.getDayOfYear() - lastCompleted.getDayOfYear();

                        return dayDifference >= 1 || yearDifference > 0;
                    };
                } else {
                    ret = p -> true;
                }
                break;
            case INTERVAL:
                int interval = WzDataTool.getInteger(src, "interval", -1); // in minutes
                // XXX handle this since I'm lazy :L
                ret = p -> interval == -1;
                break;
            case ENDMESO:
                int amount = WzDataTool.getInteger(src, "endmeso", 0);
                ret = p -> p.getStat(MESOS) >= amount;
                break;
            case POP:
                int pop = WzDataTool.getInteger(src, "pop", Short.MIN_VALUE);
                ret = p -> p.getStat(FAME) >= pop;
                break;
            // XXX start/end scripts
            // XXX field enter checks (idk)
            default:
                break;
        }
        return ret;
    }
}
