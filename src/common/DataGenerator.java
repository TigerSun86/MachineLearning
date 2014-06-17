package common;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Random;

/**
 * FileName: DataGenerator.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Jun 17, 2014 11:31:32 AM
 */
public class DataGenerator {
    private static final double R1 = (1 - (Math.sqrt(2)) / 2);
    private static final Point2D.Double[] C1 = {
            new Point2D.Double(1 - R1, R1), new Point2D.Double(R1, 1 - R1) };
    private static final int NUM = 200;
    private static final String CLASS1 = "Y";
    private static final String CLASS2 = "N";
    private static final double RATIO = 0.5;
    
    private static final RawAttrList ATTRS = new RawAttrList();
    static {
        ATTRS.xList.add(new RawAttr("X1", true));
        ATTRS.xList.add(new RawAttr("X2", true));
        ATTRS.t = new RawAttr("Class");
        ATTRS.t.valueList.add(CLASS1);
        ATTRS.t.valueList.add(CLASS2);
    }
    
    public static void main (String[] args) {
        final Region yC = new Circle(C1[0], R1);
        final Region nC = new Circle(C1[1], R1);
        final HashSet<Point2D.Double> ySet = gene(yC, NUM);
        final HashSet<Point2D.Double> nSet = gene(nC, NUM);

        final RawExampleList s = new RawExampleList();
        for (Point2D.Double p: ySet){
            final RawExample e = new RawExample();
            e.xList.add(String.valueOf(p.x));
            e.xList.add(String.valueOf(p.y));
            e.t = CLASS1;
            s.add(e);
        }
        for (Point2D.Double p: nSet){
            final RawExample e = new RawExample();
            e.xList.add(String.valueOf(p.x));
            e.xList.add(String.valueOf(p.y));
            e.t = CLASS2;
            s.add(e);
        }
        final RawExampleList[] sub = TrainTestSplitter.splitSetWithConsistentClassRatio(s, ATTRS, RATIO);
        System.out.println("Sub 1 size: "+ sub[0].size());
        System.out.println(sub[0]);
        System.out.println("Sub 2 size: "+ sub[1].size());
        System.out.println(sub[1]);
    }
    
    private static HashSet<Point2D.Double> gene(Region reg, final int num){
        final Random ran = new Random();
        final HashSet<Point2D.Double> set = new HashSet<Point2D.Double>();
        while (set.size() < num){
            final Point2D.Double p = new Point2D.Double(ran.nextDouble(), ran.nextDouble());
            if (reg.isInside(p)){
                set.add(p);
            }
        }
        return set;
    }

    private interface Region {
        public boolean isInside (Point2D.Double p);
    }

    private static class Circle implements Region {
        public final Point2D.Double c;
        public final double r;

        public Circle(Point2D.Double c, double r) {
            this.c = c;
            this.r = r;
        }

        @Override
        public boolean isInside (Point2D.Double p) {
            if (Double.compare(distance(c, p), r) <= 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    private static double distance (Point2D.Double p1, Point2D.Double p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
                * (p1.y - p2.y));
    }
}
