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
package login.handler;

import login.Login;
import channel.packet.PacketCreator;
import net.PacketReader;
import netty.PacketHandler;
import service.ChannelService;

/**
 *
 * @author Brent
 */
public class UpdateAccountStateHandler implements PacketHandler<Login> {

    @Override
    public boolean validateState(Login l) {
        return true;
    }

    @Override
    public void handle(Login l, PacketReader r) {
        int accountId = r.readInteger();
        int playerId = r.readInteger();
        String name = r.readMapleString();
        boolean isGM = r.readBool();
        String lastIP = r.readMapleString();
        int target = r.readInteger(); // can check, but not necessary
        int targetChannel = target % 100;
        
        ChannelService.getInstance().getChannel(targetChannel).write(PacketCreator.updateAccountState(accountId, playerId, name, isGM, lastIP));
    }
}
