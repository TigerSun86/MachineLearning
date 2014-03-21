package artificialNeuralNetworks.Demo;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Scanner;

import util.DisplayChart;
import util.MyMath;
import artificialNeuralNetworks.ANN.NeuralNetwork;
import common.Evaluator;
import debug.Dbg;

/**
 * FileName: TestAll.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 19, 2014 2:00:42 AM
 */
public class TestAll {
    private static final String TEST_INFO =
            "Please choose the data you want to test:\n" + "\t0 Identity.\n"
                    + "\t1 Tennis.\n" + "\t2 Iris.\n" + "\tOther_number quit\n";

    private static final String DISPLAY_INFO =
            "Please input the following command number to test:\n"
                    + "\t0 set number of hidden units, learning rate or momentum. (The setting can be used following test modes.)\n"
                    + "\t1 specified number of iteration.\n"
                    + "\t2 validation set.\n"
                    + "\t3 K-Fold cross validation.\n"
                    + "\t4 Show graph of error decreasing in train and validation set.\n"
                    + "\t5 Show graph of error decreasing in each output unit.\n"
                    + "\t6 Show graph of the encoding of each hidden unit for one example.\n"
                    + "\t7 Show graph of weights from inputs to one hidden unit.\n"
                    + "\t8 different number of hidden nodes (each with 1000 iterations). (Slow)\n"
                    + "\t9 different ratio of learning rate (each with 1000 iterations). (Slow)\n"
                    + "\t10 different ratio of momentum (each with 1000 iterations). (Slow)\n"
                    + "\t11 Compare normal training, validation training and K-Fold in different noise ratio (From 0 to 0.3). (Very slow)\n"
                    + "\tOther_number quit\n";

    private static final String SETTING_INFO =
            "Please input the following command number to set:\n"
                    + "\t0 number of hidden units.\n" + "\t1 learning rate.\n"
                    + "\t2 momentum.\n" + "\tOther_number back\n";

    @SuppressWarnings("resource")
    public static void main (final String[] args) {
        final Scanner s = new Scanner(System.in);
        System.out.println(TEST_INFO);
        AnnTest t = null;
        int command = s.nextInt();
        switch (command) {
            case 0:
                t = new TestIdentity();
                break;
            case 1:
                t = new TestTennis();
                break;
            case 2:
                t = new TestIris();
                break;
            default:
                break;
        }
        if (t == null) {
            return;
        }
        System.out.println(DISPLAY_INFO);

        boolean quit = false;
        while (!quit) {
            command = s.nextInt();
            int n;
            double r;
            switch (command) {
                case 0:
                    setting(t, s);
                    break;
                case 1:
                    System.out.println("Please input number of iterations:");
                    n = s.nextInt();
                    testIter(t, n);
                    break;
                case 2:
                    System.out
                            .println("Please input the train set ratio (0 to 0.99):");
                    r = s.nextDouble();
                    testCV(t, r);
                    break;
                case 3:
                    System.out.println("Please input the number of folds:");
                    n = s.nextInt();
                    testKF(t, n);
                    break;
                case 4:
                    System.out.println("Please input number of iterations:");
                    n = s.nextInt();
                    testErrorVsIteration(t, n);
                    break;
                case 5:
                    System.out.println("Please input number of iterations:");
                    n = s.nextInt();
                    testErrOfOut(t, n);
                    break;
                case 6:
                    System.out.println("Please input number of iterations:");
                    n = s.nextInt();
                    testOutOfHidden(t, n);
                    break;
                case 7:
                    System.out.println("Please input number of iterations:");
                    n = s.nextInt();
                    testweightsForOneHidden(t, n);
                    break;
                case 8:
                    testNHidden(t);
                    break;
                case 9:
                    testLearnRate(t);
                    break;
                case 10:
                    testMo2(t);
                    break;
                case 11:
                    System.out
                            .println("Please input the step size (recommand 0.02):");
                    r = s.nextDouble();
                    testCorrupt(t, r);
                    break;
                default:
                    quit = true;
            }

            System.out.println(DISPLAY_INFO);
        }
        s.close();
    }

    private static void setting (AnnTest t, final Scanner s) {
        boolean quit = false;
        System.out.println(SETTING_INFO);
        while (!quit) {
            final int command = s.nextInt();
            int n;
            double r;
            switch (command) {
                case 0:
                    System.out
                            .println("Please input number of hidden units (default "+Arrays.toString(t.DEF_NHIDDEN)+"):");
                    n = s.nextInt();
                    t.learner.nHidden = new int[] { n };
                    break;
                case 1:
                    System.out
                            .println("Please input learning rate (default "+t.DEF_LEARN_RATE+"):");
                    r = s.nextDouble();
                    t.learner.learnRate = r;
                    break;
                case 2:
                    System.out
                            .println("Please input momentum rate (default "+t.DEF_MOMENTUM+"):");
                    r = s.nextDouble();
                    t.learner.momentumRate = r;
                    break;
                default:
                    quit = true;
            }

            System.out.println(SETTING_INFO);
        }
    }

    public static double testIter (final AnnTest t, int maxIter) {
        Dbg.dbgSwitch = true;
        Dbg.defaultSwitch = true;
        Evaluator.DBG = true;

        final NeuralNetwork net = t.learner.iterLearning(maxIter);
        double accur = t.learner.evalTrain(net);
        System.out.println("Train accuracy: " + accur);
        accur = t.learner.evalTest(net);
        System.out.println("Test accuracy: " + accur);

        Dbg.dbgSwitch = false;
        Dbg.defaultSwitch = false;
        Evaluator.DBG = false;
        return accur;

    }

    public static double testCV (final AnnTest t, double r) {
        Dbg.dbgSwitch = true;
        Dbg.defaultSwitch = true;
        Evaluator.DBG = true;

        final NeuralNetwork net = t.learner.validationLearning(r);
        double accur = t.learner.evalTrain(net);
        System.out.println("Train accuracy: " + accur);
        accur = t.learner.evalTest(net);
        System.out.println("Test accuracy: " + accur);

        Dbg.dbgSwitch = false;
        Dbg.defaultSwitch = false;
        Evaluator.DBG = false;
        return accur;
    }

    public static double testKF (final AnnTest t, int k) {
        Dbg.dbgSwitch = true;
        Dbg.defaultSwitch = true;
        Evaluator.DBG = true;
        final NeuralNetwork net = t.learner.kFoldLearning(k);
        double accur = t.learner.evalTrain(net);
        System.out.println("Train accuracy: " + accur);
        accur = t.learner.evalTest(net);
        System.out.println("Test accuracy: " + accur);
        Dbg.dbgSwitch = false;
        Dbg.defaultSwitch = false;
        Evaluator.DBG = false;
        return accur;
    }

    private static void testErrorVsIteration (final AnnTest t, int maxIter) {
        Dbg.dbgSwitch = true;
        Dbg.defaultSwitch = true;
        LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                t.learner.errorVersusWeightsLearning(maxIter);
        DisplayChart.display(dataSet, "Artificial Neural Network Learning "
                + t.name, "Error versus iterations", "Number of iteration",
                "Error");
        Dbg.dbgSwitch = false;
        Dbg.defaultSwitch = false;
    }

    private static void testErrOfOut (final AnnTest t, int maxIter) {
        LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                t.learner.errForEachOutput(maxIter);
        DisplayChart.display(dataSet, "Artificial Neural Network Learning "
                + t.name,
                "Average squared error to train example for each output unit",
                "Number of iteration", "Error");
    }

    private static void testOutOfHidden (final AnnTest t, int maxIter) {
        LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                t.learner.outputForHidden(maxIter);
        DisplayChart.display(dataSet, "Artificial Neural Network Learning "
                + t.name, "Hidden unit encoding for the 2nd training example",
                "Number of iteration", "Value");
    }

    private static void testweightsForOneHidden (final AnnTest t, int maxIter) {
        LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                t.learner.weightsForOneHidden(maxIter);
        DisplayChart.display(dataSet, "Artificial Neural Network Learning "
                + t.name, "Weights from inputs to one hidden unit",
                "Number of iteration", "Value");
    }

    private static final double STEP = 0.1;
    private static final double TEMINAL = 1.0;

    private static void testNHidden (final AnnTest t) {
        int[] h = t.learner.nHidden; // Backup

        final LinkedHashMap<Double, Double> numMap =
                new LinkedHashMap<Double, Double>();

        for (int nH = 1; nH <= 10; nH++) {
            t.learner.nHidden = new int[] { nH };
            final NeuralNetwork net = t.learner.iterLearning(1000);
            final double accur = t.learner.evalTest(net);
            System.out
                    .println("Number of nodes: " + nH + " accuracy: " + accur);
            numMap.put((double) nH, accur);
        }

        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put("Number of hidden nodes", numMap);

        DisplayChart.display(dataSet, "Artificial Neural Network Learning "
                + t.name,
                "Effect of learning with different number of hidden nodes",
                "Number of hidden nodes", "Accuracy");

        t.learner.nHidden = h; // Recover.
    }

    private static void testLearnRate (final AnnTest t) {
        double lr = t.learner.learnRate; // Backup

        final LinkedHashMap<Double, Double> numMap =
                new LinkedHashMap<Double, Double>();

        double learningR = 0;
        while (Double.compare(learningR, TEMINAL) <= 0) {
            t.learner.learnRate = learningR;
            final NeuralNetwork net = t.learner.iterLearning(1000);
            final double accur = t.learner.evalTest(net);
            System.out.println("Learning rate: " + learningR + " accuracy: "
                    + accur);
            numMap.put((double) learningR, accur);
            learningR += STEP;
            learningR = MyMath.doubleRound(learningR, 1);
        }

        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put("Difference learning rate", numMap);

        DisplayChart.display(dataSet, "Artificial Neural Network Learning "
                + t.name, "Effect of learning with different learning rate",
                "Difference learning rate", "Accuracy");

        t.learner.learnRate = lr; // Recover.
    }

    private static LinkedHashMap<Double, Double> testMomentum (final AnnTest t) {
        final LinkedHashMap<Double, Double> moMap =
                new LinkedHashMap<Double, Double>();
        double mo = 0;
        t.learner.momentumRate = 0;
        while (Double.compare(mo, TEMINAL) <= 0) {
            final NeuralNetwork net = t.learner.iterLearning(1000);
            final double accur = t.learner.evalTest(net);
            System.out.println("Momentum rate: " + mo + " accuracy: " + accur);
            moMap.put(mo, accur);

            // Change momentum.
            mo += STEP;
            mo = MyMath.doubleRound(mo, 1);
            t.learner.momentumRate = mo;
        }

        t.learner.momentumRate = t.DEF_MOMENTUM; // Recover it.
        return moMap;
    }

    private static void testMo2 (final AnnTest t) {
        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        double mo = t.learner.momentumRate;

        t.learner.resetTrainList();
        System.out.println("No noise:");
        LinkedHashMap<Double, Double> moMap = testMomentum(t);
        dataSet.put("No noise", moMap);

        t.learner.corruptTrainList(0.1);
        System.out.println("10% noise:");
        moMap = testMomentum(t);
        dataSet.put("10% noise", moMap);

        t.learner.corruptTrainList(0.3);
        System.out.println("30% noise:");
        moMap = testMomentum(t);
        dataSet.put("30% noise", moMap);
        t.learner.resetTrainList();
        DisplayChart.display(dataSet, "Artificial Neural Network Learning "
                + t.name, "Effect of learning with different momentum rate",
                "Momentum rate", "Accuracy");

        t.learner.momentumRate = mo;
    }

    private static double singleTest (final AnnTest t, final String way) {
        double sum = 0;
        for (int i = 0; i < REPEAT_TIMES; i++) {
            final NeuralNetwork net;
            if (way.equals(ITER)) {
                net = t.learner.iterLearning(5000);
            } else if (way.equals(VAL)) {
                net = t.learner.validationLearning();
            } else {
                net = t.learner.kFoldLearning(3);
            }

            final double accur = t.learner.evalTest(net);
            sum += accur;
        }
        // Take the average as the accuracy of current ratio.
        double aver = sum / REPEAT_TIMES;
        aver = MyMath.doubleRound(aver, 3);
        return aver;
    }

    private static final String ITER = "Normal";
    private static final String VAL = "Validation";
    private static final String KFOLD = "K-Fold";
    // private static final double COR_STEP = 0.02;
    private static final double COR_TEMINAL = 0.3;
    private static final double REPEAT_TIMES = 1;

    private static void testCorrupt (final AnnTest t, double r) {
        final LinkedHashMap<Double, Double> iterMap =
                new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> valMap =
                new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> kFoldMap =
                new LinkedHashMap<Double, Double>();
        double corRatio = 0; // Go through corrupt ratio from 0 to COR_TEMINAL;
        while (Double.compare(corRatio, COR_TEMINAL) <= 0) {
            double accur = singleTest(t, ITER);
            System.out.println("Corrupt ratio: " + corRatio + " accuracy in \n"
                    + ITER + ": " + accur);
            iterMap.put(corRatio, accur);

            accur = singleTest(t, VAL);
            System.out.println(VAL + ": " + accur);
            valMap.put(corRatio, accur);

            accur = singleTest(t, KFOLD);
            System.out.println(KFOLD + ": " + accur);
            kFoldMap.put(corRatio, accur);

            // Corrupt the data.
            corRatio += r;
            corRatio = MyMath.doubleRound(corRatio, 2);
            t.learner.corruptTrainList(corRatio);
        }
        t.learner.resetTrainList();

        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put(ITER, iterMap);
        dataSet.put(VAL, valMap);
        dataSet.put(KFOLD, kFoldMap);

        DisplayChart.display(dataSet, "Artificial Neural Network Learning "
                + t.name, "Effect of learning with validation set",
                "Train set corrupted ratio", "Accuracy");
    }
}
