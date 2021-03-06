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
package world.packet;

import net.Packet;
import net.PacketWriter;
import player.Player;
import service.Configuration;
import service.GameService;

/**
 *
 * @author Brent
 */
public class PacketCreator {

    private PacketCreator() {
    }

    public static Packet getChannelInfo() {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.CHANNEL_INFO);

        pw.writeMapleString(Configuration.CHANNEL_SERVICE_KEY);

        pw.writeInteger(Configuration.CHANNEL_ID);

        pw.write(Configuration.PUBLIC_IP.getAddress());
        
        pw.writeBool(Configuration.AGE_RESTRICTED);

        pw.writeShort(Configuration.PORT);
        
        pw.writeShort(GameService.getInstance().getLoad());

        return pw.createPacket();
    }

    public static Packet broadcastMessage(int type, String msg) {
        PacketWriter pw = new PacketWriter(msg.length() + 5);

        pw.writeHeader(SendOpcode.BROADCAST_MESSAGE);

        pw.write(type);

        pw.writeMapleString(msg);

        return pw.createPacket();
    }

    public static Packet getPlayerConnected(int id, String name) {
        PacketWriter pw = new PacketWriter(name.length() + 8);

        pw.writeHeader(SendOpcode.PLAYER_CONNECTED);

        pw.writeInteger(id);

        pw.writeMapleString(name);

        return pw.createPacket();
    }

    public static Packet getPlayerDisconnected(int id, String name) {
        PacketWriter pw = new PacketWriter(name.length() + 8);

        pw.writeHeader(SendOpcode.PLAYER_DISONNECTED);

        pw.writeInteger(id);

        pw.writeMapleString(name);

        return pw.createPacket();
    }

    public static Packet transitPlayer(Player p, int target) { // XXX transfer buffs
        PacketWriter pw = new PacketWriter(p.getName().length() + 12);

        pw.writeHeader(SendOpcode.PLAYER_TRANSIT);

        pw.writeInteger(p.getId());

        pw.writeInteger(target);

        pw.writeMapleString(p.getName());

        return pw.createPacket();
    }

    public static Packet updateAccountState(int accountId, int playerId, boolean isGM, String lastIP, String name, int target) {
        PacketWriter pw = new PacketWriter(14);
        
        pw.writeHeader(SendOpcode.UPDATE_ACCOUNT_STATE);
        
        pw.writeInteger(accountId);
        
        pw.writeInteger(playerId);
        
        pw.writeMapleString(name);
        
        pw.writeBool(isGM);
        
        pw.writeMapleString(lastIP);
        
        pw.writeInteger(target);
        
        return pw.createPacket();
    }

    public static Packet getShutdown(boolean planned) {
        PacketWriter pw = new PacketWriter(3);

        pw.writeHeader(SendOpcode.SHUTDOWN);

        pw.writeBool(planned);

        return pw.createPacket();
    }
}
