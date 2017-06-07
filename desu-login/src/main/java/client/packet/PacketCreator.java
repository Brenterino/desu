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

import client.Client;
import client.Player;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import net.Packet;
import net.PacketWriter;
import service.Configuration;
import world.World;
import world.WorldChannel;

/**
 *
 * @author Brent
 */
public class PacketCreator {

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

    public static Packet getLoginFailed(int reason) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.LOGIN_STATUS);

        pw.write(reason);
        pw.write(0);
        pw.writeInteger(0);

        return pw.createPacket();
    }
    
    // epikly not used
    public static Packet getGuestLoginSuccess() {
        PacketWriter pw = new PacketWriter(37);
        
        pw.writeHeader(SendOpcode.GUEST_LOGIN_STATUS);
        pw.write(0);
        pw.write(0);
        pw.writeInteger(0); // account id
        pw.write(0); // gender idk
        pw.writeBool(false); // isGM
        pw.write(0); // admin permissions
        pw.writeMapleString(""); // account name
        pw.write(0); //
        pw.write(0);
        pw.writeLong(0); // quiet ban time
        pw.writeLong(0); // session id    
        pw.writeInteger(0); 
        pw.writeMapleString(""); // no idea 
        
        return pw.createPacket();
    }
    
    public static Packet getGuestLoginFailed(int reason) {
        PacketWriter pw = new PacketWriter(6);
        
        pw.writeHeader(SendOpcode.GUEST_LOGIN_STATUS);
        
        pw.write(reason); // 1 as the functional byte disables the button
        pw.write(0);
        
        return pw.createPacket();
    }

    public static Packet getBanMessage(int reason, long time) {
        PacketWriter pw = new PacketWriter(17);

        pw.writeHeader(SendOpcode.LOGIN_STATUS);

        pw.write(2);
        pw.write(0);
        pw.writeInteger(0x00);
        pw.write(reason);
        pw.writeLong(time);

        return pw.createPacket();
    }

    public static Packet getAuthSuccess(Client c) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.LOGIN_STATUS);

        pw.write(0);
        pw.write(0);
        pw.writeInteger(0);
        pw.writeInteger(c.getAccountId());
        pw.write(c.getAccountGender());
        pw.writeBool(c.isGM());
        pw.write(c.isGM() ? 0x80 : 0); // admin permissions
        pw.writeMapleString(c.getAccountName());
        pw.write(0);
        pw.write(0);
        pw.writeLong(0); // quiet ban time // 10
        pw.writeLong(0); // session id     // 18
        pw.writeInteger(0);
        pw.writeShort(0);

        return pw.createPacket();
    }

    public static Packet getPinOperation(int op) {
        PacketWriter pw = new PacketWriter(3);

        pw.writeHeader(SendOpcode.PIN_OPERATION);
        pw.write(op);

        return pw.createPacket();
    }

    public static Packet getPinRegistered() {
        PacketWriter pw = new PacketWriter(3);

        pw.writeHeader(SendOpcode.PIN_REGISTERED);
        pw.write(0x00); // probably some state, can tinker with later

        return pw.createPacket();
    }

    public static Packet getWorldInfo(World w) {
        PacketWriter pw = new PacketWriter(64);

        pw.writeHeader(SendOpcode.WORLD_INFO);

        pw.write(w.getWorldId());
        pw.writeMapleString(w.getWorldName());
        pw.write(w.getEventFlag());
        pw.writeMapleString(w.getEventMessage());

        pw.writeShort(0x64); // summer event stuff, kind of useless
        pw.writeShort(0x64);
        pw.write(0x00);

        Collection<WorldChannel> channels = w.getChannels();

        pw.write(channels.size());

        for (WorldChannel wc : channels) {
            pw.writeMapleString(new StringBuilder(w.getWorldName()).append("-").append(wc.ID).toString());
            pw.writeInteger(wc.LOAD);
            pw.write(wc.ID);
            pw.writeShort(wc.ID - 1);
        }

        pw.writeShort(0x00); // list for dialogues if desired
        return pw.createPacket();
    }

    public static Packet getEndOfWorldList() {
        PacketWriter pw = new PacketWriter(3);

        pw.writeHeader(SendOpcode.WORLD_INFO);

        pw.write(0xFF);

        return pw.createPacket();
    }

    public static Packet getWorldStatus(int status) {
        PacketWriter pw = new PacketWriter(3);

        pw.writeHeader(SendOpcode.WORLD_STATUS);

        pw.writeShort(status);

        return pw.createPacket();
    }

    public static Packet getCharacterList(List<Player> pl, int slots) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.CHARACTER_LIST);

        pw.writeBool(false);

        pw.write(pl.size());

        for (Player p : pl) {
            addCharacterEntry(pw, p, false);
        }

        pw.writeInteger(slots);
        return pw.createPacket();
    }
    
    public static Packet getCharacterListFailure() {
        PacketWriter pw = new PacketWriter(8);
        
        pw.writeHeader(SendOpcode.CHARACTER_LIST);
        
        pw.writeBool(true); // imagine this mean it failed B^)
        
//        pw.write(0);
//        
//        pw.writeInteger(0);
        
        return pw.createPacket();
    }

    public static Packet getAllCharacterCount(int count) {
        PacketWriter pw = new PacketWriter(11);

        pw.writeHeader(SendOpcode.ALL_CHARACTER_LIST);

        pw.writeBool(true);

        pw.writeInteger(count);

        pw.writeInteger(count + (3 - count % 3));

        return pw.createPacket();
    }

    public static Packet getAllCharacterList(List<Player> pl, int world) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.ALL_CHARACTER_LIST);

        pw.writeBool(false);

        pw.write(world);

        pw.write(pl.size());

        pl.stream().forEach(p -> addCharacterEntry(pw, p, true));

        return pw.createPacket();
    }

    private static void addCharacterEntry(PacketWriter pw, Player p, boolean all) {
        addCharacterStats(pw, p);
        addCharacterLooks(pw, p);
        pw.writeBool(p.isRanked() && !all);
        if (p.isRanked() && !all) {
            pw.writeInteger(p.getOverallRank());
            pw.writeInteger(p.getOverallRankMove());
            pw.writeInteger(p.getJobRank());
            pw.writeInteger(p.getJobRankMove());
        }
    }

    private static void addCharacterStats(PacketWriter pw, Player p) {
        pw.writeInteger(p.getId());
        pw.writeString(p.getName());
        pw.fill(0x00, 13 - p.getName().length());
        pw.write(p.getGender());
        pw.write(p.getSkinColor());
        pw.writeInteger(p.getFace());
        pw.writeInteger(p.getHairstyle());
        for (int i = 0; i < 3; i++) {
            pw.writeLong(p.getPets().get(i));
        }
        pw.write(p.getLevel());
        pw.writeShort(p.getJob());
        pw.writeShort(p.getStrength());
        pw.writeShort(p.getDexterity());
        pw.writeShort(p.getIntelligence());
        pw.writeShort(p.getLuck());
        pw.writeShort(p.getHealth());
        pw.writeShort(p.getMaximumHealth());
        pw.writeShort(p.getMana());
        pw.writeShort(p.getMaximumMana());
        pw.writeShort(p.getAvailableAP());
        pw.writeShort(p.getAvailableSP());
        pw.writeInteger(p.getExperience());
        pw.writeShort(p.getFame());
        pw.writeInteger(0x00);
        pw.writeInteger(p.getCurrentMapId());
        pw.write(p.getSpawnpoint());
        pw.writeInteger(0x00);
    }

    private static void addCharacterLooks(PacketWriter pw, Player p) {
        pw.write(p.getGender());
        pw.write(p.getSkinColor());
        pw.writeInteger(p.getFace());
        pw.writeBool(true);
        pw.writeInteger(p.getHairstyle());
        addCharacterEquipment(pw, p);
        for (int i = 0; i < 3; i++) {
            pw.writeInteger(p.getPets().get(i));
        }
    }

    private static void addCharacterEquipment(PacketWriter pw, Player p) {
        HashMap<Byte, Integer> base = new HashMap<>();
        HashMap<Byte, Integer> mask = new HashMap<>();
        for (Entry<Byte, Integer> item : p.getEquipment().entrySet()) {
            byte pos = item.getKey();
            if (pos < 100 && !base.containsKey(pos)) {
                base.put(pos, item.getValue());
            } else if (pos > 100 && pos != 111) {
                pos -= 100;
                if (base.containsKey(pos)) {
                    mask.put(pos, base.get(pos));
                }
                base.put(pos, item.getValue());
            } else if (base.containsKey(pos)) {
                mask.put(pos, item.getValue());
            }
        }
        base.forEach((k, v) -> pw.write(k).writeInteger(v));
        pw.write(0xFF);
        mask.forEach((k, v) -> pw.write(k).writeInteger(v));
        pw.write(0xFF);
        pw.writeInteger(p.getEquipment().containsKey(111) ? p.getEquipment().get(111) : 0);
    }
    
    public static Packet getAfterLoginError(int reason) {
        PacketWriter pw = new PacketWriter(4);
        
        pw.writeHeader(SendOpcode.AFTER_LOGIN_ERROR);
        
        pw.write(reason);
        
        pw.write(0);
        
        return pw.createPacket();
    }

    public static Packet getNameCheckResponse(String name, boolean taken) {
        PacketWriter pw = new PacketWriter(16);

        pw.writeHeader(SendOpcode.NAME_CHECK_RESPONSE);

        pw.writeMapleString(name);

        pw.writeBool(!taken);

        return pw.createPacket();
    }

    public static Packet getCharacterCreationResponse(Player p, boolean done) {
        PacketWriter pw = new PacketWriter(16);

        pw.writeHeader(SendOpcode.CREATE_CHARACTER_RESPONSE);

        pw.writeBool(!done);

        if (done) {
            addCharacterEntry(pw, p, false);
        }

        return pw.createPacket();
    }

    public static Packet getCharacterDeletionResponse(int cid, int state) {
        PacketWriter pw = new PacketWriter(7);

        pw.writeHeader(SendOpcode.DELETE_CHARACTER_RESPONSE);

        pw.writeInteger(cid);

        pw.write(state);

        return pw.createPacket();
    }

    public static Packet getChannelConnectionInfo(WorldChannel wc, int cid) {
        PacketWriter pw = new PacketWriter(19);

        pw.writeHeader(SendOpcode.CHANNEL_CONNECTION_INFO);

        pw.writeShort(0x00);

        pw.write(wc.IP);

        pw.writeShort(wc.PORT);

        pw.writeInteger(cid);

        pw.fill(0x00, 5);

        return pw.createPacket();
    }

    public static Packet getRelogResponse() {
        PacketWriter pw = new PacketWriter(3);

        pw.writeHeader(SendOpcode.RELOG_RESPONSE);

        pw.write(0x01);

        return pw.createPacket();
    }
}
