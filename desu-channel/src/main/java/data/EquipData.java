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

import dbf.io.DesuReader;
import dbf.io.DesuWriter;
import dbf.unit.Unit;
import player.item.Item;

/**
 *
 * @author Brent
 */
public class EquipData implements Unit {
    
    private int id;
    private boolean cash;
    private int price;
    private boolean tradeBlock;
    private boolean notSale;
    private boolean timeLimited;
    private boolean expireOnLogout;
    private boolean only; // one-of-a-kind
    private boolean quest; // quest-item title
    private boolean pachinko; // no idea
    
    private int slotMax; // (?)
    private int fs; // probably flagSet on the item
    
    private int attack, attackSpeed; // attack or not (?), idek
    
    private int knockback;
    private int tamingMob;
    
    private int tuc; // total upgrade count
    private String islot, vslot;
    private int recovery, hpRecovery, mpRecovery; // (idk why, but yeah...)
    private int reqSTR, reqDEX, reqINT, reqLUK, reqPOP, regPOP, reqLevel, reqJob;
    
    private int incSTR, incDEX, incINT, incLUK, incLUk, incSpeed, incJump, 
            incMHP, incMMP, incMDD, incPDD, incMMD, incPAD, incMAD, incACC, acc, incEVA,  
            incCraft, incFatigue, incSwim, 
            incRMAS, // poison
            incRMAL, // lightning
            incRMAI, // ice
            incRMAF, // fire
            elemDefault; // defaultElemental Damage modifier
        
    private EquipData() {
    }

    public EquipData(int id, boolean cash, int price, boolean tradeBlock, 
            boolean notSale, boolean timeLimited, boolean expireOnLogout, 
            boolean only, boolean quest, boolean pachinko, int slotMax, 
            int fs, int attack, int attackSpeed, int knockback, 
            int tamingMob, int tuc, String islot, String vslot, 
            int recovery, int hpRecovery, int mpRecovery, int reqSTR, 
            int reqDEX, int reqINT, int reqLUK, int reqPOP, int regPOP, 
            int reqLevel, int reqJob, int incSTR, int incDEX, int incINT, 
            int incLUK, int incLUk, int incSpeed, int incJump, int incMHP, 
            int incMMP, int incMDD, int incPDD, int incMMD, int incPAD, 
            int incMAD, int incACC, int acc, int incEVA, int incCraft, 
            int incFatigue, int incSwim, int incRMAS, int incRMAL, 
            int incRMAI, int incRMAF, int elemDefault) 
    {
        this.id = id;
        this.cash = cash;
        this.price = price;
        this.tradeBlock = tradeBlock;
        this.notSale = notSale;
        this.timeLimited = timeLimited;
        this.expireOnLogout = expireOnLogout;
        this.only = only;
        this.quest = quest;
        this.pachinko = pachinko;
        this.slotMax = slotMax;
        this.fs = fs;
        this.attack = attack;
        this.attackSpeed = attackSpeed;
        this.knockback = knockback;
        this.tamingMob = tamingMob;
        this.tuc = tuc;
        this.islot = islot;
        this.vslot = vslot;
        this.recovery = recovery;
        this.hpRecovery = hpRecovery;
        this.mpRecovery = mpRecovery;
        this.reqSTR = reqSTR;
        this.reqDEX = reqDEX;
        this.reqINT = reqINT;
        this.reqLUK = reqLUK;
        this.reqPOP = reqPOP;
        this.regPOP = regPOP;
        this.reqLevel = reqLevel;
        this.reqJob = reqJob;
        this.incSTR = incSTR;
        this.incDEX = incDEX;
        this.incINT = incINT;
        this.incLUK = incLUK;
        this.incLUk = incLUk;
        this.incSpeed = incSpeed;
        this.incJump = incJump;
        this.incMHP = incMHP;
        this.incMMP = incMMP;
        this.incMDD = incMDD;
        this.incPDD = incPDD;
        this.incMMD = incMMD;
        this.incPAD = incPAD;
        this.incMAD = incMAD;
        this.incACC = incACC;
        this.acc = acc;
        this.incEVA = incEVA;
        this.incCraft = incCraft;
        this.incFatigue = incFatigue;
        this.incSwim = incSwim;
        this.incRMAS = incRMAS;
        this.incRMAL = incRMAL;
        this.incRMAI = incRMAI;
        this.incRMAF = incRMAF;
        this.elemDefault = elemDefault;
    }
    
    public int getItemID() {
        return id;
    }
    
    public boolean isCash() {
        return cash;
    }
    
    public int getPrice() {
        return price;
    }
    
    public boolean hasTradeBlock() {
        return tradeBlock;
    }
    
    public boolean cannotBeSold() {
        return notSale;
    }
    
    public boolean isTimeLimited() {
        return timeLimited;
    }
    
    public boolean expiresOnLogout() {
        return expireOnLogout;
    }
    
    public boolean isOneOfAKind() {
        return only;
    }
    
    public boolean isQuestItem() {
        return quest;
    }
    
    public boolean isPachinko() {
        return pachinko;
    }
    
    public int getSlotMax() {
        return slotMax; // usually 1 anyways
    }
    
    public int getSetFlags() {
        return fs;
    }
    
    public int getAttack() { // reference to animation (?)
        return attack;
    }
    
    public int getAttackSpeed() {
        return attackSpeed;
    }
    
    public int getKnockback() {
        return knockback;
    }
    
    public int getTamingMob() {
        return tamingMob;
    }
    
    public int getTotalUpgradeCount() {
        return tuc;
    }
    
    public String getISlot() {
        return islot;
    }
    
    public String getVSlot() {
        return vslot;
    }
    
    public int getRecovery() {
        return recovery;
    }
    
    public int getHPRecovery() {
        return hpRecovery;
    }
    
    public int getMPRecovery() {
        return mpRecovery;
    }
    
    public int getRequiredStrength() {
        return reqSTR;
    }
    
    public int getRequiredDexterity() {
        return reqDEX;
    }
    
    public int getRequiredIntelligence() {
        return reqINT;
    }
    
    public int getRequiredLuck() {
        return reqLUK;
    }
    
    public int getRequiredFame() {
        return reqPOP + regPOP;
    }
    
    public int getRequiredLevel() {
        return reqLevel;
    }
    
    public int getRequiredJob() {
        return reqJob;
    }
    
    public int getStrength() {
        return incSTR;
    }
    
    public int getDexterity() {
        return incDEX;
    }
    
    public int getIntelligence() {
        return incINT;
    }
    
    public int getLuck() {
        return incLUK + incLUk;
    }
    
    public int getSpeed() {
        return incSpeed;
    }
    
    public int getJump() {
        return incJump;
    }
    
    public int getHealth() {
        return incMHP;
    }
    
    public int getMana() {
        return incMMP;
    }
    
    public int getPhysicalDefense() {
        return incPDD;
    }
    
    public int getMagicDefense() {
        return incMDD + incMMD;
    }
    
    public int getAttackDamage() {
        return incPAD;
    }
    
    public int getMagicDamage() {
        return incMAD;
    }
    
    public int getAccuracy() {
        return incACC + acc;
    }
    
    public int getAvoidability() {
        return incEVA;
    }
    
    public int getCraft() {
        return incCraft;
    }
    
    public int getFatigue() {
        return incFatigue;
    }
    
    public int getSwim() {
        return incSwim;
    }
    
    public int getPoisonDamageModifier() {
        return incRMAS;
    }
    
    public int getLightningDamageModifier() {
        return incRMAL;
    }
    
    public int getIceDamageModifier() {
        return incRMAI;
    }
    
    public int getFireDamageModifier() {
        return incRMAF;
    }
    
    public int getNonElementalDamageModifier() {
        return elemDefault;
    }

    public Item generateEquip(boolean random) { // XXX code
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	@Override
	public void serialize(DesuWriter w) {
		w.writeInteger(id).writeBool(cash).writeInteger(price).writeBool(tradeBlock);
		w.writeBool(notSale).writeBool(timeLimited).writeBool(expireOnLogout);
		w.writeBool(only).writeBool(quest).writeBool(pachinko).writeInteger(slotMax);
		w.writeInteger(fs).writeInteger(attack).writeInteger(attackSpeed).writeInteger(knockback);
		w.writeInteger(tamingMob).writeInteger(tuc).writeMapleString(islot).writeMapleString(vslot);
		
		w.writeInteger(recovery).writeInteger(hpRecovery).writeInteger(mpRecovery).writeInteger(reqSTR);
		w.writeInteger(reqDEX).writeInteger(reqINT).writeInteger(reqLUK).writeInteger(reqPOP).writeInteger(regPOP);
		w.writeInteger(reqLevel).writeInteger(reqJob).writeInteger(incSTR).writeInteger(incDEX).writeInteger(incINT);
		w.writeInteger(incLUK).writeInteger(incLUk).writeInteger(incSpeed).writeInteger(incJump).writeInteger(incMHP);
		w.writeInteger(incMMP).writeInteger(incMDD).writeInteger(incPDD).writeInteger(incMMD).writeInteger(incPAD);
		w.writeInteger(incMAD).writeInteger(incACC).writeInteger(acc).writeInteger(incEVA).writeInteger(incCraft);
		w.writeInteger(incFatigue).writeInteger(incSwim).writeInteger(incRMAS).writeInteger(incRMAL);
		w.writeInteger(incRMAI).writeInteger(incRMAF).writeInteger(elemDefault);
	}

	@Override
	public void deserialize(DesuReader r) {
		id = r.readInteger();
		cash = r.readBool();
		price = r.readInteger();
		tradeBlock = r.readBool();
		notSale = r.readBool();
		timeLimited = r.readBool();
		expireOnLogout = r.readBool();
		only = r.readBool();
		quest = r.readBool();
		pachinko = r.readBool();
		slotMax = r.readInteger();
		fs = r.readInteger();
		attack = r.readInteger();
		attackSpeed = r.readInteger();
		knockback = r.readInteger();
		tamingMob = r.readInteger();
		tuc = r.readInteger();
		islot = r.readMapleString();
		vslot = r.readMapleString();
		recovery = r.readInteger();
		hpRecovery = r.readInteger();
		mpRecovery = r.readInteger();
		reqSTR = r.readInteger();
		reqDEX = r.readInteger();
		reqINT = r.readInteger();
		reqLUK = r.readInteger();
		reqPOP = r.readInteger();
		regPOP = r.readInteger();
		reqLevel = r.readInteger();
		reqJob = r.readInteger();
		incSTR = r.readInteger();
		incDEX = r.readInteger();
		incINT = r.readInteger();
		incLUK = r.readInteger();
		incLUk = r.readInteger();
		incSpeed = r.readInteger();
		incJump = r.readInteger();
		incMHP = r.readInteger();
		incMMP = r.readInteger();
		incMDD = r.readInteger();
		incPDD = r.readInteger();
		incMMD = r.readInteger();
		incPAD = r.readInteger();
		incMAD = r.readInteger();
		incACC = r.readInteger();
		acc = r.readInteger();
		incEVA = r.readInteger();
		incCraft = r.readInteger();
		incFatigue = r.readInteger();
		incSwim = r.readInteger();
		incRMAS = r.readInteger();
		incRMAL = r.readInteger();
		incRMAI = r.readInteger();
		incRMAF = r.readInteger();
		elemDefault = r.readInteger();
	}
}
