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

import player.item.Item;

/**
 *
 * @author Brent
 */
public class ItemData {
    
    private int id;
    
    // INFO
    
    private int price;
    private int unitPrice;
    
    private int slotMax; // how much you can have in one slot, 1 is default
    
    private boolean cash;
    
    private boolean pachinko;
    
    private int meso; // meso gain from meso bags (off by a factor I think)
    
    private int life; // how many days of life an item gives to a pet
    
    private int rate; // how much of a rate modifier is attached
    
    private int npc; // npc to open
    
    private boolean soldInform; // special property that says if an item is sold to notify the user
    
    private boolean monsterBook; // 1 = monster book card
    
    private int type; // not sure
    private int mcType; // type of potion in Monster Carnival
    
    private int mob; // mob capture id
    private int mobHP; // minimum health to capture
    private int bridleProp; // no idea
    private int bridlePropChg; // no idea
    private int bridlgeMsgType; // again, no idea
    private int useDelay; // delay between usages (?)
    private int pquest; // party quest (?)
    
    private int maxLevel; // seems to only be relevant for Soloman scrolls
    
    private int create; // create something on item usage
    
    private boolean quest;
    private int questId;
    
    private int exp; // for special events, this isn't player EXP
    
    private String path; // effect path
    private String bgmPath; // background music path on item use
    private String effect; // effect path (Effect.img)
    private boolean isBgmOrEffect; // does the item trigger a Bgm or effect
    private int stateChangeItem; // field effect buffs use this to apply the real buff
    
    private boolean showMessage; // whether or not to show a message for this item
    
    private boolean only; // can only have one
    private boolean notSale; // cannot be sold
    private boolean pickUpBlock; // cannot loot
    private boolean tradBlock; // 1 = true, 0 = false
    private boolean tradeBlock; // 1 = true, 0 = false
    private boolean expireOnLogout; // 1 = true, 0 = false
    private boolean timeLimited;
    private int time; // duration of effect or specific effects of time
    
    private int recoveryHP;
    private int recoveryMP;
    
    private int reqSkillLevel; // required skill level to use mastery book
    private int skill; // technically a list of skills, but why? "skill/0"
    private int masterLevel; // the new mastery level if the book passes
    
    private int warmsupport; // for the DoT in snow environment scroll
    private int preventslip; // for slipping on icy environments
    
    private int success; // rate of doing something positive
    private int cursed; // rate of doing something negative
    
    private boolean randstat; // basically is chaos scroll
    private boolean recover; // basically is clean slate
    private int incMHP;
    private int incMMP;
    private int incSTR;
    private int incDEX;
    private int incINT;
    private int incLUK;
    private int incPAD;
    private int incMAD;
    private int incPDD;
    private int incMDD;
    private int incACC;
    private int incEVA;
    private int incSpeed;
    private int incJump;
    
    // END INFO
    
    // SPEC
    private int spec_time; // buff time
    private boolean consumeOnPickup;
    
    private int[] con; // condition boundaries for monster cards... really silly so I'll probably neglect it
    private int itemCode; // boost item drop from monster book, really stupid sometimes
    private int itemupbyitem; // boost to item drop rate from monster book
    private int mesoupbyitem; // boost to meso drop rate from monster book
    private int itemRange; // not sure what this does tbh
    private int prob; // probability bonus for drops (30 = 1.30x)
    
    private boolean respectFS; // removes DoT received from certain maps-side
    private boolean respectPimmune; // able to break through physical damage negation (?)
    private boolean respectMimmune; // able to break through magical damage negation (?)
    private int defenseState; // adds resistance to curses (and maybe other debuffs)
    private int defenseAtt; // defense bonus against poison damage
    
    private int morph; // morpherino
    private boolean ghost; // ghost buff... yep
    
    private int moveTo; // target map, 9999999 = return to nearest town
    private int returnMapQR; // stored return map based on quest
    
    // Event potions
    private int nuffSkill; // remove a buff from an opposing member's party
    private boolean party; // is a party-effecting potion
    private int cp; // gain CP from looting
    
    private boolean barrier; // is a barrier buff (probably don't need to care about this)
    private int spec_mob; // protection against a mob, "spec/mob/0", this might just be handled client-sided
    
    private int thaw; // negation of environment damage
    
    private boolean curse; // cures curse
    private boolean darkness; // cures darkness
    private boolean poison; // cures poison
    private boolean seal; // cures seal
    private boolean weakness; // cures weakness
    
    private int[] pets; // pets that can use pet food.. yeah Nexon is pretty funny
    private int inc; // increase fullness
    
    private int incFatigue; // (actually decreases fatigue for Revitalizer)
    
    private int hp; // flat hp recovery gain
    private int mp; // flat mp recovery gain
    
    private int hpR; // scaled hp recovery
    private int mpR; // scaled mana recovery
    
    private int pad; // physical attack damage
    private int mad; // magic attack damage
    private int pdd; // physical dfense
    private int mdd; // magic defense
    
    private int acc; // accuracy gain
    private int eva; // avoid gain
    
    private int speed; // increase speed
    private int jump; // increase jump
    // END SPEC
    
    private ItemData() {
    }

    public ItemData(int id, int price, int unitPrice, int slotMax, boolean cash, 
            boolean pachinko, int meso, int life, int rate, int npc, 
            boolean soldInform, boolean monsterBook, int type, int mcType, 
            int mob, int mobHP, int bridleProp, int bridlePropChg, 
            int bridlgeMsgType, int useDelay, int pquest, int maxLevel, 
            int create, boolean quest, int questId, int exp, String path, 
            String bgmPath, String effect, boolean isBgmOrEffect, 
            int stateChangeItem, boolean showMessage, boolean only, 
            boolean notSale, boolean pickUpBlock, boolean tradBlock, 
            boolean tradeBlock, boolean expireOnLogout, boolean timeLimited, 
            int time, int recoveryHP, int recoveryMP, int reqSkillLevel, 
            int skill, int masterLevel, int warmsupport, int preventslip, 
            int success, int cursed, boolean randstat, boolean recover, 
            int incMHP, int incMMP, int incSTR, int incDEX, int incINT, 
            int incLUK, int incPAD, int incMAD, int incPDD, int incMDD, 
            int incACC, int incEVA, int incSpeed, int incJump, int spec_time, 
            boolean consumeOnPickup, int[] con, int itemCode, int itemupbyitem, 
            int mesoupbyitem, int itemRange, int prob, boolean respectFS, 
            boolean respectPimmune, boolean respectMimmune, int defenseState, 
            int defenseAtt, int morph, boolean ghost, int moveTo, 
            int returnMapQR, int nuffSkill, boolean party, int cp, 
            boolean barrier, int spec_mob, int thaw, boolean curse, 
            boolean darkness, boolean poison, boolean seal, boolean weakness, 
            int[] pets, int inc, int incFatigue, int hp, int mp, int hpR, 
            int mpR, int pad, int mad, int pdd, int mdd, int acc, int eva, 
            int speed, int jump) 
    {
        this.id = id;
        this.price = price;
        this.unitPrice = unitPrice;
        this.slotMax = slotMax;
        this.cash = cash;
        this.pachinko = pachinko;
        this.meso = meso;
        this.life = life;
        this.rate = rate;
        this.npc = npc;
        this.soldInform = soldInform;
        this.monsterBook = monsterBook;
        this.type = type;
        this.mcType = mcType;
        this.mob = mob;
        this.mobHP = mobHP;
        this.bridleProp = bridleProp;
        this.bridlePropChg = bridlePropChg;
        this.bridlgeMsgType = bridlgeMsgType;
        this.useDelay = useDelay;
        this.pquest = pquest;
        this.maxLevel = maxLevel;
        this.create = create;
        this.quest = quest;
        this.questId = questId;
        this.exp = exp;
        this.path = path;
        this.bgmPath = bgmPath;
        this.effect = effect;
        this.isBgmOrEffect = isBgmOrEffect;
        this.stateChangeItem = stateChangeItem;
        this.showMessage = showMessage;
        this.only = only;
        this.notSale = notSale;
        this.pickUpBlock = pickUpBlock;
        this.tradBlock = tradBlock;
        this.tradeBlock = tradeBlock;
        this.expireOnLogout = expireOnLogout;
        this.timeLimited = timeLimited;
        this.time = time;
        this.recoveryHP = recoveryHP;
        this.recoveryMP = recoveryMP;
        this.reqSkillLevel = reqSkillLevel;
        this.skill = skill;
        this.masterLevel = masterLevel;
        this.warmsupport = warmsupport;
        this.preventslip = preventslip;
        this.success = success;
        this.cursed = cursed;
        this.randstat = randstat;
        this.recover = recover;
        this.incMHP = incMHP;
        this.incMMP = incMMP;
        this.incSTR = incSTR;
        this.incDEX = incDEX;
        this.incINT = incINT;
        this.incLUK = incLUK;
        this.incPAD = incPAD;
        this.incMAD = incMAD;
        this.incPDD = incPDD;
        this.incMDD = incMDD;
        this.incACC = incACC;
        this.incEVA = incEVA;
        this.incSpeed = incSpeed;
        this.incJump = incJump;
        this.spec_time = spec_time;
        this.consumeOnPickup = consumeOnPickup;
        this.con = con;
        this.itemCode = itemCode;
        this.itemupbyitem = itemupbyitem;
        this.mesoupbyitem = mesoupbyitem;
        this.itemRange = itemRange;
        this.prob = prob;
        this.respectFS = respectFS;
        this.respectPimmune = respectPimmune;
        this.respectMimmune = respectMimmune;
        this.defenseState = defenseState;
        this.defenseAtt = defenseAtt;
        this.morph = morph;
        this.ghost = ghost;
        this.moveTo = moveTo;
        this.returnMapQR = returnMapQR;
        this.nuffSkill = nuffSkill;
        this.party = party;
        this.cp = cp;
        this.barrier = barrier;
        this.spec_mob = spec_mob;
        this.thaw = thaw;
        this.curse = curse;
        this.darkness = darkness;
        this.poison = poison;
        this.seal = seal;
        this.weakness = weakness;
        this.pets = pets;
        this.inc = inc;
        this.incFatigue = incFatigue;
        this.hp = hp;
        this.mp = mp;
        this.hpR = hpR;
        this.mpR = mpR;
        this.pad = pad;
        this.mad = mad;
        this.pdd = pdd;
        this.mdd = mdd;
        this.acc = acc;
        this.eva = eva;
        this.speed = speed;
        this.jump = jump;
    }

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public int getSlotMax() {
        return slotMax;
    }

    public boolean isCash() {
        return cash;
    }

    public boolean isPachinko() {
        return pachinko;
    }

    public int getMeso() {
        return meso;
    }

    public int getLife() {
        return life;
    }

    public int getRate() {
        return rate;
    }

    public int getNpc() {
        return npc;
    }

    public boolean isSoldInform() {
        return soldInform;
    }

    public boolean isMonsterBook() {
        return monsterBook;
    }

    public int getType() {
        return type;
    }

    public int getMCType() {
        return mcType;
    }

    public int getMob() {
        return mob;
    }

    public int getMobHP() {
        return mobHP;
    }

    public int getBridleProp() {
        return bridleProp;
    }

    public int getBridlePropChg() {
        return bridlePropChg;
    }

    public int getBridlgeMsgType() {
        return bridlgeMsgType;
    }

    public int getUseDelay() {
        return useDelay;
    }

    public int getPartyQuest() {
        return pquest;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getCreate() {
        return create;
    }

    public boolean isQuestItem() {
        return quest;
    }

    public int getQuestId() {
        return questId;
    }

    public int getExp() {
        return exp;
    }

    public String getPath() {
        return path;
    }

    public String getBgmPath() {
        return bgmPath;
    }

    public String getEffect() {
        return effect;
    }

    public boolean isIsBgmOrEffect() {
        return isBgmOrEffect;
    }

    public int getStateChangeItem() {
        return stateChangeItem;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public boolean isOnly() {
        return only;
    }

    public boolean isNotSale() {
        return notSale;
    }

    public boolean isPickUpBlock() {
        return pickUpBlock;
    }

    public boolean isTradeBlock() {
        return tradeBlock || tradBlock;
    }

    public boolean isExpireOnLogout() {
        return expireOnLogout;
    }

    public boolean isTimeLimited() {
        return timeLimited;
    }

    public int getTime() {
        return time;
    }

    public int getRecoveryHP() {
        return recoveryHP;
    }

    public int getRecoveryMP() {
        return recoveryMP;
    }

    public int getReqSkillLevel() {
        return reqSkillLevel;
    }

    public int getSkill() {
        return skill;
    }

    public int getMasterLevel() {
        return masterLevel;
    }

    public int getWarmSupport() {
        return warmsupport;
    }

    public int getPreventSlip() {
        return preventslip;
    }

    public int getSuccess() {
        return success;
    }

    public int getCursed() {
        return cursed;
    }

    public boolean isRandstat() {
        return randstat;
    }

    public boolean isRecovery() {
        return recover;
    }

    public int getIncMHP() {
        return incMHP;
    }

    public int getIncMMP() {
        return incMMP;
    }

    public int getIncSTR() {
        return incSTR;
    }

    public int getIncDEX() {
        return incDEX;
    }

    public int getIncINT() {
        return incINT;
    }

    public int getIncLUK() {
        return incLUK;
    }

    public int getIncPAD() {
        return incPAD;
    }

    public int getIncMAD() {
        return incMAD;
    }

    public int getIncPDD() {
        return incPDD;
    }

    public int getIncMDD() {
        return incMDD;
    }

    public int getIncACC() {
        return incACC;
    }

    public int getIncEVA() {
        return incEVA;
    }

    public int getIncSpeed() {
        return incSpeed;
    }

    public int getIncJump() {
        return incJump;
    }

    public int getSpec_time() {
        return spec_time;
    }

    public boolean isConsumeOnPickup() {
        return consumeOnPickup;
    }

    public int[] getCon() {
        return con;
    }

    public int getItemCode() {
        return itemCode;
    }

    public int getItemupbyitem() {
        return itemupbyitem;
    }

    public int getMesoupbyitem() {
        return mesoupbyitem;
    }

    public int getItemRange() {
        return itemRange;
    }

    public int getProb() {
        return prob;
    }

    public boolean isRespectFS() {
        return respectFS;
    }

    public boolean isRespectPimmune() {
        return respectPimmune;
    }

    public boolean isRespectMimmune() {
        return respectMimmune;
    }

    public int getDefenseState() {
        return defenseState;
    }

    public int getDefenseAtt() {
        return defenseAtt;
    }

    public int getMorph() {
        return morph;
    }

    public boolean isGhost() {
        return ghost;
    }

    public int getMoveTo() {
        return moveTo;
    }

    public int getReturnMapQR() {
        return returnMapQR;
    }

    public int getNuffSkill() {
        return nuffSkill;
    }

    public boolean isParty() {
        return party;
    }

    public int getCp() {
        return cp;
    }

    public boolean isBarrier() {
        return barrier;
    }

    public int getSpec_mob() {
        return spec_mob;
    }

    public int getThaw() {
        return thaw;
    }

    public boolean isCurse() {
        return curse;
    }

    public boolean isDarkness() {
        return darkness;
    }

    public boolean isPoison() {
        return poison;
    }

    public boolean isSeal() {
        return seal;
    }

    public boolean isWeakness() {
        return weakness;
    }

    public int[] getPets() {
        return pets;
    }

    public int getInc() {
        return inc;
    }

    public int getIncFatigue() {
        return incFatigue;
    }

    public int getHp() {
        return hp;
    }

    public int getMp() {
        return mp;
    }

    public int getHpR() {
        return hpR;
    }

    public int getMpR() {
        return mpR;
    }

    public int getPad() {
        return pad;
    }

    public int getMad() {
        return mad;
    }

    public int getPdd() {
        return pdd;
    }

    public int getMdd() {
        return mdd;
    }

    public int getAcc() {
        return acc;
    }

    public int getEva() {
        return eva;
    }

    public int getSpeed() {
        return speed;
    }

    public int getJump() {
        return jump;
    }

    public Item generateItem() {
        return new Item(-1, id, "", id / 1000000, 1, slotMax, (byte) -1, (byte) 0, 0, cash);
    }
}
