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
package player;

import client.Client;
import client.packet.PacketCreator;
import data.internal.Constants;
import data.external.GameDatabase;
import data.skill.Beginner;
import data.skill.Buccaneer;
import data.skill.Magician;
import data.skill.Warrior;
import field.Field;
import field.FieldLife;
import field.FieldManager;
import field.Portal;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import player.community.Guild;
import player.community.Party;
import player.item.Equip;
import player.item.Inventory;
import player.item.Item;
import player.item.Minigame;
import player.item.Pet;
import player.item.Shop;
import player.stats.Effect;
import player.stats.EffectStat;
import static player.stats.EffectStat.*;

import data.internal.ExperienceTable;
import data.SkillData;
import player.stats.Job;
import player.stats.Stat;
import static player.stats.Stat.*;
import player.stats.Stats;
import quest.Quest;
import service.Configuration;
import service.GameService;
import service.Service;
import java.util.function.Consumer;

/**
 *
 * @author Brent
 */
public class Player extends FieldLife {

    // XXX needs to be saved
    private int id;
    private int owner;

    private String name;
    private int manaAP, healthAP;
    private int marriageID;

    private int gender;

    private Stats stats;

    private int stance = 0;
    private boolean hidden = false;
    private Point position = new Point();
    private int field, spawn, fh;

    private Field cField;

    private Client client;

    private int buddySlots;

    private int equipSlots, useSlots, etcSlots,
            setupSlots, cashSlots;

    private Inventory<Equip> EQUIP;
    private Inventory<Item> USE;
    private Inventory<Item> ETC;
    private Inventory<Item> SETUP;
    private Inventory<Item> CASH;

    private List<Integer> visible;
    private List<Integer> controlled;

    private Map<Integer, SkillInfo> skills;
    private Map<Integer, Cooldown> cooldowns;
    private Map<Integer, KeyBinding> bindings;
    private Map<Integer, QuestInfo> activeQuests;
    private Map<Integer, QuestInfo> completedQuests;

    private Pet[] activePets = new Pet[3];

    private int[] ROCK = new int[5];
    private int[] VIP_ROCK = new int[10];

    // social
    private Party party;
    private Guild guild;
    private Buddylist buddies;

    private int chair;
    private int itemEffect;
    private String chalkboardMessage;

    private Shop activeShop;
    private Minigame activeMinigame;

    private final List<Effect> appliedStatuses;

    private int realMaxHealth, realMaxMana, realStrength, realDexterity,
            realIntelligence, realLuck, realMagic, realWeaponAttack;
    private int realSpeed, realJump;
    private int realLowerRangeEnd, realUpperRangeEnd;

    private long loginTime = System.currentTimeMillis();
    private AtomicInteger mobKills = new AtomicInteger(0); // for 3!6!9! event thing

    private final static Random r = new Random();

    public Player(int pid) {
        id = pid;
        stats = new Stats();
        skills = new HashMap<>();
        cooldowns = new HashMap<>();
        bindings = new HashMap<>();
        activeQuests = new HashMap<>();
        completedQuests = new HashMap<>();
        appliedStatuses = Collections.synchronizedList(new LinkedList<>());
        visible = Collections.synchronizedList(new LinkedList<Integer>());
        controlled = Collections.synchronizedList(new LinkedList<Integer>());
    }

    public static class KeyBinding {

        private int type;
        private int action;

        private KeyBinding() {
        }

        public KeyBinding(int t, int a) {
            type = t;
            action = a;
        }

        public int getType() {
            return type;
        }

        public int getAction() {
            return action;
        }
    }

    public static class SkillInfo {

        public int level;
        public int mastery;

        public SkillInfo(int lvl, int mas) {
            level = lvl;
            mastery = mas;
        }

        public int getLevel() {
            return level;
        }

        public int getMastery() {
            return mastery;
        }

        public boolean hasMastery() {
            return mastery > 0;
        }
    }

    public static class Cooldown {

        private long time;
        private final long startTime = System.currentTimeMillis();

        public Cooldown(long length) {
            time = length;
        }

        public long getLength() {
            return time;
        }

        public long remaining() {
            return System.currentTimeMillis() - startTime + time;
        }
    }

    public static class QuestInfo {

        public long completionTime;
        public String questData = "";

        public QuestInfo(long completionTime, String questData) {
            this.completionTime = completionTime;
            this.questData = questData;
        }

        public boolean isMedalQuest() {
            return false; // XXX find out later
        }

        public int getMedalQuestId() {
            return 0; // XXX find out later
        }

        public int getMedalQuestProgress() {
            return 0; // XXX find out later
        }
    }

    public static Player load(int cid) {
        if (cid == -1) {
            return null;
        }
        Player p = new Player(cid);

        Connection c = GameService.getInstance().getDatabase().getConnection();

        try {
            PreparedStatement ps = c.prepareStatement("SELECT * from characters WHERE id = ?");
            ps.setInt(1, cid);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                p.name = rs.getString("name");
                p.owner = rs.getInt("account");

                p.setStat(SKIN, rs.getByte("skin"));
                p.setStat(FACE, rs.getShort("face"));
                p.setStat(HAIR, rs.getShort("hair"));

                // pet0
                p.setStat(LEVEL, rs.getShort("level"));
                p.setStat(JOB, rs.getShort("job"));

                p.setStat(STR, rs.getShort("str"));
                p.setStat(DEX, rs.getShort("dex"));
                p.setStat(INT, rs.getShort("intelligence"));
                p.setStat(LUK, rs.getShort("luk"));

                p.setStat(HP, rs.getShort("hp"));
                p.setStat(MAXHP, rs.getShort("maxhp"));
                p.setStat(MP, rs.getShort("mp"));
                p.setStat(MAXMP, rs.getShort("maxmp"));

                p.setStat(AP, rs.getShort("ap"));
                p.setStat(SP, rs.getShort("sp"));

                p.setStat(EXP, rs.getInt("exp"));

                p.setStat(FAME, rs.getShort("fame"));

                p.setStat(MESOS, rs.getInt("meso"));

                // pet1
                // pet2
                // non-fundamental stats
                p.gender = rs.getByte("gender");

                p.healthAP = rs.getShort("healthAP");
                p.manaAP = rs.getShort("manaAP");
                // end non-fundamental stats

                p.field = rs.getInt("map");
                p.spawn = rs.getByte("spawn");

                p.buddySlots = rs.getByte("buddySlots");
                p.equipSlots = rs.getByte("equipSlots");
                p.useSlots = rs.getByte("useSlots");
                p.etcSlots = rs.getByte("etcSlots");
                p.setupSlots = rs.getByte("setupSlots");
                p.cashSlots = rs.getByte("cashSlots");
                p.marriageID = rs.getInt("marriageID");
            } else {
                rs.close();
                ps.close();
                return null;
            }
            rs.close();
            ps.close();

            // key bindings
            ps = c.prepareStatement("SELECT mapping FROM keymap WHERE id = ?");
            ps.setInt(1, cid);

            rs = ps.executeQuery();

            if (rs.next()) {
                String map = rs.getString("mapping");
                map = map.substring(0, map.length() - 1);
                String[] mapp = map.split(";");
                for (String s : mapp) {
                    String[] sMap = s.split(",");
                    int key = Integer.parseInt(sMap[0]);
                    int type = Integer.parseInt(sMap[1]);
                    int action = Integer.parseInt(sMap[2]);
                    KeyBinding bind = new KeyBinding(type, action);
                    p.bindings.put(key, bind);
                }
            }
            rs.close();
            ps.close();
            // end key bindings

            ps = c.prepareStatement("SELECT uid, level, mastery FROM skills WHERE owner = ?");
            ps.setInt(1, cid);

            rs = ps.executeQuery();

            while (rs.next()) {
                p.skills.put(rs.getInt("uid"),
                        new SkillInfo(rs.getInt("level"),
                                rs.getInt("mastery")));
            }
            rs.close();
            ps.close();

            // cooldowns
            ps = c.prepareStatement("SELECT uid, length FROM cooldowns WHERE owner = ?");
            ps.setInt(1, cid);

            rs = ps.executeQuery();

            while (rs.next()) {
                p.cooldowns.put(rs.getInt("uid"),
                        new Cooldown(rs.getLong("length")));
            }
            rs.close();
            ps.close();
            // end cooldowns

            // quests 
            ps = c.prepareStatement("SELECT * FROM quests WHERE owner = ?");
            ps.setInt(1, cid);

            rs = ps.executeQuery();

            while (rs.next()) {
                int questId = rs.getInt("uid");
                long completionTime = rs.getLong("completed");
                String questData = rs.getString("questData");
                if (completionTime > 0) { // completed
                    p.completedQuests.put(questId, new QuestInfo(completionTime, questData));
                } else { // active
                    p.activeQuests.put(questId, new QuestInfo(completionTime, questData));
                }
            }
            rs.close();
            ps.close();
            // end quests

            // teleport rocks
            ps = c.prepareStatement("SELECT * FROM rock WHERE id = ?");
            ps.setInt(1, cid);

            rs = ps.executeQuery();

            int endRockIndex = 0;
            int endVIPIndex = 0;
            if (rs.next()) {
                String rock = rs.getString("rock");
                if (rock.length() > 0) {
                    rock = rock.substring(0, rock.length() - 1);
                    String[] locs = rock.split(";");
                    int iterMax = Math.min(locs.length, 5);
                    for (endRockIndex = 0; endRockIndex < iterMax; endRockIndex++) {
                        p.ROCK[endRockIndex] = Integer.parseInt(locs[endRockIndex]);
                    }
                }
                String vip = rs.getString("vip");
                if (vip.length() > 0) {
                    vip = vip.substring(0, vip.length() - 1);
                    String[] locs = vip.split(";");
                    int iterMax = Math.min(locs.length, 5);
                    for (endVIPIndex = 0; endVIPIndex < iterMax; endVIPIndex++) {
                        p.VIP_ROCK[endVIPIndex] = Integer.parseInt(locs[endVIPIndex]);
                    }
                }
            }
            for (int i = endRockIndex; i < 5; i++) {
                p.ROCK[i] = 999999999;
            }
            for (int i = endVIPIndex; i < 10; i++) {
                p.VIP_ROCK[i] = 999999999;
            }
            // end teleport rocks

            p.buddies = Buddylist.load(c, p.buddySlots, p);
            p.EQUIP = Inventory.load(c, cid, p.equipSlots, Inventory.Type.EQUIP);
            p.USE = Inventory.load(c, cid, p.useSlots, Inventory.Type.USE);
            p.ETC = Inventory.load(c, cid, p.etcSlots, Inventory.Type.ETC);
            p.SETUP = Inventory.load(c, cid, p.setupSlots, Inventory.Type.SETUP);
            p.CASH = Inventory.load(c, cid, p.cashSlots, Inventory.Type.CASH);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        p.cField = FieldManager.getField(p.field);

        return p;
    }

    public void save() {
        // XXX code saverino
        Connection c = GameService.getInstance().getDatabase().getConnection();
        try {
            c.setAutoCommit(false); // move this connection into one complete commit
            
            PreparedStatement ps = c.prepareStatement("UPDATE characters SET level = ?, exp = ?, job = ?, ap = ?, sp = ?, hp = ?, maxhp = ?, healthAP = ?, mp = ?, maxmp = ?, manaAP = ?, str = ?, dex = ?, intelligence = ?, luk = ?, meso = ?, fame = ?, skin = ?, face = ?, hair = ?, map = ?, spawn = ?, buddySlots = ?, equipSlots = ?, useSlots = ?, etcSlots = ?, setupSlots = ?, cashSlots = ?, marriageID = ? WHERE id = ?");
            ps.setInt(1, getStat(LEVEL));
            ps.setInt(2, getStat(EXP));
            ps.setInt(3, getStat(JOB));
            ps.setInt(4, getStat(AP));
            ps.setInt(5, getStat(SP));
            ps.setInt(6, getStat(HP));
            ps.setInt(7, getStat(MAXHP));
            ps.setInt(8, healthAP);
            ps.setInt(9, getStat(MP));
            ps.setInt(10, getStat(MAXMP));
            ps.setInt(11, manaAP);
            ps.setInt(12, getStat(STR));
            ps.setInt(13, getStat(DEX));
            ps.setInt(14, getStat(INT));
            ps.setInt(15, getStat(LUK));
            ps.setInt(16, getStat(MESOS));
            ps.setInt(17, getStat(FAME));
            ps.setInt(18, getStat(SKIN));
            ps.setInt(19, getStat(FACE));
            ps.setInt(20, getStat(HAIR));
            ps.setInt(21, cField.getFieldId());
            ps.setInt(22, cField.getClosestSpawn(this));
            ps.setInt(23, buddies.getCapacity());
            ps.setInt(24, EQUIP.getCapacity());
            ps.setInt(25, USE.getCapacity());
            ps.setInt(26, ETC.getCapacity());
            ps.setInt(27, SETUP.getCapacity());
            ps.setInt(28, CASH.getCapacity());
            ps.setInt(29, marriageID);
            ps.setInt(30, id);
            ps.executeUpdate();
            ps.close();
            
            StringBuilder bindBuild = new StringBuilder();
            
            bindings.forEach((k, v) -> {
                bindBuild.append(k);
                bindBuild.append(",");
                bindBuild.append(v.getType());
                bindBuild.append(",");
                bindBuild.append(v.getAction());
                bindBuild.append(";");
            });
            
            ps = c.prepareStatement("UPDATE keymap SET mapping = ? WHERE id = ?");
            ps.setString(1, bindBuild.toString());
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
            
            ps = c.prepareStatement("DELETE FROM skills WHERE owner = ?");
            ps.setInt(1, id);
            ps.execute();
            ps.close();
            
            ps = c.prepareStatement("INSERT INTO skills (owner, uid, level, mastery) VALUES (?, ?, ?, ?)");
            ps.setInt(1, id);
            for (Entry<Integer, SkillInfo> e : skills.entrySet()) {
                SkillInfo s = e.getValue();
                
                ps.setInt(2, e.getKey());
                ps.setInt(3, s.getLevel());
                ps.setInt(4, s.getMastery());
                ps.execute();
            }
            ps.close();
            
            ps = c.prepareStatement("DELETE FROM cooldowns WHERE owner = ?");
            ps.setInt(1, id);
            ps.execute();
            ps.close();
            
            ps = c.prepareStatement("INSERT INTO cooldowns (owner, uid, length) VALUES (?, ?, ?)");
            ps.setInt(1, id);
            for (Entry<Integer, Cooldown> e : cooldowns.entrySet()) {
                Cooldown cd = e.getValue();
                
                ps.setInt(2, e.getKey());
                ps.setLong(3, cd.remaining());
                ps.execute();
            }
            ps.close();
            
            ps = c.prepareStatement("DELETE FROM quests WHERE owner = ?");
            ps.setInt(1, id);
            ps.execute();
            ps.close();
            
            ps = c.prepareStatement("INSERT INTO quests (owner, uid, completed, questData) VALUES (?, ?, ?, ?)");
            ps.setInt(1, id);
            for (Entry<Integer, QuestInfo> e : activeQuests.entrySet()) {
                QuestInfo q = e.getValue();
                
                ps.setInt(2, e.getKey());
                ps.setLong(3, 0);
                ps.setString(4, q.questData);
                ps.execute();
            }
            for (Entry<Integer, QuestInfo> e : completedQuests.entrySet()) {
                QuestInfo q = e.getValue();
                
                ps.setInt(2, e.getKey());
                ps.setLong(3, q.completionTime);
                ps.setString(4, q.questData);
                ps.execute();
            }
            ps.close();
            
            StringBuilder nRBuild = new StringBuilder();
            StringBuilder vRBuild = new StringBuilder();
            for (int i = 0; i < ROCK.length; i++) {
                nRBuild.append(ROCK[i]).append(";");
            }
            for (int i = 0; i < VIP_ROCK.length; i++) {
                vRBuild.append(VIP_ROCK[i]).append(";");
            }
            
            ps = c.prepareStatement("UPDATE rock SET rock = ?, vip = ? WHERE id = ?");
            ps.setString(1, nRBuild.toString());
            ps.setString(2, vRBuild.toString());
            ps.setInt(3, id);
            ps.executeUpdate();
            ps.close();
            
            buddies.save(c);
            EQUIP.save(c, id);
            USE.save(c, id);
            SETUP.save(c, id);
            ETC.save(c, id);
            CASH.save(c, id);
            
            c.commit();
        } catch (Exception e) {
            System.out.println("[SEVERE] Failed saving player.  Attempting to rollback commit.");
            e.printStackTrace();
            try {
                c.rollback();
            } catch (SQLException re) {
                System.out.println("[SEVERE] Unable to rollback commit.  Something really bad happened.");
            }
        } finally {
            try {
                c.setAutoCommit(true);
                c.close();
            } catch (Exception e) {
                System.out.println("[SEVERE] Unable to move the used connection back into an auto commit state.");          
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getGender() {
        return gender;
    }

    public Pet getActivePet(int i) {
        return activePets[i];
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client c) {
        this.client = c;
    }

    public String getName() {
        return name;
    }

    public boolean isMarried() {
        return marriageID > 0;
    }

    public int getMarriageID() {
        return marriageID;
    }

    public int getCurrentFieldId() {
        return field;
    }

    public int getSpawnpoint() {
        return spawn;
    }

    public Buddylist getBuddylist() {
        return buddies;
    }

    public Iterator<Equip> getEquipped() {
        return EQUIP.equippedSupplier().get().filter(e -> e.getSlot() < 0).iterator();
    }

    public Inventory<Equip> getEquipInventory() {
        return EQUIP;
    }

    public Inventory<Item> getUseInventory() {
        return USE;
    }

    public Inventory<Item> getSetupInventory() {
        return SETUP;
    }

    public Inventory<Item> getEtcInventory() {
        return ETC;
    }

    public Inventory<Item> getCashInventory() {
        return CASH;
    }

    public boolean gainItem(int itemId, int amount) {
        Item item;
        
        if (GameDatabase.isEquip(itemId)) {
            item = GameDatabase.getEquip(itemId).generateEquip(true);
        } else {
            item = GameDatabase.getItem(itemId).generateItem();
            item.setQuantity(amount);
        }
        
        return gainItem(item);
    }
    
    public boolean gainItem(Item item) {
        Inventory target = GameDatabase.getInventoryForItem(item.getItemId(), this);
        boolean added = target.addItem(item);
        
        // XXX dank memes
        target.showInventoryModifications(this, false);
        
        return added;
    }

    // XXX this could be improved by doing an id check to see
    // where the prefix of the item lies
    public int getItemCount(int id) {
        int count = 0;
        count += EQUIP.getItemCount(id);
        count += USE.getItemCount(id);
        count += SETUP.getItemCount(id);
        count += ETC.getItemCount(id);
        count += CASH.getItemCount(id);
        return count;
    }

    public Map<Integer, SkillInfo> getSkills() {
        return skills;
    }

    public Map<Integer, Cooldown> getCooldowns() {
        return cooldowns;
    }

    public Map<Integer, QuestInfo> getActiveQuests() {
        return activeQuests;
    }

    public Map<Integer, QuestInfo> getCompletedQuests() {
        return completedQuests;
    }

    public int getQuestKillCount(int id) { // XXX quest stuff
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getQuestState(int id) {
        return completedQuests.containsKey(id) ? Quest.QUEST_COMPLETE
                : activeQuests.containsKey(id) ? Quest.QUEST_IN_PROGRESS
                : Quest.QUEST_AVAILABLE;
    }

    public boolean requiresItemForQuest(int itemId) {
        // XXX DO THIS
        return false;
    }

    public int[] getSavedLocations() {
        return ROCK;
    }

    public int[] getVIPSavedLocations() {
        return VIP_ROCK;
    }

    public int getItemEffect() { // nope
        return itemEffect; // XXX yeah idk
    }

    public int getChair() {
        return chair; // XXX can change this later, etc.
    }

    @Override
    public Type getObjectType() {
        return Type.PLAYER;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void toggleHidden() {
        hidden = !hidden;
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
    public Point getPosition() {
        return position;
    }

    @Override
    public void setPosition(Point p) {
        position.x = p.x;
        position.y = p.y;
    }

    @Override
    public int getFh() {
        return fh;
    }

    @Override
    public void sendSpawnData(Client c) {
        c.write(PacketCreator.spawnPlayer(this));
    }

    @Override
    public void sendDestroyData(Client c, boolean special) {
        c.write(PacketCreator.removePlayer(id));
    }

    @Override
    public void sendControlGain(Client mc) {
    }

    @Override
    public void sendControlLoss(Client mc) {
    }

    public void addControlled(int oid) {
        controlled.add(oid);
    }

    public void removeControlled(int oid) {
        controlled.remove((Integer) oid); // needs to be casted
    }

    public void addVisibleObject(int oid) {
        visible.add(oid);
    }

    public void removeVisibleObject(int oid) {
        visible.remove((Integer) oid);
    }

    public boolean canSee(int oid) {
        return visible.contains(oid);
    }

    public List<Integer> getVisibleObjects() {
        return visible;
    }

    public boolean hasControl(int oid) {
        return controlled.contains(oid);
    }

    public List<Integer> getControlled() {
        return controlled;
    }

    public void changeField(Field to) {
        changeField(to, to.getPortal(0));
    }

    public void changeField(Field to, Portal target) {
        cField.removePlayer(this);

        visible.clear();
        controlled.clear();

        cField = to;

        setPosition(target.getPosition()); // idk

        getClient().write(PacketCreator.changeField(to.getFieldId(), target.getId(), getStat(HP)));

        to.spawnPlayer(this);
        // XXX party update -> do this in player update maybe?
    }

    public void setField(Field f) {
        field = f.getFieldId();
        cField = f;
    }

    public Field getField() {
        return cField;
    }

    public boolean isGM() {
        return getClient().isGM();
    }

    public String getChalkboardMessage() {
        return chalkboardMessage;
    }

    public Guild getGuild() {
        return guild; // XXX have channel load guild and then find it here
    }
    
    public int getDropOwnershipId() {
        if (party != null) {
            return party.getId();
        }
        return id;
    }

    public Party getParty() {
        return party; // XXX handle parties
    }

    public Map<Integer, KeyBinding> getBindings() {
        return bindings;
    }

    public void modifyKeyBinding(int key, int type, int action) {
        if (bindings.containsKey(key)) {
            bindings.remove(key);
        }

        bindings.put(key, new KeyBinding(type, action));
    }

    public Shop getShop() {
        return activeShop;
    }

    public Minigame getMinigame() {
        return activeMinigame;
    }

    public boolean isAlive() {
        return getStat(HP) > 0;
    }

    public boolean hasSkill(int id) {
        return id == 0 || skills.containsKey(id);
    }

    public int getSkillLevel(int id) {
        if (skills.containsKey(id)) {
            return skills.get(id).getLevel();
        }
        return 0;
    }

    public SkillInfo getSkill(int id) {
        if (skills.containsKey(id)) {
            return skills.get(id);
        }
        return null;
    }

    public boolean isOffCooldown(int id) {
        if (cooldowns.containsKey(id)) { // XXX does this need to be a hack for removing a cooldown as well?
            return cooldowns.get(id).remaining() <= 0;
        }
        return true;
    }

    public void incrementMonsterKills() {
        if (Configuration.THIRD_KILL_EVENT) {
            mobKills.incrementAndGet();
        }
    }

    public int getThirdKillBonus(int hours, int exp) {
        if (Configuration.THIRD_KILL_EVENT) {
            if (mobKills.get() >= 3) {
                mobKills.set(0);
            }
            return (int) (getThirdKillPercentage(hours) / 100f * exp);
        }
        return 0;
    }

    public int getThirdKillPercentage(int hours) {
        int mod = 0;
        if (Configuration.THIRD_KILL_EVENT) {
            switch (hours) {
                case 0:
                    mod = 0;
                    break;
                case 1:
                    mod = 30;
                    break;
                case 2:
                    mod = 100;
                    break;
                case 3:
                    mod = 150;
                    break;
                case 4:
                    mod = 180;
                    break;
                default:
                    mod = 200;
                    break;
            }
        }
        return mod;
    }
    
    public void gainMeso(int meso) {
        gainMeso(meso, false);
    }
    
    public void gainMeso(int meso, boolean quest) {
        int curMeso = getStat(MESOS);
        if (curMeso + meso > 0) {
            addToStat(MESOS, meso);
            
            getClient().write(PacketCreator.showMesoGain(meso, quest));
            applyChangedStats(true);
        } else {  
            // overflow or underflow (possibly exploit), 
            // this would generally result from not checking prior
        }
    }

    public void gainExp(int baseExp, boolean killer) {
        gainExp(baseExp, false, killer);
    }

    // since this is a critical part of our code we may experience
    // concurrency problems here if for some reason there was no
    // lock to prevent problems from happening. Just decided to
    // make all accesses to this method flagged as synchronized
    // mainly to save headache trauma. Odin decided to just use
    // an atomic reference for this, but I don't think that would
    // necessarily work for the sake of what we want to happen...
    // May be interesting just to assign a map to have a task group
    // and handle gainExp by invoking this method after monster kill
    // as to make our sessions progress faster, but maybe later...
    public synchronized void gainExp(int baseEXP, boolean quest, boolean killer) {
        int hours = (int) ((System.currentTimeMillis() - loginTime) / 3600000);

        // XXX modify experience by experience coupon as well
        int exp = (int) (baseEXP * (Service.getInstance().getWorld().getExperienceModifier() / 100f));
        if (hasAppliedStatEffect(HOLY_SYMBOL)) { // XXX may only apply if > 1 player in party on a map
            exp += (int) (exp * getAppliedStatEffect(HOLY_SYMBOL) / 100f);
        }
        int eventBonus = (int) (exp * (Configuration.EVENT_EXP_MOD / 100f)); // non-stacking event (kinda like 2x)
        int weddingBonus = 0; // not sure about the specifics of this bonus occurs, but can be used as a feature later
        int partyBonusPercentage = party != null ? party.getPartyBonus(field) : 0;
        int partyBonus = (int) ((partyBonusPercentage / 100f) * exp);
        partyBonus = (int) (partyBonus * Configuration.PARTY_EXP_MOD > 0 ? (Configuration.PARTY_EXP_MOD / 100f) : 0);
        int thirdKillBonus = getThirdKillBonus(hours, exp);
        int thirdKillBonusPercentage = getThirdKillPercentage(hours);

        int xp = exp + eventBonus + weddingBonus + partyBonus + thirdKillBonus;

        if (getStat(LEVEL) < 200) {
            addToStat(EXP, xp);
            int toNext = ExperienceTable.forNextLevel(getStat(LEVEL));
            if (getStat(EXP) >= toNext) {
                changeStat(EXP, getStat(EXP) - toNext);
                levelUp();
                toNext = ExperienceTable.forNextLevel(getStat(LEVEL) + 1);
                if (getStat(EXP) >= toNext) { // handled this like GMS
                    changeStat(EXP, toNext - 1);
                }
            }
        } else {
            changeStat(EXP, 0);
        }
        // the base exp has to include partyBonus for this function to work
        getClient().write(PacketCreator.showEXPGain(exp + partyBonus,
                eventBonus, weddingBonus,
                partyBonusPercentage != 0 ? (partyBonusPercentage - 100) : 0,
                thirdKillBonusPercentage, hours,
                quest, killer));
        applyChangedStats();
    }

    public void levelUp() {
        int prefix = getStat(JOB) / 100;
        
        updateHPMP(Stat.MAXHP.getValue() | Stat.MAXMP.getValue(), true);
        
        incrementStat(LEVEL);
        if (getStat(JOB) != Job.BEGINNER.getId()) {
            addToStat(SP, 3);
        }
        
        if (getStat(LEVEL) < 11 && prefix == 0) {
            addToStat(STR, 4);
            addToStat(DEX, 1);
        } else {
            addToStat(AP, 5);
        }
        
        recalculateLocalStats();

        changeStat(HP, realMaxHealth);
        changeStat(MP, realMaxMana);

        applyChangedStats();

        // XXX foreign effect for level up
        // XXX guild notification
        // XXX party update
    }
    
    

    public void changeJob(int id) {
        changeStat(JOB, id);

        int prefix = id / 100;
        int suffix = id % 10;
        
        incrementStat(SP);
        // XXX handle this in script
        if (suffix < 1) { // hp/mp bonus
            int divSuf = id % 100;
            switch (prefix) {
                case 1: // warrior
                    if (divSuf == 0) {
                        HPInternal(200 + r.nextInt(50));
                        changeStat(STR, 35);
                        changeStat(DEX, 4);
                        changeStat(INT, 4);
                        changeStat(LUK, 4);
                        changeStat(AP, 23);
                    } else { // XXX data seems to be inconsistent here, but come back later
                        HPInternal(300 + r.nextInt(50));
                        MPInternal(100 + r.nextInt(50));
                    }
                    break;
                case 2: // mage
                    if (divSuf == 0) {
                        MPInternal(100 + r.nextInt(50));
                        changeStat(STR, 4);
                        changeStat(DEX, 4);
                        changeStat(INT, 20);
                        changeStat(LUK, 4);
                        changeStat(AP, 28);
                    } else {
                        MPInternal(450 + r.nextInt(50));
                    }
                    break;
                case 3: // bowman
                case 4: // thief
                case 5: // pirate
                    if (divSuf == 0) {
                        HPInternal(100 + r.nextInt(50));
                        MPInternal(30 + r.nextInt(20));

                        changeStat(STR, 4);
                        changeStat(DEX, 20);
                        changeStat(INT, 4);
                        changeStat(LUK, 4);
                        changeStat(AP, 38);
                        switch (prefix) {
                            case 3:
                            case 4:
                                addToStat(DEX, 5);
                                subtractFromStat(AP, 5);
                                break;
                            default:
                                break;
                        }
                    } else {
                        HPInternal(300 + r.nextInt(50));
                        MPInternal(150 + r.nextInt(50));
                    }
                    break;
                default:
                    break;
            }
            if (getStat(LEVEL) > 10 && divSuf == 0) { // give AP back for overleveling, so nice
                addToStat(AP, (getStat(LEVEL) - 11) * 5);
            }
        } else {
            addToStat(AP, 5);
            if (suffix == 2) {
                addToStat(SP, 2);
            }
        }
        applyChangedStats();

        // XXX show job change effect to third party
    }
    
    public void updateHPMP(int statupMask, boolean fromLevel) {
        int prefix = getStat(JOB) / 100;
        if (prefix < 0 || prefix > Constants.MAX_JOB_PREFIX) return;
        
        boolean updateHP = (statupMask & Stat.MAXHP.getValue()) != 0;
        boolean updateMP = (statupMask & Stat.MAXMP.getValue()) != 0;
        
        // skill information loading can be fixed up later
        // for now, this should work
        int skill = 0, x = 0, y = 0;
        switch (prefix) {
            case 1:
                if (updateHP)
                    skill = Warrior.IMPROVED_MAXHP_INCREASE;
                break;
            case 2:
                if (updateMP)
                    skill = Magician.IMPROVED_MAXMP_INCREASE;
                break;
            case 5:
                if (updateHP)
                    skill = Buccaneer.IMPROVE_MAXHP;
                break;
            default:
                break;
        }
        
        if (skill != 0) {
            int skillLevel = getSkillLevel(skill);
            
            if (skillLevel > 0) {
                SkillData info = GameDatabase.getSkill(skill);
                
                x = info.getProperty(skillLevel, "x");
                y = info.getProperty(skillLevel, "y");
            }
        }
        
        int index = (prefix * 6) + (fromLevel ? 0 : 3);
        
        if (updateHP) {
            HPMPInternal(Constants.HP_GAINS, 
                    index, 
                    realStrength, 
                    x, 
                    y, 
                    fromLevel, 
                    this::HPInternal);
        }
        
        if (updateMP) {
            HPMPInternal(Constants.MP_GAINS, 
                    index, 
                    realIntelligence, 
                    x, 
                    y, 
                    fromLevel, 
                    this::MPInternal);
        }
    }

    public void addHealthAP() {
        healthAP++;
        
        updateHPMP(Stat.MAXHP.getValue(), false);
    }

    public void addManaAP() {
        manaAP++;
        
        updateHPMP(Stat.MAXMP.getValue(), false);
    }
    
    private void HPMPInternal(int[] data, int index, int scaler, int x, int y, boolean fromLevel, Consumer<Integer> internal) {  
        int min   = data[index], 
            max   = data[index + 1],
            scale = data[index + 2],
            total = min + 
                    r.nextInt(max - min) +
                    scaler * scale / 200 +
                    (fromLevel ? x : y);

        internal.accept(total);
    }
    
    private void HPInternal(int val) {
        addToStat(MAXHP, val);
        if (getStat(MAXHP) <= 50) {
            changeStat(MAXHP, 50);
        }
        if (getStat(MAXHP) > 30000) {
            changeStat(MAXHP, 30000);
        }
    }

    private void MPInternal(int val) {
        addToStat(MAXMP, val);
        if (getStat(MAXHP) <= 5) {
            changeStat(MAXHP, 5);
        }
        if (getStat(MAXMP) > 30000) {
            changeStat(MAXMP, 30000);
        }
    }

    public void revive() {
        int target = cField.getReturnField();

        // XXX check for safety charm and anything else that
        // prevents EXP loss
        if (getStat(LEVEL) < Constants.MAX_LEVEL) {
            double penalty;
            int endingExp,
                prefix = getStat(JOB) / 100,
                expToNextLevel = ExperienceTable.forNextLevel(getStat(LEVEL));

            if (cField.isTown()) {
                penalty = 0.01D;
            } else {
                if (prefix == 3) { // archers have reduced exp loss, apparently...
                    penalty = 0.08D;
                } else {
                    penalty = 0.20D;
                }

                penalty = penalty / (double) getStat(LUK) + 0.05D;
            }
            
            endingExp = getStat(EXP) - (int) (expToNextLevel * penalty);
            
            if (endingExp < 0) endingExp = 0;
            
            changeStat(EXP, endingExp);
        }
        
        changeStat(HP, 50);
        changeStat(MP, 5);

        stance = 0;

        changeField(FieldManager.getField(target));
        
        applyChangedStats();
    }

    public void recalculateLocalStats() {
        realMaxHealth = getStat(MAXHP);
        realMaxMana = getStat(MAXMP);
        realStrength = getStat(STR);
        realDexterity = getStat(DEX);
        realIntelligence = getStat(INT);
        realLuck = getStat(LUK);
        Iterator<Equip> eqps = getEquipped();
        while (eqps.hasNext()) {
            Equip e = eqps.next();

            realMaxHealth += e.getHp();
            realMaxMana += e.getMp();
            realStrength += e.getStr();
            realDexterity += e.getDex();
            realIntelligence += e.getInt();
            realLuck += e.getLuk();
        }
        if (hasAppliedStatEffect(HYPER_BODY_HP)
                && hasAppliedStatEffect(HYPER_BODY_MP)) {
            float nHP = realMaxHealth * (1 + (getAppliedStatEffect(HYPER_BODY_HP) / 100));
            float nMP = realMaxHealth * (1 + (getAppliedStatEffect(HYPER_BODY_MP) / 100));
            realMaxHealth = (int) nHP;
            realMaxMana = (int) nMP;
        }
        // XXX recalculate range info with other buffs applied
        realMaxHealth = (int) Math.min(realMaxHealth, 30000);
        realMaxMana = (int) Math.min(realMaxMana, 30000);
    }

    public void setStat(Stat k, int val) {
        stats.setStat(k, val);
    }

    public int getStat(Stat k) {
        return stats.getStat(k);
    }

    public void changeStat(Stat k, int val) {
        stats.changeStat(k, val);
    }

    public void addToStat(Stat k, int val) {
        stats.addTo(k, val);
    }

    public void subtractFromStat(Stat k, int val) {
        stats.subtractFrom(k, val);
    }

    public void incrementStat(Stat k) {
        stats.increment(k);
    }

    public void decrementStat(Stat k) {
        stats.decrement(k);
    }
    
    public void applyChangedStats() {
        applyChangedStats(false);
    }

    public void applyChangedStats(boolean item) {
        stats.applyChangedStats(this, item);
    }

    // this should only be called for specific cases
    // like when a player gains a skill through a quest
    public void addSkill(int id, int level, int mastery) {
        if (!skills.containsKey(id)) {
            skills.put(id, new SkillInfo(level, mastery));
            getClient().write(PacketCreator.updateSkill(id, getSkill(id)));
        }
    }

    public void applyBeginnerSP(int id) {
        int totalSP = getSkillLevel(Beginner.NIMBLE_FEET);
        totalSP += getSkillLevel(Beginner.RECOVERY);
        totalSP += getSkillLevel(Beginner.THREE_SNAILS);

        int maxSP = Math.min(getStat(LEVEL), 7) - 1;

        if (maxSP - totalSP > 0) {
            applySP(id, true);
        }
    }

    public void applySP(int id) {
        applySP(id, false);
    }

    private void applySP(int id, boolean beginner) {
        SkillData s = GameDatabase.getSkill(id);

        int maxLevel = s.getMaxLevel();
        boolean reqMastery = s.isInvisible() && !hasSkill(id);

        if (!reqMastery
                && getSkillLevel(id) < maxLevel) {
            if (!hasSkill(id)) {
                addSkill(id, 1, 0);
            } else {
                getSkill(id).level++;
            }
            getClient().write(PacketCreator.updateSkill(id, getSkill(id)));
            if (!beginner) {
                decrementStat(SP);
            }
        }

        applyChangedStats();
    }

    public void gainComboOrbs(int attackCount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void consumeComboOrbs(int skill) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void gainEnergyCharge(int attackCount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // XXX recode buffs
    public void giveStatusEffect(Effect s) {
        for (EffectStat iss : s.getStatups().keySet()) {
            cancelAppliedStatEffect(iss);
        }
        appliedStatuses.add(s);
        
        client.schedule(() -> removeStatusEffect(s), s.getDuration());
    }
    
    public void removeStatusEffect(Effect s) {
        appliedStatuses.remove(s);

        client.write(PacketCreator.cancelTemporaryStati(s));
    }

    public boolean hasAppliedStatEffect(EffectStat stat) {
        return appliedStatuses.stream().anyMatch(s -> s.hasStatup(stat));
    }

    public int getAppliedStatEffect(EffectStat stat) {
        synchronized (appliedStatuses) {
            for (Effect s : appliedStatuses) {
                if (s.hasStatup(stat)) {
                    return s.getStatupValue(stat);
                }
            }
        }
        return 0;
    }

    // XXX recode this
    public void cancelAppliedStatEffect(EffectStat stat) {
        List<Effect> toRemove = new LinkedList<>();
        
        synchronized (appliedStatuses) {
            for (Effect aS : appliedStatuses) {
                if (aS.hasStatup(stat)) {
                    aS.removeStatup(stat);
                }
                
                if (aS.getStatups().isEmpty()) {
                    toRemove.add(aS);
                }
            }
        }
        
        toRemove.forEach(this::removeStatusEffect);
    }
}
