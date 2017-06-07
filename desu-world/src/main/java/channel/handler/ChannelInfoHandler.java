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
package channel.handler;

import channel.WorldChannel;
import channel.packet.PacketCreator;
import net.PacketReader;
import netty.PacketHandler;
import service.ChannelService;
import service.Configuration;

/**
 *
 * @author Brent
 */
public class ChannelInfoHandler implements PacketHandler<WorldChannel> {

    @Override
    public boolean validateState(WorldChannel c) {
        return !c.isValid();
    }

    @Override
    public void handle(WorldChannel c, PacketReader r) {
        String validation = r.readMapleString();
        c.setValid(validation.equals(Configuration.CHANNEL_SERVICE_KEY));
        if (c.isValid()) {
            c.setChannelId(r.readInteger());
            c.setPublicIP(r.read(4));
            c.setAgeRestricted(r.readBool());
            c.setPublicPort(r.readShort());
            c.setLoad(r.readShort());
            
            c.write(PacketCreator.giveWorldInfo());
            
            System.out.printf("[Info] Channel %s has been validated by the server.%n", c.getChannelId());
            
            ChannelService.getInstance().addChannel(c);
        } else {
            c.close();
        }
    }
}
