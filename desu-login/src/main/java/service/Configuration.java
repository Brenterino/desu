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
 * Configurations regarding the Login Server. Mirrors the way Invictus
 * used to load configurations.
 * 
 * @author Brent
 */
public class Configuration {
    
    // [Login Service Information]
    public static int PORT;
    public static int SERVER_TYPE;
    public static short MAPLE_VERSION;
    public static String BUILD_VERSION;
    public static boolean SERVER_CHECK;
    public static int MAXIMUM_CONNECTIONS;

    // [Database Information]
    public static String URL;
    public static String USER;
    public static String PASS;

    // [World Service Details]
    public static int WORLD_SERVICE_PORT;
    public static String WORLD_SERVICE_KEY;

    static {
        File f = new File("config.ini");
        if (!f.exists()) {
            try (FileOutputStream fout = new FileOutputStream(f)) {
                PrintStream out = new PrintStream(fout);
                out.println("[Login Service Information]");
                out.println("PORT = ");
                out.println("SERVER_TYPE = ");
                out.println("MAPLE_VERSION = ");
                out.println("BUILD_VERSION = ");
                out.println("SERVER_CHECK = ");
                out.println("MAXIMUM_CONNECTIONS = ");
                out.println();
                out.println("[Database Information]");
                out.println("URL = ");
                out.println("USER = ");
                out.println("PASS = ");
                out.println();
                out.println("[World Service Details]");
                out.println("WORLD_SERVICE_PORT = ");
                out.println("WORLD_SERVICE_KEY = ");
                fout.flush();
                fout.close();
            } catch (Exception e) {
            }
            System.out.println("Please configure 'config.ini' and relaunch the Login Server.");
            System.exit(0);
        }
        Properties p = new Properties();
        try (FileReader fr = new FileReader(f)) {
            p.load(fr);
            PORT = Integer.parseInt(p.getProperty("PORT"));
            SERVER_TYPE = Integer.parseInt(p.getProperty("SERVER_TYPE"));
            MAPLE_VERSION = Short.parseShort(p.getProperty("MAPLE_VERSION"));
            BUILD_VERSION = p.getProperty("BUILD_VERSION");
            SERVER_CHECK = Boolean.parseBoolean(p.getProperty("SERVER_CHECK", "false"));
            MAXIMUM_CONNECTIONS = Integer.parseInt(p.getProperty("MAXIMUM_CONNECTIONS", "1000"));
            URL = p.getProperty("URL");
            USER = p.getProperty("USER");
            PASS = p.getProperty("PASS");
            WORLD_SERVICE_PORT = Integer.parseInt(p.getProperty("WORLD_SERVICE_PORT"));
            WORLD_SERVICE_KEY = p.getProperty("WORLD_SERVICE_KEY");
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.clear();
    }

    private Configuration() {
    }
}
