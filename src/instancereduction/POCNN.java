package instancereduction;

import java.util.BitSet;
import java.util.HashMap;

import util.Dbg;

import common.MapTool;
import common.RawAttr;
import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName: POCNN.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 13, 2014 11:50:40 AM
 */
public class POCNN {
    
    public static RawExampleList selectingPocNN(final RawExampleList s1,
            final RawExampleList s2){
        
        final RawExample[] xp = findingPocNN(s1, s2);
        final RawExample xp1 = xp[0];
        final RawExample xp2 = xp[1];
        final HyperPlane h = new HyperPlane(xp1, xp2);
        final RawExampleList r1s1 = new RawExampleList();
        final RawExampleList r2s1 = new RawExampleList();
        for (RawExample x: s1){
            if (Double.compare(h.ask(x),0) >=0){
                r1s1.add(x);
            } else {
                r2s1.add(x);
            }
        }
        final RawExampleList r1s2 = new RawExampleList();
        final RawExampleList r2s2 = new RawExampleList();
        for (RawExample x: s2){
            if (Double.compare(h.ask(x),0) >=0){
                r1s2.add(x);
            } else {
                r2s2.add(x);
            }
        }
        if (!r1s1.isEmpty() && !r1s2.isEmpty()){
            
        }
        if (!r2s1.isEmpty() && !r2s2.isEmpty()){
            
        }
        
        return null;
    }
    private static class HyperPlane {
        public final double[] w;
        public final double b;
        public HyperPlane(RawExample xp1, RawExample xp2){
            w = getW(xp1, xp2);
            
            final double[] c = middlePoint(xp1, xp2);
            double bt = 0;
            for (int i = 0; i < w.length; i++){
                bt += w[i] * c[i];
            }
            b = bt;
        }
        public double ask(RawExample x){
            double ret = 0;
            for (int i = 0; i < w.length; i++){
                final double vx = Double.parseDouble(x.xList.get(i));
                ret += w[i] * vx;
            }
            return ret - b;
        }
        private static double[] getW (RawExample xp1, RawExample xp2) {
            final double[] w = new double[xp1.xList.size()];
            for (int i = 0; i < xp1.xList.size(); i++){
                final double v1 = Double.parseDouble(xp1.xList.get(i));
                final double v2 = Double.parseDouble(xp2.xList.get(i));
                w[i] = v1 - v2;
            }
            double wMode = 0;
            for (int i = 0; i < w.length; i++){
                wMode += w[i] * w[i];
            }
            wMode = Math.sqrt(wMode);
            if (Double.compare(wMode, 0) == 0){ // Avoid mode equals to zero.
                wMode = Double.MIN_VALUE;
            }

            for (int i = 0; i < w.length; i++){
                w[i] /= wMode;
            }

            return w;
        }

        private static double[] middlePoint (RawExample xp1, RawExample xp2) {
            final double[] c = new double[xp1.xList.size()];
            for (int i = 0; i < xp1.xList.size(); i++){
                final double v1 = Double.parseDouble(xp1.xList.get(i));
                final double v2 = Double.parseDouble(xp2.xList.get(i));
                c[i] = (v1 + v2) /2;
            }
            return c;
        }
    }


    public static RawExample[] findingPocNN (final RawExampleList s1,
            final RawExampleList s2) {
        final RawExampleList st1;
        final RawExampleList st2;
        if (s1.size() >= s2.size()) {
            st1 = s1;
            st2 = s2;
        } else {
            st1 = s2;
            st2 = s1;
        }
        final RawExample xm = meanOf(st1);
        final RawExample xp1 = nearestNeighbor(st2, xm);
        final RawExample xp2 = nearestNeighbor(st1, xp1);

        final RawExample[] ret = new RawExample[2];
        if (s1.size() >= s2.size()) {
            ret[0] = xp2;
            ret[1] = xp1;
        } else {
            ret[1] = xp2;
            ret[0] = xp1;
        }

        return ret;
    }

    private static RawExample meanOf (final RawExampleList s) {
        final double[] mean = new double[s.get(0).xList.size()];
        for (RawExample e : s) {
            for (int i = 0; i < mean.length; i++) {
                mean[i] += Double.parseDouble(e.xList.get(i));
            }
        }
        for (int i = 0; i < mean.length; i++) {
            mean[i] /= s.size();
        }
        final RawExample xm = new RawExample();
        for (int i = 0; i < mean.length; i++) {
            final String str = String.valueOf(mean[i]);
            xm.xList.add(str);
        }
        xm.t = s.get(0).t;
        return xm;
    }

    private static RawExample nearestNeighbor (final RawExampleList s,
            final RawExample x) {
        final double[] v1 = new double[x.xList.size()];
        for (int i = 0; i < v1.length; i++) {
            v1[i] = Double.parseDouble(x.xList.get(i));
        }

        RawExample nn = null;
        double minDis = Double.POSITIVE_INFINITY;
        for (RawExample e : s) {
            double dis = 0;
            for (int i = 0; i < x.xList.size(); i++) {
                final double v2 = Double.parseDouble(e.xList.get(i));
                dis += Math.abs(v1[i] - v2);
            }
            if (Double.compare(minDis, dis) < 0) {
                minDis = dis;
                nn = e;
            }
        }
        assert nn != null;
        return nn;
    }
}
