package common;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Random;

import common.Region.Circle;

/**
 * FileName: DataGenerator.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Jun 17, 2014 11:31:32 AM
 */
public class DataGenerator {
    public static final String[] CLASS = {"Y", "N"};
    
    private static final double R1 = (1 - (Math.sqrt(2)) / 2);
    private static final Point2D.Double[] C1 = {
            new Point2D.Double(1 - R1, R1), new Point2D.Double(R1, 1 - R1) };
    private static final int NUM = 200;

    private static final double RATIO = 0.5;
    
    private static final RawAttrList ATTRS = new RawAttrList();
    static {
        ATTRS.xList.add(new RawAttr("X1", true));
        ATTRS.xList.add(new RawAttr("X2", true));
        ATTRS.t = new RawAttr("Class");
        ATTRS.t.valueList.add(CLASS[0]);
        ATTRS.t.valueList.add(CLASS[1]);
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
            e.t = CLASS[0];
            s.add(e);
        }
        for (Point2D.Double p: nSet){
            final RawExample e = new RawExample();
            e.xList.add(String.valueOf(p.x));
            e.xList.add(String.valueOf(p.y));
            e.t = CLASS[1];
            s.add(e);
        }
        final RawExampleList[] sub = TrainTestSplitter.splitSetWithConsistentClassRatio(s, ATTRS, RATIO);
        System.out.println("Sub 1 size: "+ sub[0].size());
        System.out.println(sub[0]);
        System.out.println("Sub 2 size: "+ sub[1].size());
        System.out.println(sub[1]);
    }
    
    private static HashSet<Point2D.Double> gene(final Region reg, final int num){
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
}
