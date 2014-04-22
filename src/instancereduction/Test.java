package instancereduction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import artificialNeuralNetworks.ANN.AnnLearner;
import artificialNeuralNetworks.ANN.AnnLearner.AccurAndIter;

import common.DataCorrupter;
import common.RawAttrList;
import common.RawExampleList;
import common.TrainTestSplitter;

/**
 * FileName: Test.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Apr 19, 2014 8:57:06 PM
 */
public class Test {
    private static final String[][] DATA_SOURCE =
            {
                    {
                            "Iris",
                            "http://my.fit.edu/~sunx2013/MachineLearning/iris-attr.txt",
                            "http://my.fit.edu/~sunx2013/MachineLearning/iris.txt" },
                    {
                            "Wine",
                            "http://my.fit.edu/~sunx2013/MachineLearning/wine-attr.txt",
                            "http://my.fit.edu/~sunx2013/MachineLearning/wine.txt" },
                    {
                            "Breast Cancer Wisconsin (Diagnostic)",
                            "http://my.fit.edu/~sunx2013/MachineLearning/wdbc-attr.txt",
                            "http://my.fit.edu/~sunx2013/MachineLearning/wdbc.txt" },
                    {
                            "Image Segmentation",
                            "http://my.fit.edu/~sunx2013/MachineLearning/segmentation-attr.txt",
                            "http://my.fit.edu/~sunx2013/MachineLearning/segmentation.txt" } };

    private double learnRate = 0.1;
    private double momentum = 0.1;
    private int[] numOfHiddenNodes = { 3, 5, 10 };
    private double[] noiseRateCases = { 0, 0.1, 0.2 };
    private int timesOfGeneratingTrainTest = 3;
    private String[][] dataSets = DATA_SOURCE;

    /* private static final String ATTR_FILE_URL =
     * "http://my.fit.edu/~sunx2013/MachineLearning/rcitest-attr.txt";
     * private static final String TRAIN_FILE_URL =
     * "http://my.fit.edu/~sunx2013/MachineLearning/rcitest-train.txt";
     * private static final String TEST_FILE_URL =
     * "http://cs.fit.edu/~pkc/classes/ml/data/iris-test.txt"; */
    private static final String TEST_INFO =
            "Please choose the data you want to test:\n" + "\t0 Iris.\n"
                    + "\t1 Wine.\n"
                    + "\t2 Breast Cancer Wisconsin (Diagnostic).\n"
                    + "\t3 Image Segmentation.\n" + "\t4 Comprehensive test.\n"
                    + "\tOther_number quit\n";

    public static void main (String[] args) {
        final Scanner s = new Scanner(System.in);
        while (true) {
            final Test t;
            System.out.println(TEST_INFO);
            int command = getCommandNumber(s);
            if (command < DATA_SOURCE.length && command >= 0) {
                t = new Test();
                t.dataSets = new String[][] { DATA_SOURCE[command] };
            } else if (command == DATA_SOURCE.length) {
                t = new Test(); // Comprehensive test.
            } else {
                t = null;
            }
            if (t == null) {
                break;
            }
            setting(t, s);

            for (int dataCase = 0; dataCase < t.dataSets.length; dataCase++) {
                TestOneDataSet(t, dataCase);
            }
        } // End of while (true) {
        s.close();
    }

    private static void TestOneDataSet (Test t, int dataCase) {
        // Noise, FDS/ENN/RCI, Size/Accur/Iter
        final double[][][] sta = new double[t.noiseRateCases.length][3][3];
        for (int i = 0; i < t.noiseRateCases.length; i++) {
            sta[i] = new double[3][3];
            for (int j = 0; j < 3; j++) {
                sta[i][j] = new double[3];
                for (int k = 0; k < 3; k++) {
                    sta[i][j][k] = 0;
                }
            }
        }

        final String dataSetName = t.dataSets[dataCase][0];
        final String attrFile = t.dataSets[dataCase][1];
        final String dataFile = t.dataSets[dataCase][2];
        System.out.println("Data set: " + dataSetName);

        final RawAttrList rawAttr = new RawAttrList(attrFile);
        final RawExampleList exs = new RawExampleList(dataFile);
        final AnnLearner annLearner =
                new AnnLearner(rawAttr, t.learnRate, t.momentum);
        for (int times = 1; times <= t.timesOfGeneratingTrainTest; times++) {
            System.out.println("Times: " + times);

            Collections.shuffle(exs); // Shuffle examples.
            final RawExampleList[] exs2 =
                    TrainTestSplitter.split(exs,
                            TrainTestSplitter.DEFAULT_RATIO);
            final RawExampleList train = exs2[0];
            final RawExampleList test = exs2[1];
            annLearner.setRawTest(test);

            for (int noiseI = 0; noiseI < t.noiseRateCases.length; noiseI++) {
                final double noiseRate = t.noiseRateCases[noiseI];
                System.out.printf("Noise: %.2f%n", noiseRate);
                // Full data set.
                RawExampleList rawTrainWithNoise =
                        DataCorrupter.corrupt(train, rawAttr, noiseRate);
                annLearner.setRawTrainWithNoise(rawTrainWithNoise);
                sta[noiseI][0][0] += rawTrainWithNoise.size();
                System.out.printf("FDS: size %d", rawTrainWithNoise.size());
                for (int nH : t.numOfHiddenNodes) {
                    annLearner.setNumOfHiddenNodes(nH);
                    final AccurAndIter aai = annLearner.kFoldLearning2(3);
                    final double accur = aai.accur;
                    final int iter = aai.iter;
                    sta[noiseI][0][1] += accur;
                    sta[noiseI][0][2] += iter;
                    System.out.printf(" nH %d accur %.4f iter %d", nH, accur,
                            iter);
                }
                System.out.println();
                // ENN.
                rawTrainWithNoise = ENN.reduce(rawTrainWithNoise, rawAttr);
                annLearner.setRawTrainWithNoise(rawTrainWithNoise);
                sta[noiseI][1][0] += rawTrainWithNoise.size();
                System.out.printf("ENN: size %d", rawTrainWithNoise.size());
                for (int nH : t.numOfHiddenNodes) {
                    annLearner.setNumOfHiddenNodes(nH);
                    final AccurAndIter aai = annLearner.kFoldLearning2(3);
                    final double accur = aai.accur;
                    final int iter = aai.iter;
                    sta[noiseI][1][1] += accur;
                    sta[noiseI][1][2] += iter;
                    System.out.printf(" nH %d accur %.4f iter %d", nH, accur,
                            iter);
                }
                System.out.println();
                // RCI.
                rawTrainWithNoise = RCI.reduce(rawTrainWithNoise, rawAttr);
                annLearner.setRawTrainWithNoise(rawTrainWithNoise);
                sta[noiseI][2][0] += rawTrainWithNoise.size();
                System.out.printf("RCI: size %d", rawTrainWithNoise.size());
                for (int nH : t.numOfHiddenNodes) {
                    annLearner.setNumOfHiddenNodes(nH);
                    final AccurAndIter aai = annLearner.kFoldLearning2(3);
                    final double accur = aai.accur;
                    final int iter = aai.iter;
                    sta[noiseI][2][1] += accur;
                    sta[noiseI][2][2] += iter;
                    System.out.printf(" nH %d accur %.4f iter %d", nH, accur,
                            iter);
                }
                System.out.println();
            }
        }

        System.out.printf("%s statistic infomation%n", dataSetName);
        for (int i = 0; i < t.noiseRateCases.length; i++) {
            System.out.printf("Noise: %.2f%n", t.noiseRateCases[i]);
            for (int j = 0; j < 3; j++) {
                // Size repeated timesOfGeneratingTrainTest times.
                sta[i][j][0] /= t.timesOfGeneratingTrainTest;
                // Accuracy repeated
                // timesOfGeneratingTrainTest * numOfHiddenNodes.length times.
                sta[i][j][1] /=
                        (t.timesOfGeneratingTrainTest * t.numOfHiddenNodes.length);
                // Number of iterations repeated
                // timesOfGeneratingTrainTest * numOfHiddenNodes.length times.
                sta[i][j][2] /=
                        (t.timesOfGeneratingTrainTest * t.numOfHiddenNodes.length);

                if (j == 0) {
                    System.out.print("FDS: ");
                } else if (j == 1) {
                    System.out.print("ENN: ");
                } else {
                    System.out.print("RCI: ");
                }
                System.out.printf("size %d accur %.4f iter %d%n",
                        Math.round(sta[i][j][0]), sta[i][j][1],
                        Math.round(sta[i][j][2]));
            }
        }
    }

    private static final String SETTING1 =
            "Do you want to change the default setting? (y or n)";
    private static final String SETTING2 =
            "Please input the times regenerating training and test sets:";
    private static final String SETTING3 =
            "Please input the numbers of hidden nodes to test (c to continue):";
    private static final String SETTING4 =
            "Please input the noise ratios to test (c to continue):";
    private static final String SETTING5 =
            "Please input the default learning rate:";
    private static final String SETTING6 =
            "Please input the default momentum rate:";

    private static void setting (final Test t, final Scanner s) {
        System.out.println(SETTING1);
        String com = s.nextLine();
        if (!com.equalsIgnoreCase("y")) {
            return;
        }
        // Times regenerating training and test sets.
        System.out.println(SETTING2 + " (default "
                + t.timesOfGeneratingTrainTest + ")");
        t.timesOfGeneratingTrainTest = getInt(s);
        // Numbers of nodes.
        final ArrayList<Integer> nH = new ArrayList<Integer>();
        while (true) {
            System.out.println(SETTING3 + " (default "
                    + Arrays.toString(t.numOfHiddenNodes) + ")");
            int n = 0;
            try {
                n = Integer.parseInt(s.nextLine());
            } catch (NumberFormatException e) {
                n = 0;
            }
            if (n <= 0) {
                break;
            }
            nH.add(n);
        }
        if (!nH.isEmpty()) {
            t.numOfHiddenNodes = new int[nH.size()];
            for (int i = 0; i < t.numOfHiddenNodes.length; i++) {
                t.numOfHiddenNodes[i] = nH.get(i);
            }
        }
        // Noise rates
        final ArrayList<Double> noise = new ArrayList<Double>();
        while (true) {
            System.out.println(SETTING4 + " (default "
                    + Arrays.toString(t.noiseRateCases) + ")");
            double r = Double.NaN;
            try {
                r = Double.parseDouble(s.nextLine());
            } catch (NumberFormatException e) {
                r = Double.NaN;
            }
            if (Double.isNaN(r) || Double.compare(r, 1) > 0
                    || Double.compare(r, 0) < 0) {
                break;
            }
            noise.add(r);
        }
        if (!noise.isEmpty()) {
            t.noiseRateCases = new double[noise.size()];
            for (int i = 0; i < t.noiseRateCases.length; i++) {
                t.noiseRateCases[i] = noise.get(i);
            }
        }
        // Learning rate.
        System.out.println(SETTING5 + " (default " + t.learnRate + ")");
        t.learnRate = getDouble(s);
        // Momentum rate.
        System.out.println(SETTING6 + " (default " + t.momentum + ")");
        t.momentum = getDouble(s);
    }

    private static int getCommandNumber (final Scanner s) {
        final String next = s.nextLine();
        int ret = -1;
        try {
            ret = Integer.parseInt(next);
        } catch (NumberFormatException e) {
            ret = -1;
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
