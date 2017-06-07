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
package dbf;

import java.io.File;
import java.nio.file.Path;

/**
 *
 * @author Brent
 */
public final class DesuFile {

    private File storage;
    
    private DesuFile() {}
    
    public DesuFile(File src) {
        storage = src;
    }
    
    public DesuFile(Path src) {
        storage = src.toFile();
    }
    
    public void load() {
        
    }
}
