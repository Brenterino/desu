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
package client;

import client.packet.PacketCreator;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import netty.NettyClient;
import service.Configuration;
import service.Service;
import service.WorldService;
import util.TimeUtil;

/**
 * Represents a client within the Login Server state.
 *
 * @author Brent
 */
public class Client extends NettyClient {

    private int id = -1;
    private String name;
    private boolean gm;
    private boolean banned;
    private long banTime;
    private byte banReason;
    private int gender = 0x0A;
    private int characterSlots = 6;
    private int world = -1;
    private int channel;
    private String pin = "";
    private String lastIP = "";
    private boolean pinVerified = false;
    private boolean loggedin = false;
    private boolean transition = false;
    private int player = -1;
    private List<Player> players;
    private LocalDate birthDate = LocalDate.now();
    private ScheduledFuture<?> ping;

    public Client(Channel c, byte[] alpha, byte[] delta) {
        super(c, alpha, delta);
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String nP) {
        pin = nP;
    }

    public void triggerTransition() {
        transition = true;
    }

    public boolean isLoggedIn() {
        return loggedin;
    }

    public void setLoggedIn(boolean l) {
        loggedin = l;
    }

    public void setWorldChannelSelection(int w, int c) {
        world = w;
        channel = c;
    }

    public int getWorld() {
        return world;
    }

    public int getChannel() {
        return channel;
    }

    public void setCharacterSlots(int v) {
        characterSlots = v;
    }

    public int getCharacterSlots() {
        return characterSlots;
    }

    public void setAccountID(int a) {
        id = a;
    }

    public int getAccountId() {
        return id;
    }

    public void setAccountName(String userName) {
        name = userName;
    }

    public String getAccountName() {
        return name;
    }

    public void setAccountGender(int g) {
        gender = g;
    }

    public int getAccountGender() {
        return gender;
    }

    public void setGM(boolean b) {
        gm = b;
    }

    public boolean isGM() {
        return gm;
    }

    public void setLastIP(String ip) {
        lastIP = ip;
    }

    public String getLastIP() {
        return lastIP;
    }

    public void setBirthday(LocalDate birthday) {
        birthDate = birthday;
    }

    public LocalDate getBirthday() {
        return birthDate;
    }

    public void setBanned(boolean b) {
        banned = b;
    }

    private void setBanReason(byte bR) {
        banReason = bR;
    }

    private void setBanTime(long bT) {
        banTime = bT;
    }

    public byte getBanReason() {
        return banReason;
    }

    public long getBanTime() {
        return banTime;
    }

    public boolean isBanned() {
        return banned;
    }

    public boolean isPinVerified() {
        return pinVerified;
    }

    public void setPinVerified(boolean p) {
        pinVerified = p;
    }

    public void setPlayer(int p) {
        player = p;
    }

    public void addPlayer(Player p) {
        players.add(p);
    }    
    
    public void startPing(Channel c) {
        ping = c.eventLoop().scheduleAtFixedRate(() -> 
                c.writeAndFlush(PacketCreator.getPing()), 5, 5, TimeUnit.SECONDS);
    }
    
    public void cancelPingTask() {
        if (ping != null) {
            ping.cancel(true);
        }
    }

    public void softDisconnect(boolean save) {
        if (id > 0 && save) {
            Connection c = Service.getInstance().getDatabase().getConnection();
            try {
                PreparedStatement ps = c.prepareStatement("UPDATE accounts SET state = ?, pin = ?, gender = ?, ip = ? WHERE id = ?");

                ps.setInt(1, transition ? 2 : 0);
                ps.setString(2, pin);
                ps.setInt(3, gender);
                ps.setString(4, gm ? getLastIP() : getIP()); // stable IP only for GM accounts
                ps.setInt(5, id);

                ps.executeUpdate();

                ps.close();

                if (transition && player != -1) {
                    ps = c.prepareStatement("INSERT INTO transition (account, player, source) VALUES (?, ?, ?)");
                    ps.setInt(1, id);
                    ps.setInt(2, player);
                    ps.setInt(3, 0); // source was login server if value = 0, channel sources are coded by (world id * 100) + (channel id)

                    ps.execute();

                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace(); // XXX remove later
            } finally {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        setLoggedIn(false);
        setPinVerified(false);
    }

    public void initialLogin(String name, String pass) {
        Connection con = Service.getInstance().getDatabase().getConnection();
        
        try {
            PreparedStatement ps = con.prepareStatement("SELECT id, state, gm, pass, birthday, ip FROM accounts WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String dbPass = rs.getString("pass");
                if (dbPass.equals(pass)) { // XXX eventually, we can encrypt/hash password for security reasons; not necessary atm
                    setAccountID(rs.getInt("id"));
                    setAccountName(name);
                    setGM(rs.getByte("gm") > 0);
                    setLastIP(rs.getString("ip")); // XXX change this to a stable IP instead of last IP for players later
                    setBirthday(rs.getDate("birthday").toLocalDate());
                    if (isBanned(this, con)) {
                        write(PacketCreator.getBanMessage(getBanReason(), getBanTime()));
                    } else {
                        if (rs.getByte("state") > 0 || (Configuration.SERVER_CHECK && !isGM())) {
                            write(PacketCreator.getLoginFailed(7));
                        } else {
                            if (!getLastIP().equals(getIP())) { // trying to access account from remote location, need to validate first
                                write(PacketCreator.getLoginFailed(isGM() ? 13 : 17));
                            } else {
                                acquireAccountData(con);
                                write(PacketCreator.getAuthSuccess(this));
                            }
                        }
                    }
                } else {
                    write(PacketCreator.getLoginFailed(4));
                }
            } else {
                write(PacketCreator.getLoginFailed(5));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            write(PacketCreator.getLoginFailed(8));
            e.printStackTrace(); // XXX remove later
        } finally {
            try {
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void acquireAccountData(Connection con) {
        if (id == -1) {
            return;
        }
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET state = 1 WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();

            ps = con.prepareStatement("SELECT slots, pin, gender FROM accounts WHERE id = ?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                setCharacterSlots(rs.getInt("slots"));
                setPin(rs.getString("pin"));
                setAccountGender(rs.getInt("gender"));
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace(); // XXX remove later
        }
        setLoggedIn(true);
    }

    public static boolean isBanned(Client c, Connection con) {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT banned, banreason, bantime FROM accounts WHERE id = ?");

            ps.setInt(1, c.getAccountId());

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) { // would be strange if this were the case
                c.setBanned(false);
            } else {
                c.setBanned(rs.getByte("banned") > 0);
                c.setBanReason(rs.getByte("banreason"));
                c.setBanTime(rs.getLong("bantime"));
            }
            rs.close();
            ps.close();

            if (c.isBanned()) {
                long lift = c.getBanTime();
                if (lift != 0) {
                    Calendar now = Calendar.getInstance();

                    if (now.getTimeInMillis() > c.getBanTime()) {
                        c.setBanned(false);
                        c.setBanReason((byte) 0);
                        c.setBanTime(0);

                        ps = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = 0, bantime = 0 WHERE id = ?");
                        ps.setInt(1, c.getAccountId());
                        ps.executeUpdate();
                        ps.close();
                    } else {
                        c.setBanTime(TimeUtil.getTimestamp(lift));
                    }
                } else {
                    c.setBanTime(0xFFFFFFFFFFFFFFFFL); // real stamp for permaban
                }
            }

            if (!c.isBanned()) {
                ps = con.prepareStatement("SELECT * FROM netfilter WHERE ip = ?");
                ps.setString(1, c.getIP());

                rs = ps.executeQuery();

                if (rs.next()) {
                    c.setBanned(true);
                    c.setBanReason((byte) 0);
                    c.setBanTime(0xFFFFFFFFFFFFFFFFL);
                }

                rs.close();
                ps.close();
            }

            return c.isBanned();
        } catch (Exception e) {
            e.printStackTrace(); // XXX remove later
            return false;
        }
    }

    public List<Player> getPlayersInWorld(int world, boolean reload) {
        if (!reload) {
            return players;
        }
        LinkedList<Player> ret = new LinkedList<>();
        if (world != -1) {
            Connection c = WorldService.getInstance().getWorld(world).getDatabase().getConnection();
            try {
                PreparedStatement ps = c.prepareStatement("SELECT name FROM characters WHERE account = ?");
                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ret.add(Player.loadPlayer(c, rs.getString("name"), world));
                }
                ps.close();
                rs.close();
            } catch (Exception e) {
                e.printStackTrace(); // XXX remove later
            } finally {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            WorldService.getInstance().getWorlds().forEach(w -> {
                Connection c = w.getDatabase().getConnection();
                try {
                    PreparedStatement ps = c.prepareStatement("SELECT name FROM characters WHERE account = ?");
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        ret.add(Player.loadPlayer(c, rs.getString("name"), w.getWorldId()));
                    }
                    ps.close();
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace(); // XXX remove later
                } finally {
                    try {
                        c.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return players = ret;
    }

    public int deleteCharacter(int cid) {
        Player p = null;
        for (Player cp : players) {
            if (cp.getId() == cid) {
                p = cp;
            }
        }
        // XXX various checks required before realistically allowing deletion
        if (p != null) {
            Connection c = WorldService.getInstance().getWorld(world).getDatabase().getConnection();
            try { // XXX update as more data is added into the database
                PreparedStatement ps = c.prepareStatement("DELETE FROM characters WHERE id = ?");
                ps.setInt(1, cid);
                ps.executeUpdate();
                ps.close();

                ps = c.prepareStatement("DELETE FROM equips WHERE id = ?");
                ps.setInt(1, cid);
                ps.executeUpdate();
                ps.close();

                ps = c.prepareStatement("DELETE FROM items WHERE id = ?");
                ps.setInt(1, cid);
                ps.executeUpdate();
                ps.close();

                ps = c.prepareStatement("DELETE FROM keymap WHERE id = ?");
                ps.setInt(1, cid);
                ps.executeUpdate();
                ps.close();

                return 0x00;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }             
            }
        }
        return 0x01;
    }

    public boolean checkAgeRestriction(boolean isApplied) {
        return !isApplied
                || isGM()
                || Period.between(birthDate, LocalDate.now()).getYears() >= 20; // 20+
    }
}
