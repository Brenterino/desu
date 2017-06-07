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
import service.Configuration;
import service.WorldService;
import world.World;
import world.WorldChannel;

/**
 *
 * @author Brent
 */
public class WorldInfoHandler implements PacketHandler<World> {

    @Override
    public boolean validateState(World c) {
        return !c.isValid();
    }

    @Override
    public void handle(World w, PacketReader r) {
        String validation = r.readMapleString();
        w.setValid(validation.equals(Configuration.WORLD_SERVICE_KEY));
        if (w.isValid()) {
            w.setWorldId(r.readInteger());
            w.setWorldName(r.readMapleString());
            w.setEventFlag(r.read());
            w.setEventMessage(r.readMapleString());
            w.setDatabaseInfo(r.readMapleString(), r.readMapleString(), r.readMapleString());
            if (r.readBool()) { // channel data available
                int chCount = r.readInteger();
                for (int i = 0; i < chCount; i++) {
                    WorldChannel nC = new WorldChannel();
                    nC.ID = r.read();
                    nC.IP = r.read(4);
                    nC.AGE_RESTRICTED = r.readBool();
                    nC.LOAD = r.readInteger();
                    nC.PORT = r.readShort();
                }
            }
            WorldService.getInstance().addWorld(w);
            System.out.printf("[Info] %s has been validated by the server.%n", w.getWorldName());
        } else {
            w.close();
        }
    }
}
