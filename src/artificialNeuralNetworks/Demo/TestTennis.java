package artificialNeuralNetworks.Demo;

import artificialNeuralNetworks.ANN.AnnLearner;

/**
 * FileName: TestTennis.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 13, 2014 1:52:52 AM
 */
public class TestTennis extends AnnTest {
    private static final String ATTR_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/tennis-attr.txt";
    private static final String TRAIN_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/tennis-train.txt";
    private static final String TEST_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/tennis-test.txt";

    public TestTennis() {
        DEF_NHIDDEN = new int[] { 3 };
        DEF_LEARN_RATE = 0.1;
        DEF_MOMENTUM = 0;

        learner = new AnnLearner(ATTR_FILE_URL, TRAIN_FILE_URL, TEST_FILE_URL);
        learner.nHidden = DEF_NHIDDEN;
        learner.learnRate = DEF_LEARN_RATE;
        learner.momentumRate = DEF_MOMENTUM;

        name = "Tennis test";
    }
}