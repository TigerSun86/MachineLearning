package genetic;

import artificialNeuralNetworks.ANN.AnnAttrList;
import common.Evaluator;
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
    public static void test (final String attrFName, final String trainFName,
            final String testFName) {
        final RawAttrList rawAttr = new RawAttrList(attrFName);

        final RawExampleList rawTrain = new RawExampleList(trainFName);
        final RawExampleList rawTest;
        if (testFName != null) {
            rawTest = new RawExampleList(testFName);
        } else {
            rawTest = null;
        }

        BitStringRules rule = GA.gaLearning(rawTrain, rawAttr, fitness_threshold, numP, r, m);

        // Evaluator.evaluate(rule, rawTrain);
        // Evaluator.evaluate(rule, rawTest);
    }
}
