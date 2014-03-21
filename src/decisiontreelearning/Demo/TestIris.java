package decisiontreelearning.Demo;

/**
 * FileName: TestIris.java
 * @Description: Test Iris sample.
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Feb 25, 2014
 */
import java.util.HashMap;
import java.util.LinkedHashMap;

import util.ChartData;
import util.DisplayChart;
import util.MyMath;
import decisiontreelearning.DecisionTree.DecisionTreeTest;

public class TestIris {
    private static final String ATTR_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/iris-attr.txt";
    private static final String TRAIN_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/iris-train.txt";
    private static final String TEST_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/iris-test.txt";

    private static final String CMD_SIMPLE = "-s";
    private static final String CMD_COMPRE = "-c";
    private static final HashMap<String, Integer> CMD_PRUNE_MAP =
            new HashMap<String, Integer>();
    static {
        CMD_PRUNE_MAP.put("no", DecisionTreeTest.NO_PRUNE);
        CMD_PRUNE_MAP.put("rule", DecisionTreeTest.RP_PRUNE);
        CMD_PRUNE_MAP.put("com", DecisionTreeTest.CR_PRUNE);
    }

    public static void main (final String[] args) {
        final boolean[] comprePrune =
                new boolean[DecisionTreeTest.CR_PRUNE + 1];
        comprePrune[DecisionTreeTest.NO_PRUNE] = true;
        comprePrune[DecisionTreeTest.RP_PRUNE] = true;
        comprePrune[DecisionTreeTest.CR_PRUNE] = false;
        if (args.length == 0) {
            comprehensiveTest(comprePrune);
            return;
        }

        boolean isSimple = false;
        int simPruneWay = DecisionTreeTest.CR_PRUNE;
        double simCorruptRatio = 0;
        if (args[0].equalsIgnoreCase(CMD_SIMPLE)) {
            isSimple = true;
            if (args.length >= 2) {
                Integer way = CMD_PRUNE_MAP.get(args[1]);
                if (way != null) {
                    simPruneWay = way;
                }
            }
            if (args.length == 3) {
                try {
                    simCorruptRatio = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {// Do nothing.
                }
            }
        } else if (args[0].equalsIgnoreCase(CMD_COMPRE)) {
            if (args.length == 2) {
                comprePrune[DecisionTreeTest.NO_PRUNE] = false;
                comprePrune[DecisionTreeTest.RP_PRUNE] = false;
                comprePrune[DecisionTreeTest.CR_PRUNE] = false;
                final String[] ways = args[1].split(",");
                for (String p : ways) {
                    Integer way = CMD_PRUNE_MAP.get(p);
                    if (way != null) {
                        comprePrune[way] = true;
                    }
                }
            }
        }

        if (isSimple) {
            simpleTest(simPruneWay, simCorruptRatio);
        } else {
            comprehensiveTest(comprePrune);
        }
    }

    private static void simpleTest (final int needPrune,
            final double corruptRatio) {
        DecisionTreeTest.testDecisionTree(ATTR_FILE_URL, TRAIN_FILE_URL,
                TEST_FILE_URL, needPrune, corruptRatio);
    }

    private static final double COR_STEP = 0.02;
    private static final double COR_TEMINAL = 0.3;
    private static final double REPEAT_TIMES = 10;
    private static final String[] PRUNE_WAYS = { "No pruning",
            "Rule Post-pruning", "Combined Rule Post-pruning" };

    private static void comprehensiveTest (final boolean[] comprePrune) {
        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        for (int way = DecisionTreeTest.NO_PRUNE; way <= DecisionTreeTest.CR_PRUNE; way++) {
            if (comprePrune[way]) {
                System.out.println(PRUNE_WAYS[way]);
                final LinkedHashMap<Double, Double> map = groupTest(way);
                dataSet.put(PRUNE_WAYS[way], map);
            }
        }

        display(dataSet);
    }

    private static void display (
            final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet) {
        ChartData cd = new ChartData();
        cd.dataSet.putAll(dataSet);
        cd.windowTitle = "Decision tree test";
        cd.chartTitle = "Effect of rule post-pruning in decision tree learning";
        cd.categoryAxisLabel = "Train set corrupted ratio";
        cd.valueAxisLabel = "Accuracy";
        DisplayChart.display(cd);
    }

    private static LinkedHashMap<Double, Double> groupTest (final int pruneWay) {
        final LinkedHashMap<Double, Double> map =
                new LinkedHashMap<Double, Double>();
        double corRatio = 0; // Go through corrupt ratio from 0 to COR_TEMINAL;
        while (Double.compare(corRatio, COR_TEMINAL) <= 0) {
            double sum = 0;
            for (int i = 0; i < REPEAT_TIMES; i++) {
                // Each ratio repeats REPEAT_TIMES.
                final double accu =
                        DecisionTreeTest.testDecisionTree(ATTR_FILE_URL,
                                TRAIN_FILE_URL, TEST_FILE_URL, pruneWay,
                                corRatio);
                sum += accu;
            }
            // Take the average as the accuracy of current ratio.
            double aver = sum / REPEAT_TIMES;
            aver = MyMath.doubleRound(aver, 3);
            System.out.println("Accuracy: " + aver + "\tList Corrupt ratio: "
                    + corRatio);
            map.put(corRatio, aver); // Record result for chart.
            corRatio += COR_STEP;
            corRatio = MyMath.doubleRound(corRatio, 2);
        }
        return map;
    }
}
