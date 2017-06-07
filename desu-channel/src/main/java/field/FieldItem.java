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
package field;

import client.Client;
import client.packet.PacketCreator;
import java.awt.Point;
import player.Player;
import player.item.Item;
import util.TimeUtil;

/**
 *
 * @author Brent
 */
public class FieldItem extends FieldEntity {

    private int owner;
    private int amount;
    private int itemId;
    private Item item;
    private int ownership;
    private int srcOid;
    private boolean quest;
    private boolean playerDrop;
    private boolean looted = false;
    private Point position = new Point();
    private long expiration = TimeUtil.PERMANENT_TIME; // no idea
    private long dropTime = System.currentTimeMillis();
    
    public FieldItem(Item item, int itemId, int amount, Point pos, int srcOid, int owner, int ownership, boolean quest, boolean player) {
        this.item = item;
        this.itemId = itemId;
        this.amount = amount;
        this.srcOid = srcOid;
        this.owner = owner;
        this.ownership = ownership;
        this.quest = quest;
        this.position = pos;
        playerDrop = player;
    }

    public Item getItem() {
        return item;
    }
    
    public boolean isMeso() {
        return itemId == 0;
    }
    
    public void setLooted() {
        looted = true;
    }
    
    public boolean isLooted() {
        return looted;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() - 180000 - dropTime > 0;
    }
    
    public boolean isProtected() {
        return ownership < 2 && System.currentTimeMillis() - 15000 - dropTime > 0;
    }

    public boolean isPlayerDrop() {
        return playerDrop;
    }
    
    public boolean shouldBeSeenBy(Player p) {
        return (!quest || p.requiresItemForQuest(itemId)) && p.getPosition().distanceSq(position) <= Field.CHARACTER_VIEW_DISTANCE;
    }
    
    public int getOwner() {
        return owner;
    }
    
    public int getOwnership() {
        return ownership;
    }

    public int getSourceOid() {
        return srcOid;
    }
    
    public int getItemId() {
        return itemId;
    }
    
    public int getAmount() {
        return amount;
    }

    public long getExpiration() {
        if (isMeso())
            return 0;
        return expiration;
    }

    @Override
    public Type getObjectType() {
        return Type.ITEM;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void toggleHidden() {
    }

    @Override
    public int getStance() {
        return -1;
    }

    @Override
    public void setStance(int s) {
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void setPosition(Point p) {
        position.x = p.x;
        position.y = p.y;
    }

    @Override
    public int getFh() {
        return -1;
    }

    @Override
    public void sendSpawnData(Client mc) {
        if (shouldBeSeenBy(mc.getPlayer()))
            mc.write(PacketCreator.spawnFieldItem(this, 2, new Point(0, 0), getPosition(), 0));
    }

    @Override
    public void sendDestroyData(Client mc, boolean spc) {
        mc.write(PacketCreator.removeFieldItem(this, 1, 0));
    }

}
