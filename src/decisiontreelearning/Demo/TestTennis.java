package decisiontreelearning.Demo;

/**
 * FileName: TestTennis.java
 * @Description: Test Tennis sample.
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Feb 25, 2014
 */
import decisiontreelearning.DecisionTree.DecisionTreeTest;

public class TestTennis {
    private static final String ATTR_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/tennis-attr.txt";
    private static final String TRAIN_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/tennis-train.txt";
    private static final String TEST_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/tennis-test.txt";

    public static void main (final String[] args) {
        DecisionTreeTest.testDecisionTree(ATTR_FILE_URL, TRAIN_FILE_URL,
                TEST_FILE_URL, DecisionTreeTest.NO_PRUNE);
    }
}
