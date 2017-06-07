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

import java.util.EnumMap;
import quest.Requirement.Check;
import wz.WzObject;

/**
 *
 * @author Brent
 */
public class Quest {

    private int id;
//    private int startingNPC;
//    private int endingNPC;
//    private int levelMinimum = 0; // lvmin
//    private int levelMaximum = 200; // lvmax
//    private int fameMinimum = 0;
//    private int interval = 0; // interval; delay between completion and next acceptance in minutes
//    private boolean repeatable = false; // interval in starting conditions = 0
//    private String startScript; // startscript
//    private String endScript; // endscript
//    private String startingDate; // first date to be able to do this quest
//    private String endingDate; // last date to be able to do this quest
    // XXX timer is in the questinfo.img file
    // XXX timeLimit is within QuestInfo.img
//    private Map<Integer, Integer> monsterTrack = new HashMap<>();
    private EnumMap<Requirement, Check> startReq = new EnumMap<>(Requirement.class);
    private EnumMap<Requirement, Check> endReq = new EnumMap<>(Requirement.class);
//    private EnumMap<Requirement, List<Condition>> finishReq = new EnumMap<>(Requirement.class);
    public static final int QUEST_AVAILABLE = 0;
    public static final int QUEST_IN_PROGRESS = 1;
    public static final int QUEST_COMPLETE = 2;

    private Quest() {
    }

    public Quest(int qid, WzObject<?, ?> check, WzObject<?, ?> act) {
        id = qid;
        parse(check, act);
    }

    private void parse(WzObject<?, ?> check, WzObject<?, ?> act) {
        WzObject<?, ?> check0 = check.getChild("0");
        // XXX code quest stuff B^)
        WzObject<?, ?> check1 = check.getChild("1");
    }
}
