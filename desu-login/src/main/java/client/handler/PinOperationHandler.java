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
package client.handler;

import client.Client;
import client.packet.PacketCreator;
import net.PacketReader;
import netty.PacketHandler;
import util.HexTool;

/**
 *
 * @author Brent
 */
public class PinOperationHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return !c.isBanned() && c.isLoggedIn();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        // we can also bypass this with just sending the pin accept operation (aka operation 0)
        if (r.available() > 1) {
            byte pByte = r.readByte();
            byte oByte = r.readByte();
            if (pByte == 1) {
                if (oByte == 1) {
                    if (c.getPin().isEmpty()) {
                        c.write(PacketCreator.getPinOperation(1)); // request pin registration
                    } else {
                        c.write(PacketCreator.getPinOperation(4)); // request pin
                    }
                } else {
                    r.readInteger(); // garbage data
                    String pin = r.readMapleString();
                    if (c.getPin().equals(pin)) {
                        c.setPinVerified(true);
                        c.write(PacketCreator.getPinOperation(0)); // pin accepted
                    } else {
                        c.write(PacketCreator.getPinOperation(2)); // pin failed, try again
                    }
                }
            } else if (pByte == 2) { // request to change pin 
                r.readInteger();
                if (oByte == 0) {
                    String pin = r.readMapleString();
                    if (c.getPin().equals(pin)) {
                        c.setPin("");
                        c.write(PacketCreator.getPinOperation(1)); // request pin registration
                    }
                } else {
                    System.out.printf("Unknown Pin Operation Packet: %s.%n", HexTool.toHex(r.getData())); // no clue, dump packet
                }
            }
        } else {
            c.softDisconnect(true);
        }
    }
}
