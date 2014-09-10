package ripperk;

import java.util.LinkedHashMap;
import java.util.Scanner;

import util.Dbg;
import util.DisplayChart;
import util.MyMath;
import common.DataCorrupter;
import common.Evaluator;
import common.Hypothesis;
import common.RawAttrList;
import common.RawExampleList;
import dataset.DataSet;
import dataset.Ids_mixed;
import dataset.Iris;
import dataset.Restaurant;

/**
 * FileName: Test.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 1, 2014 1:36:40 PM
 */
public class Test {
    private static final int TIMES = 1;
    private static final DataSet[] DATA_SOURCE = { new Restaurant(),
            new Ids_mixed(), new Iris() };

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
        System.out.print("Please choose test mode: ");
        System.out
                .println("0 simple test 1 train_test test 2 prune test 3 optimazation test");
        final int testmode = getInt(sc);
        if (testmode == 0) {
            simpleTest(dsetindex, sc);
        } else if (testmode == 1) {
            trainTestTest(dsetindex);
        } else if (testmode == 2) {
            pruneTest(dsetindex);
        } else if (testmode == 3) {
            opTest(dsetindex);
        } else {
            return;
        }
        sc.close();
    }

    private static void simpleTest (final int dsetindex, final Scanner sc) {
        Dbg.dbgSwitch = true;
        Dbg.defaultSwitch = true;
        // Read data from files.
        final RawAttrList rawAttr =
                new RawAttrList(DATA_SOURCE[dsetindex].getAttrFileUrl());
        final RawExampleList rawTrain =
                new RawExampleList(DATA_SOURCE[dsetindex].getTrainFileUrl());
        final RawExampleList rawTest =
                new RawExampleList(DATA_SOURCE[dsetindex].getTestFileUrl());
        System.out
                .println("Please input the noise rate, need pruning or not, and k. "
                        + "Eg: \"0.2 1 1\" means 0.2 noise rate, need pruning, and k = 1.");
        final double noiseRate = sc.nextDouble();
        final boolean needPruning = (sc.nextInt() == 1);
        final int k = sc.nextInt();
        System.out
                .printf("Data set is %s, noise rate is %.2f, needPruning = %s k = %d%n",
                        DATA_SOURCE[dsetindex].getName(), noiseRate,
                        Boolean.toString(needPruning), k);
        
        final RawExampleList noiseTrain =
                DataCorrupter.corrupt(rawTrain, rawAttr, noiseRate);
        final Hypothesis h = RIPPERk.learn(noiseTrain, rawAttr, needPruning, k);
        System.out.println("Learnt hypothesis: ");
        System.out.println(h);
        double accur = Evaluator.evaluate(h, noiseTrain);
        System.out.println("train" + accur);
        accur = Evaluator.evaluate(h, rawTest);
        // Get accuracy of predictor on test set.
        System.out.println("test" + accur);
        Dbg.dbgSwitch = false;
        Dbg.defaultSwitch = false;
    }

    private static void trainTestTest (final int dsetindex) {
        // Read data from files.
        final RawAttrList rawAttr =
                new RawAttrList(DATA_SOURCE[dsetindex].getAttrFileUrl());
        final RawExampleList rawTrain =
                new RawExampleList(DATA_SOURCE[dsetindex].getTrainFileUrl());
        final RawExampleList rawTest =
                new RawExampleList(DATA_SOURCE[dsetindex].getTestFileUrl());

        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();

        final LinkedHashMap<Double, Double> sel0 =
                new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> sel1 =
                new LinkedHashMap<Double, Double>();
        dataSet.put("Train", sel0);
        dataSet.put("Test", sel1);

        final double[][] accuracy = new double[11][2];
        double noiseRate = 0.0;
        for (int i = 0; i < 11; i++) {
            System.out.printf("Noise rate %.2f%n", noiseRate);
            for (int j = 0; j < TIMES; j++) {
                final RawExampleList noiseTrain =
                        DataCorrupter.corrupt(rawTrain, rawAttr, noiseRate);
                final Hypothesis h =
                        RIPPERk.learn(noiseTrain, rawAttr, true, 1);
                double accur = Evaluator.evaluate(h, noiseTrain);
                accuracy[i][0] += accur;
                accur = Evaluator.evaluate(h, rawTest);
                accuracy[i][1] += accur;
            }
            accuracy[i][0] /= TIMES;
            accuracy[i][1] /= TIMES;
            System.out.println("average train " + accuracy[i][0]);
            System.out.println("average test " + accuracy[i][1]);
            sel0.put(noiseRate, accuracy[i][0]);
            sel1.put(noiseRate, accuracy[i][1]);
            noiseRate += 0.02;
            noiseRate = MyMath.doubleRound(noiseRate, 2);
        }

        DisplayChart.display(dataSet, DATA_SOURCE[dsetindex].getName(),
                "Train and test", "Noise rate", "Accuracy");
    }

    private static void pruneTest (final int dsetindex) {
        // Read data from files.
        final RawAttrList rawAttr =
                new RawAttrList(DATA_SOURCE[dsetindex].getAttrFileUrl());
        final RawExampleList rawTrain =
                new RawExampleList(DATA_SOURCE[dsetindex].getTrainFileUrl());
        final RawExampleList rawTest =
                new RawExampleList(DATA_SOURCE[dsetindex].getTestFileUrl());

        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();

        final LinkedHashMap<Double, Double> sel0 =
                new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> sel1 =
                new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> sel2 =
                new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> sel3 =
                new LinkedHashMap<Double, Double>();
        dataSet.put("Train", sel0);
        dataSet.put("Test", sel1);
        dataSet.put("Train Prune", sel2);
        dataSet.put("Test Prune", sel3);

        final double[][] accuracy = new double[11][4];
        double noiseRate = 0.0;
        for (int i = 0; i < 11; i++) {
            System.out.printf("Noise rate %.2f%n", noiseRate);
            for (int j = 0; j < TIMES; j++) {
                final RawExampleList noiseTrain =
                        DataCorrupter.corrupt(rawTrain, rawAttr, noiseRate);
                Hypothesis h = RIPPERk.learn(noiseTrain, rawAttr, false, 1);
                double accur = Evaluator.evaluate(h, noiseTrain);
                accuracy[i][0] += accur;

                accur = Evaluator.evaluate(h, rawTest);
                accuracy[i][1] += accur;

                h = RIPPERk.learn(noiseTrain, rawAttr, true, 1);
                accur = Evaluator.evaluate(h, noiseTrain);
                accuracy[i][2] += accur;

                accur = Evaluator.evaluate(h, rawTest);
                accuracy[i][3] += accur;

            }
            accuracy[i][0] /= TIMES;
            accuracy[i][1] /= TIMES;
            accuracy[i][2] /= TIMES;
            accuracy[i][3] /= TIMES;
            System.out.println("average train " + accuracy[i][0]);
            System.out.println("average test " + accuracy[i][1]);
            System.out.println("average train prune " + accuracy[i][2]);
            System.out.println("average test prune " + accuracy[i][3]);
            sel0.put(noiseRate, accuracy[i][0]);
            sel1.put(noiseRate, accuracy[i][1]);
            sel2.put(noiseRate, accuracy[i][2]);
            sel3.put(noiseRate, accuracy[i][3]);
            noiseRate += 0.02;
            noiseRate = MyMath.doubleRound(noiseRate, 2);
        }

        DisplayChart.display(dataSet, DATA_SOURCE[dsetindex].getName(),
                "With and without prune", "Noise rate", "Accuracy");
    }

    private static void opTest (final int dsetindex) {
        // Read data from files.
        final RawAttrList rawAttr =
                new RawAttrList(DATA_SOURCE[dsetindex].getAttrFileUrl());
        final RawExampleList rawTrain =
                new RawExampleList(DATA_SOURCE[dsetindex].getTrainFileUrl());
        final RawExampleList rawTest =
                new RawExampleList(DATA_SOURCE[dsetindex].getTestFileUrl());

        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();

        final LinkedHashMap<Double, Double> sel0 =
                new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> sel1 =
                new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> sel2 =
                new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> sel3 =
                new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> sel4 =
                new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> sel5 =
                new LinkedHashMap<Double, Double>();
        dataSet.put("Train k=0", sel0);
        dataSet.put("Test k=0", sel1);
        dataSet.put("Train k=1", sel2);
        dataSet.put("Test k=1", sel3);
        dataSet.put("Train k=2", sel4);
        dataSet.put("Test k=2", sel5);

        final double[][] accuracy = new double[11][6];
        double noiseRate = 0.0;
        for (int i = 0; i < 11; i++) {
            System.out.printf("Noise rate %.2f%n", noiseRate);
            for (int j = 0; j < TIMES; j++) {
                final RawExampleList noiseTrain =
                        DataCorrupter.corrupt(rawTrain, rawAttr, noiseRate);
                Hypothesis h = RIPPERk.learn(noiseTrain, rawAttr, true, 0);
                double accur = Evaluator.evaluate(h, noiseTrain);
                accuracy[i][0] += accur;

                accur = Evaluator.evaluate(h, rawTest);
                accuracy[i][1] += accur;

                h = RIPPERk.learn(noiseTrain, rawAttr, true, 1);
                accur = Evaluator.evaluate(h, noiseTrain);
                accuracy[i][2] += accur;

                accur = Evaluator.evaluate(h, rawTest);
                accuracy[i][3] += accur;

                h = RIPPERk.learn(noiseTrain, rawAttr, true, 2);
                accur = Evaluator.evaluate(h, noiseTrain);
                accuracy[i][4] += accur;

                accur = Evaluator.evaluate(h, rawTest);
                accuracy[i][5] += accur;
            }
            accuracy[i][0] /= TIMES;
            accuracy[i][1] /= TIMES;
            accuracy[i][2] /= TIMES;
            accuracy[i][3] /= TIMES;
            accuracy[i][4] /= TIMES;
            accuracy[i][5] /= TIMES;
            System.out.println("average train k = 0 " + accuracy[i][0]);
            System.out.println("average test k = 0 " + accuracy[i][1]);
            System.out.println("average train k = 1 " + accuracy[i][2]);
            System.out.println("average test k = 1 " + accuracy[i][3]);
            System.out.println("average train k = 2 " + accuracy[i][4]);
            System.out.println("average test k = 2 " + accuracy[i][5]);
            sel0.put(noiseRate, accuracy[i][0]);
            sel1.put(noiseRate, accuracy[i][1]);
            sel2.put(noiseRate, accuracy[i][2]);
            sel3.put(noiseRate, accuracy[i][3]);
            sel4.put(noiseRate, accuracy[i][4]);
            sel5.put(noiseRate, accuracy[i][5]);
            noiseRate += 0.02;
            noiseRate = MyMath.doubleRound(noiseRate, 2);
        }

        DisplayChart.display(dataSet, DATA_SOURCE[dsetindex].getName(),
                "Different times of optimization", "Noise rate", "Accuracy");
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
}
