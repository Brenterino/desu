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
package player.stats;

/**
 *
 * @author Brent
 */
public enum Job {

    BEGINNER(0, 6),
    
    WARRIOR(100, 61),
    FIGHTER(110, 121),
    CRUSADER(111, 151),
    HERO(112, 243),
    PAGE(120, 121),
    WHITE_KNIGHT(121, 151),
    PALADIN(122, 243),
    SPEARMAN(130, 121),
    DRAGON_KNIGHT(131, 151),
    DARK_KNIGHT(132, 243),
    
    MAGICIAN(200, 67),
    FP_WIZARD(210, 121),
    FP_MAGE(211, 151),
    FP_ARCH_MAGE(212, 243),
    IL_WIZARD(220, 121),
    IL_MAGE(221, 151),
    IL_ARCH_MAGE(222, 243),
    CLERIC(230, 121),
    PRIEST(231, 151),
    BISHOP(232, 241),
    
    BOWMAN(300, 61),
    HUNTER(310, 121),
    RANGER(311, 151),
    BOW_MASTER(312, 243),
    CROSSBOWMAN(320, 121),
    SNIPER(321, 151),
    MARKSMAN(322, 243),
    
    THIEF(400, 61),
    ASSASSIN(410, 121),
    HERMIT(411, 151),
    NIGHT_LORD(412, 243),
    BANDIT(420, 121),
    CHIEF_BANDIT(421, 151),
    SHADOWER(422, 241),
    
    PIRATE(500, 61),
    BRAWLER(510, 121),
    MARAUDER(511, 151),
    BUCCANEER(512, 243),
    GUNSLINGER(520, 121),
    OUTLAW(521, 151),
    CORSAIR(522, 243),
    
    GM(900),
    SUPER_GM(910),
    
    UNKNOWN(0xFFFFFFFF);
    
    private final int id;
    private final int sp;

    private Job(int id) {
        this(id, -1);
    }

    private Job(int id, int sp) {
        this.id = id;
        this.sp = sp;
    }

    public int getId() {
        return id;
    }
    
    public int getClassPrefix() {
        return id / 100;
    }

    public static Job getById(int id) {
        for (Job j : values()) {
            if (j.getId() == id) {
                return j;
            }
        }
        return UNKNOWN;
    }

    public int getSP() {
        return sp;
    }

    public boolean isGM() {
        return id >= 800 && id <= 910;
    }

    public boolean canHaveSkill(int skillId) {
        int classPrefix = skillId / 1000000;
        int job = skillId / 10000;
        
        return classPrefix == 0 
                || (classPrefix % 10 == 0 && classPrefix / 10 == getClassPrefix() / 10)
                || (getClassPrefix() == classPrefix && getId() >= job);
    }
}