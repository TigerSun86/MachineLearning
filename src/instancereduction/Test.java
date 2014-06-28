package instancereduction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Scanner;

import artificialNeuralNetworks.ANN.AnnLearner;
import artificialNeuralNetworks.ANN.AnnLearner.AcSizeItTime;

import common.DataCorrupter;
import common.MappedAttrList;
import common.RawAttrList;
import common.RawExampleList;
import common.TrainTestSplitter;

import dataset.Bupa;
import dataset.DataSet;
import dataset.Glass;
import dataset.Haberman;
import dataset.Heart;
import dataset.Image;
import dataset.Ionosphere;
import dataset.Iris;
import dataset.Wdbc;
import dataset.Wine;

/**
 * FileName: Test.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Apr 19, 2014 8:57:06 PM
 */
public class Test {
    // Only use 3 stuff from data source: name, attrfile, datafile.
    private static final DataSet[] DATA_SOURCE = { new Iris(), new Wine(),
            new Glass(), new Heart(), new Haberman(), new Bupa(),
            new Ionosphere(), new Wdbc(), new Image() };

    private static final Reducible[] METHODS = { new FDS(), new ENN(),
            new RCI(), new FCNN(), new SPOCNN(), new RPOCNN(), new HMNEI(),
            new DROP3(), new RanR() };
    private static final String[] METHOD_NAMES = { "FDS", "ENN", "RCI", "FCNN",
            "SPOCNN", "RPOCNN", "HMNEI", "DROP3", "Ran" };

    private BitSet dataFlag = new BitSet(DATA_SOURCE.length);
    private BitSet metFlag = new BitSet(METHODS.length);
    private double learnRate;
    private double momentum;
    private int[] numOfHiddenNodes;
    private double[] noiseRateCases;
    private int timesOfGeneratingTrainTest;

    private static final boolean PRINT_TO_FILE = true;

    public Test() {
        dataFlag = new BitSet(DATA_SOURCE.length);
        // dataFlag.set(0, DATA_SOURCE.length); // Enable all data sets.
        dataFlag.set(0);
        metFlag = new BitSet(METHODS.length);
        metFlag.set(0, METHODS.length); // Enable all methods.
        learnRate = 0.1;
        momentum = 0.1;
        numOfHiddenNodes = new int[] { 3, 5, 10 };
        noiseRateCases = new double[] { 0, 0.05, 0.1 };
        timesOfGeneratingTrainTest = 10;
    }

    public static void main (String[] args) throws FileNotFoundException {
        final Test t = new Test();
        final Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println(t.getMainScreenInfo());
            int command = getCommandNumber(s);
            if (command == 1) {
                t.run();
            } else if (command == 2) {
                t.setDataSet(s);
            } else if (command == 3) {
                t.setMethodSet(s);
            } else if (command == 4) {
                t.setOther(s);
            } else {
                break;
            }
        } // End of while (true) {
        s.close();
    }

    private void run () throws FileNotFoundException {
        final PrintStream ps = System.out;
        String dbgFile = "";
        if (PRINT_TO_FILE) {
            dbgFile =
                    "output"
                            + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                                    .format(new java.util.Date()) + ".txt";
            System.out.println("Output redirecting to: " + dbgFile);
            System.setOut(new PrintStream(new FileOutputStream(dbgFile)));
        }

        for (int dataCase = 0; dataCase < DATA_SOURCE.length; dataCase++) {
            if (dataFlag.get(dataCase)) {
                TestOneDataSet(dataCase);
            }
        }

        if (PRINT_TO_FILE) {
            System.out.close();
            System.setOut(ps);
            System.out.println("File writing finished at: " + dbgFile);
        }
    }

    private void TestOneDataSet (final int dataCase) {
        // 1st dimension: noise rate.
        // 2nd dimension: methods.
        // 3rd dimension: Size/Accur/Iter/InstanceEditingTime/TrainingTime
        final double[][][] sta =
                new double[noiseRateCases.length][METHODS.length][5];
        for (int i = 0; i < noiseRateCases.length; i++) {
            sta[i] = new double[METHODS.length][5];
            for (int j = 0; j < sta[i].length; j++) {
                sta[i][j] = new double[5];
                for (int k = 0; k < sta[i][j].length; k++) {
                    sta[i][j][k] = 0;
                }
            }
        }

        final String dataSetName = DATA_SOURCE[dataCase].getName();
        final String attrFile = DATA_SOURCE[dataCase].getAttrFileUrl();
        final String dataFile = DATA_SOURCE[dataCase].getDataFileUrl();
        System.out.println("Data set: " + dataSetName);

        final RawAttrList rawAttr = new RawAttrList(attrFile);
        final RawExampleList originalExs = new RawExampleList(dataFile);
        // Map all attributes in range 0 to 1.
        final MappedAttrList mAttr = new MappedAttrList(originalExs, rawAttr);
        // Rescale (map) all data in range 0 to 1.
        final RawExampleList exs = mAttr.mapExs(originalExs, rawAttr);

        final AnnLearner annLearner =
                new AnnLearner(rawAttr, learnRate, momentum);
        for (int times = 1; times <= timesOfGeneratingTrainTest; times++) {
            System.out.println("Times: " + times);

            Collections.shuffle(exs); // Shuffle examples.
            final RawExampleList[] exs2 =
                    TrainTestSplitter.splitSetWithConsistentClassRatio(exs,
                            rawAttr, TrainTestSplitter.DEFAULT_RATIO);
            final RawExampleList train = exs2[0];
            final RawExampleList test = exs2[1];
            annLearner.setRawTest(test);

            for (int noiseI = 0; noiseI < noiseRateCases.length; noiseI++) {
                final double noiseRate = noiseRateCases[noiseI];
                System.out.printf("Noise: %.2f%n", noiseRate);

                final RawExampleList rawTrainWithNoise =
                        DataCorrupter.corrupt(train, rawAttr, noiseRate);
                // Set data set for ANN learning.
                annLearner.setRawTrainWithNoise(rawTrainWithNoise);

                // Use different method to reduce.
                for (int metIndex = 0; metIndex < METHODS.length; metIndex++) {
                    if (metFlag.get(metIndex)) {
                        System.out.printf("%s: ", METHOD_NAMES[metIndex]);
                        final Reducible method = METHODS[metIndex];
                        // Use different hidden nodes to train.
                        for (int nH : numOfHiddenNodes) {
                            annLearner.setNumOfHiddenNodes(nH);
                            // Train by neural network.
                            final AcSizeItTime result =
                                    annLearner
                                            .reductionLearningWith3Fold(method);

                            sta[noiseI][metIndex][0] += result.accur;
                            sta[noiseI][metIndex][1] += result.size;
                            sta[noiseI][metIndex][2] += result.iter;
                            sta[noiseI][metIndex][3] += result.editTime;
                            sta[noiseI][metIndex][4] += result.trainTime;
                            System.out
                                    .printf(" nH %d accur %.4f size %d iter %d editTime %d trainTime %d",
                                            nH, result.accur, result.size,
                                            result.iter, result.editTime,
                                            result.trainTime);
                        }
                        System.out.println();
                    }
                }
            }
        }

        System.out.printf("%s statistic information%n", dataSetName);
        System.out
                .printf("EditWay NoiseRate Accuracy NumOfInstances NumofIterations "
                        + "InstanceEditingTime TrainingTime (in nano second)%n");
        for (int i = 0; i < noiseRateCases.length; i++) {
            for (int j = 0; j < METHODS.length; j++) {
                if (metFlag.get(j)) {
                    for (int k = 0; k < 5; k++) {
                        // all result statistic data repeated
                        // timesOfGeneratingTrainTest * numOfHiddenNodes.length
                        // times.
                        sta[i][j][k] /=
                                (timesOfGeneratingTrainTest * numOfHiddenNodes.length);
                    }

                    // EditWay NoiseRate Accuracy NumOfInstances NumofIterations
                    // InstanceEditingTime TrainingTime
                    System.out.printf("%s, ", METHOD_NAMES[j]);
                    System.out.printf("%.2f, ", noiseRateCases[i]);
                    System.out.printf("%.4f, %4d, %5d, %d, %d%n", sta[i][j][0],
                            Math.round(sta[i][j][1]), Math.round(sta[i][j][2]),
                            Math.round(sta[i][j][3]), Math.round(sta[i][j][4]));
                }
            }
        }
    }

    private String getMainScreenInfo () {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("Please input the number of operation:%n"
                + "1, Run.%n"));

        sb.append("2, Set data source. (");
        sb.append(getCurDataSet());
        sb.append(String.format(")%n"));

        sb.append(String.format("3, Set method. ("));
        sb.append(getCurMethods());
        sb.append(String.format(")%n"));

        sb.append(String.format("4, Set other parameters. (Current: "));
        sb.append(getCurOther());
        sb.append(String.format(")%nOther, Quit.%n"));
        return sb.toString();
    }

    private String getCurDataSet () {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("Current: "));
        for (int i = 0; i < DATA_SOURCE.length; i++) {
            if (dataFlag.get(i)) {
                sb.append(String.format("%d, %s. ", i + 1,
                        DATA_SOURCE[i].getName()));
            }
        }
        return sb.toString();
    }

    private String getSetDataInfo () {
        final StringBuilder sb = new StringBuilder();
        sb.append("Data Set: ");
        for (int i = 0; i < DATA_SOURCE.length; i++) {
            sb.append(String.format("%d, %s ", i + 1, DATA_SOURCE[i].getName()));
        }
        sb.append(String.format("%n"));

        sb.append(getCurDataSet());

        sb.append(String.format("%n"));

        sb.append(String
                .format("Please input the index that you want to enable or disable (0 to quit): %n"));
        return sb.toString();
    }

    private void setDataSet (final Scanner s) {
        while (true) {
            System.out.println(getSetDataInfo());
            final int i = getInt(s);
            if (i == 0 || i > DATA_SOURCE.length) {
                break;
            }
            dataFlag.flip(i - 1);
        }
    }

    private String getCurMethods () {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("Current: "));
        for (int i = 0; i < METHODS.length; i++) {
            if (metFlag.get(i)) {
                sb.append(String.format("%d, %s. ", i + 1, METHOD_NAMES[i]));
            }
        }
        return sb.toString();
    }

    private String getSetMethodInfo () {
        final StringBuilder sb = new StringBuilder();
        sb.append("Method Set: ");
        for (int i = 0; i < METHODS.length; i++) {
            sb.append(String.format("%d, %s ", i + 1, METHOD_NAMES[i]));
        }
        sb.append(String.format("%n"));

        sb.append(getCurMethods());

        sb.append(String.format("%n"));

        sb.append(String
                .format("Please input the index that you want to enable or disable (0 to quit): %n"));
        return sb.toString();
    }

    private void setMethodSet (final Scanner s) {
        while (true) {
            System.out.println(getSetMethodInfo());
            final int i = getInt(s);
            if (i == 0 || i > METHODS.length) {
                break;
            }
            metFlag.flip(i - 1);
        }
    }

    private String getCurOther () {
        final StringBuilder sb = new StringBuilder();
        sb.append(String
                .format("Current: 1, learning rate: %.1f. 2, momentum: %.1f. 3, numOfHiddenNodes: %s. 4, noiseRateCases: %s. 5, repeating times: %d.",
                        learnRate, momentum, Arrays.toString(numOfHiddenNodes),
                        Arrays.toString(noiseRateCases),
                        timesOfGeneratingTrainTest));
        return sb.toString();
    }

    private String getSetOtherInfo () {
        final StringBuilder sb = new StringBuilder();
        sb.append(getCurOther());

        sb.append(String
                .format("%nPlease input the index that you want to set (0 to quit): %n"));
        return sb.toString();
    }

    private void setOther (final Scanner s) {
        while (true) {
            System.out.println(getSetOtherInfo());
            final int i = getInt(s);
            if (i == 0 || i > 5) {
                break;
            }
            switch (i) {
                case 1:
                    System.out.println("Please input the learning rate:");
                    learnRate = getDouble(s);
                    break;
                case 2:
                    System.out.println("Please input the momentum rate:");
                    momentum = getDouble(s);
                    break;
                case 3:
                    // Numbers of nodes.
                    final ArrayList<Integer> nH = new ArrayList<Integer>();
                    while (true) {
                        System.out
                                .println("Please input the numbers of hidden nodes to test (0 to quit):");
                        final int n = getInt(s);
                        if (n == 0) {
                            break;
                        }
                        nH.add(n);
                    }
                    if (!nH.isEmpty()) {
                        numOfHiddenNodes = new int[nH.size()];
                        for (int j = 0; j < numOfHiddenNodes.length; j++) {
                            numOfHiddenNodes[j] = nH.get(j);
                        }
                    }
                    break;
                case 4:
                    // Noise rates
                    final ArrayList<Double> noise = new ArrayList<Double>();
                    while (true) {
                        System.out
                                .println("Please input the noise ratios to test (-1 to quit):");
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
                        noiseRateCases = new double[noise.size()];
                        for (int j = 0; j < noiseRateCases.length; j++) {
                            noiseRateCases[j] = noise.get(j);
                        }
                    }
                    break;
                case 5:
                    System.out
                            .println("Please input the times regenerating training and test sets:");
                    timesOfGeneratingTrainTest = getInt(s);
                    break;
            }
        }
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
