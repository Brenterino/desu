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
package channel.packet;

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

    public static Packet giveWorldInfo() {
        PacketWriter pw = new PacketWriter(32);
        
        pw.writeHeader(SendOpcode.WORLD_INFO);
        
        pw.writeInteger(Configuration.WORLD_ID);
        pw.writeInteger(Configuration.EXPERIENCE_MOD);
        pw.writeInteger(Configuration.DROP_MOD);
        
        pw.writeMapleString(Configuration.SERVER_MESSAGE);
        
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
    
    public static Packet updateAccountState(int accountId, int playerId, String name, boolean isGM, String lastIP) {
        PacketWriter pw = new PacketWriter(14);
        
        pw.writeHeader(login.packet.SendOpcode.UPDATE_ACCOUNT_STATE);
        
        pw.writeInteger(accountId);
        
        pw.writeInteger(playerId);
        
        pw.writeMapleString(name);
        
        pw.writeBool(isGM);
        
        pw.writeMapleString(lastIP);
        
        return pw.createPacket();
    }
}
