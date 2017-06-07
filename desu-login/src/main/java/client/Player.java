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
package client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import service.WorldService;

/**
 *
 * @author Brent
 */
public class Player {

    private int id;
    private String name;
    private int world;
    private byte gender;
    private byte skinColor;
    private int face;
    private int hairStyle;
    private int level = 1;
    private short strength;
    private short dexterity;
    private short intelligence;
    private short luck;
    private int job;
    private int maximumHealth = 50;
    private int health = 50;
    private int maximumMana = 5;
    private int mana = 5;
    private short availableAP;
    private short availableSP;
    private int experience;
    private short fame;
    private byte spawnPoint;
    private int currentMapId;
    private int rank = 0;
    private int jobRank = 0;
    private int rankMove = 0;
    private int jobRankMove = 0;
    private List<Integer> pets;
    private Map<Byte, Integer> equipment;
    public static final String DEFAULT_KEYMAP;
    
    static {
        StringBuilder map = new StringBuilder();
        map.append("2,4,10;");
        map.append("3,4,12;");
        map.append("4,4,13;");
        map.append("5,4,18;");
        map.append("6,4,24;");
        map.append("7,4,21;");
        map.append("16,4,8;");
        map.append("17,4,5;");
        map.append("18,4,0;");
        map.append("19,4,4;");
        map.append("23,4,1;");
        map.append("24,4,25;");
        map.append("25,4,19;");
        map.append("26,4,14;");
        map.append("27,4,15;");
        map.append("29,5,52;");
        map.append("31,4,2;");
        map.append("33,4,26;");
        map.append("34,4,17;");
        map.append("35,4,11;");
        map.append("37,4,3;");
        map.append("38,4,20;");
        map.append("39,4,27;");
        map.append("40,4,16;");
        map.append("41,4,23;");
        map.append("43,4,9;");
        map.append("44,5,50;");
        map.append("45,5,51;");
        map.append("46,4,6;");
        map.append("48,4,22;");
        map.append("50,4,7;");
        map.append("56,5,53;");
        map.append("57,5,54;");
        map.append("59,6,100;");
        map.append("60,6,101;");
        map.append("61,6,102;");
        map.append("62,6,103;");
        map.append("63,6,104;");
        map.append("64,6,105;");
        map.append("65,6,106;");
        DEFAULT_KEYMAP = map.toString();
    }
    
    public Player(String n) {
        name = n;
        equipment = new HashMap<>();
        pets = new ArrayList<>(3);
        pets.addAll(Arrays.asList(0, 0, 0));
    }
    
    public static boolean checkName(String name, int wid) {
        if (name.length() > 13) {
            return false;
        }
        Connection c = WorldService.getInstance().getWorld(wid).getDatabase().getConnection();
        boolean ret = false;
        try {
            PreparedStatement ps = c.prepareStatement("SELECT id FROM characters WHERE name = ?");
            ps.setString(1, name);
            
            ResultSet rs = ps.executeQuery();
            ret = rs.next();
            
            ps.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {                    
            try {
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return !ret;
    }
    
    public static Player loadPlayer(Connection c, String name, int wid) {
        if (name == null) {
            return null;
        }
        Player ret = new Player(name);
        try {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM characters WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rs.getInt("account");
                ret.id = rs.getInt("id");
                ret.world = wid;
                ret.level = rs.getShort("level");
                ret.experience = rs.getInt("exp");
                ret.job = rs.getInt("job");
                ret.availableAP = rs.getShort("ap");
                ret.availableSP = rs.getShort("sp");
                ret.health = rs.getInt("hp");
                ret.maximumHealth = rs.getInt("maxhp");
                ret.mana = rs.getInt("mp");
                ret.maximumMana = rs.getInt("maxmp");
                ret.strength = rs.getShort("str");
                ret.dexterity = rs.getShort("dex");
                ret.intelligence = rs.getShort("intelligence");
                ret.luck = rs.getShort("luk");
                ret.fame = rs.getShort("fame");
                ret.gender = rs.getByte("gender");
                ret.face = rs.getShort("face");
                ret.hairStyle = rs.getShort("hair");
                ret.skinColor = rs.getByte("skin");
                ret.currentMapId = rs.getInt("map");
                ret.spawnPoint = rs.getByte("spawn");
            } else {
                ps.close();
                rs.close();
                return null;
            }
            rs.close();
            ps.close();

            // Ranking
            
            ps = c.prepareStatement("SELECT rank, jobrank, rankmove, jobrankmove FROM rankings WHERE id = ?");
            ps.setInt(1, ret.id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                ret.rank = rs.getInt("rank");
                ret.jobRank = rs.getInt("jobrank");
                ret.rankMove = rs.getInt("rankmove");
                ret.jobRankMove = rs.getInt("jobrankmove");
            }
            
            rs.close();
            ps.close();
            
            // Equips
            ps = c.prepareStatement("SELECT uid, pos FROM equips WHERE owner = ? && equipped = 1");
            ps.setInt(1, ret.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                ret.equipment.put(rs.getByte("pos"), rs.getInt("uid"));
            }
            rs.close();
            ps.close();

            // Pets
            ps = c.prepareStatement("SELECT uid FROM pets WHERE owner = ?");
            ps.setInt(1, ret.id);
            rs = ps.executeQuery();
            int index = 0;
            while (rs.next() && index < 3) {
                ret.pets.set(index++, rs.getInt("uid"));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ret;
    }
    
    public final boolean save(int account) {
        Connection c = WorldService.getInstance().getWorld(world).getDatabase().getConnection();
        try {
            PreparedStatement ps = c.prepareStatement("INSERT INTO characters (account, name, level, exp, job, ap, sp, hp, maxhp, mp, maxmp, str, dex, intelligence, luk, fame, gender, skin, face, hair, map, spawn) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, account);
            ps.setString(2, name);
            ps.setInt(3, level);
            ps.setInt(4, experience);
            ps.setInt(5, job);
            ps.setShort(6, availableAP);
            ps.setShort(7, availableSP);
            ps.setInt(8, health);
            ps.setInt(9, maximumHealth);
            ps.setInt(10, mana);
            ps.setInt(11, maximumMana);
            ps.setShort(12, strength);
            ps.setShort(13, dexterity);
            ps.setShort(14, intelligence);
            ps.setShort(15, luck);
            ps.setShort(16, fame);
            ps.setByte(17, gender);
            ps.setByte(18, skinColor);
            ps.setInt(19, face);
            ps.setInt(20, hairStyle);
            ps.setInt(21, currentMapId);
            ps.setByte(22, spawnPoint);
            ps.execute();
            ps.close();
            
            ps = c.prepareStatement("SELECT id FROM characters WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            } else {
                ps.close();
                rs.close();
                return false;
            }
            ps.close();
            rs.close();
            
            ps = c.prepareStatement("INSERT INTO rankings (id, rank, jobrank, rankmove, jobrankmove, finalrank, finaljobrank) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, id);
            for (int i = 2; i < 8; i++) {
                ps.setInt(i, 0);
            }
            ps.execute();
            ps.close();
            
            for (Entry<Byte, Integer> e : equipment.entrySet()) {
                ps = c.prepareStatement("INSERT INTO equips (owner, uid, pos, slots, attack, defence, equipped) VALUES (?, ?, ?, ?, ?, ?, ?)");
                byte pos = e.getKey();
                ps.setInt(1, id);
                ps.setInt(2, e.getValue());
                ps.setInt(3, pos);
                ps.setByte(4, (byte) (pos == 7 ? 5 : 7));
                if (pos == 5) {
                    ps.setInt(5, 0);
                    ps.setInt(6, 3);
                } else if (pos == 6 || pos == 7) {
                    ps.setInt(5, 0);
                    ps.setInt(6, 2);
                } else {
                    ps.setInt(5, 15);
                    ps.setInt(6, 0);
                }
                ps.setByte(7, (byte) 1);
                ps.execute();
                ps.close();
            }
            
            ps = c.prepareStatement("INSERT INTO keymap (id, mapping) VALUES (?, ?)");
            ps.setInt(1, id);
            ps.setString(2, DEFAULT_KEYMAP);
            ps.execute();
            ps.close();
            
            ps = c.prepareStatement("INSERT INTO rock (id, rock, vip) VALUES (?, DEFAULT, DEFAULT)");
            ps.setInt(1, id);
            ps.execute();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {                    
            try {
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String n) {
        name = n;
    }

    public int getId() {
        return id;
    }
    
    public void setId(int i) {
        id = i;
    }
    
    public int getWorld() {
        return world;
    }
    
    public void setWorld(int w) {
        world = w;
    }

    public int getGender() {
        return gender;
    }
    
    public void setGender(byte g) {
        gender = g;
    }

    public int getSkinColor() {
        return skinColor;
    }
    
    public void setSkinColor(byte sc) {
        skinColor = sc;
    }

    public int getFace() {
        return face;
    }
    
    public void setFace(int f) {
        face = f;
    }

    public int getHairstyle() {
        return hairStyle;
    }
    
    public void setHairstyle(int hs) {
        hairStyle = hs;
    }

    public int getLevel() {
        return level;
    }
    
    public void setLevel(int l) {
        level = l;
    }

    public int getJob() {
        return job;
    }
    
    public void setJob(int j) {
        job = j;
    }

    public int getStrength() {
        return strength;
    }
    
    public void setStrength(short s) {
        strength = s;
    }

    public int getDexterity() {
        return dexterity;
    }
    
    public void setDexterity(short d) {
        dexterity = d;
    }

    public int getIntelligence() {
        return intelligence;
    }
    
    public void setIntelligence(short i) {
        intelligence = i;
    }

    public int getLuck() {
        return luck;
    }
    
    public void setLuck(short l) {
        luck = l;
    }

    public int getHealth() {
        return health;
    }
    
    public void setHealth(int h) {
        health = h;
    }

    public int getMaximumHealth() {
        return maximumHealth;
    }
    
    public void setMaximumHealth(int mh) {
        maximumHealth = mh;
    }

    public int getMana() {
        return mana;
    }
    
    public void setMana(int m) {
        mana = m;
    }

    public int getMaximumMana() {
        return maximumMana;
    }
    
    public void setMaximumMana(int mm) {
        maximumMana = mm;
    }

    public int getAvailableAP() {
        return availableAP;
    }
    
    public void setAvailableAP(short aa) {
        availableAP = aa;
    }

    public int getAvailableSP() {
        return availableSP;
    }
    
    public void setAvailableSP(short as) {
        availableSP = as;
    }

    public int getFame() {
        return fame;
    }
    
    public void setFame(short f) {
        fame = f;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int e) {
        experience = e;
    }
    
    public int getSpawnpoint() {
        return spawnPoint;
    }
    
    public void setSpawnpoint(byte sp) {
        spawnPoint = sp;
    }

    public int getCurrentMapId() {
        return currentMapId;
    }
    
    public void setCurrentMapId(int cmi) {
        currentMapId = cmi;
    }

    public Map<Byte, Integer> getEquipment() {
        return equipment;
    }

    public List<Integer> getPets() {
        return pets;
    }

    public boolean isRanked() {
        return rank != 0 && jobRank != 0;
    }

    public int getOverallRank() {
        return rank;
    }

    public int getJobRank() {
        return jobRank;
    }

    public int getOverallRankMove() {
        return rankMove;
    }

    public int getJobRankMove() {
        return jobRankMove;
    }
}
