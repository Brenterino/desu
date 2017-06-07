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
package data;

import data.skill.Beginner;
import data.skill.Bishop;
import data.skill.BowMaster;
import data.skill.Buccaneer;
import data.skill.Corsair;
import data.skill.DarkKnight;
import data.skill.FPArchMage;
import data.skill.GM;
import data.skill.Hero;
import data.skill.ILArchMage;
import data.skill.Magician;
import data.skill.Marksman;
import data.skill.MobSkill;
import data.skill.NightLord;
import data.skill.Paladin;
import data.skill.Pirate;
import data.skill.Shadower;
import data.skill.Thief;
import field.monster.MonsterEffect;
import field.monster.MonsterEffectStat;
import java.util.HashMap;
import java.util.List;
import player.stats.Effect;
import player.stats.EffectStat;

import static player.stats.EffectStat.*;

/**
 * 
 * @author Brent
 */
public final class SkillData {

    private int skillId;
    private int skillType;
    private int maxLevel = 0;
    private int masterLevel = 0;
    private boolean invisible = false;
    private HashMap<Integer, LevelData> skill;

    private SkillData() {}
    
    public SkillData(int id, int type, int mastery, boolean invis, 
            List<HashMap<String, Integer>> data) {
        skillId = id;
        skillType = type;
        masterLevel = mastery;
        invisible = invis;
        maxLevel = data.size();
        
        skill = new HashMap<>();
        
        for (int i = 0; i < data.size(); i++) {
            skill.put(i + 1, new LevelData(data.get(i)));
        }
    }
    
    public int getMaxLevel() {
        return maxLevel;
    }
    
    public int getMasterLevel() {
        return masterLevel;
    }
    
    public boolean isInvisible() {
        return invisible;
    }

    public int getHPConsumption(int level) {
        return skill.get(level).getProperty("hpCon");
    }

    public int getMPConsumption(int level) {
        return skill.get(level).getProperty("mpCon");
    }

    public int getProperty(int level, String key) {
        return skill.get(level).getProperty(key);
    }

    public Effect generatePlayerBuff(int level) {
        return skill.get(level).createPlayerBuff();
    }
    
    public Effect generatePlayerDebuff(int level) {
        return skill.get(level).createPlayerDebuff();
    }
    
    public MonsterEffect generateMonsterBuff(int level) {
        return skill.get(level).createMonsterBuff();
    }
    
    public MonsterEffect generateMonsterDebuff(int level) {
        return skill.get(level).createMonsterDebuff();
    }

    public class LevelData {

        private Effect buff;
        private Effect debuff;
        private MonsterEffect mobBuff;
        private MonsterEffect mobDebuff;
        
        private int skillLevel;
        public HashMap<String, Integer> prop;

        public LevelData(HashMap<String, Integer> props) {
            prop = props;
        }

        public int getProperty(String key) {
            if (prop.containsKey(key)) {
                return prop.get(key);
            }
            return 0;
        }
        
        public boolean hasProperty(String key) {
            return prop.containsKey(key);
        }
        
        public MonsterEffect createMonsterBuff() {
            if (mobBuff != null) {
                return mobBuff;
            }
            MonsterEffect in = new MonsterEffect(skillId << 24 | skillLevel << 8); // hack for not having to write two shorts
            
            in.setDuration(prop.get("time"));
            
            switch (skillId) {
                case MobSkill.WEAPON_ATTACK:
                case MobSkill.WEAPON_ATTACK_SUPPORT:
                case MobSkill.SPONTANEOUS_WEAPON_ATTACK:
                    applyMobStat(in, "x", MonsterEffectStat.WEAPON_ATTACK_UP);
                    break;
                case MobSkill.WEAPON_DEFENSE:
                case MobSkill.WEAPON_DEFENSE_SUPPORT:
                case MobSkill.SPONTANEOUS_WEAPON_DEFENSE:
                    applyMobStat(in, "x", MonsterEffectStat.WEAPON_DEFENSE_UP);
                    break;
                case MobSkill.MAGIC_ATTACK:
                case MobSkill.MAGIC_ATTACK_SUPPORT:
                case MobSkill.SPONTANEOUS_MAGIC_ATTACK:
                    applyMobStat(in, "x", MonsterEffectStat.MAGIC_ATTACK_UP);
                    break;
                case MobSkill.MAGIC_DEFENSE:
                case MobSkill.MAGIC_DEFENSE_SUPPORT:
                case MobSkill.SPONTANEOUS_MAGIC_DEFENSE:
                    applyMobStat(in, "x", MonsterEffectStat.MAGIC_DEFENSE_UP);
                    break;
                case MobSkill.SPONTANEOUS_SPEED:
                    applyMobStat(in, "x", MonsterEffectStat.SPEED);
                    break;
                case MobSkill.SPONTANEOUS_ACCURACY:
                    applyMobStat(in, "x", MonsterEffectStat.ACCURACY);
                    break;
                case MobSkill.SPONTANEOUS_AVOIDABILITY:
                    applyMobStat(in, "x", MonsterEffectStat.AVOIDABILITY);
                    break;
                case MobSkill.WEAPON_IMMUNE:
                    applyMobStat(in, "x", MonsterEffectStat.WEAPON_IMMUNITY);
                    break;
                case MobSkill.MAGIC_IMMUNE:
                    applyMobStat(in, "x", MonsterEffectStat.MAGIC_IMMUNITY);
                    break;
                default:
                    break;
            }
            
            return mobBuff = in;
        }
        
        public MonsterEffect createMonsterDebuff() {
            if (mobDebuff != null) {
                return mobDebuff;
            }
            MonsterEffect in = new MonsterEffect(skillId);
            
            in.setDuration(prop.get("time"));
            
            switch (skillId) {
                case FPArchMage.SLOW:
                case ILArchMage.SLOW:
                case BowMaster.HAMSTRING:
                    applyMobStat(in, "x", MonsterEffectStat.SPEED);
                    break;
                case Marksman.BLIND:
                    applyMobStat(in, "x", MonsterEffectStat.ACCURACY);
                    break;                
                case Hero.COMA_AXE:
                case Hero.COMA_SWORD:
                case Hero.SHOUT:
                case Paladin.CHARGED_BLOW:
                case BowMaster.ARROW_BOMB_BOW:
                case BowMaster.SILVER_HAWK:
                case Marksman.GOLDEN_EAGLE:
                case Shadower.ASSAULTER:
                case Shadower.BOOMERANG_STEP:
                case Buccaneer.BACKSPIN_BLOW:
                case Buccaneer.DOUBLE_UPPERCUT:
                case Buccaneer.DEMOLITION:
                case Buccaneer.SNATCH:
                case Buccaneer.BARRAGE:
                case Corsair.BLANK_SHOT:
                    applyMobStat(in, "", MonsterEffectStat.STUN, 1);
                    break;
                case FPArchMage.ELQUINES:
                case FPArchMage.PARALYZE:
                case ILArchMage.COLD_BEAM:
                case ILArchMage.ICE_STRIKE:
                case ILArchMage.ELEMENT_COMPOSITION:
                case Marksman.BLIZZARD:
                case Marksman.FROSTPREY:
                case Corsair.ICE_SPLITTER:
                    applyMobStat(in, "", MonsterEffectStat.FREEZE, 1);
                    break;
                case FPArchMage.POISON_BREATH:
                case FPArchMage.ELEMENT_COMPOSITION:
                    applyMobStat(in, "", MonsterEffectStat.POISON, 1);
                    break;
                case FPArchMage.FIRE_DEMON:
                case ILArchMage.ICE_DEMON:
                    applyMobStat(in, "", MonsterEffectStat.POISON, 1);
                    applyMobStat(in, "", MonsterEffectStat.FREEZE, 1);
                    break;
                case Thief.DISORDER:
                case Paladin.THREATEN:
                    applyMobStat(in, "x", MonsterEffectStat.WEAPON_ATTACK);
                    applyMobStat(in, "y", MonsterEffectStat.WEAPON_DEFENSE);
                    break;
                case FPArchMage.SEAL:
                case ILArchMage.SEAL:
                    applyMobStat(in, "", MonsterEffectStat.SEAL);
                    break;
                case Bishop.DOOM:
                    applyMobStat(in, "", MonsterEffectStat.DOOM, 1);
                    break;
                case NightLord.SHADOW_WEB:
                    applyMobStat(in, "", MonsterEffectStat.SHADOW_WEB, 1);
                    break;
                case Corsair.HYPNOTIZE:
                    applyMobStat(in, "", MonsterEffectStat.CONFUSED, 1);
                    break;
                case NightLord.NINJA_AMBUSH:
                case Shadower.NINJA_AMBUSH:
                    applyMobStat(in, "damage", MonsterEffectStat.AMBUSH);
                    break;
                case NightLord.TAUNT:
                case Shadower.TAUNT:
                    applyMobStat(in, "x", MonsterEffectStat.SHOWDOWN);
                    applyMobStat(in, "x", MonsterEffectStat.WEAPON_DEFENSE);
                    applyMobStat(in, "x", MonsterEffectStat.MAGIC_DEFENSE);
                    break;
                    
                default:
                    break;
            }
            return in;
        }
        
        public Effect createPlayerBuff() {
            if (buff != null) {
                return buff;
            }
            Effect in = new Effect(skillId);
            in.setDuration(prop.get("time") * 1000);
            applyStat(in, "pad", WEAPON_ATTACK);
            applyStat(in, "pdd", WEAPON_DEFENSE);
            applyStat(in, "mad", MAGIC_ATTACK);
            applyStat(in, "mdd", MAGIC_DEFENSE);
            applyStat(in, "acc", ACCURACY);
            applyStat(in, "eva", AVOIDABILITY);
            applyStat(in, "speed", SPEED);
            applyStat(in, "jump", JUMP);
            if (skillType == 2) { // booster skills
                in.addStatup(BOOSTER, prop.get("x"));
                return buff = in;
            }
            if (prop.containsKey("morph")) { // morph skills
                applyStat(in, "morph", MORPH);
                return buff = in;
            }
            switch (skillId) {
                case Buccaneer.ENERGY_CHARGE:
                    applyStat(in, "x", ENERGY_CHARGE);
                    break;
                case Corsair.WINGS:
                    applyStat(in, "x", WINGS);
                    break;
                case Pirate.DASH:
                    applyStat(in, "x", DASH_SPEED);
                    applyStat(in, "y", DASH_JUMP);
                    break;
                case Corsair.BULLSEYE:
                case Corsair.HOMING_BEACON:
                    applyStat(in, "x", HOMING_BEACON);
                    break;
                case Beginner.MONSTER_RIDER:
                    applyStat(in, "", MONSTER_RIDING, skillId);
                    break;
                case Buccaneer.SPEED_INFUSION:
                case Corsair.SPEED_INFUSION:
                    applyStat(in, "x", SPEED_INFUSION);
                    break;
                case Beginner.RECOVERY:
                    applyStat(in, "x", RECOVERY);
                    break;
                case Bishop.MAPLE_WARRIOR:
                case BowMaster.MAPLE_WARRIOR:
                case Buccaneer.MAPLE_WARRIOR:
                case Corsair.MAPLE_WARRIOR:
                case DarkKnight.MAPLE_WARRIOR:
                case ILArchMage.MAPLE_WARRIOR:
                case Hero.MAPLE_WARRIOR:
                case FPArchMage.MAPLE_WARRIOR:
                case Marksman.MAPLE_WARRIOR:
                case NightLord.MAPLE_WARRIOR:
                case Paladin.MAPLE_WARRIOR:
                case Shadower.MAPLE_WARRIOR:
                    applyStat(in, "x", MAPLE_WARRIOR);
                    break;
                case DarkKnight.POWER_STANCE:
                case Hero.POWER_STANCE:
                case Paladin.POWER_STANCE:
                    applyStat(in, "iprop", STANCE);
                    break;
                case BowMaster.SHARP_EYES:
                case Marksman.SHARP_EYES:
                    applyStat(in, "", SHARP_EYES, getProperty("x") << 8 | getProperty("y"));
                    break;
                case Bishop.MANA_REFLECTION:
                case ILArchMage.MANA_REFLECTION:
                case FPArchMage.MANA_REFLECTION:
                    applyStat(in, "", MANA_REFLECTION, 1);
                    break;
                case NightLord.SHADOW_STARS:
                    applyStat(in, "", SHADOW_CLAW, 0);
                    break;
                case Bishop.INFINITY:
                case ILArchMage.INFINITY:
                case FPArchMage.INFINITY:
                    applyStat(in, "x", INFINITY);
                    break;
                case Bishop.HOLY_SHIELD:
                    applyStat(in, "x", HOLY_SHIELD);
                    break;
                case BowMaster.HAMSTRING:
                    applyStat(in, "x", HAMSTRING);
                    break;
                case Marksman.BLIND:
                    applyStat(in, "x", BLIND);
                    break;
                case BowMaster.CONCENTRATE:
                    applyStat(in, "x", CONCENTRATE);
                    break;
                case Beginner.ECHO_OF_HERO:
                    applyStat(in, "x", ECHO_OF_HERO);
                    break;
                case Magician.MAGIC_GUARD:
                    applyStat(in, "x", MAGIC_GUARD);
                    break;
                case Thief.DARK_SIGHT:
                    applyStat(in, "x", DARK_SIGHT);
                    break;
                case Hero.POWER_GUARD:
                case Paladin.POWER_GUARD:
                    applyStat(in, "x", POWER_GUARD);
                    break;
                case DarkKnight.HYPER_BODY:
                case GM.HYPER_BODY:
                    applyStat(in, "x", HYPER_BODY_HP);
                    applyStat(in, "y", HYPER_BODY_MP);
                    break;
                case Bishop.INVINCIBLE:
                    applyStat(in, "x", INVINCIBLE);
                    break;
                case BowMaster.SOUL_ARROW_BOW:
                case Marksman.SOUL_ARROW_CROSSBOW:
                    applyStat(in, "x", SOUL_ARROW);
                    break;
                case Hero.COMBO_ATTACK:
                    applyStat(in, "", COMBO, 1);
                    break;
                case Paladin.BLIZZARD_CHARGE_BW:
                case Paladin.DIVINE_CHARGE_BW:
                case Paladin.FIRE_CHARGE_SWORD:
                case Paladin.FLAME_CHARGE_BW:
                case Paladin.HOLY_CHARGE_SWORD:
                case Paladin.ICE_CHARGE_SWORD:
                case Paladin.LIGHTNING_CHARGE_BW:
                case Paladin.THUNDER_CHARGE_SWORD:
                    applyStat(in, "x", CHARGE);
                    break;
                case DarkKnight.DRAGON_BLOOD:
                    applyStat(in, "x", DRAGON_BLOOD);
                    break;
                case Bishop.HOLY_SYMBOL:
                case GM.HOLY_SYMBOL:
                    applyStat(in, "x", HOLY_SYMBOL);
                    break;
                case NightLord.MESO_UP:
                    applyStat(in, "x", MESO_UP);
                    break;
                case NightLord.SHADOW_PARTNER:
                    applyStat(in, "x", SHADOW_PARTNER);
                    break;
                case Shadower.PICKPOCKET:
                    applyStat(in, "x", PICK_POCKET);
                    break;
                case Shadower.MESO_GUARD:
                    applyStat(in, "x", MESO_GUARD);
                    break;
                default:
                    break;
            }
            return (buff = in);
        }
        
        public Effect createPlayerDebuff() {
            if (debuff != null) {
                return debuff;
            }
            Effect in = new Effect(skillId << 24 | skillLevel << 8); // hack for not having to write two shorts
            in.setDuration(prop.get("time"));
         
            switch (skillId) {
                case MobSkill.SEAL:
                    applyStat(in, "", SEAL, 1);
                    break;
                case MobSkill.DARKNESS:
                    applyStat(in, "", DARKNESS, 1);
                    break;
                case MobSkill.WEAKEN:
                    applyStat(in, "", WEAKEN, 1);
                    break;
                case MobSkill.STUN:
                    applyStat(in, "", STUN, 1);
                    break;
                case MobSkill.CURSE:
                    applyStat(in, "", CURSE, 1);
                    break;
                case MobSkill.POISON:
                    applyStat(in, "x", POISON);
                    break;
                case MobSkill.SLOW:
                    applyStat(in, "x", SLOW);
                    break;
                case MobSkill.SEDUCE:
                    applyStat(in, "x", SEDUCE);
                    break;
                case MobSkill.BANISH:
                    applyStat(in, "", BANISH, 1);
                    break;
                case MobSkill.INVERSE_CONTROLS: // both 1 in data
                    applyStat(in, "x", INVERSE_CONTROLS);
                    break;
                default:
                    break;
            }
            return in;
        }

        private void applyStat(Effect mb, String key, EffectStat bs) {
            applyStat(mb, key, bs, -1);
        }

        private void applyStat(Effect mb, String key, EffectStat bs, int ov) {
            if (ov != -1) {
                mb.addStatup(bs, ov);
                return;
            }
            if (prop.containsKey(key)) {
                int val = prop.get(key);
                if (val != 0) {
                    mb.addStatup(bs, prop.get(key));
                }
            }
        }
        
        private void applyMobStat(MonsterEffect mb, String key, MonsterEffectStat bs) {
            applyMobStat(mb, key, bs, -1);
        }        
        
        private void applyMobStat(MonsterEffect mb, String key, MonsterEffectStat bs, int ov) {
            if (ov != -1) {
                mb.addStatup(bs, ov);
                return;
            }
            if (prop.containsKey(key)) {
                int val = prop.get(key);
                if (val != 0) {
                    mb.addStatup(bs, prop.get(key));
                }
            }
        }
    }
}
