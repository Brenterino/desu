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
package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Configurations regarding the World Server. Mirrors the way Invictus
 * used to load configurations.
 * 
 * @author Brent
 */
public class Configuration {
    
    // [World Server Information]
    public static int WORLD_ID;
    public static String WORLD_NAME;
    public static int EXPERIENCE_MOD;
    public static int DROP_MOD;
    public static int EVENT_FLAG;
    public static String EVENT_MESSAGE;
    public static String SERVER_MESSAGE;
    
    // [Login Server Information]
    public static String WORLD_SERVICE;
    public static short WORLD_SERVICE_PORT;
    public static String WORLD_SERVICE_KEY;
    
    // [Ranking Worker Information]
    public static boolean RANKING_WORKER;
    public static long RANKING_WORKER_INTERVAL;

    // [Database Information]
    public static String URL;
    public static String USER;
    public static String PASS;

    // [Channel Service Details]
    public static int CHANNEL_SERVICE_PORT;
    public static String CHANNEL_SERVICE_KEY;

    static {
        File f = new File("config.ini");
        if (!f.exists()) {
            try (FileOutputStream fout = new FileOutputStream(f)) {
                PrintStream out = new PrintStream(fout);
                out.println("[World Server Information]");
                out.println("WORLD_ID = ");
                out.println("WORLD_NAME = ");
                out.println("EXPERIENCE_MOD = ");
                out.println("DROP_MOD = ");
                out.println("EVENT_FLAG = ");
                out.println("EVENT_MESSAGE = ");
                out.println("SERVER_MESSAGE = ");
                out.println();
                out.println("[Login Server Information]");
                out.println("WORLD_SERVICE = ");
                out.println("WORLD_SERVICE_PORT = ");
                out.println("WORLD_SERVICE_KEY = ");
                out.println();
                out.println("[Ranking Worker Information]");
                out.println("RANKING_WORKER = ");
                out.println("RANKING_WORKER_INTERVAL = ");
                out.println();
                out.println("[Database Information]");
                out.println("URL = ");
                out.println("USER = ");
                out.println("PASS = ");
                out.println();
                out.println("[Channel Service Details]");
                out.println("CHANNEL_SERVICE_PORT = ");
                out.println("CHANNEL_SERVICE_KEY = ");
                fout.flush();
                fout.close();
            } catch (Exception e) {
            }
            System.out.println("Please configure 'config.ini' and relaunch the World Server.");
            System.exit(0);
        }
        Properties p = new Properties();
        try (FileReader fr = new FileReader(f)) {
            p.load(fr);
            WORLD_ID = Integer.parseInt(p.getProperty("WORLD_ID"));
            WORLD_NAME = p.getProperty("WORLD_NAME");
            EXPERIENCE_MOD = Integer.parseInt(p.getProperty("EXPERIENCE_MOD"));
            DROP_MOD = Integer.parseInt(p.getProperty("DROP_MOD"));
            EVENT_FLAG = Integer.parseInt(p.getProperty("EVENT_FLAG"));
            EVENT_MESSAGE = p.getProperty("EVENT_MESSAGE");
            SERVER_MESSAGE = p.getProperty("SERVER_MESSAGE");
            
            WORLD_SERVICE = p.getProperty("WORLD_SERVICE");
            WORLD_SERVICE_PORT = Short.parseShort(p.getProperty("WORLD_SERVICE_PORT"));
            WORLD_SERVICE_KEY = p.getProperty("WORLD_SERVICE_KEY");
            
            RANKING_WORKER = Boolean.parseBoolean(p.getProperty("RANKING_WORKER", "false"));
            RANKING_WORKER_INTERVAL = Long.parseLong(p.getProperty("RANKING_WORKER_INTERVAL", "0"));
            
            URL = p.getProperty("URL");
            USER = p.getProperty("USER");
            PASS = p.getProperty("PASS");
            
            CHANNEL_SERVICE_PORT = Integer.parseInt(p.getProperty("CHANNEL_SERVICE_PORT"));
            CHANNEL_SERVICE_KEY = p.getProperty("CHANNEL_SERVICE_KEY");
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.clear();
    }

    private Configuration() {
    }
}
