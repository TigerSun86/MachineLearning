package instancereduction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import util.SysUtil;
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
                            "Ionosphere",
                            "http://my.fit.edu/~sunx2013/MachineLearning/ionosphere-attr.txt",
                            "http://my.fit.edu/~sunx2013/MachineLearning/ionosphere.txt" },
                    {
                            "Liver Disorders",
                            "http://my.fit.edu/~sunx2013/MachineLearning/bupa-attr.txt",
                            "http://my.fit.edu/~sunx2013/MachineLearning/bupa.txt" },
                    {
                            "Image Segmentation",
                            "http://my.fit.edu/~sunx2013/MachineLearning/segmentation-attr.txt",
                            "http://my.fit.edu/~sunx2013/MachineLearning/segmentation.txt" },
                    {
                            "Car Evaluation",
                            "http://my.fit.edu/~sunx2013/MachineLearning/car-attr.txt",
                            "http://my.fit.edu/~sunx2013/MachineLearning/car.txt" } };

    private double learnRate = 0.1;
    private double momentum = 0.1;
    private int[] numOfHiddenNodes = { 3, 5, 10 };
    private double[] noiseRateCases = { 0, 0.1, 0.2 };
    private int timesOfGeneratingTrainTest = 3;
    private String[][] dataSets = DATA_SOURCE;

    private static final String TEST_INFO =
            "Please choose the data you want to test:\n" + "\t0 Iris.\n"
                    + "\t1 Wine.\n"
                    + "\t2 Breast Cancer Wisconsin (Diagnostic).\n"
                    + "\t3 Ionosphere.\n" + "\t4 Liver disorders.\n"
                    + "\t5 Image Segmentation.\n" + "\t6 Car Evaluation.\n"
                    + "\t7 Comprehensive test.\n" + "\tOther_number quit\n";
    private static final boolean PRINT_TO_FILE = true;
    private static final String DBG_FILE = "output.txt";

    public static void main (String[] args) throws FileNotFoundException {
        final PrintStream ps = System.out;

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
            if (PRINT_TO_FILE) {
                System.out.println("Output redirecting to: "+DBG_FILE);
                System.setOut(new PrintStream(new FileOutputStream(DBG_FILE)));
            }
            for (int dataCase = 0; dataCase < t.dataSets.length; dataCase++) {
                TestOneDataSet(t, dataCase);
            }
            if (PRINT_TO_FILE) {
                System.out.close();
                System.setOut(ps);
                System.out.println("File writing finished at: "+DBG_FILE);
            }
        } // End of while (true) {
        s.close();

    }

    private static void TestOneDataSet (Test t, int dataCase) {
        // Noise, FDS/ENN/RCI, Size/Accur/Iter/InstanceEditingTime/TrainingTime
        final double[][][] sta = new double[t.noiseRateCases.length][3][5];
        for (int i = 0; i < t.noiseRateCases.length; i++) {
            sta[i] = new double[3][5];
            for (int j = 0; j < sta[i].length; j++) {
                sta[i][j] = new double[5];
                for (int k = 0; k < sta[i][j].length; k++) {
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
                // Full data set has no editing time.
                sta[noiseI][0][3] += 0;
                System.out.printf(" EditTime %d", 0);
                for (int nH : t.numOfHiddenNodes) {
                    annLearner.setNumOfHiddenNodes(nH);
                    long trainTime = SysUtil.getCpuTime();
                    final AccurAndIter aai = annLearner.kFoldLearning2(3);
                    trainTime = SysUtil.getCpuTime() - trainTime;
                    final double accur = aai.accur;
                    final int iter = aai.iter;
                    sta[noiseI][0][1] += accur;
                    sta[noiseI][0][2] += iter;
                    sta[noiseI][0][4] += trainTime;
                    System.out.printf(" nH %d accur %.4f iter %d trainTime %d",
                            nH, accur, iter, trainTime);
                }
                System.out.println();
                // ENN.
                long ennEditTime = SysUtil.getCpuTime();
                rawTrainWithNoise = ENN.reduce(rawTrainWithNoise, rawAttr);
                ennEditTime = SysUtil.getCpuTime() - ennEditTime;
                annLearner.setRawTrainWithNoise(rawTrainWithNoise);
                sta[noiseI][1][0] += rawTrainWithNoise.size();
                System.out.printf("ENN: size %d", rawTrainWithNoise.size());
                sta[noiseI][1][3] += ennEditTime;
                System.out.printf(" EditTime %d", ennEditTime);
                for (int nH : t.numOfHiddenNodes) {
                    annLearner.setNumOfHiddenNodes(nH);
                    long trainTime = SysUtil.getCpuTime();
                    final AccurAndIter aai = annLearner.kFoldLearning2(3);
                    trainTime = SysUtil.getCpuTime() - trainTime;
                    final double accur = aai.accur;
                    final int iter = aai.iter;
                    sta[noiseI][1][1] += accur;
                    sta[noiseI][1][2] += iter;
                    sta[noiseI][1][4] += trainTime;
                    System.out.printf(" nH %d accur %.4f iter %d trainTime %d",
                            nH, accur, iter, trainTime);
                }
                System.out.println();
                // RCI.
                long rciEditTime = SysUtil.getCpuTime();
                rawTrainWithNoise = RCI.reduce(rawTrainWithNoise, rawAttr);
                rciEditTime = SysUtil.getCpuTime() - rciEditTime;
                // RCI editing time is ENN time + RCI time, because RCI have to
                // process data set based on the result of ENN.
                rciEditTime += ennEditTime;
                annLearner.setRawTrainWithNoise(rawTrainWithNoise);
                sta[noiseI][2][0] += rawTrainWithNoise.size();
                System.out.printf("RCI: size %d", rawTrainWithNoise.size());
                sta[noiseI][2][3] += rciEditTime;
                System.out.printf(" EditTime %d", rciEditTime);
                for (int nH : t.numOfHiddenNodes) {
                    annLearner.setNumOfHiddenNodes(nH);
                    long trainTime = SysUtil.getCpuTime();
                    final AccurAndIter aai = annLearner.kFoldLearning2(3);
                    trainTime = SysUtil.getCpuTime() - trainTime;
                    final double accur = aai.accur;
                    final int iter = aai.iter;
                    sta[noiseI][2][1] += accur;
                    sta[noiseI][2][2] += iter;
                    sta[noiseI][2][4] += trainTime;
                    System.out.printf(" nH %d accur %.4f iter %d trainTime %d",
                            nH, accur, iter, trainTime);
                }
                System.out.println();
            }
        }

        System.out.printf("%s statistic infomation%n", dataSetName);
        System.out
                .printf("EditWay NoiseRate Accuracy NumOfInstances NumofIterations "
                        + "InstanceEditingTime TrainingTime (in nano second)%n");
        for (int i = 0; i < t.noiseRateCases.length; i++) {

            for (int j = 0; j < 3; j++) {

                // NumOfInstances repeated timesOfGeneratingTrainTest times.
                sta[i][j][0] /= t.timesOfGeneratingTrainTest;
                // Accuracy repeated
                // timesOfGeneratingTrainTest * numOfHiddenNodes.length times.
                sta[i][j][1] /=
                        (t.timesOfGeneratingTrainTest * t.numOfHiddenNodes.length);
                // Number of iterations repeated
                // timesOfGeneratingTrainTest * numOfHiddenNodes.length times.
                sta[i][j][2] /=
                        (t.timesOfGeneratingTrainTest * t.numOfHiddenNodes.length);
                // InstanceEditingTime repeated timesOfGeneratingTrainTest
                // times.
                sta[i][j][3] /= t.timesOfGeneratingTrainTest;
                // Training time repeated
                // timesOfGeneratingTrainTest * numOfHiddenNodes.length times.
                sta[i][j][4] /=
                        (t.timesOfGeneratingTrainTest * t.numOfHiddenNodes.length);

                // EditWay NoiseRate Accuracy NumOfInstances NumofIterations
                // InstanceEditingTime TrainingTime
                if (j == 0) {
                    System.out.print("FDS ");
                } else if (j == 1) {
                    System.out.print("ENN ");
                } else {
                    System.out.print("RCI ");
                }
                System.out.printf(" %.2f", t.noiseRateCases[i]);
                // Accuracy NumOfInstances NumofIterations InstanceEditingTime
                // TrainingTime
                System.out.printf(" %.4f %4d %5d %20d %20d%n", sta[i][j][1],
                        Math.round(sta[i][j][0]), Math.round(sta[i][j][2]),
                        Math.round(sta[i][j][3]), Math.round(sta[i][j][4]));
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
