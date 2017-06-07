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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Brent
 * @author Matze for the code from OdinMS
 */
public class FootholdTree {
    
    private FootholdTree nw = null;
    private FootholdTree ne = null;
    private FootholdTree sw = null;
    private FootholdTree se = null;
    private ArrayList<Foothold> footholds = new ArrayList<>();
    private Point p1;
    private Point p2;
    private Point center;
    private int depth = 0;
    private static int maxDepth = 8;
    private int maxDropX;
    private int minDropX;

    public FootholdTree(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
        center = new Point((p2.x - p1.x) / 2, (p2.y - p1.y) / 2);
    }

    public FootholdTree(Point p1, Point p2, int depth) {
        this.p1 = p1;
        this.p2 = p2;
        this.depth = depth;
        center = new Point((p2.x - p1.x) / 2, (p2.y - p1.y) / 2);
    }

    public void insert(Foothold f) {
        if (depth == 0) {
            if (f.x1 > maxDropX) {
                maxDropX = f.x1;
            }
            if (f.x1 < minDropX) {
                minDropX = f.x1;
            }
            if (f.x2 > maxDropX) {
                maxDropX = f.x2;
            }
            if (f.x2 < minDropX) {
                minDropX = f.x2;
            }
        }
        if (depth == maxDepth ||
                (f.x1 >= p1.x && f.x2 <= p2.x &&
                f.y1 >= p1.y && f.y2 <= p2.y)) {
            footholds.add(f);
        } else {
            if (nw == null) {
                nw = new FootholdTree(p1, center, depth + 1);
                ne = new FootholdTree(new Point(center.x, p1.y), new Point(p2.x, center.y), depth + 1);
                sw = new FootholdTree(new Point(p1.x, center.y), new Point(center.x, p2.y), depth + 1);
                se = new FootholdTree(center, p2, depth + 1);
            }
            if (f.x2 <= center.x && f.y2 <= center.y) {
                nw.insert(f);
            } else if (f.x1 > center.x && f.y2 <= center.y) {
                ne.insert(f);
            } else if (f.x2 <= center.x && f.y1 > center.y) {
                sw.insert(f);
            } else {
                se.insert(f);
            }
        }
    }

    private List<Foothold> getRelevants(Point p) {
        return getRelevants(p, new ArrayList<>());
    }

    private List<Foothold> getRelevants(Point p, List<Foothold> list) {
        list.addAll(footholds);
        if (nw != null) {
            if (p.x <= center.x && p.y <= center.y) {
                nw.getRelevants(p, list);
            } else if (p.x > center.x && p.y <= center.y) {
                ne.getRelevants(p, list);
            } else if (p.x <= center.x && p.y > center.y) {
                sw.getRelevants(p, list);
            } else {
                se.getRelevants(p, list);
            }
        }
        return list;
    }

    private Foothold findWallR(Point p1, Point p2) {
        Foothold ret;
        for (Foothold f : footholds) {
            if (f.isWall() && f.x1 >= p1.x && f.x1 <= p2.x &&
                    f.y1 >= p1.y && f.y2 <= p1.y) {
                return f;
            }
        }
        if (nw != null) {
            if (p1.x <= center.x && p1.y <= center.y) {
                ret = nw.findWallR(p1, p2);
                if (ret != null) {
                    return ret;
                }
            }
            if ((p1.x > center.x || p2.x > center.x) && p1.y <= center.y) {
                ret = ne.findWallR(p1, p2);
                if (ret != null) {
                    return ret;
                }
            }
            if (p1.x <= center.x && p1.y > center.y) {
                ret = sw.findWallR(p1, p2);
                if (ret != null) {
                    return ret;
                }
            }
            if ((p1.x > center.x || p2.x > center.x) && p1.y > center.y) {
                ret = se.findWallR(p1, p2);
                if (ret != null) {
                    return ret;
                }
            }
        }
        return null;
    }

    public Foothold findWall(Point p1, Point p2) {
        if (p1.y != p2.y) {
            throw new IllegalArgumentException();
        }
        return findWallR(p1, p2);
    }

    public Foothold findBelow(Point p) {
        List<Foothold> relevants = getRelevants(p);
        List<Foothold> xMatches = new ArrayList<>();
        for (Foothold fh : relevants) {
            if (fh.x1 <= p.x && fh.x2 >= p.x) {
                xMatches.add(fh);
            }
        }
        Collections.sort(xMatches);
        for (Foothold fh : xMatches) {
            if (!fh.isWall() && fh.y1 != fh.y2) {
                int calcY;
                double s1 = Math.abs(fh.y2 - fh.y1);
                double s2 = Math.abs(fh.x2 - fh.x1);
                double s4 = Math.abs(p.x - fh.x1);
                double alpha = Math.atan(s2 / s1);
                double beta = Math.atan(s1 / s2);
                double s5 = Math.cos(alpha) * (s4 / Math.cos(beta));
                if (fh.y2 < fh.y1) {
                    calcY = fh.y1 - (int) s5;
                } else {
                    calcY = fh.y1 + (int) s5;
                }
                if (calcY >= p.y) {
                    return fh;
                }
            } else if (!fh.isWall()) {
                if (fh.y1 >= p.y) {
                    return fh;
                }
            }
        }
        return null;
    }

    public int getMaxDropX() {
        return maxDropX;
    }

    public int getMinDropX() {
        return minDropX;
    }
}
