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
package wz.common;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.*;
import java.util.zip.Inflater;

/**
 *
 * @author Brent
 */
public final class PNG {

    private int width;
    private int height;
    private int format;
    private byte[] data;
    private Image img = null;
    private boolean inflated = false;
    private static final int[] ZAHLEN = new int[]{0x02, 0x01, 0x00, 0x03};

    public PNG(int w, int h, int f, byte[] rD) {
        width = w;
        height = h;
        format = f;
        data = rD;
    }

    public Image getImage(boolean store) {
        if (img != null) {
            return img;
        }
        if (!inflated) {
            inflateData();
        }
        Image ret = createImage();
        if (store) {
            img = ret;
        }
        return ret;
    }

    public void inflateData() {
        int len;
        int size;
        int bufSize = height * width * 8;
        byte[] decBuff = new byte[bufSize > 2 ? bufSize : 2];
        size = height * width;
        switch (format) {
            case 2:
                size *= 2;
            case 1:
            case 513:
                size *= 4;
                break;
            case 517:
                size /= 128;
                break;
            case 1026:
                // DXT1 Format
                System.out.println("DXT1 Format is currently unsupported.");
                break;
            default:
                System.out.println("New image format: " + format);
                break;
        }
        byte[] unc = new byte[size];
        if (size > data.length) {
            Inflater dec = new Inflater();
            dec.setInput(data, 0, data.length);
            try {
                len = dec.inflate(unc);
            } catch (Exception e) {
                unc = data;
                len = unc.length;
            }
            dec.end();
        } else {
            unc = data;
            len = unc.length;
        }
        int index;
        switch (format) {
            case 1:
                for (int i = 0; i < size; i++) {
                    int lo = unc[i] & 0x0F;
                    int hi = unc[i] & 0xF0;
                    index = i << 1;
                    decBuff[index] = (byte) (((lo << 4) | lo) & 0xFF);
                    decBuff[index + 1] = (byte) (hi | (hi >>> 4) & 0x0F);
                }
                break;
            case 2:
                decBuff = unc;
                break;
            case 513:
                for (int i = 0; i < len; i += 2) {
                    int r = (unc[i + 1]) & 0xF8;
                    int g = ((unc[i + 1] & 0x07) << 5) | ((unc[i] & 0xE0) >> 3);
                    int b = ((unc[i] * 0x1F) << 3);
                    index = i << 1;
                    decBuff[index] = (byte) (b | (b >> 5));
                    decBuff[index + 1] = (byte) (g | (g >> 6));
                    decBuff[index + 2] = (byte) (r | (r >> 5));
                    decBuff[index + 3] = (byte) 0xFF;
                }
                break;
            case 517:
                int a;
                for (int i = 0; i < len; i++) {
                    for (int j = 0; j < 8; j++) {
                        a = ((unc[i] & (0x01 << (7 - j))) >> (7 - j)) * 0xFF;
                        for (int k = 0; k < 16; k++) {
                            index = (i << 9) + (j << 6) + k * 2;
                            decBuff[index] = (byte) a;
                            decBuff[index + 1] = (byte) a;
                            decBuff[index + 2] = (byte) a;
                            decBuff[index + 3] = (byte) 0xFF;
                        }
                    }
                }
                break;
        }
        data = decBuff;
    }

    private Image createImage() {
        DataBufferByte imgData = new DataBufferByte(data, data.length);
        SampleModel model = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width, height, 4, width * 4, ZAHLEN);
        WritableRaster raster = Raster.createWritableRaster(model, imgData, new Point(0, 0));
        BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        ret.setData(raster);
        return ret;
    }

    public boolean isInflated() {
        return inflated;
    }

    public byte[] rawData() {
        return data;
    }
    
    public int getFormat() {
        return format;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.width;
        hash = 97 * hash + this.height;
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PNG) {
            PNG other = (PNG) o;
            return other.height == height && other.width == width
                    && other.data == data;
        }
        return false;
    }
}
