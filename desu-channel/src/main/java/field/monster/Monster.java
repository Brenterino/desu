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
package field.monster;

import client.Client;
import client.packet.PacketCreator;
import field.FieldLife;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import player.Player;

/**
 *
 * @author Brent
 */
public class Monster extends FieldLife {

    private int id;
    private int fh;
    private int exp;
    private int level;
    private int link;
    private Point pos;
    private int HP, MP;
    private int lHP, lMP;
    private int mobType;
    private int stance = 5;
    private boolean boss = false;
    private boolean aggro = false;
    private boolean undead = false;
    private boolean publicReward = false;
    private boolean explosiveReward = false;
    private List<Skill> skills = new LinkedList<>(); // XXX check to see if skills have multiples, if so we need to go by id and level
    private List<Cooldown> cooldowns = new LinkedList<>();
    private final HashMap<Integer, AtomicInteger> takenDamage = new HashMap<>();

    public class Skill {
        
        public int id;
        public int level;
    }
    
    public class Cooldown {

        public int id;
        public int level;
        public long start;
        public long duration;

        public long remaining() {
            return duration - System.currentTimeMillis() - start;
        }
    }

    private Monster() {
    }

    public Monster(Monster gen) {
        id = gen.id;
        exp = gen.exp;
        undead = gen.undead;
        level = gen.level;
        link = gen.link;
        HP = gen.HP;
        MP = gen.MP;
        lHP = HP;
        lMP = MP;
        mobType = gen.mobType;
        skills = gen.skills;
        publicReward = gen.publicReward;
        explosiveReward = gen.explosiveReward;
    }

    public Monster(int id, int exp, int level, int link,
            int HP, int MP, int mobType, boolean undead,
            boolean publicReward, boolean explosiveReward) {
        this.id = id;
        this.exp = exp;
        this.level = level;
        this.link = link;
        this.HP = HP;
        this.MP = MP;
        this.mobType = mobType;
        this.undead = undead;
        this.publicReward = publicReward;
        this.explosiveReward = explosiveReward;
    }

    public int getExperience() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public int getLink() {
        return link;
    }

    public void applyMPChange(int val) {
        lMP += val;
    }

    public synchronized void damage(Player src, int damage) {
        if (!isAlive()) { // as a result of locking
            return;
        }
        int trueDamage = Math.min(lHP, damage);

        lHP -= trueDamage;

        if (takenDamage.containsKey(src.getId())) { // synchronized around this got handled
            takenDamage.get(src.getId()).addAndGet(trueDamage);
        } else {
            takenDamage.put(src.getId(), new AtomicInteger(trueDamage));
        }

        aggro = true;

        // XXX may want to send HP bar packet here, but idk
        if (lHP <= 0) {
            if (getController() != NO_CONTROLLER) {
                src.getField().killMonster(this, src.getId(), takenDamage);
                takenDamage.clear();
                // XXX if we want to do something special with the person to score the killing blow, do it here
                return;
            }
        }

        if (!boss) {
            src.getClient().write(PacketCreator.showMonsterHP(getOid(), Math.max(getRemainingHealthRatio(), 1)));
        } else {
            // XXX note: boss HP bar
        }
    }

    public int getDamageDealtBy(int id) {
        if (takenDamage.containsKey(id)) {
            return takenDamage.get(id).get();
        }
        return 0;
    }

    public boolean isAlive() {
        return lHP > 0;
    }

    public boolean isBoss() {
        return boss;
    }

    public int getId() {
        return id;
    }

    public int getHP() {
        return lHP;
    }

    public int getRemainingHealthRatio() {
        return (int) Math.ceil(lHP * 100 / HP);
    }

    public int getMP() {
        return lMP;
    }

    public int getMaxHP() {
        return HP;
    }

    public int getMaxMP() {
        return MP;
    }

    public int getMobType() {
        return mobType;
    }

    public int getDropOwnership() {
        return explosiveReward ? 3 : publicReward ? 2 : 0;
    }
    
    public void setFh(int fh) {
        this.fh = fh;
    }

    @Override
    public int getFh() {
        return fh;
    }

    @Override
    public boolean isHidden() {
        return false; // mobs are never "hidden"
    }

    @Override
    public void toggleHidden() {
    }

    @Override
    public Point getPosition() {
        return pos;
    }

    @Override
    public void setPosition(Point p) {
        pos = p;
    }

    @Override
    public int getStance() {
        return stance;
    }

    @Override
    public void setStance(int s) {
        stance = s;
    }

    @Override
    public void sendSpawnData(Client c) {
        c.write(PacketCreator.spawnMonster(this, !boss)); // XXX idk lel
    }

    @Override
    public void sendDestroyData(Client c, boolean spc) {
        c.write(PacketCreator.removeMonster(getOid(), spc)); // destroy data -> remove ani, hmmm idk if this will cause d/c if true
    }

    @Override
    public void sendControlLoss(Client mc) {
        mc.write(PacketCreator.removeMonsterControl(this));
    }

    @Override
    public void sendControlGain(Client mc) {
        mc.write(PacketCreator.giveMonsterControl(this, false, false));
    }

    @Override
    public Type getObjectType() {
        return Type.MONSTER;
    }

    public boolean isUndead() { // XXX add undead check
        return undead;
    }

    public void triggerAggro() {
        aggro = true;
    }

    public boolean isAggressive() {
        return aggro;
    }
    
    public void addSkill(int skill, int level) {
        Skill s = new Skill();
        
        s.id = skill;
        s.level = level;
        
        skills.add(s);
    }

    public boolean hasSkills() {
        return !skills.isEmpty();
    }

    public int getSkillCount() {
        return skills.size();
    }

    public Skill getRandomSkill() {
        return skills.get(new Random().nextInt(skills.size()));
    }
    
    public Skill getSkill(int id, int level) {
        return skills.stream()
                .filter(s -> s.id == id)
                .filter(s -> s.level == level)
                .findAny()
                .orElse(null);
    }

    public boolean hasSkill(int id, int level) {
        return getSkill(id, level) != null;
    }

    public Cooldown getCooldown(int id, int level) {
        return cooldowns.stream()
                 .filter(c -> c.id == id)
                 .filter(c -> c.level == level)
                 .findAny()
                 .orElse(null);
    }

    public boolean canUseSkill(int id, int level, int healthThreshold, int mpCost) {
        Cooldown c = getCooldown(id, level);
        if (healthThreshold <= getRemainingHealthRatio()) {
            if (lMP > mpCost) {
                if (c != null && c.remaining() <= 0) {
                    cooldowns.remove(c);
                    return true;
                }
            }
        }
        return false;
    }
}
