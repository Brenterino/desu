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
import data.EquipData;
import data.external.GameDatabase;
import data.ItemData;
import data.internal.Slots;
import field.Field;
import field.FieldItem;
import java.awt.Point;
import net.PacketReader;
import netty.PacketHandler;
import player.Player;
import player.Violation;
import player.item.Inventory;
import player.item.Item;
import player.stats.EffectStat;

/**
 *
 * @author Brent
 */
public class ModifyInventoryHandler implements PacketHandler<Client> {

    @Override
    public boolean validateState(Client c) {
        return c.isLoggedin();
    }

    @Override
    public void handle(Client c, PacketReader r) {
        r.readInteger(); // exclRequest = int32
        int t = r.read();// type = int8
        byte src = (byte) r.readShort();// src = int16
        byte dst = (byte) r.readShort();// dst = int16
        int count = r.readShort();// count = int16

        Inventory.Type TYPE = Inventory.Type.getByType(t);

        assert !TYPE.equals(Inventory.Type.UNKNOWN);

        Player p = c.getPlayer();

        Inventory target = null;

        switch (TYPE) {
            case EQUIP:
                if (src > 0 && dst > 0 || dst == 0) {
                    target = p.getEquipInventory();
                    break;
                }
                handleEquipment(p, src, dst);
                break;
            case USE:
                target = p.getUseInventory();
                break;
            case ETC:
                target = p.getEtcInventory();
                break;
            case SETUP:
                target = p.getSetupInventory();
                break;
            case CASH:
                target = p.getCashInventory();
                break;
            default: // should never happen
                c.write(PacketCreator.enableActions());
                return;
        }

        if (target != null) {
            if (dst != 0) { // move item
                target.moveItem(src, dst);
            } else { // XXX check to see if player is currently in a trade
                Item tItem = target.get(src);
                if (tItem != null) {
                    Field f = p.getField();
                    boolean disappear = false;
                    // int itemid, int itemoid, int dropperoid, int ownerid, Point dropfrom, Point dropto, byte mod
                    // item.getItemId(), drop.getObjectId(), 0, 0, dropper.getPosition(), droppos, (byte) 3), drop.getPosition());
                    // Item item, int itemId, int amount, Point pos, int srcOid, int owner, int ownership, boolean quest, boolean player
                    Point dropLoc = f.calcDropPosition(p.getPosition(), p.getPosition());
                    FieldItem item;
                    if (GameDatabase.isEquip(tItem.getItemId())) {
                        EquipData info = GameDatabase.getEquip(tItem.getItemId());
                        
                        disappear = info.isCash() || info.hasTradeBlock() || info.isQuestItem();
                        
                        item = new FieldItem(tItem, tItem.getItemId(), 1, dropLoc, p.getOid(), 0, 2, false, true);
                        
                        target.removeItem(src); // yep
                    } else {
                        ItemData info = GameDatabase.getItem(tItem.getItemId());
                        
                        disappear = info.isCash() || info.isTradeBlock() || info.isQuestItem();
                        
                        if (tItem.getQuantity() > count) {
                            target.removeFromSlot(tItem.getSlot(), count);
                            // Item item, int itemId, int amount, Point pos, int srcOid, int owner, int ownership, boolean quest, boolean player
                            item = new FieldItem(null, tItem.getItemId(), count, dropLoc, p.getOid(), 0, 2, false, true);
                        } else if (tItem.getQuantity() == count) {
                            target.removeItem(tItem.getSlot());
                            
                            item = new FieldItem(tItem, tItem.getItemId(), tItem.getQuantity(),  dropLoc, p.getOid(), 0, 2, false, true);
                        } else { // nice try guy
                            throw new AssertionError(Violation.PACKET_EDITTING);
                        }
                    }
                    
                    
                    f.spawnFieldItem(p.getPosition(), item, disappear ? 3 : 1, 0);
                }
            }
            target.showInventoryModifications(p, false);
        }

        c.write(PacketCreator.enableActions());
    }

    private void handleEquipment(Player p, byte src, byte dst) {
        Inventory equip = p.getEquipInventory();

        boolean toEquip = src > 0 && dst < 0;

        Item sItem = equip.get(src);
        Item dItem = equip.get(dst);

        int targetId = toEquip ? sItem.getItemId() : dItem != null ? dItem.getItemId() : 0;
        boolean canEquip = true;
        if (targetId != 0) { // if an item is not being moved into the equipped state,
            // then there is no point in doing these checks
            Slots target = Slots.getFromItemId(targetId);
            canEquip &= target.isAllowed(dst, toEquip);
            if (target.isTwoHanded() && equip.isSlotTaken(-10)) {
                canEquip &= equip.moveItem((byte) -10, (byte) -1); // move shield to next available
            }
        }
        if (canEquip) {
            if (src == -11 || dst == -11) { // changing weapons cancels booster
                p.cancelAppliedStatEffect(EffectStat.BOOSTER);
            }
            equip.moveItem(src, dst);
            equip.showInventoryModifications(p, false);
        }
    }
}
