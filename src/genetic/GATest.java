package genetic;

import common.Evaluator;
import common.Hypothesis;
import common.RawAttrList;
import common.RawExampleList;
import decisiontreelearning.DecisionTree.DecisionTreeTest;

/**
 * FileName: GATest.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Apr 1, 2014 3:34:28 PM
 */
public class GATest {
/*    private static final String ATTR_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/tennis-attr.txt";
    private static final String TRAIN_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/tennis-train.txt";
    private static final String TEST_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/tennis-test.txt";*/
    private static final String ATTR_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/iris-attr.txt";
    private static final String TRAIN_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/iris-train.txt";
    private static final String TEST_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/iris-test.txt";

    public static void main (String[] args) {
        test(ATTR_FILE_URL, TRAIN_FILE_URL, TEST_FILE_URL);
    }

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

        Hypothesis rule = GA.gaLearning(rawTrain, rawAttr, 0.9, 100, 0.6, 0.01);

        System.out.println( "Train accuracy: "+Evaluator.evaluate(rule, rawTrain));
        System.out.println( "Test accuracy: "+Evaluator.evaluate(rule, rawTest));
    }
}
