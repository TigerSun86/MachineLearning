package decisiontreelearning.Demo;

import decisiontreelearning.DecisionTree.DecisionTreeTest;

public class TestCh2 {
    private static final String ATTR_FILE_URL =
            "file:///C:/WorkSpace/MachineLearning/ch2-attr.txt";
    private static final String TRAIN_FILE_URL =
            "file:///C:/WorkSpace/MachineLearning/ch2-train.txt";
    private static final String TEST_FILE_URL =
            null;

    public static void main (final String[] args) {
        DecisionTreeTest.testDecisionTree(ATTR_FILE_URL, TRAIN_FILE_URL,
                TEST_FILE_URL, DecisionTreeTest.CR_PRUNE);
    }

}
