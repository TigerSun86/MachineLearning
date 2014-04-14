package genetic;

import java.util.LinkedHashMap;
import java.util.Scanner;

import util.Dbg;
import util.DisplayChart;
import util.MyMath;

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
                    + "\t2 Different generation numbers with selection strategy.\n"
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
        while (true) {
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
            if (t == null) {
                break;
            }
            boolean quit = false;
            while (!quit && t != null) {
                System.out.println(DISPLAY_INFO);
                command = getCommandNumber(s);
                switch (command) {
                    case 0:
                        setting(t, s);
                        break;
                    case 1:
                        simpleTest(t);
                        break;
                    case 2:
                        System.out
                                .println("Please input the base number of generation:");
                        final int n1 = getInt(s);
                        System.out
                                .println("Please input the step number of generation:");
                        final int n2 = getInt(s);
                        System.out
                                .println("Please input the terminal number of generation:");
                        final int n3 = getInt(s);
                        System.out
                                .println("Please input number of repeats for each generation:");
                        final int n4 = getInt(s);
                        selectionTest(t, n1, n2, n3, n4);
                        break;
                    case 3:
                        System.out
                                .println("Please input number of repeats for each rate:");
                        final int n = getInt(s);
                        ReplacementTest(t, n);
                        break;
                    default:
                        quit = true;
                } // End of switch (command) {
            } // End of while (!quit && t != null) {
        } // End of while (true) {
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

    private static void selectionTest (final GAProblem t, final int base,
            final int step, final int ternimal, final int numOfRepeat) {
        final int selectionBackup = t.selectWay;
        final double accurBackup = t.accuracyThreshold;
        final int geneBackup = t.maxGeneration;

        t.accuracyThreshold = 1.0; // Cancel the accuracy stop criterion.

        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();

        for (int i = GA.SELECT_FIT_PRO; i <= GA.SELECT_RANK; i++) {
            final LinkedHashMap<Double, Double> sel =
                    new LinkedHashMap<Double, Double>();
            if (i == GA.SELECT_FIT_PRO) {
                System.out.println("Testing fitness-proportional");
                dataSet.put("Fitness-proportional", sel);
            } else if (i == GA.SELECT_TOUR) {
                System.out.println("Testing tournament");
                dataSet.put("Tournament", sel);
            } else {
                System.out.println("Testing rank");
                dataSet.put("Rank", sel);
            }

            t.selectWay = i;

            for (int j = base; j <= ternimal; j += step) {
                System.out.println("Generation number: " + j);
                t.maxGeneration = j;
                sel.put((double) j, 0.0);
                for (int k = 0; k < numOfRepeat; k++) {
                    final Hypothesis h =
                            GA.gaLearning(t.rawTrain, t.rawAttr,
                                    t.accuracyThreshold, t.maxGeneration,
                                    t.numP, t.r, t.m, t.selectWay);
                    double accur = Evaluator.evaluate(h, t.rawTrain);
                    System.out.print("Train: " + accur);
                    accur = Evaluator.evaluate(h, t.rawTest);
                    System.out.println(", test: " + accur);
                    double ac = sel.get((double) j);
                    ac += accur;
                    sel.put((double) j, ac);
                }

                double ac = sel.get((double) j);
                ac /= numOfRepeat;
                sel.put((double) j, ac);
            }
        }
        DisplayChart.display(dataSet, "Genetic Algorithm learning " + t.name,
                "Accuracy of best individual after certain generations",
                "Number of generations with different selection strategy",
                "Accuracy");

        t.selectWay = selectionBackup;
        t.accuracyThreshold = accurBackup;
        t.maxGeneration = geneBackup;
    }

    private static final double REPLACE_START = 0.1;
    private static final double REPLACE_STEP = 0.1;
    private static final double REPLACE_TERMINAL = 0.9;

    private static void ReplacementTest (final GAProblem t,
            final int numOfRepeat) {
        final double rBackup = t.r;
        final int selectionBackup = t.selectWay;
        
        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();

        for (int i = GA.SELECT_FIT_PRO; i <= GA.SELECT_RANK; i++) {
            final LinkedHashMap<Double, Double> sel =
                    new LinkedHashMap<Double, Double>();
            if (i == GA.SELECT_FIT_PRO) {
                System.out.println("Testing fitness-proportional");
                dataSet.put("Fitness-proportional", sel);
            } else if (i == GA.SELECT_TOUR) {
                System.out.println("Testing tournament");
                dataSet.put("Tournament", sel);
            } else {
                System.out.println("Testing rank");
                dataSet.put("Rank", sel);
            }
            
            t.selectWay = i;
            
            for (double j = REPLACE_START; Double.compare(j, REPLACE_TERMINAL) <= 0; j =
                    MyMath.doubleRound(j + REPLACE_STEP, 2)) {
                System.out.println("Replacement rate :" + j);
                t.r = j;
                sel.put(j, 0.0);
                for (int k = 0; k < numOfRepeat; k++) {
                    final Hypothesis h =
                            GA.gaLearning(t.rawTrain, t.rawAttr,
                                    t.accuracyThreshold, t.maxGeneration,
                                    t.numP, t.r, t.m, t.selectWay);
                    double accur = Evaluator.evaluate(h, t.rawTrain);
                    System.out.print("Train: " + accur);
                    accur = Evaluator.evaluate(h, t.rawTrain);
                    System.out.println(", test: " + accur);
                    double ac = sel.get(j);
                    ac += accur;
                    sel.put(j, ac);
                }
                double ac = sel.get(j);
                ac /= numOfRepeat;
                sel.put(j, ac);
            }
        }
        DisplayChart.display(dataSet, "Genetic Algorithm learning " + t.name,
                "Different replacement rates versus accuracy",
                "Replacement rate with different selection strategy",
                "Accuracy");

        t.r = rBackup;
        t.selectWay = selectionBackup;
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
        public String name;
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
                this.name = "Tennis";
                this.rawAttr = new RawAttrList(ATTR_FILE_URL[0]);
                this.rawTrain = new RawExampleList(TRAIN_FILE_URL[0]);
                this.rawTest = new RawExampleList(TEST_FILE_URL[0]);
                this.accuracyThreshold = 0.9;
                this.maxGeneration = 50;
                this.numP = 20;
                this.r = 0.6;
                this.m = 0.01;
                this.selectWay = GA.SELECT_RANK;
            } else {
                this.name = "Iris";
                this.rawAttr = new RawAttrList(ATTR_FILE_URL[1]);
                this.rawTrain = new RawExampleList(TRAIN_FILE_URL[1]);
                this.rawTest = new RawExampleList(TEST_FILE_URL[1]);
                this.accuracyThreshold = 0.95;
                this.maxGeneration = 50;
                this.numP = 100;
                this.r = 0.6;
                this.m = 0.01;
                this.selectWay = GA.SELECT_RANK;
            }
        }
    }
}
