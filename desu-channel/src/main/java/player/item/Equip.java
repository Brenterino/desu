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
package player.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Brent
 */
public class Equip extends Item {

    private byte slots;
    private short str;
    private short dex;
    private short _int;
    private short luk;
    private short hp;
    private short mp;
    private short attack;
    private short magic;
    private short wdef;
    private short mdef;
    private short acc;
    private short avoid;
    private short speed;
    private short jump;
    private short hands;
    private byte upgrades;
    private int level;
    private int experience;
    private int skillboost;
    private int skill0, skill1;
    private long locked;

    private Equip(int uid, int id, String name, int it, byte slot, byte flag, long expiration, boolean cash) {
        super(uid, id, name, it, 1, 1, slot, flag, expiration, cash);
    }

    public Equip(int uid, int id, String name, int it, byte slot, byte slots, byte upgrades, byte flag,
            short str, short dex, short _int, short luk, short hp, short mp,
            short attack, short magic, short wdef, short mdef, short acc,
            short avoid, short speed, short jump, short hands,
            long locked, int skill0, int skill1,
            int skillboost, int level, int experience,
            long expiration, boolean cash) {
        super(uid, id, name, it, 1, 1, slot, flag, expiration, cash);
        this.str = str;
        this.dex = dex;
        this._int = _int;
        this.luk = luk;
        this.hp = hp;
        this.mp = mp;
        this.upgrades = upgrades;
        this.attack = attack;
        this.magic = magic;
        this.wdef = wdef;
        this.mdef = mdef;
        this.acc = acc;
        this.avoid = avoid;
        this.speed = speed;
        this.jump = jump;
        this.hands = hands;
        this.locked = locked;
        this.skill0 = skill0;
        this.skill1 = skill1;
        this.skillboost = skillboost;
        this.level = level;
        this.experience = experience;
    }

    public short getInt() {
        return _int;
    }

    public void setInt(short _int) {
        this._int = _int;
    }

    public short getAcc() {
        return acc;
    }

    public void setAcc(short acc) {
        this.acc = acc;
    }

    public short getAvoid() {
        return avoid;
    }

    public void setAvoid(short avoid) {
        this.avoid = avoid;
    }

    public short getDex() {
        return dex;
    }

    public void setDex(short dex) {
        this.dex = dex;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public byte getUpgrades() {
        return upgrades;
    }

    public void setUpgrades(byte upgrades) {
        this.upgrades = upgrades;
    }

    public int getSkill0() {
        return skill0;
    }

    public void setSkill0(int skill0) {
        this.skill0 = skill0;
    }

    public int getSkill1() {
        return skill1;
    }

    public void setSkill1(int skill1) {
        this.skill1 = skill1;
    }

    public int getSkillBoost() {
        return skillboost;
    }

    public void setSkillBoost(int skillboost) {
        this.skillboost = skillboost;
    }

    public short getHands() {
        return hands;
    }

    public void setHands(short hands) {
        this.hands = hands;
    }

    public short getHp() {
        return hp;
    }

    public void setHp(short hp) {
        this.hp = hp;
    }

    public short getJump() {
        return jump;
    }

    public void setJump(short jump) {
        this.jump = jump;
    }

    public short getLuk() {
        return luk;
    }

    public void setLuk(short luk) {
        this.luk = luk;
    }

    public long getLockedDuration() {
        return locked;
    }

    public void setLockedDuration(long locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked > 0;
    }

    public short getMagic() {
        return magic;
    }

    public void setMagic(short magic) {
        this.magic = magic;
    }

    public short getMdef() {
        return mdef;
    }

    public void setMdef(short mdef) {
        this.mdef = mdef;
    }

    public short getMp() {
        return mp;
    }

    public void setMp(short mp) {
        this.mp = mp;
    }

    public short getSpeed() {
        return speed;
    }

    public void setSpeed(short speed) {
        this.speed = speed;
    }

    public byte getSlots() {
        return slots;
    }

    public void setSlots(byte slots) {
        this.slots = slots;
    }

    public short getStr() {
        return str;
    }

    public void setStr(short str) {
        this.str = str;
    }

    public short getAttack() {
        return attack;
    }

    public void setAttack(short attack) {
        this.attack = attack;
    }

    public short getWdef() {
        return wdef;
    }

    public void setWdef(short wdef) {
        this.wdef = wdef;
    }

    public static Equip load(ResultSet rs) {
        try {
            byte slot = (byte) (rs.getByte("pos") * (rs.getBoolean("equipped") ? -1 : 1));
            Equip eq = new Equip(rs.getInt("id"),
                    rs.getInt("uid"),
                    rs.getString("tag"),
                    1,
                    slot,
                    rs.getByte("flag"),
                    rs.getLong("expiration"),
                    rs.getBoolean("cash"));
            eq.setSlots(rs.getByte("slots"));
            eq.setUpgrades(rs.getByte("upgrades"));
            eq.setLevel(rs.getByte("level"));
            eq.setExperience(rs.getInt("exp"));
            eq.setSkill0(rs.getInt("skill0"));
            eq.setSkill1(rs.getInt("skill1"));
            eq.setSkillBoost(rs.getInt("skillboost"));
            eq.setStr(rs.getShort("str"));
            eq.setDex(rs.getShort("dex"));
            eq.setInt(rs.getShort("int"));
            eq.setLuk(rs.getShort("luk"));
            eq.setHp(rs.getShort("hp"));
            eq.setMp(rs.getShort("mp"));
            eq.setAttack(rs.getShort("attack"));
            eq.setMagic(rs.getShort("magic"));
            eq.setWdef(rs.getShort("defence"));
            eq.setMdef(rs.getShort("mdefence"));
            eq.setAcc(rs.getShort("accuracy"));
            eq.setAvoid(rs.getShort("avoidability"));
            eq.setHands(rs.getShort("hands"));
            eq.setSpeed(rs.getShort("speed"));
            eq.setJump(rs.getShort("jump"));
            eq.setLockedDuration(rs.getLong("locked"));
            return eq;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(int cid, Connection c) throws SQLException {
        PreparedStatement ps;
        if (getDatabaseId() == -1) {
            ps = c.prepareStatement("INSERT INTO equips (owner, uid, pos, equipped, slots, upgrades, flag, level, exp, skill0, skill1, skillboost, str, dex, `int`, luk, hp, mp, attack, magic, defence, mdefence, accuracy, avoidability, hands, speed, jump, locked, tag, expiration, cash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        } else {
            ps = c.prepareStatement("UPDATE equips SET owner = ?, uid = ?, pos = ?, equipped = ?, slots = ?, upgrades = ?, flag = ?, level = ?, exp = ?, skill0 = ?, skill1 = ?, skillboost = ?, str = ?, dex = ?, `int` = ?, luk = ?, hp = ?, mp = ?, attack = ?, magic = ?, defence = ?, mdefence = ?, accuracy = ?, avoidability = ?, hands = ?, speed = ?, jump = ?, locked = ?, tag = ?, expiration = ?, cash = ? WHERE id = ?");
        }
        ps.setInt(1, cid);
        ps.setInt(2, getItemId());
        ps.setInt(3, Math.abs(getSlot()));
        ps.setBoolean(4, getSlot() < 0);
        ps.setByte(5, slots);
        ps.setByte(6, upgrades);
        ps.setByte(7, getFlag());
        ps.setInt(8, level);
        ps.setInt(9, experience);
        ps.setInt(10, skill0);
        ps.setInt(11, skill1);
        ps.setInt(12, skillboost);
        ps.setShort(13, str);
        ps.setShort(14, dex);
        ps.setShort(15, _int);
        ps.setShort(16, luk);
        ps.setShort(17, hp);
        ps.setShort(18, mp);
        ps.setShort(19, attack);
        ps.setShort(20, magic);
        ps.setShort(21, wdef);
        ps.setShort(22, mdef);
        ps.setShort(23, acc);
        ps.setShort(24, avoid);
        ps.setShort(25, hands);
        ps.setShort(26, speed);
        ps.setShort(27, jump);
        ps.setLong(28, locked);
        ps.setString(29, getName());
        ps.setLong(30, getExpiration());
        ps.setInt(31, isCash() ? 1 : 0);
        if (getDatabaseId() > 0) {
            ps.setLong(32, getDatabaseId());
        }
        ps.execute();
        ps.close();
    }
}
