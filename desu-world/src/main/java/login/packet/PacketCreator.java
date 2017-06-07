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
package login.packet;

import channel.WorldChannel;
import java.util.Collection;
import net.Packet;
import net.PacketWriter;
import service.Configuration;

/**
 *
 * @author Brent
 */
public class PacketCreator {

    private PacketCreator() {
    }
    
    public static Packet getWorldInfo(Collection<WorldChannel> chs) {
        PacketWriter pw = new PacketWriter(32);
        
        pw.writeHeader(SendOpcode.WORLD_INFO);
        
        pw.writeMapleString(Configuration.WORLD_SERVICE_KEY);
        
        pw.writeInteger(Configuration.WORLD_ID);
        pw.writeMapleString(Configuration.WORLD_NAME);
        pw.write(Configuration.EVENT_FLAG);
        pw.writeMapleString(Configuration.EVENT_MESSAGE);
        
        pw.writeMapleString(Configuration.URL);
        pw.writeMapleString(Configuration.USER);
        pw.writeMapleString(Configuration.PASS);
        
        pw.writeBool(!chs.isEmpty());
        if (!chs.isEmpty()) {
            for (WorldChannel ch : chs) {
                pw.write(ch.getChannelId());
                pw.write(ch.getPublicIP());
                pw.writeBool(ch.isAgeRestricted());
                pw.writeShort(ch.getPublicPort());
                pw.writeShort(ch.getLoad());
            }
        }   
        
        return pw.createPacket();
    }
    
    public static Packet getChannelUpdate(int mode, WorldChannel ch) {
        PacketWriter pw = new PacketWriter(32);
        
        pw.writeHeader(SendOpcode.CHANNEL_UPDATE);
        
        pw.write(mode);
        pw.write(ch.getChannelId());
        switch (mode) {
            case 0: // load changed
                pw.writeShort(ch.getLoad());
                break;
            case 1: // connection info changed
                pw.write(ch.getPublicIP());
                pw.writeShort(ch.getPublicPort());
                break;
            case 2: // new channel
                pw.write(ch.getPublicIP());
                pw.writeBool(ch.isAgeRestricted());
                pw.writeShort(ch.getPublicPort());
                pw.writeShort(ch.getLoad());
                break;
            case 3: // deregister channel
                break;
        }
        
        return pw.createPacket();
    }
    
    public static Packet broadcastMessage(int type, String msg) {
        PacketWriter pw = new PacketWriter(msg.length() + 5);
        
        pw.writeHeader(SendOpcode.BROADCAST_MESSAGE);
        
        pw.write(type);
        
        pw.writeMapleString(msg);
        
        return pw.createPacket();
    }
    
    public static Packet getShutdown(boolean planned) {
        PacketWriter pw = new PacketWriter(3);

        pw.writeHeader(SendOpcode.SHUTDOWN);

        pw.writeBool(planned);

        return pw.createPacket();
    }
    
    public static Packet updateAccountState(int accountId, int playerId, int sourceId, String name, boolean isGM, String lastIP, int target) {
        PacketWriter pw = new PacketWriter(14);
        
        pw.writeHeader(SendOpcode.UPDATE_ACCOUNT_STATE);
        
        pw.writeInteger(accountId);
        
        pw.writeInteger(playerId);
        
        pw.writeInteger(sourceId);
        
        pw.writeMapleString(name);
        
        pw.writeBool(isGM);
        
        pw.writeMapleString(lastIP);
        
        pw.writeInteger(target);
        
        return pw.createPacket();
    }
}
