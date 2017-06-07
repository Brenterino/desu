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
package client.packet;

import data.skill.Beginner;
import field.FieldItem;
import field.FieldLife;
import field.NPC;
import field.Portal;
import field.Reactor;
import field.monster.Monster;
import field.movement.Movement;
import java.awt.Point;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.Packet;
import net.PacketWriter;
import player.Player;
import player.Player.SkillInfo;
import player.attack.AttackData;
import player.community.Guild;
import player.item.Equip;
import player.item.Inventory;
import player.item.Inventory.Modification;
import player.item.Item;
import player.item.Minigame;
import player.item.Mount;
import player.item.Pet;
import player.item.Ring;
import player.item.Shop;
import player.stats.Effect;
import player.stats.EffectStat;
import static player.stats.EffectStat.*;
import player.stats.Stat;
import static player.stats.Stat.*;
import service.Configuration;
import util.TimeUtil;

/**
 *
 * @author Brent
 */
public class PacketCreator {

    private static Random r = new Random();

    private final static int REMOTE_BUFF_LEAD = 0x00FC0000; // stats that always get sent even if not applied

    private PacketCreator() {
    }

    public static Packet getHello(byte[] siv, byte[] riv) {
        PacketWriter pw = new PacketWriter(12);

        pw.writeShort(0x0E);
        pw.writeShort(Configuration.MAPLE_VERSION);
        pw.writeMapleString(Configuration.BUILD_VERSION);
        pw.write(riv);
        pw.write(siv);
        pw.write(Configuration.SERVER_TYPE);

        return pw.createPacket();
    }

    public static Packet getPing() {
        PacketWriter pw = new PacketWriter(2);

        pw.writeHeader(SendOpcode.PING);

        return pw.createPacket();
    }

    public static Packet changeField(int id, int spawn, int health) {
        PacketWriter pw = new PacketWriter(24);
        
        pw.writeHeader(SendOpcode.WARP_PLAYER);
        pw.writeInteger(Configuration.CHANNEL_ID - 1);
        pw.write(0x02); // field key
        pw.write(0x00); // whether or not we attach character data
        pw.writeShort(0); // some magic here

        // byte for res here (?)
        pw.writeInteger(id);
        pw.write(spawn);
        pw.writeShort(health);

        // boolean isChasing
        pw.write(0); // XXX teleport rock chasing byte
//        if (isChasing) {
//            pw.writeInt(targetX);
//            pw.writeInt(targetY);
//        }

        pw.writeLong(TimeUtil.getTimestamp(System.currentTimeMillis()));

        return pw.createPacket();
    }

    public static Packet playerLogin(Player p) {
        PacketWriter pw = new PacketWriter(64);

        pw.writeHeader(SendOpcode.WARP_PLAYER);

        pw.writeInteger(Configuration.CHANNEL_ID - 1);

        pw.write(0x01); // field key
        pw.write(0x01); // attach character data

        pw.writeShort(0x00);

        for (int i = 0; i < 3; i++) { // this is used to set the damage calculation seed
            pw.writeInteger(r.nextInt());
        }

        addPlayerInfo(pw, p);

        pw.writeLong(TimeUtil.getTimestamp(System.currentTimeMillis()));

        return pw.createPacket();
    }

    private static void addPlayerInfo(PacketWriter pw, Player p) {
        pw.writeLong(-1);

        addPlayerStats(pw, p);

        pw.write(p.getBuddylist().getCapacity());

        addInventoryInfo(pw, p);

        addSkillInfo(pw, p);

        addQuestInfo(pw, p);

        addRingInfo(pw, p);

        addTeleportRockInfo(pw, p);

        pw.writeShort(0);

        pw.writeShort(0);
    }

    private static void addPlayerStats(PacketWriter pw, Player p) {
        pw.writeInteger(p.getId());
        pw.writeString(p.getName());
        pw.fill(0x00, 13 - p.getName().length());
        pw.write(p.getGender());
        pw.write(p.getStat(SKIN)); // irregular

        FACE.serialize(pw, p);
        HAIR.serialize(pw, p);

        for (int i = 0; i < 3; i++) {
            Pet active = p.getActivePet(i);
            if (active != null) {
                pw.writeLong(p.getActivePet(i).getItemId());
            } else {
                pw.writeLong(0);
            }
        }

        LEVEL.serialize(pw, p);
        JOB.serialize(pw, p);
        STR.serialize(pw, p);
        DEX.serialize(pw, p);
        INT.serialize(pw, p);
        LUK.serialize(pw, p);
        HP.serialize(pw, p);
        MAXHP.serialize(pw, p);
        MP.serialize(pw, p);
        MAXMP.serialize(pw, p);
        AP.serialize(pw, p);
        SP.serialize(pw, p);
        EXP.serialize(pw, p);

        pw.writeShort(p.getStat(FAME)); // irregular

        pw.writeInteger(p.isMarried() ? 1 : 0);
        pw.writeInteger(p.getCurrentFieldId());
        pw.write(p.getSpawnpoint());
        pw.writeInteger(0x00);
    }

    private static void addInventoryInfo(PacketWriter pw, Player p) {
        MESOS.serialize(pw, p);

        pw.write(p.getEquipInventory().getCapacity());
        pw.write(p.getUseInventory().getCapacity());
        pw.write(p.getSetupInventory().getCapacity());
        pw.write(p.getEtcInventory().getCapacity());
        pw.write(p.getCashInventory().getCapacity());
        
        for (Item i : p.getEquipInventory()) {
            if (i.getSlot() < 0 && !i.isCash()) {
                addItemInfo(pw, i);
            }
        }
        pw.write(0x00);
        
        for (Item i : p.getEquipInventory()) {
            if (i.getSlot() < 0 && i.isCash()) {
                addItemInfo(pw, i);
            }
        }
        pw.write(0x00);

        for (Item i : p.getEquipInventory()) {
            if (i.getSlot() > 0) { // lol h4x
                addItemInfo(pw, i);
            }
        }
        pw.write(0x00);
        
        for (Item i : p.getUseInventory()) {
            addItemInfo(pw, i);
        }
        pw.write(0x00);
        
        for (Item i : p.getSetupInventory()) {
            addItemInfo(pw, i);
        }
        pw.write(0x00);
        
        for (Item i : p.getEtcInventory()) {
            addItemInfo(pw, i);
        }
        pw.write(0x00);
        
        for (Item i : p.getCashInventory()) {
            addItemInfo(pw, i);
        }
        pw.write(0x00);
    }

    private static void addItemInfo(PacketWriter pw, Item i) {
        addItemInfo(pw, i, false);
    }

    private static void addItemInfo(PacketWriter pw, Item i, boolean ignorePos) {
        int pos = Math.abs(i.getSlot());
        Equip e = i instanceof Equip ? (Equip) i : null;
        if (!ignorePos) {
            if (pos > 100) { // for cash equipment
                pw.write(0);
            }
            pw.write(pos > 100 ? pos - 100 : pos);
        }

        pw.write(e != null ? 1 : i.isPet() ? 3 : 2);

        pw.writeInteger(i.getItemId());

        pw.writeBool(i.isCash()); // no idea
        if (i.isCash()) {
            pw.writeLong(i.getDatabaseId());
        }
        pw.writeLong(TimeUtil.getTimestamp(i.getExpiration()));
        if (i.isPet()) {
            Pet p = (Pet) i;

            pw.writeString(p.getName());
            pw.fill(0x00, 13 - p.getName().length());
            pw.write(p.getLevel());
            pw.writeShort(p.getCloseness());
            pw.write(p.getFullness());

            pw.writeLong(TimeUtil.getTimestamp(i.getExpiration())); // same timestamp maybe?
            pw.writeInteger(0x00);
            return;
        }

        if (e == null) {
            pw.writeShort(i.getQuantity());
            pw.writeMapleString(i.getName());
            pw.writeShort(i.getFlag());
            if (i.isThrowingStar() || i.isBullet()) {
                pw.write(0x02, 0x00, 0x00, 0x00, 0x54, 0x00, 0x00, 0x34);
            }
            return;
        }
        pw.write(e.getSlots());
        pw.write(e.getUpgrades());
        pw.writeShort(e.getStr());
        pw.writeShort(e.getDex());
        pw.writeShort(e.getInt());
        pw.writeShort(e.getLuk());
        pw.writeShort(e.getHp());
        pw.writeShort(e.getMp());
        pw.writeShort(e.getAttack());
        pw.writeShort(e.getMagic());
        pw.writeShort(e.getWdef());
        pw.writeShort(e.getMdef());
        pw.writeShort(e.getAcc());
        pw.writeShort(e.getAvoid());
        pw.writeShort(e.getHands());
        pw.writeShort(e.getSpeed());
        pw.writeShort(e.getJump());
        pw.writeMapleString(e.getName());
        pw.write(e.getFlag());
        // byte for unequipped ring (?), but idk zzz
        if (!i.isCash()) {
            pw.write(0);
            pw.write(0); // byte? idk just random byte 420 blaze it
            pw.write(e.getLevel());
            pw.writeInteger(e.getExperience());
            pw.writeShort(0);
        }
    }

    private static void addSkillInfo(PacketWriter pw, Player p) {
        Map<Integer, Player.SkillInfo> skills = p.getSkills();
        pw.writeShort(skills.size());
        skills.forEach((s, i) -> {
            pw.writeInteger(s);
            pw.writeShort(i.level);
            pw.writeShort(0); // expiration (?)
            if (i.hasMastery()) {
                pw.writeInteger(i.mastery);
            }
        });
        Map<Integer, Player.Cooldown> cooldowns = p.getCooldowns();
        pw.writeShort(cooldowns.size());
        cooldowns.forEach((s, c) -> {
            pw.writeInteger(s);
            pw.writeShort((int) (c.remaining() / 1000));
        });
    }

    private static void addQuestInfo(PacketWriter pw, Player p) {
        Map<Integer, Player.QuestInfo> active = p.getActiveQuests();

        pw.writeShort(active.size());

        active.forEach((i, q) -> {
            pw.writeShort(i);
            pw.writeMapleString(q.questData);
            if (q.isMedalQuest()) {
                pw.writeShort(q.getMedalQuestId());
                pw.writeMapleString(Integer.toString(q.getMedalQuestProgress()));
            }
        });

        Map<Integer, Player.QuestInfo> completed = p.getCompletedQuests();

        pw.writeShort(completed.size());

        completed.forEach((i, q) -> {
            pw.writeShort(i);
            pw.writeLong(TimeUtil.getTimestamp(q.completionTime));
        });
    }

    private static void addRingInfo(PacketWriter pw, Player p) {
        List<Ring> crush = new ArrayList<>();
        List<Ring> friendship = new ArrayList<>();
        Ring wedding = null;

        Iterator<Equip> eqpd = p.getEquipped();
        while (eqpd.hasNext()) {
            Equip i = eqpd.next();
            if (i.isRing()) {
                Ring ring = (Ring) i;
                if (ring.isFriendshipRing()) {
                    friendship.add(ring);
                } else if (ring.isCrushRing()) {
                    crush.add(ring);
                } else if (ring.isWeddingRing()) {
                    wedding = ring;
                }
            }
        }

        Collections.sort(crush);
        Collections.sort(friendship);

        pw.writeShort(crush.size());
        for (Ring ring : crush) {
            pw.writeInteger(ring.getPartnerCharacterId());
            pw.writeString(ring.getPartnerName());
            pw.fill(0, 13 - ring.getPartnerName().length());
            pw.writeLong(ring.getDatabaseId());
            pw.writeLong(ring.getPartnerRingDatabaseId());
        }

        pw.writeShort(friendship.size());
        for (Ring ring : friendship) {
            pw.writeInteger(ring.getPartnerCharacterId());
            pw.writeString(ring.getPartnerName());
            pw.fill(0, 13 - ring.getPartnerName().length());
            pw.writeLong(ring.getDatabaseId());
            pw.writeLong(ring.getPartnerRingDatabaseId());
            pw.writeInteger(ring.getItemId());
        }

        pw.writeBool(wedding != null);
        pw.write(0);
        if (wedding != null) { // upholding gender binaries, RIP
            int groom = p.getGender() == 0 ? p.getId() : wedding.getPartnerCharacterId();
            int bride = p.getGender() != 0 ? p.getId() : wedding.getPartnerCharacterId();
            String groomName = p.getGender() == 0 ? p.getName() : wedding.getPartnerName();
            String brideName = p.getGender() != 0 ? p.getName() : wedding.getPartnerName();
            
            pw.writeInteger(wedding.getMarriageId());
            pw.writeInteger(groom);
            pw.writeInteger(bride);
            pw.writeShort(3); // short for whether or not they're actually "married", I'm lazy
            pw.writeInteger(wedding.getItemId()); // groom ring
            pw.writeInteger(wedding.getItemId()); // bride ring
            pw.writeString(groomName);
            pw.fill(0, 13 - groomName.length());
            pw.writeString(brideName);
            pw.fill(0, 13 - brideName.length());
        }
        
        pw.writeShort(0); // is this part of ^?
    }

    private static void addTeleportRockInfo(PacketWriter pw, Player p) {
        int[] teleportRock = p.getSavedLocations();
        int[] vipTeleportRock = p.getVIPSavedLocations();
        for (int i = 0; i < 5; i++) {
            pw.writeInteger(teleportRock[i]);
        }
        for (int i = 0; i < 10; i++) {
            pw.writeInteger(vipTeleportRock[i]);
        }
    }

    private static void addMask(ByteBuffer bb, Player p, EffectStat s) {
        if (p.hasAppliedStatEffect(s)) {
            bb.putInt(4 * (s.getPosition() - 1), s.getValue());
        }
    }

    private static void serializeStat(PacketWriter pw, Player p, EffectStat s, int t) {
        if (p.hasAppliedStatEffect(s)) {
            switch (t) {
                case 1:
                    pw.write(p.getAppliedStatEffect(s));
                    break;
                case 2:
                    pw.writeShort(p.getAppliedStatEffect(s));
                    break;
                case 4:
                    pw.writeInteger(p.getAppliedStatEffect(s));
                    break;
                default:
                    break;
            }
        }
    }

    private static void addForeignEffects(PacketWriter pw, Player p) {
        ByteBuffer mask = ByteBuffer.allocate(16);

        mask.putInt(0, REMOTE_BUFF_LEAD); // these generally get sent

        addMask(mask, p, SPEED);
        addMask(mask, p, COMBO);
        addMask(mask, p, CHARGE);
        addMask(mask, p, STUN);
        addMask(mask, p, DARKNESS);
        addMask(mask, p, SEAL);
        addMask(mask, p, WEAKEN);
        addMask(mask, p, CURSE);
        addMask(mask, p, POISON);
        addMask(mask, p, SHADOW_PARTNER);
        addMask(mask, p, DARK_SIGHT);
        addMask(mask, p, SOUL_ARROW);
        addMask(mask, p, MORPH);
        addMask(mask, p, GHOST_MORPH);
        addMask(mask, p, SEDUCE);
        addMask(mask, p, SHADOW_CLAW);
        addMask(mask, p, BANISH);
        addMask(mask, p, BARRIER);
        addMask(mask, p, INVERSE_CONTROLS);
        addMask(mask, p, RESPECT_P_IMMUNE);
        addMask(mask, p, RESPECT_M_IMMUNE);
        addMask(mask, p, DEFENSE_ATTACK);
        addMask(mask, p, DEFENSE_STATE);

        pw.write(mask.array());

        serializeStat(pw, p, SPEED, 1);
        serializeStat(pw, p, COMBO, 1);
        serializeStat(pw, p, CHARGE, 4);
        serializeStat(pw, p, STUN, 4);
        serializeStat(pw, p, DARKNESS, 4);
        serializeStat(pw, p, SEAL, 4);
        serializeStat(pw, p, WEAKEN, 4);
        serializeStat(pw, p, CURSE, 4);
        serializeStat(pw, p, POISON, 4);
        serializeStat(pw, p, POISON, 2);
        serializeStat(pw, p, MORPH, 2);
        serializeStat(pw, p, GHOST_MORPH, 2);
        serializeStat(pw, p, SEDUCE, 4);
        serializeStat(pw, p, SHADOW_CLAW, 4);
        serializeStat(pw, p, BANISH, 4);
        serializeStat(pw, p, BARRIER, 4);
        serializeStat(pw, p, INVERSE_CONTROLS, 4);
        serializeStat(pw, p, RESPECT_P_IMMUNE, 4);
        serializeStat(pw, p, RESPECT_M_IMMUNE, 4);
        serializeStat(pw, p, DEFENSE_ATTACK, 4);
        serializeStat(pw, p, DEFENSE_STATE, 4);

        pw.write(0); // nDefenseAtt_CS
        pw.write(0); // nDefenseATT_State

        int CHAR_MAGIC = r.nextInt();

        // ENERGY_CHARGE
        pw.writeInteger(0);
        pw.writeInteger(0);
        pw.write(0);
        pw.writeInteger(CHAR_MAGIC);
        pw.writeShort(0);

        // DASH_SPEED
        pw.writeInteger(0);
        pw.writeInteger(0);
        pw.write(0);
        pw.writeInteger(CHAR_MAGIC);
        pw.writeShort(0);

        // DASH_JUMP
        pw.writeInteger(0);
        pw.writeInteger(0);
        pw.write(0);
        pw.writeInteger(CHAR_MAGIC);
        pw.writeShort(0);

        // RIDING_MOUNT
        pw.writeInteger(0);
        pw.writeInteger(0);
        pw.write(0);
        pw.writeInteger(CHAR_MAGIC);
        pw.writeShort(0);

        Mount mount = (Mount) p.getEquipInventory().get(-18);

        if (p.hasAppliedStatEffect(MONSTER_RIDING) && mount != null) {
            pw.writeInteger(mount.getItemId());
            pw.writeInteger(Beginner.MONSTER_RIDER);
        } else {
            pw.writeLong(0);
        }
        pw.write(0);
        pw.writeInteger(CHAR_MAGIC);

        // PARTY_BOOSTER
        pw.writeInteger(0);
        pw.writeInteger(0);
        pw.write(0);
        pw.writeInteger(CHAR_MAGIC);
        pw.write(0);
        pw.writeInteger(CHAR_MAGIC);
        pw.writeShort(0);

        // GUIDED_BULLET
        pw.writeInteger(0);
        pw.writeInteger(0);
        pw.write(0);
        pw.writeInteger(CHAR_MAGIC);
        pw.writeInteger(0); // XXX this is targeted mob ID
    }

    public static Packet spawnPlayer(Player p) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.SPAWN_PLAYER);

        pw.writeInteger(p.getId());

        pw.writeMapleString(p.getName());

        if (p.getGuild() != null) {
            Guild g = p.getGuild();

            pw.writeMapleString(g.getName());
            pw.writeShort(g.getLogoBG());
            pw.write(g.getLogoBGColor());
            pw.writeShort(g.getLogo());
            pw.write(g.getLogoColor());
        } else {
            pw.writeLong(0);
        }

        addForeignEffects(pw, p);
        JOB.serialize(pw, p);
        addPlayerLooks(pw, p);
        pw.writeInteger(p.getCashInventory().getItemCount(5110000));
        pw.writeInteger(p.getItemEffect());
        pw.writeInteger(p.getChair());
        pw.writePosition(p.getPosition());
        pw.write(p.getStance());
        pw.writeShort(p.getFh());
        pw.writeBool(p.isGM()); // GM Effect (?)

        for (int i = 0; i < 3; i++) {
            Pet active = p.getActivePet(i);
            if (active != null) {
                addPetInfo(pw, active, false);
            }
        }
        pw.write(0); // end of pets byte

        Mount mount = (Mount) p.getEquipInventory().get(-18);
        if (mount == null) {
            pw.writeInteger(1);
            pw.writeLong(0);
        } else {
            pw.writeInteger(mount.getMountLevel());
            pw.writeInteger(mount.getMountExp());
            pw.writeInteger(mount.getFatigue());
        }

        if (p.getShop() != null) {
            addPlayerShopInfo(pw, p.getShop());
        } else if (p.getMinigame() != null) {
            addMinigameInfo(pw, p.getMinigame());
        } else {
            pw.write(0);
        }

        pw.writeBool(p.getChalkboardMessage() != null);
        if (p.getChalkboardMessage() != null) {
            pw.writeMapleString(p.getChalkboardMessage());
        }

        addRingLooks(pw, p);
        addMarriageRingLooks(pw, p);

        pw.fill(0x00, 3);

        pw.write(0); // CarnivalPQ team byte

        return pw.createPacket();
    }

    public static Packet removePlayer(int id) {
        PacketWriter pw = new PacketWriter(6);

        pw.writeHeader(SendOpcode.REMOVE_PLAYER);

        pw.writeInteger(id);

        return pw.createPacket();
    }

    private static void addPlayerLooks(PacketWriter pw, Player p) {
        addPlayerLooks(pw, p, false);
    }

    private static void addPlayerLooks(PacketWriter pw, Player p, boolean mega) {
        pw.write(p.getGender());
        pw.write(p.getStat(SKIN)); // irregular

        FACE.serialize(pw, p);

        pw.writeBool(!mega);

        HAIR.serialize(pw, p);

        addCharacterEquipment(pw, p);

        for (int i = 0; i < 3; i++) {
            Pet pet = p.getActivePet(i);
            pw.writeInteger(pet != null ? pet.getItemId() : 0);
        }
    }

    private static void addCharacterEquipment(PacketWriter pw, Player p) {
        HashMap<Byte, Integer> base = new HashMap<>();
        HashMap<Byte, Integer> mask = new HashMap<>();
        Iterator<Equip> eqpd = p.getEquipped();
        while (eqpd.hasNext()) {
            Equip item = eqpd.next();
            byte pos = (byte) Math.abs(item.getSlot());
            if (pos < 100 && !base.containsKey(pos)) {
                base.put(pos, item.getItemId());
            } else if (pos > 100 && pos != 111) {
                pos -= 100;
                if (base.containsKey(pos)) {
                    mask.put(pos, base.get(pos));
                }
                base.put(pos, item.getItemId());
            } else if (base.containsKey(pos)) {
                mask.put(pos, item.getItemId());
            }
        }
        base.forEach((k, v) -> pw.write(k).writeInteger(v));
        pw.write(0xFF);
        mask.forEach((k, v) -> pw.write(k).writeInteger(v));
        pw.write(0xFF);
        pw.writeInteger(p.getEquipInventory().isSlotTaken(-111)
                ? p.getEquipInventory().get(-111).getItemId() : 0);
    }

    private static void addPetInfo(PacketWriter pw, Pet active, boolean show) {
        pw.write(1);
        if (show) {
            pw.write(0);
        }

        pw.writeInteger(active.getItemId());
        pw.writeMapleString(active.getName());
        pw.writeLong(active.getDatabaseId());
        pw.writePosition(active.getPosition());
        pw.write(active.getStance());
        pw.writeInteger(active.getFh());
    }

    private static void addRingLooks(PacketWriter pw, Player p) {
        List<Ring> rings = new ArrayList<>();
        Iterator<Equip> eqpd = p.getEquipped();
        while (eqpd.hasNext()) {
            Equip e = eqpd.next();
            if (e.isRing()) {
                Ring ring = (Ring) e;
                if (!ring.isWeddingRing()) {
                    rings.add(ring);
                }
            }
        }
        Collections.sort(rings);
        if (!rings.isEmpty()) {
            for (Ring ring : rings) {
                pw.write(1);
                pw.writeLong(ring.getDatabaseId());
                pw.writeLong(ring.getPartnerRingDatabaseId());
                pw.writeInteger(ring.getItemId());
            }
        } else {
            pw.write(0);
        }
    }

    private static void addMarriageRingLooks(PacketWriter pw, Player p) {
        Ring slavery = null;
        Iterator<Equip> eqp = p.getEquipped();
        while (eqp.hasNext()) {
            Equip e = eqp.next();
            if (e.isRing()) {
                Ring ring = (Ring) e;
                if (ring.isWeddingRing()) {
                    slavery = ring;
                    break;
                }
            }
        }
        if (slavery != null) {
            pw.writeInteger(p.getId());
            pw.writeInteger(slavery.getPartnerCharacterId());
            pw.writeInteger(slavery.getDatabaseId());
        } else {
            pw.write(0);
        }
    }

    private static void addPlayerShopInfo(PacketWriter pw, Shop s) {
        pw.write(4);
        pw.writeInteger(s.getOid());
        pw.writeMapleString(s.getDescription());
        pw.writeBool(s.isPrivate());
        pw.write(0x00);
        pw.write(0x01); // XXX check this for shops (next available slot?)
        pw.write(s.hasAvailableSlot() ? s.getVisitorCount() : 1);
        pw.write(0);
    }

    private static void addMinigameInfo(PacketWriter pw, Minigame g) {
        pw.write(g.getGameType());
        pw.writeInteger(g.getOid());
        pw.writeMapleString(g.getDescription());
        pw.writeBool(g.isPrivate());
        pw.write(g.getPieceType());
        pw.write(g.getPlayerCount());
        pw.write(2);
        pw.writeBool(g.hasFreeSlot());
    }

    public static Packet spawnMonster(Monster target, boolean nSpawn) {
        return spawnMonster(target, nSpawn, 0);
    }

    public static Packet spawnMonster(Monster target, boolean nSpawn, int effect) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.SPAWN_MONSTER);

        addMonsterInformation(pw, target, nSpawn, effect);

        return pw.createPacket();
    }

    public static Packet giveMonsterControl(Monster target, boolean nSpawn, boolean aggro) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.MONSTER_CONTROL);

        pw.write(aggro ? 0x02 : 0x01);

        addMonsterInformation(pw, target, nSpawn, 0);

        return pw.createPacket();
    }

    public static Packet removeMonsterControl(Monster target) {
        PacketWriter pw = new PacketWriter(7);

        pw.writeHeader(SendOpcode.MONSTER_CONTROL);

        pw.write(0);

        pw.writeInteger(target.getOid());

        return pw.createPacket();
    }

    public static Packet removeMonster(int oid, boolean animated) {
        PacketWriter pw = new PacketWriter(7);

        pw.writeHeader(SendOpcode.REMOVE_MONSTER);

        pw.writeInteger(oid);

        pw.writeBool(animated);

        return pw.createPacket();
    }

    public static Packet showMonsterHP(int oid, int remainingHP) {
        PacketWriter pw = new PacketWriter(7);

        pw.writeHeader(SendOpcode.SHOW_MONSTER_HP);

        pw.writeInteger(oid);

        pw.write(remainingHP);

        return pw.createPacket();
    }

    private static void addMonsterInformation(PacketWriter pw, Monster mob, boolean nSpawn, int effect) {
        pw.writeInteger(mob.getOid());
        pw.write(mob.getController() == FieldLife.NO_CONTROLLER ? 5 : 1);
        pw.writeInteger(mob.getId());
        pw.fill(0x00, 3); // 3 in 62
        pw.write(0x08);
        pw.writeInteger(0x00);
        pw.writePosition(mob.getPosition());
        pw.write(mob.getStance());
        pw.writeShort(0x00);
        pw.writeShort(mob.getFh());
        if (effect > 0) {
            pw.write(effect);
            pw.write(0x00);
            pw.writeShort(0x00);
            if (effect == 15) {
                pw.write(0x00);
            }
        }
        if (nSpawn) {
            pw.writeShort(-2);
        } else {
            pw.writeShort(-1);
        }
        pw.writeInteger(0x00);
    }

    public static Packet spawnNPC(NPC target) {
        PacketWriter pw = new PacketWriter(22);

        pw.writeHeader(SendOpcode.SPAWN_NPC);

        addNPCInfo(pw, target, true);

        return pw.createPacket();
    }

    public static Packet removeNPC(int oid, boolean animated) {
        PacketWriter pw = new PacketWriter(7);

        pw.writeHeader(SendOpcode.REMOVE_NPC);

        pw.writeInteger(oid);

        pw.writeBool(animated); // don't know what this is necessarily, but yolo

        return pw.createPacket();
    }

    public static Packet giveNPCControl(NPC target, boolean miniMap) {
        PacketWriter pw = new PacketWriter(23);

        pw.writeHeader(SendOpcode.NPC_CONTROL);

        pw.write(0x01);

        addNPCInfo(pw, target, miniMap);

        return pw.createPacket();
    }

    public static Packet removeNPCControl(NPC target) {
        PacketWriter pw = new PacketWriter(7);

        pw.writeHeader(SendOpcode.NPC_CONTROL);

        pw.write(0x00); // disable control

        pw.writeInteger(target.getOid());

        return pw.createPacket();
    }

    private static void addNPCInfo(PacketWriter pw, NPC npc, boolean miniMap) {
        pw.writeInteger(npc.getOid());
        pw.writeInteger(npc.getId());
        pw.writeShort(npc.getPosition().x);
        pw.writeShort(npc.getCy());
        pw.writeBool(npc.getF() != 1);
        pw.writeShort(npc.getFh());
        pw.writeShort(npc.getRx0());
        pw.writeShort(npc.getRx1());
        pw.writeBool(miniMap);
    }

    public static Packet spawnPortal(Portal target) {
        PacketWriter pw = new PacketWriter(10);

        pw.writeHeader(SendOpcode.SPAWN_PORTAL);

        pw.writeInteger(target.getPt());
        pw.writeInteger(target.getTm());
        if (target.getPosition() != null) {
            pw.writePosition(target.getPosition());
        }

        return pw.createPacket();
    }

    public static Packet spawnReactor(Reactor target) {
        PacketWriter pw = new PacketWriter(16);

        pw.writeHeader(SendOpcode.SPAWN_REACTOR);

        pw.writeInteger(target.getOid());
        pw.writeInteger(target.getId());
        pw.write(target.getState());
        pw.writePosition(target.getPosition());
        pw.write(0); // m_bFlip

        return pw.createPacket();
    }

    public static Packet removeReactor(Reactor target) {
        PacketWriter pw = new PacketWriter(11);

        pw.writeHeader(SendOpcode.REMOVE_REACTOR);

        pw.writeInteger(target.getOid());
        pw.write(target.getState());
        pw.writePosition(target.getPosition());

        return pw.createPacket();
    }
    
    public static Packet spawnFieldItem(FieldItem item, int enterType, Point src, Point dst, int delay) {
        PacketWriter pw = new PacketWriter(32);
        
        pw.writeHeader(SendOpcode.SPAWN_FIELD_ITEM);
        
        pw.write(enterType); // nEnterType
        pw.writeInteger(item.getOid()); // m_dwDropID
        pw.writeBool(item.isMeso()); // isMoney
        pw.writeInteger(item.getItemId()); // dropInfo
        pw.writeInteger(item.getOwner()); // owner party ID or owner ID
        pw.write(item.getOwnership()); // own type
        pw.writePosition(dst);
        pw.writeInteger(item.getOwnership() == 0 ? item.getSourceOid() : 0); // no idea
        
        if (enterType != 2) {
            pw.writePosition(src);
            pw.writeShort(delay);
        }
        
        if (!item.isMeso()) {
            pw.writeLong(TimeUtil.getTimestamp(item.getExpiration()));
        }
        pw.writeBool(!item.isPlayerDrop());
        
        return pw.createPacket();
    }
    
    public static Packet removeFieldItem(FieldItem item, int leaveType, int option) {
        PacketWriter pw = new PacketWriter(16);
        
        pw.writeHeader(SendOpcode.REMOVE_FIELD_ITEM);
        pw.write(leaveType);
        pw.writeInteger(item.getOid());
        if (leaveType > 1) {
            if (leaveType == 4) {
                pw.writeShort(option); // pet loot, is this supposed to be a byte
            } else {
                pw.writeInteger(option);
            }
        }
        
        return pw.createPacket();
    }

    // works for debuffs + buffs
    public static Packet applyTemporaryStati(Effect effect) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.GIVE_BUFF);

        effect.serialize(pw);
        
        Map<EffectStat, Integer> statups = effect.getStatups();

        statups.forEach((i, b) -> {
            pw.writeShort(b);
            pw.writeInteger(effect.getSkill());
            pw.writeInteger(effect.getDuration());
        });

        if (effect.hasStatup(MONSTER_RIDING)) {
            pw.writeInteger(effect.getStatupValue(MONSTER_RIDING));
        } else if (effect.hasStatup(HOMING_BEACON)) {
            pw.writeInteger(effect.getStatupValue(HOMING_BEACON));
        } else {
            pw.writeShort(0x00);
        }

        pw.writeShort(0x00); // this is delay before applying
        pw.write(0x00);

        return pw.createPacket();
    }
    
    public static Packet cancelTemporaryStati(Effect effect) {
        PacketWriter pw = new PacketWriter(32);
        
        pw.writeHeader(SendOpcode.CANCEL_BUFF);
        
        effect.serialize(pw);
        
        pw.write(0x03); // XXX not sure what this is (?), we can check later
                        // might be some delay meme
        
        return pw.createPacket();
    }

    public static Packet sendKeymap(Player p) {
        PacketWriter pw = new PacketWriter(453);

        pw.writeHeader(SendOpcode.KEYMAP);

        pw.write(0x00);

        Map<Integer, Player.KeyBinding> bindings = p.getBindings();

        for (int i = 0; i < 90; i++) {
            if (bindings.containsKey(i)) {
                Player.KeyBinding bind = bindings.get(i);
                pw.write(bind.getType());
                pw.writeInteger(bind.getAction());
            } else {
                pw.fill(0x00, 5);
            }
        }

        return pw.createPacket();
    }

    public static Packet sendCooldown(int skillId, int duration) {
        PacketWriter pw = new PacketWriter(10);

        pw.writeHeader(SendOpcode.COOLDOWN);

        pw.writeInteger(skillId);
        pw.writeInteger(duration);

        return pw.createPacket();
    }

    public static Packet movePlayer(int cid, Collection<Movement> mov) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.MOVE_PLAYER);

        pw.writeInteger(cid);

        pw.writeInteger(0x00);

        serializeMovement(pw, mov);

        return pw.createPacket();
    }

    public static Packet showAttack(int src, AttackData d) {
        return showAttack(src, d, 0, false, false);
    }

    public static Packet showAttack(int src, AttackData d, int projectile, boolean magic) {
        return showAttack(src, d, projectile, magic, !magic);
    }

    public static Packet showAttack(int src, AttackData d, int projectile, boolean magic, boolean ranged) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(ranged ? SendOpcode.RANGED_ATTACK : magic ? SendOpcode.MAGIC_ATTACK : SendOpcode.CLOSE_RANGE_ATTACK);

        pw.writeInteger(src);

        pw.write(d.attackInfo);

        if (d.skill > 0) {
            pw.write(d.isMesoExplosion() ? 0x1E : 0xFF);
            pw.writeInteger(d.skill);
        } else {
            pw.write(0);
        }
        pw.write(0);
        pw.write(d.stance);
        pw.write(d.speed);
        pw.write(0x0A);
        pw.writeInteger(projectile);
        d.damage.forEach((k, v) -> {
            if (v != null) {
                pw.writeInteger(k);
                pw.write(0xFF);
                for (Integer dmg : v) {
                    pw.writeInteger(dmg);
                }
            }
        });
        if (magic && d.charge != -1) {
            pw.writeInteger(d.charge);
        } else if (ranged) {
            pw.writeInteger(0);
        }
        return pw.createPacket();
    }

    public static Packet getNPCAction(short dialog, int oid, Point origin, Collection<Movement> mov) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.NPC_ACTION);

        pw.writeInteger(oid);

        pw.writeShort(dialog); // probably some other way this state is handled, but idk

        if (mov != null) {
            pw.writePosition(origin);

            serializeMovement(pw, mov);
        }

        return pw.createPacket();
    }

    public static Packet moveMonster(boolean useSkill, int s, int s1, int s2, int s3, int oid, Point origin, Collection<Movement> mov) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.MONSTER_ACTION);

        pw.writeInteger(oid);
        pw.writeBool(useSkill);
        pw.write(s).write(s1).write(s2).write(s3).write(0);

        pw.writePosition(origin);

        serializeMovement(pw, mov);

        return pw.createPacket();
    }

    public static Packet sendMonsterActionResponse(int oid, int move, int mp, boolean aggro) {
        return sendMonsterActionResponse(oid, move, mp, 0, 0, aggro);
    }

    public static Packet sendMonsterActionResponse(int oid, int move, int mp, int id, int level, boolean aggro) {
        PacketWriter pw = new PacketWriter(16);

        pw.writeHeader(SendOpcode.MONSTER_ACTION_RESPONSE);

        pw.writeInteger(oid);
        pw.writeShort(move);
        pw.writeBool(aggro);
        pw.writeShort(mp);
        pw.write(id);
        pw.write(level);

        return pw.createPacket();
    }

    private static void serializeMovement(PacketWriter pw, Collection<Movement> mov) {
        pw.write(mov.size());
        for (Movement m : mov) {
            m.serialize(pw);
        }
    }

    public static Packet getFacialExpression(int src, int emote) {
        PacketWriter pw = new PacketWriter(10);

        pw.writeHeader(SendOpcode.FACIAL_EXPRESSION);

        pw.writeInteger(src);
        pw.writeInteger(emote);

        return pw.createPacket();
    }

    public static Packet getFieldChat(int src, String msg, boolean white, int unk) {
        PacketWriter pw = new PacketWriter(10 + msg.length());

        pw.writeHeader(SendOpcode.FIELD_CHAT);

        pw.writeInteger(src);

        pw.writeBool(white);

        pw.writeMapleString(msg);

        pw.write(unk);

        return pw.createPacket();
    }
    
    public static Packet showItemGain(int id, int quantity) {
        PacketWriter pw = new PacketWriter(20);
        
        pw.writeHeader(SendOpcode.SHOW_STAT_CHANGE);
        pw.write(0);
        pw.write(0);
        pw.writeInteger(id);
        pw.writeInteger(quantity);
        pw.writeInteger(0);
        pw.writeInteger(0);
        
        return pw.createPacket();
    }
    
    public static Packet showItemGainQuest(int id, int quantity) {
        PacketWriter pw = new PacketWriter(12);
        
        pw.writeHeader(SendOpcode.SHOW_ITEM_GAIN);
        pw.write(3);
        pw.write(1);
        pw.writeInteger(id);
        pw.writeInteger(quantity);
        
        return pw.createPacket();
    }
    
    public static Packet showMesoGain(int gain, boolean quest) {
        PacketWriter pw = new PacketWriter(10);
        
        pw.writeHeader(SendOpcode.SHOW_STAT_CHANGE);
        
        // XXX do debug to find these values out
        if (!quest) {
            pw.write(0); // 'item' quest stat
            pw.write(1);
        } else {
            pw.write(5);
        }
        pw.writeInteger(gain);
        pw.writeShort(0); // meso gain from inet cafe
        
        return pw.createPacket();
    }
    
    public static Packet showEXPGain(int base, boolean quest) {
        return showEXPGain(base, 0, 0, 0, 0, 0, quest, false);
    }

    public static Packet showEXPGain(int base, int event, int wedding, int party, int thirdBonus, int hours, boolean quest, boolean killer) {
        PacketWriter pw = new PacketWriter(21);

        pw.writeHeader(SendOpcode.SHOW_STAT_CHANGE);

        pw.write(3); // EXP stat

        pw.writeBool(killer); // white text = killer

        pw.writeInteger(base); // base experience

        pw.writeBool(quest); // "onQuest"

        pw.writeInteger(event); // non-stackable event EXP

        pw.write(thirdBonus); // 0 = not third mob or no event

        pw.write(party); // add party bonus to base, then this splits it off to display

        pw.writeInteger(wedding); // wedding bonus exp

        if (thirdBonus > 0) {
            pw.write(hours); // shows how many hours for third bonus
        }

        if (quest) { // "onQuest"
            pw.write(0);
            // if this byte was > 0, then another byte would be written
            // detailing questBonusRemainCount (not sure if v62)
        }

        // the graphical for this is essentially the following:
        // floors the modifier so only three real options exist (0x, 1x, 2x)
        // and takes the bonus party experience and cuts the displayed value
        // in half since it should have been added to the base experience
        pw.write(Configuration.PARTY_EXP_MOD); // party exp modifier (bonus exp)

        return pw.createPacket();
    }

    public static Packet enableActions() {
        return updatePlayerStats(Collections.EMPTY_MAP, true);
    }

    public static Packet updatePlayerStats(Map<Stat, Integer> stats, boolean item) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.UPDATE_STATS);

        pw.writeBool(item);

        int appliedMask = 0;
        for (Stat s : stats.keySet()) {
            appliedMask |= s.getValue();
        }
        pw.writeInteger(appliedMask);
        stats.forEach((s, v) -> {
            s.serialize(pw, v);
        });
        return pw.createPacket();
    }

    public static Packet updateSkill(int id, SkillInfo s) {
        PacketWriter pw = new PacketWriter(18);

        pw.writeHeader(SendOpcode.UPDATE_SKILL);
        pw.write(1);
        pw.writeShort(1);
        pw.writeInteger(id);
        pw.writeInteger(s.level);
        pw.writeInteger(s.mastery);
        pw.write(1);

        return pw.createPacket();
    }

    public static Packet updateBuddyChannel(int id, int channel) {
        PacketWriter pw = new PacketWriter(12);
        pw.writeHeader(SendOpcode.BUDDYLIST);
        pw.write(0x14);
        pw.writeInteger(id);
        pw.write(0);
        pw.writeInteger(channel);
        return pw.createPacket();
    }

    public static Packet modifyInventory(boolean silent, List<Modification> mods) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.MODIFY_INVENTORY);

        pw.writeBool(!silent);

        pw.write(mods.size());

        int movementInfo = -1;

        for (Modification m : mods) {
            pw.write(m.action);
            pw.write(m.inventory);
            pw.writeShort(m.action == Modification.MOVE_ITEM ? m.old_pos : m.new_pos);
            switch (m.action) {
                case Modification.ADD_ITEM:
                    addItemInfo(pw, m.target, true);
                    break;
                case Modification.CHANGE_QUANTITY:
                    pw.writeShort(m.count);
                    break;
                case Modification.MOVE_ITEM:
                    pw.writeShort(m.new_pos);
                    movementInfo = m.inventory == Inventory.Type.EQUIP.getType()
                            ? m.new_pos < 0 ? 0
                            : m.old_pos < 0 ? 1
                            : movementInfo : movementInfo;
                    break;
                case Modification.REMOVE_ITEM:
                    movementInfo = m.inventory == Inventory.Type.EQUIP.getType()
                            && m.new_pos < 0 ? 1 : movementInfo;
                    break;
                default:
                    break;
            }
        }

        if (movementInfo >= 0) {
            pw.writeBool(movementInfo != 0);
        }

        return pw.createPacket();
    }

    public static Packet forceEquip() {
        PacketWriter pw = new PacketWriter(2);

        pw.writeHeader(SendOpcode.FORCE_EQUIP);

        return pw.createPacket();
    }
}
