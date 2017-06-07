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

import java.sql.Connection;
import java.sql.PreparedStatement;
import net.PacketReader;
import netty.PacketHandler;
import service.Service;
import service.WorldService;
import world.World;
import world.packet.PacketCreator;

/**
 *
 * @author Brent
 */
public class UpdateAccountStateHandler implements PacketHandler<World>  {

    @Override
    public boolean validateState(World w) {
        return w.isValid();
    }

    @Override
    public void handle(World w, PacketReader r) {
        int account = r.readInteger();
        int playerId = r.readInteger();
        int src = r.readInteger();        
        String name = r.readMapleString();
        boolean gm = r.readBool();
        String lastIP = r.readMapleString();
        int target = r.readInteger();
        
        // XXX do database check to make sure the creds are legit. fire people if they aren't
        
        Connection c = Service.getInstance().getDatabase().getConnection();
        try {
            if (target == 0) { // disconnect account completely since the target is this server
                PreparedStatement ps = c.prepareStatement("UPDATE accounts SET state = 0 WHERE id = ?");
                ps.setInt(1, account);
                ps.executeUpdate();
                ps.close();
                
                ps = c.prepareStatement("DELETE FROM transition WHERE account = ? && player = ?");
                ps.setInt(1, account);
                ps.setInt(2, playerId);
                ps.executeUpdate(); // may be an indicator of hax0rinos
                ps.close();
            } else { // update transition table
                PreparedStatement ps = c.prepareStatement("UPDATE transition SET source = ? WHERE account = ? && player = ?");
                ps.setInt(1, src);
                ps.setInt(2, account);
                ps.setInt(3, playerId);
                ps.executeUpdate();
                ps.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (target > 0) {
            int world = target / 100;
            int channel = target % 100;
            
            World tW = WorldService.getInstance().getWorld(world); // for magic
            
            if (w != null) {
                if (tW.getChannel(channel) != null) {
                    tW.write(PacketCreator.updateAccountState(account, playerId, name, gm, lastIP, target));
                }
            }
        }
    }
}
