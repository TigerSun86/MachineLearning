package artificialNeuralNetworks.Demo;

import artificialNeuralNetworks.ANN.AnnLearner;

/**
 * FileName: TestIris.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 9, 2014 8:15:11 AM
 */
public class TestIris extends AnnTest {
    private static final String ATTR_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/iris-attr.txt";
    private static final String TRAIN_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/iris-train.txt";
    private static final String TEST_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/iris-test.txt";

    public TestIris() {
        DEF_NHIDDEN = new int[] { 2 };
        DEF_LEARN_RATE = 0.1;
        DEF_MOMENTUM = 0.2;

        learner = new AnnLearner(ATTR_FILE_URL, TRAIN_FILE_URL, TEST_FILE_URL);
        learner.nHidden = DEF_NHIDDEN;
        learner.learnRate = DEF_LEARN_RATE;
        learner.momentumRate = DEF_MOMENTUM;

        name = "Iris test";
    }
}