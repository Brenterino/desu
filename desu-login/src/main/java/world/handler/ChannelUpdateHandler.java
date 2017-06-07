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
package world.handler;

import net.PacketReader;
import netty.PacketHandler;
import world.World;
import world.WorldChannel;

/**
 *
 * @author Brent
 */
public class ChannelUpdateHandler implements PacketHandler<World> {

    @Override
    public boolean validateState(World c) {
        return c.isValid();
    }

    @Override
    public void handle(World w, PacketReader r) {
        int op = r.read();
        int ch = r.read();
        WorldChannel wc = w.getChannel(ch);
        if (wc != null) {
            switch (op) {
                case 0: // channel load changed
                    wc.LOAD = r.readShort();
                    break;
                case 1: // channel IP & port changed
                    wc.IP = r.read(4);
                    wc.PORT = r.readShort();
                    break;
                case 3: // deregister channel
                    w.removeChannel(wc);
                    break;
                default: // unknown, disconnect world
                    w.close();
                    break;
            }
        } else if (op == 2) { // register a new channel
            wc = new WorldChannel();
            
            wc.ID = ch;
            wc.IP = r.read(4);
            wc.AGE_RESTRICTED = r.readBool();
            wc.PORT = r.readShort();
            wc.LOAD = r.readShort();
            
            w.addChannel(wc);
        }
    }
}
