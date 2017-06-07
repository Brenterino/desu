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

import client.packet.PacketCreator;
import data.DropData;
import data.EquipData;
import data.external.GameDatabase;
import data.ItemData;
import field.FieldObject.Type;
import field.monster.Monster;
import io.netty.util.concurrent.ScheduledFuture;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.Packet;
import player.Player;
import player.community.Party;
import service.Configuration;
import service.Service;
import wz.WzObject;

/**
 *
 * @author Brent
 */
public final class Field {

    private int id;
    private boolean fly;
    private boolean swim; // for anti-hack
    private boolean town;
    private int returnField;
    private float mobRate;
    private int fieldType;
    private int forcedReturn;
    private String onFirstUserEnter;
    private String onUserEnter;
    private int timeLimit;
    private int pulseHolderCID;
    private ScheduledFuture<?> pulseTask;
    private final AtomicInteger runningOid;
    private ArrayList<Spawner> spawns;
    private ArrayList<Portal> portals;
    private ArrayList<Rectangle> areas;
    private LinkedList<Reactor> reactors;
    private Map<Integer, Monster> monsters;
    private Map<Integer, FieldItem> items;
    private Map<Integer, FieldObject> objects;
    private FootholdTree footholdTreeMap;
    private Supplier<Stream<Player>> playerSupplier;
    private Supplier<Stream<NPC>> npcSupplier;
    private Random fieldRandom = new Random();
    public static final int CHARACTER_VIEW_DISTANCE = 722500;
    // XXX all of this code may not actually be thread-safe simply because I'm a nerd

    public Field(int id) {
        this.id = id;
        spawns = new ArrayList<>();
        reactors = new LinkedList<>();
        areas = new ArrayList<>();
        objects = Collections.synchronizedMap(new HashMap<Integer, FieldObject>());
        monsters = Collections.synchronizedMap(new HashMap<Integer, Monster>());
        items = Collections.synchronizedMap(new HashMap<Integer, FieldItem>());
        runningOid = new AtomicInteger(0);
        playerSupplier = () -> objects.values().stream().filter(o -> o.getObjectType().equals(Type.PLAYER)).map(o -> (Player) o);
        npcSupplier = () -> objects.values().stream().filter(o -> o.getObjectType().equals(Type.NPC)).map(o -> (NPC) o);
    }

    public Stream<Player> getPlayers() {
        return playerSupplier.get();
    }

    public void purge() { // XXX convert to returning a list of players?
        spawns.clear();
        portals.clear();
        reactors.clear();
        monsters.clear();
        objects.clear();
        // moves this into a dead-map state and will be collected hopefully
    }

    public int getFieldId() {
        return id;
    }

    public void forceFieldId(int id) {
        this.id = id;
    }

    public void setTown(boolean town) {
        this.town = town;
    }

    public boolean isTown() {
        return town;
    }

    public void setFlyEnabled(boolean fly) {
        this.fly = fly;
    }

    public boolean isFlyEnabled() {
        return fly;
    }

    public void setOnFirstUserEnter(String s) {
        onFirstUserEnter = s;
    }

    public String getOnFirstUserEnter() {
        return onFirstUserEnter;
    }

    public void setOnUserEnter(String s) {
        onUserEnter = s;
    }

    public String getOnUserEntry() {
        return onUserEnter;
    }

    public void setForcedReturn(int id) {
        forcedReturn = id;
    }

    public int getForcedReturn() {
        return forcedReturn;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setReturnField(int returnField) {
        this.returnField = returnField;
    }

    public int getReturnField() {
        return returnField;
    }

    public void setMobRate(float mobRate) {
        this.mobRate = mobRate;
    }

    public float getMobRate() {
        return mobRate;
    }

    public void setSwimEnabled(boolean swim) {
        this.swim = swim;
    }

    public boolean isSwimEnabled() {
        return swim;
    }

    public void setFieldType(int ft) {
        fieldType = ft;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFootholdMap(FootholdTree treeMap) {
        footholdTreeMap = treeMap;
    }

    public void addArea(Rectangle area) {
        areas.add(area);
    }

    public ArrayList<Rectangle> getAreas() {
        return areas;
    }

    public Point calcPositionBelow(Point src) {
        Foothold fh = footholdTreeMap.findBelow(src);
        if (fh == null) {
            return null;
        }
        int dropY = fh.y1;
        if (!fh.isWall() && fh.y1 != fh.y2) {
            double s1 = Math.abs(fh.y2 - fh.y1);
            double s2 = Math.abs(fh.x2 - fh.x1);
            double s5 = Math.cos(Math.atan(s2 / s1)) * (Math.abs(src.x - fh.x1) / Math.cos(Math.atan(s1 / s2)));
            if (fh.y2 < fh.y1) {
                dropY = fh.y1 - (int) s5;
            } else {
                dropY = fh.y1 + (int) s5;
            }
        }
        return new Point(src.x, dropY);
    }

    public Point calcDropPosition(Point src, Point def) {
        Point ret = calcPositionBelow(new Point(src.x, src.y - 50));

        return ret == null ? def : ret;
    }

    public void addFieldObject(FieldObject mmo) {
        mmo.setOid(nextOid());
        objects.put(mmo.getOid(), mmo);
    }

    public void removeFieldObject(FieldObject mmo) {
        objects.remove(mmo.getOid());
    }

    public FieldObject getFieldObject(int oid) {
        return objects.get(oid);
    }

    public boolean hasNPC(int npcid) {
        return npcSupplier.get().anyMatch(n -> n.getId() == npcid);
    }

    public void addSpawner(Spawner sp) {
        spawns.add(sp);
    }

    public void parsePortalList(WzObject<?, ?> par) {
        portals = new ArrayList<>(par.getChildren().size());
        for (int i = 0; i < par.getChildren().size(); i++) {
            portals.add(null);
        }
        for (WzObject o : par) {
            int pid = Integer.valueOf(o.getName());
            portals.set(pid, new Portal(pid, o));
        }
        portals.trimToSize();
    }

    public void addReactor(Reactor mr) {
        reactors.add(mr);
    }

    private int nextOid() {
        if (runningOid.incrementAndGet() > 2000000000) {
            runningOid.set(1000);
        }
        if (objects.containsKey(runningOid.get())) {
            while (objects.containsKey(runningOid.incrementAndGet()));
        }
        return runningOid.get();
    }

    public void spawnMonster(Monster mob) {
        mob.setOid(nextOid());
        objects.put(mob.getOid(), mob);
        monsters.put(mob.getOid(), mob);

        getPlayers().forEach(p -> {
            mob.sendSpawnData(p.getClient());
            if (mob.getController() == FieldLife.NO_CONTROLLER) {
                mob.giveControl(p);
            }
        });
    }

    // XXX may be able to do this better
    public void killMonster(Monster mob, int killer, Map<Integer, AtomicInteger> dist) {
        int control = mob.getController();
        FieldObject controller = objects.get(control);
        mob.removeControl((Player) controller);

        getPlayers().forEach(p -> mob.sendDestroyData(p.getClient(), true));

        objects.remove(mob.getOid());
        monsters.remove(mob.getOid());

        int exp = mob.getExperience();

        int totalHealth = mob.getMaxHP();

        Map<Integer, Integer> expDist = new HashMap<>();
        
        // calculate highest damager
        int highestSplit = 0;
        int highestDamager = 0;
        
        for (Map.Entry<Integer, Integer> e : expDist.entrySet()) {
            int o = e.getKey();
            int s = e.getValue();
            if (s > highestSplit) {
                highestSplit = s;
                highestDamager = o;
            }
        }
        final int highestDamageCID = highestDamager;

        Map<Party, Integer> partyExp = new HashMap<>();

        // 80% of pool is split amongst all the damagers
        dist.forEach((p, d) -> expDist.put(p, (int) (0.80f * Math.floorDiv(exp * d.get(), totalHealth))));

        // expDist is known for each player assuming no party, collect EXP into parties
        getPlayers().filter(p -> expDist.containsKey(p.getId())).forEach(p -> {
            boolean isKiller = p.getId() == killer;
            int xp = expDist.get(p.getId());
            if (isKiller) { // killer gives flat 20%  to party pool/individual yield
                xp += Math.floorDiv(exp, 5);
            }
            Party party = p.getParty();
            if (party != null) {
                int pXP = xp + (partyExp.containsKey(party) ? partyExp.get(party) : 0);
                partyExp.put(party, pXP);
            } else {
                if (!mob.isBoss() && isKiller) {
                    p.incrementMonsterKills();
                }
                p.gainExp(xp, isKiller);
            }
        });

        if (!partyExp.isEmpty()) {
            partyExp.forEach((i, x) -> {
                    i.distributeExp(x, id, killer, mob.isBoss(), mob.getLevel(), totalHealth, highestDamageCID);             
            });
        }

        generateDrops(mob, killer);
    }

    private void generateDrops(Monster mob, int killer) {
        Player dropOwner = null;
        Optional<Player> dOwner = getPlayers().filter(p -> p.getId() == killer).findFirst();
        if (dOwner.isPresent()) {
            dropOwner = dOwner.get();
        }
        int ownership = mob.getDropOwnership();
        int ownerId = 0;
        if (dropOwner != null) {
            if (ownership == 0) {
                ownership = dropOwner.getParty() != null ? 1 : 0;
                ownerId = dropOwner.getDropOwnershipId();
            }
        } else { // makes the item "free" loot
            ownership = 2;
        }

        int mobPosX = mob.getPosition().x;
        Point pos = new Point(0, mob.getPosition().y);
        float serverMod = (Service.getInstance().getWorld().getDropModifier() * Configuration.EVENT_EXP_MOD) / 10000f;
        DropData dropData = GameDatabase.getDropData(mob.getId());
        List<DropData.Drop> drops = new ArrayList<>(dropData.getDrops());

        Collections.shuffle(drops);

        int dropCount = 1;
        List<FieldItem> toDrop = new ArrayList<>();
        for (DropData.Drop drop : drops) {
            if (fieldRandom.nextInt(999999) < drop.getChance() * serverMod) {
                // XXX may need to prioritize drop position calculations for items
                // that are not quest related
                if (ownership == 3) {
                    pos.x = (int) (mobPosX + (dropCount % 2 == 0 ? (40 * (dropCount + 1) / 2) : -(40 * (dropCount / 2))));
                } else {
                    pos.x = (int) (mobPosX + (dropCount % 2 == 0 ? (25 * (dropCount + 1) / 2) : -(25 * (dropCount / 2))));
                }

                FieldItem nFieldItem = null;
                Point dropPos = calcDropPosition(pos, mob.getPosition());
                if (drop.getItemId() == 0) {
                    int meso = fieldRandom.nextInt(drop.getMaximumQuantity() - drop.getMinimumQuantity()) + drop.getMinimumQuantity();

                    if (meso > 0) {
                        meso *= serverMod;// XXX calculate with meso rate modifiers of killer(?)

                        nFieldItem = new FieldItem(null, 0, meso, dropPos, mob.getOid(), ownerId, ownership, false, false);
                    }
                } else {
                    boolean quest;
                    int itemId = drop.getItemId();
                    int amount = 1;
                    if (GameDatabase.isEquip(itemId)) {
                        EquipData e = GameDatabase.getEquip(itemId);
                        
                        quest = e.isQuestItem();
                    } else {
                        amount = drop.getMaximumQuantity() > 1 ? fieldRandom.nextInt(drop.getDropVariance()) + drop.getMinimumQuantity() : 1;

                        ItemData i = GameDatabase.getItem(itemId);

                        quest = i.isQuestItem();
                    }

                    nFieldItem = new FieldItem(null, itemId, amount, dropPos, mob.getOid(), ownerId, ownership, quest, false);
                }

                if (nFieldItem != null) {
                    toDrop.add(nFieldItem);
                }
                dropCount++;
            }
        }

        for (FieldItem drop : toDrop) {
            spawnFieldItem(mob.getPosition(), drop, 1, 0); // XXX experiment with delay
        }
    }

    public void spawnFieldItem(Point start, FieldItem item, int enterType, int delay) {
        if (enterType != 3) {
            item.setOid(nextOid());
            objects.put(item.getOid(), item);
            items.put(item.getOid(), item);
        }
        broadcast(PacketCreator.spawnFieldItem(item, enterType, start, item.getPosition(), delay), p -> item.shouldBeSeenBy(p)); 
        // XXX see if this is alright
    }
    
    public void removeFieldItem(FieldItem item, int leaveType, int delay) {
        items.remove(item.getOid());
        objects.remove(item.getOid());
        
        broadcast(PacketCreator.removeFieldItem(item, leaveType, delay), p -> item.shouldBeSeenBy(p));
    }

    public void spawnPlayer(Player p) {
        spawnPlayer(p, true);
    }

    public void spawnPlayer(Player p, boolean spawn) {
        if (spawn) {
            p.setPosition(getSpawnLocation(p.getSpawnpoint()));
        }
        // XXX onUserEnter junk
        // XXX field limitations
        // XXX pets
        broadcast(PacketCreator.spawnPlayer(p), p.isHidden());

        addFieldObject(p);

        Supplier<Stream<FieldObject>> viewable = () -> objects.values().stream().filter(o -> !o.isHidden());

        Supplier<Stream<FieldObject>> visible = () -> viewable.get().filter(o -> !o.isNonRanged());

        viewable.get().filter(o -> o.isNonRanged()).forEach(o -> o.sendSpawnData(p.getClient()));

        viewable.get().filter(o -> o.getObjectType().equals(Type.NPC)).map(o -> (NPC) o).forEach(n -> updateController(n, p));

        visible.get().filter(o -> o.getObjectType().equals(Type.MONSTER)).map(o -> (Monster) o).forEach(m -> updateController(m, p));

        visible.get().filter(o -> inRange(p.getPosition(), o.getPosition())).forEach(o -> {
            o.sendSpawnData(p.getClient());
            p.addVisibleObject(o.getOid());
        });

        reactors.stream().filter(r -> (!r.isHidden() && inRange(p.getPosition(), r.getPosition()))).
                forEach(r -> {
                    r.sendSpawnData(p.getClient());
                    p.addVisibleObject(r.getOid());
                });
        if (fieldType > 0) { // for maple island tutorial, sauna, etc
            p.getClient().write(PacketCreator.forceEquip());
        }
        // XXX spawn summon
        // XXX events
        // XXX clock
        // XXX boat
        if (pulseTask == null) {
            pulseHolderCID = p.getId();
            pulseTask = p.getClient().startFieldPulse(this);
        }
    }
    

    public void removePlayer(Player p) {
        List<Integer> toRemove = new ArrayList<>(p.getControlled());

        for (Integer r : toRemove) {
            FieldObject o = getFieldObject(r);
            if (o != null) {
                if (o instanceof FieldLife) {
                    ((FieldLife) o).removeControl(p);
                }
            }
        }
        removeFieldObject(p);

        toRemove.stream().forEach(target -> findNewController(target));

        getPlayers().forEach(o -> p.sendDestroyData(o.getClient()));
        
        if (pulseHolderCID == p.getId()) { // if this player was the pulse holder, cancel it and find someone else
            pulseTask.cancel(false); // let the last iteration be ran if it's in progress so we can reschedule
            pulseTask = null;
            pulseHolderCID = -1;
            Optional<Player> nextHolder = getPlayers().findAny();
            if (nextHolder.isPresent()) { // lets pulses not happen if the map is dead
                // this actually scales linearily with active maps due to this
                Player target = nextHolder.get();
                
                pulseHolderCID = target.getId();
                target.getClient().startFieldPulse(this);
            }
        }
    }
    
    // pulse the field to apply any updates to it
    public void pulse() {
        checkItemLife();
        checkRespawn(false);
    }
    
    private void checkItemLife() {
        List<FieldItem> expiring = new ArrayList<>();
        for (FieldItem item : items.values()) {
            if (item.isExpired()) {
                expiring.add(item);
            }
        }
        for (FieldItem item : expiring) {
            removeFieldItem(item, 1, 0); // XXX experiment with delay
        }
    }

    private void checkRespawn(boolean forceSpawn) {
        int shouldSpawn = (int) ((spawns.size() - monsters.size())
                * (1 + Math.floor(mobRate / 100)));
//        mobRate -> not sure how exponential this
//        actually makes mob spawns, going to see
//        if we should use this in some sort of calc
//        like: 1 + floor(mobRate / 100)
        if (shouldSpawn > 0 || forceSpawn) {
            Collections.shuffle(spawns);
            int count = 0;
            for (Spawner sp : spawns) {
                if (sp.shouldSpawn()
                        || (forceSpawn && 
                        !sp.getMonster().isBoss())) { // no force respawn bosses :)
                    sp.respawn(this, forceSpawn);
                }
                count++;
                if (count >= shouldSpawn) {
                    break;
                }
            }
        }
    }

    private static boolean inRange(Point from, Point to) {
        return inRange(from, to, CHARACTER_VIEW_DISTANCE);
    }

    private static boolean inRange(Point from, Point to, double range) {
        return from.distanceSq(to) <= range;
    }

    private Point getSpawnLocation(int spawn) {
        Portal ret = portals.get(spawn);
        if (ret != null && ret.getPn().equals("sp")) {
            return ret.getPosition();
        } else {
            for (Portal p : portals) { // first portal we find that is spawn
                if (p != null && p.getPn().equals("sp")) {
                    return p.getPosition();
                }
            }
        }
        if (!portals.isEmpty()) {
            return portals.get(0).getPosition();
        } else {
            return new Point(0, 0); // XXX find position by foothold, this shouldn't happen
        }
    }

    public int getClosestSpawn(Player c) {
        int closest = -1;
        double dist = 0xFFFFFFFF;
        // XXX debug this
        for (int i = 0; i < portals.size(); i++) {
            Portal p = portals.get(i);
            if (p != null && p.getPn().equals("sp")) {
                if (closest != -1) {
                    double cdist = p.getPosition().distance(c.getPosition());
                    if (cdist < dist) {
                        dist = cdist;
                        closest = i;
                    }
                } else {
                    closest = i;
                }
            }
        }
        return closest == -1 ? 0 : closest;
    }

    private void findNewController(int oid) {
        Player p = getPlayers().findFirst().orElse(null);
        if (p != null) {
            FieldObject o = getFieldObject(oid);
            if (o instanceof NPC
                    || o instanceof Monster) {
                updateController((FieldLife) o, p);
            }
        }
    }

    private void updateController(FieldLife l, Player p) {
        if (l.getController() == FieldLife.NO_CONTROLLER) {
            l.giveControl(p);
        }
    }

    public void updatePlayerView(Player p) {
        Integer[] visible = p.getVisibleObjects().toArray(new Integer[0]);
        for (Integer i : visible) {
            if (objects.containsKey(i)) {
                updateObjectVisibility(p, i);
            }
        }
        objects.values().stream().filter(o -> !o.isNonRanged() && !p.canSee(o.getOid()) && inRange(p.getPosition(), o.getPosition())).forEach(o -> {
            o.sendSpawnData(p.getClient());
            p.addVisibleObject(o.getOid());
        });
    }

    private void updateObjectVisibility(Player p, int oid) {
        FieldObject o = objects.get(oid);
        if (!p.canSee(oid) && !o.isNonRanged()) {
            if (o.getObjectType().equals(Type.SUMMON)
                    || inRange(p.getPosition(), o.getPosition())) {
                p.addVisibleObject(oid);
                o.sendSpawnData(p.getClient());
                if (o.getObjectType().equals(Type.MONSTER)) {
                    Monster mob = (Monster) o;
                    updateController(mob, p);
                }
            }
        } else if (!o.isNonRanged()) {
            if (!o.getObjectType().equals(Type.SUMMON)
                    && !inRange(p.getPosition(), o.getPosition())) {
                p.removeVisibleObject(oid);
                o.sendDestroyData(p.getClient());
                // XXX check to make sure even if 
            }
        }
    }

    public void broadcast(Packet p) {
        broadcast(p, false, -1);
    }

    public void broadcast(Packet p, int... exclude) {
        broadcast(p, false, exclude);
    }

    public void broadcast(Packet p, boolean gm) {
        broadcast(p, gm, -1);
    }

    public void broadcast(Packet p, boolean gm, int... exclude) { // may need an option for range box
        for (FieldObject o : objects.values()) {
            if (o instanceof Player) {
                Player t = (Player) o;
                boolean skip = false;
                for (int i = 0; i < exclude.length; i++) {
                    if (exclude[i] == t.getId()) {
                        skip = true;
                        break;
                    }
                }
                if (!skip) {
                    if (!gm || (gm && t.isGM())) {
                        t.getClient().write(p.clone());
                    }
                }
            }
        }
    }

    public void broadcast(Packet packet, Predicate<Player> cond) {
        getPlayers().filter(cond).forEach(p -> p.getClient().write(packet));
    }

    public Portal getPortal(int portal) {
        return portals.get(portal);
    }

    public Portal getPortalByName(String pn) {
        for (Portal mp : portals) {
            if (mp.getPn().equals(pn)) {
                // this isn't accurate if there's multiple
                // portals with the same name
                return mp;
            }
        }
        return null;
    }
}
