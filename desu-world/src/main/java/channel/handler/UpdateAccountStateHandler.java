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
import login.packet.PacketCreator;
import net.PacketReader;
import netty.PacketHandler;
import service.Configuration;
import service.Service;

/**
 *
 * @author Brent
 */
public class UpdateAccountStateHandler implements PacketHandler<WorldChannel> {

    @Override
    public boolean validateState(WorldChannel wc) {
        return wc.isValid();
    }

    @Override
    public void handle(WorldChannel wc, PacketReader r) {
        int accountId = r.readInteger();
        int playerId = r.readInteger();
        int sourceId = wc.getChannelId() + Configuration.WORLD_ID * 100;
        String name = r.readMapleString();
        boolean isGM = r.readBool();
        String lastIP = r.readMapleString();
        int target = r.readInteger();
        
        Service.getInstance().getLoginServer().write(PacketCreator.updateAccountState(accountId, playerId, sourceId, name, isGM, lastIP, target));
    }
}
