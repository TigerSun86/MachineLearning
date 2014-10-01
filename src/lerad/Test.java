package lerad;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Scanner;

import util.Dbg;
import util.DisplayChart;
import util.MyMath;

import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

import dataset.DataSet;
import dataset.LERAD_Toy;
import dataset.LERAD_haberman;
import dataset.LERAD_ids;

/**
 * FileName: Test.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 23, 2014 9:43:37 PM
 */
public class Test {
    private static final DataSet[] DATA_SOURCE = { new LERAD_Toy(),
            new LERAD_ids(), new LERAD_haberman() };
    private static double noise = 0;

    public static void main (String[] args) {
        final Scanner sc = new Scanner(System.in);
        System.out.print("Please choose data set: ");
        for (int i = 0; i < DATA_SOURCE.length; i++) {
            System.out.print(i + " " + DATA_SOURCE[i].getName() + " ");
        }
        System.out.println();
        final int dsetindex = getInt(sc);
        if (dsetindex >= DATA_SOURCE.length) {
            return;
        }
        final LERAD learner = new LERAD();
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("Please choose test mode: ");
            System.out.println("0. set default parameters");
            System.out.println("1. simple test");
            System.out.println("2. different false positive rate test");
            System.out.println("3. vary parameters test");
            if (dsetindex != 0) System.out.println("4. noise test");
            System.out.println("other. quit");
            final int testmode = getInt(sc);
            if (testmode == 0) {
                set(learner, sc);
            } else if (testmode == 1) {
                simpleTest(dsetindex, learner, sc);
            } else if (testmode == 2) {
                fpRateTest(dsetindex, learner, sc);
            } else if (testmode == 3) {
                parameterTest(dsetindex, learner, sc);
            } else if (testmode == 4 && dsetindex != 0) {
                noiseTest(dsetindex, learner, sc);
            } else {
                isRunning = false;
            }
        }

        sc.close();
    }

    private static void set (LERAD learner, Scanner sc) {
        System.out.print("Please choose parameter to change: ");
        System.out
                .printf("0. L = %d, 1. M = %d, 2. S = %d, 3. Val_rate = %.2f, 4. Noise rate = %.3f%n",
                        learner.L, learner.M, learner.S, learner.valRate, noise);
        final int command = getInt(sc);
        final String[] str = { "L", "M", "S", "Validation rate","Noise rate" };
        if (command >= str.length) {
            return;
        }
        System.out.println("Please input new " + str[command]);
        if (command == 3 || command == 4) {// val or noise.
            final double value = getDouble(sc);
            if (command == 3) {
                learner.valRate = value;
            } else {
                noise = value;
            }
        } else {
            final int value = getInt(sc);
            if (command == 0) {
                learner.L = value;
            } else if (command == 1) {
                learner.M = value;
            } else {
                learner.S = value;
            }
        }

    }

    private static void simpleTest (final int dsetindex, final LERAD learner,
            final Scanner sc) {
        Dbg.dbgSwitch = true;
        Dbg.defaultSwitch = true;
        // Read data from files.
        final RawAttrList rawAttr =
                new RawAttrList(DATA_SOURCE[dsetindex].getAttrFileUrl());
        final RawExampleList rawTrain =
                new RawExampleList(DATA_SOURCE[dsetindex].getTrainFileUrl());
        final RawExampleList rawTest =
                new RawExampleList(DATA_SOURCE[dsetindex].getTestFileUrl());

        final RawExampleList noiseTrain =
                getNoiseTrain2(dsetindex, rawTrain, noise);

        System.out.println("Please input false positive rate:");
        final double fp = getDouble(sc);
        final RuleList h = learner.learn(noiseTrain, rawAttr);
        System.out.println("Learnt hypothesis: ");
        System.out.println(h);

        LERAD.getAUCby1PercentFp(rawTest, rawAttr, h, fp, false);

        Dbg.dbgSwitch = false;
        Dbg.defaultSwitch = false;
    }

    private static void fpRateTest (final int dsetindex, final LERAD learner,
            final Scanner sc) {
        Dbg.dbgSwitch = true;
        Dbg.defaultSwitch = true;
        // Read data from files.
        final RawAttrList rawAttr =
                new RawAttrList(DATA_SOURCE[dsetindex].getAttrFileUrl());
        final RawExampleList rawTrain =
                new RawExampleList(DATA_SOURCE[dsetindex].getTrainFileUrl());
        final RawExampleList rawTest =
                new RawExampleList(DATA_SOURCE[dsetindex].getTestFileUrl());

        final RawExampleList noiseTrain =
                getNoiseTrain2(dsetindex, rawTrain, noise);

        final RuleList h = learner.learn(noiseTrain, rawAttr);
        System.out.println("Learnt hypothesis: ");
        System.out.println(h);

        final double[][] fads =
                LERAD.getAUCby1PercentFp(rawTest, rawAttr, h, 0, true);

        final LinkedHashMap<Double, Double> sel0 =
                new LinkedHashMap<Double, Double>();
        for (double[] fad : fads) {
            sel0.put(fad[0], fad[1]);
        }
        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put("False positive rate", sel0);
        DisplayChart.display(dataSet, DATA_SOURCE[dsetindex].getName(),
                "Different false positive rate test", "False positive rate",
                "Detection rate");
        Dbg.dbgSwitch = false;
        Dbg.defaultSwitch = false;
    }

    private static void parameterTest (final int dsetindex,
            final LERAD learner, final Scanner sc) {
        // Read data from files.
        final RawAttrList rawAttr =
                new RawAttrList(DATA_SOURCE[dsetindex].getAttrFileUrl());
        final RawExampleList rawTrain =
                new RawExampleList(DATA_SOURCE[dsetindex].getTrainFileUrl());
        final RawExampleList rawTest =
                new RawExampleList(DATA_SOURCE[dsetindex].getTestFileUrl());

        final RawExampleList noiseTrain =
                getNoiseTrain2(dsetindex, rawTrain, noise);

        System.out.println("Please input false positive rate:");
        final double fp = getDouble(sc);

        final int lbackup = learner.L;
        LinkedHashMap<Double, Double> sel0 =
                new LinkedHashMap<Double, Double>();
        // Vary L.
        for (int l : new int[] { 1, 50, 100, 1000 }) {
            learner.L = l;
            final RuleList h = learner.learn(noiseTrain, rawAttr);
            final double[][] aucAndFp =
                    LERAD.getAUCby1PercentFp(rawTest, rawAttr, h, fp, false);
            sel0.put((double) l, aucAndFp[0][1]);
            System.out.println("L is " + l);
            System.out.printf("fp %.4f auc %.4f%n", aucAndFp[0][0],
                    aucAndFp[0][1]);
            System.out.println("Learnt hypothesis: ");
            System.out.println(h);

        }
        learner.L = lbackup;

        LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put("Vary L", sel0);
        DisplayChart.display(dataSet, DATA_SOURCE[dsetindex].getName(),
                "Vary parameter test", "Vary L", "AUC");

        final int mbackup = learner.M;
        sel0 = new LinkedHashMap<Double, Double>();
        // Vary M.
        for (int m : new int[] { 1, 2, 4, 10 }) {
            learner.M = m;
            final RuleList h = learner.learn(noiseTrain, rawAttr);
            final double[][] aucAndFp =
                    LERAD.getAUCby1PercentFp(rawTest, rawAttr, h, fp, false);
            sel0.put((double) m, aucAndFp[0][1]);
            System.out.println("M is " + m);
            System.out.printf("fp %.4f auc %.4f%n", aucAndFp[0][0],
                    aucAndFp[0][1]);
            System.out.println("Learnt hypothesis: ");
            System.out.println(h);

        }
        learner.M = mbackup;
        dataSet = new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put("Vary M", sel0);
        DisplayChart.display(dataSet, DATA_SOURCE[dsetindex].getName(),
                "Vary parameter test", "Vary M", "AUC");

        final int sbackup = learner.S;
        sel0 = new LinkedHashMap<Double, Double>();
        // Vary S.
        for (int s : new int[] { 2, 10, 100, 1000 }) {
            learner.S = s;
            final RuleList h = learner.learn(noiseTrain, rawAttr);
            final double[][] aucAndFp =
                    LERAD.getAUCby1PercentFp(rawTest, rawAttr, h, fp, false);
            sel0.put((double) s, aucAndFp[0][1]);
            System.out.println("S is " + s);
            System.out.printf("fp %.4f auc %.4f%n", aucAndFp[0][0],
                    aucAndFp[0][1]);
            System.out.println("Learnt hypothesis: ");
            System.out.println(h);

        }
        learner.S = sbackup;
        dataSet = new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put("Vary S", sel0);
        DisplayChart.display(dataSet, DATA_SOURCE[dsetindex].getName(),
                "Vary parameter test", "Vary S", "AUC");

        final double vbackup = learner.valRate;
        sel0 = new LinkedHashMap<Double, Double>();
        // Vary val rate.
        for (double v : new double[] { 0.1, 0.3, 0.5, 0.9 }) {
            learner.valRate = v;
            final RuleList h = learner.learn(noiseTrain, rawAttr);
            final double[][] aucAndFp =
                    LERAD.getAUCby1PercentFp(rawTest, rawAttr, h, fp, false);
            sel0.put((double) v, aucAndFp[0][1]);
            System.out.println("Val rate is " + v);
            System.out.printf("fp %.4f auc %.4f%n", aucAndFp[0][0],
                    aucAndFp[0][1]);
            System.out.println("Learnt hypothesis: ");
            System.out.println(h);

        }
        learner.valRate = vbackup;
        dataSet = new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put("Vary Validation rate", sel0);
        DisplayChart.display(dataSet, DATA_SOURCE[dsetindex].getName(),
                "Vary parameter test", "Vary Validation rate", "AUC");
    }

    private static void noiseTest (final int dsetindex, final LERAD learner,
            Scanner sc) {
        final RawAttrList rawAttr =
                new RawAttrList(DATA_SOURCE[dsetindex].getAttrFileUrl());
        final RawExampleList rawTrain =
                new RawExampleList(DATA_SOURCE[dsetindex].getTrainFileUrl());
        final RawExampleList rawTest =
                new RawExampleList(DATA_SOURCE[dsetindex].getTestFileUrl());

        final RawExampleList rawNoise =
                new RawExampleList(DATA_SOURCE[dsetindex].getDataFileUrl());
        System.out.println("Please input false positive rate:");
        
        final double fp = getDouble(sc);

        final LinkedHashMap<Double, Double> sel0 =
                new LinkedHashMap<Double, Double>();

        double noise = 0.00;
        for (int i = 0; i < 11; i++) {
            final RawExampleList trainWithNoise =
                    getNoiseTrain(rawTrain, rawNoise, noise);
            final RuleList h = learner.learn(trainWithNoise, rawAttr);
            final double[][] aucAndFp =
                    LERAD.getAUCby1PercentFp(rawTest, rawAttr, h, fp, false);

            sel0.put(noise, aucAndFp[0][1]);

            System.out.println("noise is " + noise);
            System.out.printf("fp %.4f auc %.4f%n", aucAndFp[0][1],
                    aucAndFp[0][1]);
            System.out.println(h);
            noise += 0.01;
        }

        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put("Noise rate", sel0);
        DisplayChart.display(dataSet, DATA_SOURCE[dsetindex].getName(),
                "Different noise rate test", "Noise rate", "AUC");

    }

    private static RawExampleList getNoiseTrain2 (int dsetindex,
            RawExampleList rawTrain, double noise) {
        if (Double.compare(noise, 0) <= 0) {
            return rawTrain;
        }
        final RawExampleList rawNoise =
                new RawExampleList(DATA_SOURCE[dsetindex].getDataFileUrl());
        final RawExampleList trainWithNoise =
                getNoiseTrain(rawTrain, rawNoise, noise);
        return trainWithNoise;
    }

    private static RawExampleList getNoiseTrain (RawExampleList rawTrain,
            RawExampleList rawNoise, double noise) {
        if (Double.compare(noise, 0) <= 0) {
            return rawTrain;
        }
        int noiseNum = (int) Math.round(noise * rawTrain.size());
        if (noiseNum > rawNoise.size()) {
            noiseNum = rawNoise.size();
        }
        // Discard array needs to be sorted, but picked doesn't.
        final int[] discardIndexes = MyMath.mOutofN(noiseNum, rawTrain.size());
        Arrays.sort(discardIndexes);
        final int[] pickedFromNoise = MyMath.mOutofN(noiseNum, rawNoise.size());
        final RawExampleList ret = new RawExampleList();
        int j = 0;
        int k = 0;
        for (int i = 0; i < rawTrain.size(); i++) {
            if ((j < discardIndexes.length) && (i == discardIndexes[j])) {
                final RawExample e = rawNoise.get(pickedFromNoise[k]);
                ret.add(e);
                j++;
                k++;
            } else {
                final RawExample e = rawTrain.get(i);
                ret.add(e);
            }
        }
        return ret;
    }

    private static int getInt (final Scanner s) {
        int ret = -1;
        while (ret < 0) {
            final String next = s.nextLine();
            try {
                ret = Integer.parseInt(next);
            } catch (NumberFormatException e) {
                ret = -1;
            }

            if (ret < 0) {
                System.out.println("Please reinput:");
            }
        }
        return ret;
    }

    private static double getDouble (final Scanner s) {
        double ret = Double.NaN;
        while (Double.isNaN(ret)) {
            final String next = s.nextLine();
            try {
                ret = Double.parseDouble(next);
            } catch (NumberFormatException e) {
                ret = Double.NaN;
            }
            // Only accept 0 <= ret <= 1.
            if (Double.compare(ret, 0) < 0 || Double.compare(ret, 1) > 0) {
                System.out.println("Please reinput:");
                ret = Double.NaN;
            }
        }
        return ret;
    }
}
