package common;

import java.awt.geom.Point2D;

import util.MyMath;

/**
 * FileName: Region.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Jun 17, 2014 5:19:31 PM
 */
public interface Region {
    public boolean isInside (Point2D.Double p);

    /**
     * Circle region (include the boundary)
     * c: center. r: radius.
     * */
    public static class Circle implements Region {
        public final Point2D.Double c;
        public final double r;

        public Circle(Point2D.Double c, double r) {
            this.c = c;
            this.r = r;
        }

        @Override
        public boolean isInside (Point2D.Double p) {
            if (Double.compare(MyMath.distance(c, p), r) <= 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Ribbon region between two lines, include the boundary.
     * y = kx + b1
     * y = kx + b2
     * 
     * Return true if P(x, y) is
     * y-kx-b1<=0 and y-kx-b2>=0
     * */
    public static class Ribbon implements Region {
        public final double k; // Slope.
        public final double b1; // Higher one.
        public final double b2; // Lower one.

        public Ribbon(double k, double b1, double b2) {
            this.k = k;
            if (Double.compare(b1, b2) >= 0) {
                this.b1 = b1;
                this.b2 = b2;
            } else {
                this.b1 = b2;
                this.b2 = b1;
            }
        }

        @Override
        public boolean isInside (Point2D.Double p) {
            if ((p.y - k * p.x - b1 <= 0) && (p.y - k * p.x - b2 >= 0)) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String toString () {
            return String.format("[y = %.2fx + %.2f, y = %.2fx + %.2f]", k, b1,
                    k, b2);
        }
    }
    
    /**
     * Parallelogram region between 2 ribbons, include the boundary.
     * 
     * Return true if P(x, y) is inside both of 2 ribbons.
     * */
    public static class Parallelogram implements Region {
        public final Ribbon rib1;
        public final Ribbon rib2;

        public Parallelogram(Ribbon rib1, Ribbon rib2) {
            this.rib1 = rib1;
            this.rib2 = rib2;
        }

        @Override
        public boolean isInside (Point2D.Double p) {
            if (rib1.isInside(p) && rib2.isInside(p)) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String toString () {
            return "[" + rib1.toString() + rib2.toString() + "]";
        }
    }
}
