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
package dbf.io;

import java.io.IOException;
import java.io.InputStream;

import util.Reader;

/**
 *
 * @author Brent
 */
public class DesuReader extends Reader {

	private int offset = 0;
    private InputStream base;
    
    public DesuReader() {
    	// XXX CODE
    }
    
    @Override
    public int read() {
    	try {
    		return base.read();
    	} catch (IOException e) {
    		return -1;
    	} finally {
    		offset++;
    	}
    }

    @Override
    public Reader skip(int num) {
    	if (num < 1) return this;
    	
    	try {
    		base.skip(num);
    	} catch (IOException e) {
    		// swallow
    	}
    	offset += num;
		
		return this;
    }

    @Override
    public int available() {
    	try { 
    		return base.available();
    	} catch (IOException e) {
    		return 0;
    	}
    }

    @Override
    public int getOffset() {
    	return offset;
    }

    @Override
    public void close() {
    	try {
    		base.close();
    	} catch (IOException e) {
    		// swallow
    	}
    }
}
