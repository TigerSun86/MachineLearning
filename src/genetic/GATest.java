package genetic;

import java.util.Scanner;

import util.Dbg;
import common.Evaluator;
import common.Hypothesis;
import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName: GATest.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Apr 1, 2014 3:34:28 PM
 */
public class GATest {
    private static final String TEST_INFO =
            "Please choose the data you want to test:\n" + "\t0 Tennis.\n"
                    + "\t1 Iris.\n" + "\tOther_number quit\n";

    private static final String DISPLAY_INFO =
            "Please input the following command number to test:\n"
                    + "\t0 Set Population size (p), Replacement rate (r), Mutation rate (m),\n"
                    + "\t  Accuracy threshold, Number of generations, Selection strategy.\n"
                    + "\t1 Simple test.\n"
                    + "\t2 Different selection strategy.\n"
                    + "\t3 Different replacement rate.\n"
                    + "\tOther_number Quit.\n";

    private static final String SETTING_INFO =
            "Please input the following command number to set:\n"
                    + "\t0 Population size (p).\n"
                    + "\t1 Replacement rate (r).\n"
                    + "\t2 Mutation rate (m).\n" + "\t3 Accuracy threshold.\n"
                    + "\t4 Number of generations.\n"
                    + "\t5 Selection strategy.\n" + "\tOther_number back\n";
    private static final String STRATEGY_INFO =
            "Please input the following command number to set selection strategy:\n"
                    + "\t0 Fitness-proportional.\n" + "\t1 Tournament.\n"
                    + "\t2 Rank.\n" + "\tOther_number back\n";

    public static void main (String[] args) {
        final Scanner s = new Scanner(System.in);

        final GAProblem t;
        System.out.println(TEST_INFO);
        int command = getCommandNumber(s);
        switch (command) {
            case 0:
                t = new GAProblem("Tennis");
                break;
            case 1:
                t = new GAProblem("Iris");
                break;
            default:
                t = null;
                break;
        }

        boolean quit = false;
        while (!quit && t != null) {
            System.out.println(DISPLAY_INFO);
            command = getCommandNumber(s);
            int n;
            switch (command) {
                case 0:
                    setting(t, s);
                    break;
                case 1:
                    simpleTest(t);
                    break;
                case 2:
                    System.out
                            .println("Please input number of repeatations for each strategy:");
                    n = getInt(s);
                    selectionTest(t, n);
                    break;
                case 3:
                    System.out
                            .println("Please input number of repeatations for each rate:");
                    n = getInt(s);
                    ReplacementTest(t, n);
                    break;
                default:
                    quit = true;
            }
        }
        s.close();

    }

    private static void setting (final GAProblem t, final Scanner s) {
        boolean quit = false;
        while (!quit) {
            System.out.println(SETTING_INFO);
            final int command = getCommandNumber(s);
            int n;
            double r;
            switch (command) {
                case 0:
                    System.out.println("Please input population size (default "
                            + t.numP + "):");
                    n = getInt(s);
                    t.numP = n;
                    break;
                case 1:
                    System.out
                            .println("Please input replacement rate (default "
                                    + t.r + "):");
                    r = getDouble(s);
                    t.r = r;
                    break;
                case 2:
                    System.out.println("Please input mutation rate (default "
                            + t.m + "):");
                    r = getDouble(s);
                    t.m = r;
                    break;
                case 3:
                    System.out
                            .println("Please input accuracy threshold (default "
                                    + t.accuracyThreshold + "):");
                    r = getDouble(s);
                    t.accuracyThreshold = r;
                    break;
                case 4:
                    System.out
                            .println("Please input number of generations (default "
                                    + t.maxGeneration + "):");
                    n = getInt(s);
                    t.maxGeneration = n;
                    break;
                case 5:
                    System.out.println(STRATEGY_INFO);
                    n = getInt(s);
                    switch (n) {
                        case 0:
                            t.selectWay = GA.SELECT_FIT_PRO;
                            break;
                        case 1:
                            t.selectWay = GA.SELECT_TOUR;
                            break;
                        case 2:
                            t.selectWay = GA.SELECT_RANK;
                            break;
                    }
                    break;
                default:
                    quit = true;
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

    private static void simpleTest (final GAProblem t) {
        Dbg.dbgSwitch = true;
        Dbg.defaultSwitch = true;
        GA.DBG = true;
        OffspringProducer.DBG = true;
        Mutator.DBG = true;
        final Hypothesis h =
                GA.gaLearning(t.rawTrain, t.rawAttr, t.accuracyThreshold,
                        t.maxGeneration, t.numP, t.r, t.m, t.selectWay);

        System.out.println("Train accuracy: "
                + Evaluator.evaluate(h, t.rawTrain));
        System.out
                .println("Test accuracy: " + Evaluator.evaluate(h, t.rawTest));
        Dbg.dbgSwitch = false;
        Dbg.defaultSwitch = false;
        GA.DBG = false;
        OffspringProducer.DBG = false;
        Mutator.DBG = false;
    }

    private static void
            selectionTest (final GAProblem t, final int numOfRepeat) {
        final int selectionBackup = t.selectWay;
        final double[] averAccur = new double[GA.SELECT_RANK + 1];
        for (int i = GA.SELECT_FIT_PRO; i <= GA.SELECT_RANK; i++) {
            if (i == GA.SELECT_FIT_PRO) {
                System.out.println("Testing fitness-proportional");
            } else if (i == GA.SELECT_TOUR) {
                System.out.println("Testing tournament");
            } else {
                System.out.println("Testing rank");
            }
            t.selectWay = i;
            for (int j = 0; j < numOfRepeat; j++) {
                final Hypothesis h =
                        GA.gaLearning(t.rawTrain, t.rawAttr,
                                t.accuracyThreshold, t.maxGeneration, t.numP,
                                t.r, t.m, t.selectWay);
                double accur = Evaluator.evaluate(h, t.rawTrain);
                System.out.print("Train: " + accur);
                accur = Evaluator.evaluate(h, t.rawTest);
                System.out.println(", test: " + accur);
                averAccur[i] += accur;
            }
        }

        for (int i = GA.SELECT_FIT_PRO; i <= GA.SELECT_RANK; i++) {
            if (i == GA.SELECT_FIT_PRO) {
                System.out.printf("Accuracy fitness-proportional: %.3f",
                        averAccur[i] / numOfRepeat);
            } else if (i == GA.SELECT_TOUR) {
                System.out.printf(", tournament: %.3f", averAccur[i]
                        / numOfRepeat);
            } else {
                System.out.printf(", rank: %.3f%n", averAccur[i] / numOfRepeat);
            }
        }
        t.selectWay = selectionBackup;
    }

    private static final double REPLACE_START = 0.1;
    private static final double REPLACE_STEP = 0.1;
    private static final double REPLACE_TERMINAL = 0.9;

    private static void ReplacementTest (final GAProblem t,
            final int numOfRepeat) {
        final double rBackup = t.r;
        final double[] averAccur = new double[9];

        for (double i = REPLACE_START; Double.compare(i, REPLACE_TERMINAL) <= 0; i +=
                REPLACE_STEP) {
            System.out.println("Replacement rate :" + i);
            for (int j = 0; j < numOfRepeat; j++) {
                final Hypothesis h =
                        GA.gaLearning(t.rawTrain, t.rawAttr,
                                t.accuracyThreshold, t.maxGeneration, t.numP,
                                t.r, t.m, t.selectWay);
                double accur = Evaluator.evaluate(h, t.rawTrain);
                System.out.print("Train: " + accur);
                accur = Evaluator.evaluate(h, t.rawTest);
                System.out.println(", test: " + accur);
                averAccur[(int) Math.round(i * 10) - 1] += accur;
            }
        }
        for (int i = 0; i < 9; i++) {
            System.out.printf("Replacement %.1f, accuracy: %.3f%n",
                    (i + 1) / 10.0, averAccur[i] / numOfRepeat);
        }
        t.r = rBackup;
    }

    private static class GAProblem {
        private static final String ATTR_FILE_URL[] = {
                "http://cs.fit.edu/~pkc/classes/ml/data/tennis-attr.txt",
                "http://cs.fit.edu/~pkc/classes/ml/data/iris-attr.txt" };
        private static final String TRAIN_FILE_URL[] = {
                "http://cs.fit.edu/~pkc/classes/ml/data/tennis-train.txt",
                "http://cs.fit.edu/~pkc/classes/ml/data/iris-train.txt" };
        private static final String TEST_FILE_URL[] = {
                "http://cs.fit.edu/~pkc/classes/ml/data/tennis-test.txt",
                "http://cs.fit.edu/~pkc/classes/ml/data/iris-test.txt" };

        public RawAttrList rawAttr;
        public RawExampleList rawTrain;
        public RawExampleList rawTest;
        public double accuracyThreshold;
        public int maxGeneration;
        public int numP;
        public double r;
        public double m;
        public int selectWay;

        public GAProblem(final String testCase) {
            if (testCase.equalsIgnoreCase("Tennis")) {
                this.rawAttr = new RawAttrList(ATTR_FILE_URL[0]);
                this.rawTrain = new RawExampleList(TRAIN_FILE_URL[0]);
                this.rawTest = new RawExampleList(TEST_FILE_URL[0]);
                this.accuracyThreshold = 0.9;
                this.maxGeneration = 50;
                this.numP = 10;
                this.r = 0.6;
                this.m = 0.01;
                this.selectWay = GA.SELECT_RANK;
            } else {
                this.rawAttr = new RawAttrList(ATTR_FILE_URL[1]);
                this.rawTrain = new RawExampleList(TRAIN_FILE_URL[1]);
                this.rawTest = new RawExampleList(TEST_FILE_URL[1]);
                this.accuracyThreshold = 0.9;
                this.maxGeneration = 50;
                this.numP = 100;
                this.r = 0.6;
                this.m = 0.01;
                this.selectWay = GA.SELECT_RANK;
            }
        }
    }
}
