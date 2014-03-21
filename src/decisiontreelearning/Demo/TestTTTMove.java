package decisiontreelearning.Demo;

import decisiontreelearning.DecisionTree.DecisionTreeTest;

public class TestTTTMove {
    /* private static final String ATTR_FILE_URL =
     * "http://my.fit.edu/~sunx2013/ArtificialIntelligence/HW5/ID3/src/resource/ttt-move-attr.txt"
     * ;
     * private static final String TRAIN_FILE_URL =
     * "http://my.fit.edu/~sunx2013/ArtificialIntelligence/HW5/ID3/src/resource/ttt-move-train.txt"
     * ;
     * private static final String TEST_FILE_URL =
     * "http://my.fit.edu/~sunx2013/ArtificialIntelligence/HW5/ID3/src/resource/ttt-move-test.txt"
     * ;
     * private static final String ATTR2_FILE_URL =
     * "http://my.fit.edu/~sunx2013/ArtificialIntelligence/HW5/ID3/src/resource/ttt-move-attr2.txt"
     * ;
     * private static final String TRAIN2_FILE_URL =
     * "http://my.fit.edu/~sunx2013/ArtificialIntelligence/HW5/ID3/src/resource/ttt-move-train2.txt"
     * ;
     * private static final String TEST2_FILE_URL =
     * "http://my.fit.edu/~sunx2013/ArtificialIntelligence/HW5/ID3/src/resource/ttt-move-test2.txt"
     * ; */
    private static final String ATTR_FILE_URL =
            "file:///C:/WorkSpace/MachineLearning/ttt-move-attr.txt";
    private static final String TRAIN_FILE_URL =
            "file:///C:/WorkSpace/MachineLearning/ttt-move-train.txt";
    private static final String TEST_FILE_URL =
            "file:///C:/WorkSpace/MachineLearning/ttt-move-test.txt";
    private static final String ATTR2_FILE_URL =
            "file:///C:/WorkSpace/MachineLearning/ttt-move-attr2.txt";
    private static final String TRAIN2_FILE_URL =
            "file:///C:/WorkSpace/MachineLearning/ttt-move-train2.txt";
    private static final String TEST2_FILE_URL =
            "file:///C:/WorkSpace/MachineLearning/ttt-move-test2.txt";

    public static void main (final String[] args) {
        DecisionTreeTest.testDecisionTree(ATTR_FILE_URL, TRAIN_FILE_URL,
                TEST_FILE_URL, DecisionTreeTest.CR_PRUNE);
        DecisionTreeTest.testDecisionTree(ATTR2_FILE_URL, TRAIN2_FILE_URL,
                TEST2_FILE_URL, DecisionTreeTest.CR_PRUNE);
    }
}
