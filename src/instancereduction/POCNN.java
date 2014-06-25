package instancereduction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import util.Combination2OutOfN;
import util.Dbg;
import util.SysUtil;
import artificialNeuralNetworks.ANN.AnnLearner;
import artificialNeuralNetworks.ANN.AnnLearner.AccurAndIter;

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
    public static final String MODULE = "POCNN";
    public static final boolean DBG = true;
    private static String attrf =
            "http://my.fit.edu/~sunx2013/MachineLearning/iris-attr.txt";
    private static String trainf =
            "http://my.fit.edu/~sunx2013/MachineLearning/iris-train.txt";
    private static String testf =
            "http://my.fit.edu/~sunx2013/MachineLearning/iris-test.txt";

    public static void main (final String[] args) {

        final RawAttrList rawAttr = new RawAttrList(attrf);
        final RawExampleList exs = new RawExampleList(trainf);
        final RawExampleList test = new RawExampleList(testf);
        final AnnLearner annLearner = new AnnLearner(rawAttr, 0.1, 0.1);
        long ennEditTime = SysUtil.getCpuTime();
        RawExampleList rawTrainWithNoise = rPocNN(exs, rawAttr);
        ennEditTime = SysUtil.getCpuTime() - ennEditTime;
        annLearner.setRawTrainWithNoise(rawTrainWithNoise);

        annLearner.setRawTest(test);
        System.out.printf("POCNN: size %d", rawTrainWithNoise.size());

        System.out.printf(" EditTime %d", ennEditTime);
        int nH = 3;

        annLearner.setNumOfHiddenNodes(nH);
        long trainTime = SysUtil.getCpuTime();
        final AccurAndIter aai = annLearner.kFoldLearning2(3);
        trainTime = SysUtil.getCpuTime() - trainTime;
        final double accur = aai.accur;
        final int iter = aai.iter;

        System.out.printf(" nH %d accur %.4f iter %d trainTime %d", nH, accur,
                iter, trainTime);

        System.out.println();
    }

    public static RawExampleList sPocNN (final RawExampleList exs,
            final RawAttrList attrs) {
        final RawExampleList[] subS = seperateSbyClass(exs, attrs);
        // Use HashSet to store the result to avoid duplicate examples.
        final HashSet<RawExample> result = new HashSet<RawExample>();
        // Number of classes
        final int n = attrs.t.valueList.size();
        final Combination2OutOfN c = new Combination2OutOfN(n);
        while (true) {
            int[] com = c.next();
            if (com == null) {
                break;
            }
            Dbg.print(DBG, MODULE,
                    "Processing classes: " + Arrays.toString(com));

            final int s1Index = com[0] - 1;
            final int s2Index = com[1] - 1;
            final RawExampleList s1 = subS[s1Index];
            final RawExampleList s2 = subS[s2Index];
            result.addAll(selectingPocNN(s1, s2));
        }
        final RawExampleList ret = new RawExampleList();
        ret.addAll(result);
        Dbg.print(DBG, MODULE, "Final data set:" + ret.size() + Dbg.NEW_LINE
                + ret);
        return ret;
    }

    public static RawExampleList rPocNN (final RawExampleList exs,
            final RawAttrList attrs) {
        final RawExampleList[] subS = seperateSbyClass(exs, attrs);
        // Use HashSet to store the result to avoid duplicate examples.
        final HashSet<RawExample> result = new HashSet<RawExample>();
        // Number of classes
        final int n = attrs.t.valueList.size();
        final Combination2OutOfN c = new Combination2OutOfN(n);
        while (true) {
            int[] com = c.next();
            if (com == null) {
                break;
            }
            Dbg.print(DBG, MODULE,
                    "Processing classes: " + Arrays.toString(com));

            final int s1Index = com[0] - 1;
            final int s2Index = com[1] - 1;
            final RawExampleList s1 = subS[s1Index];
            final RawExampleList s2 = subS[s2Index];
            result.addAll(replacingPocNN(s1, s2));
        }
        final RawExampleList ret = new RawExampleList();
        ret.addAll(result);
        Dbg.print(DBG, MODULE, "Final data set:" + ret.size() + Dbg.NEW_LINE
                + ret);
        return ret;
    }

    private static RawExampleList[] seperateSbyClass (final RawExampleList s,
            final RawAttrList attrs) {
        final ArrayList<String> classes = attrs.t.valueList;
        final RawExampleList[] subS = new RawExampleList[classes.size()];
        for (int i = 0; i < subS.length; i++) {
            subS[i] = new RawExampleList();
        }
        for (RawExample e : s) {
            final int index = classes.indexOf(e.t);
            subS[index].add(e);
        }
        return subS;
    }

    private static RawExampleList selectingPocNN (final RawExampleList s1,
            final RawExampleList s2) {
        final RawExample[] xp = findingPocNN(s1, s2);
        final RawExample xp1 = xp[0];
        final RawExample xp2 = xp[1];
        final HyperPlane h = new HyperPlane(xp1, xp2);
        final RawExampleList r1s1 = new RawExampleList();
        final RawExampleList r2s1 = new RawExampleList();
        for (RawExample x : s1) {
            if (Double.compare(h.ask(x), 0) >= 0) {
                r1s1.add(x);
            } else {
                r2s1.add(x);
            }
        }
        final RawExampleList r1s2 = new RawExampleList();
        final RawExampleList r2s2 = new RawExampleList();
        for (RawExample x : s2) {
            if (Double.compare(h.ask(x), 0) >= 0) {
                r1s2.add(x);
            } else {
                r2s2.add(x);
            }
        }

        final RawExampleList ret = new RawExampleList();
        ret.add(xp1);
        ret.add(xp2);
        if (!r1s1.isEmpty() && !r1s2.isEmpty()) { // Misclassification in r1
            ret.addAll(selectingPocNN(r1s1, r1s2));
        }
        if (!r2s1.isEmpty() && !r2s2.isEmpty()) { // Misclassification in r2
            ret.addAll(selectingPocNN(r2s1, r2s2));
        }

        Dbg.print(DBG, MODULE, "Selected instances:" + ret.size()
                + Dbg.NEW_LINE + ret);
        return ret;
    }

    private static RawExampleList replacingPocNN (final RawExampleList s1,
            final RawExampleList s2) {
        final RawExample[] xp = findingPocNN(s1, s2);
        final RawExample xp1 = xp[0];
        final RawExample xp2 = xp[1];

        final HyperPlane h = new HyperPlane(xp1, xp2);
        final RawExampleList r1s1 = new RawExampleList();
        final RawExampleList r2s1 = new RawExampleList();
        for (RawExample x : s1) {
            if (Double.compare(h.ask(x), 0) >= 0) {
                r1s1.add(x);
            } else {
                r2s1.add(x);
            }
        }
        final RawExampleList r1s2 = new RawExampleList();
        final RawExampleList r2s2 = new RawExampleList();
        for (RawExample x : s2) {
            if (Double.compare(h.ask(x), 0) >= 0) {
                r1s2.add(x);
            } else {
                r2s2.add(x);
            }
        }
        final RawExampleList ret = new RawExampleList();
        if (!r1s1.isEmpty() && !r1s2.isEmpty()) { // Misclassification in r1
            ret.addAll(replacingPocNN(r1s1, r1s2));
        } else {
            final RawExample xmor = getXmor(r1s1, r1s2);
            ret.add(xmor);
        }
        if (!r2s1.isEmpty() && !r2s2.isEmpty()) { // Misclassification in r2
            ret.addAll(replacingPocNN(r2s1, r2s2));
        } else {
            final RawExample xmor = getXmor(r2s1, r2s2);
            ret.add(xmor);
        }

        Dbg.print(DBG, MODULE, "Replaced instances:" + ret.size()
                + Dbg.NEW_LINE + ret);
        return ret;
    }

    private static RawExample getXmor (RawExampleList s1, RawExampleList s2) {
        final RawExampleList s;
        if (s1.isEmpty()) {
            s = s2;
        } else {
            s = s1;
        }
        final RawExample xmor = meanOf(s);
        return xmor;
    }

    private static class HyperPlane {
        public final double[] w;
        public final double b;
        public final RawExample xp; // To prevent identical points.

        public HyperPlane(RawExample xp1, RawExample xp2) {
            boolean isEqual = true;
            for (int i = 0; i < xp1.xList.size(); i++) {
                if (!xp1.xList.get(i).equals(xp2.xList.get(i))) {
                    isEqual = false;
                    break;
                }
            }

            if (!isEqual) {
                w = getW(xp1, xp2);

                final double[] c = middlePoint(xp1, xp2);
                double bt = 0;
                for (int i = 0; i < w.length; i++) {
                    bt += w[i] * c[i];
                }
                b = bt;
            } else {
                // To prevent identical points having different classes.
                // Slope (w) is random, point (c) is xp1.
                w = new double[xp1.xList.size()];
                for (int i = 0; i < w.length; i++) {
                    w[i] = Math.random();
                }
                final double[] c = new double[xp1.xList.size()];
                for (int i = 0; i < c.length; i++) {
                    c[i] = Double.parseDouble(xp1.xList.get(i));
                }
                double bt = 0;
                for (int i = 0; i < w.length; i++) {
                    bt += w[i] * c[i];
                }
                b = bt;
            }
            xp = xp1;
        }

        public double ask (RawExample x) {
            double ret = 0;
            for (int i = 0; i < w.length; i++) {
                final double vx = Double.parseDouble(x.xList.get(i));
                ret += w[i] * vx;
            }
            if (Double.compare(ret - b, 0) != 0) {
                return ret - b;
            } else {
                // If many points are just on the plane, return separately to
                // prevent two identical points which have same class but never
                // got separated.
                return x.equals(xp) ? Double.MIN_VALUE : -Double.MIN_VALUE;
            }
        }

        private static double[] getW (RawExample xp1, RawExample xp2) {
            final double[] w = new double[xp1.xList.size()];
            for (int i = 0; i < xp1.xList.size(); i++) {
                final double v1 = Double.parseDouble(xp1.xList.get(i));
                final double v2 = Double.parseDouble(xp2.xList.get(i));
                w[i] = v1 - v2;
            }
            double wMode = 0;
            for (int i = 0; i < w.length; i++) {
                wMode += w[i] * w[i];
            }
            wMode = Math.sqrt(wMode);
            if (Double.compare(wMode, 0) == 0) { // Avoid mode equals to zero.
                wMode = Double.MIN_VALUE;
            }

            for (int i = 0; i < w.length; i++) {
                w[i] /= wMode;
            }

            return w;
        }

        private static double[] middlePoint (RawExample xp1, RawExample xp2) {
            final double[] c = new double[xp1.xList.size()];
            for (int i = 0; i < xp1.xList.size(); i++) {
                final double v1 = Double.parseDouble(xp1.xList.get(i));
                final double v2 = Double.parseDouble(xp2.xList.get(i));
                c[i] = (v1 + v2) / 2;
            }
            return c;
        }
    }

    private static RawExample[] findingPocNN (final RawExampleList s1,
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
            if (Double.compare(minDis, dis) > 0) {
                minDis = dis;
                nn = e;
            }
        }
        assert nn != null;
        return nn;
    }
}
