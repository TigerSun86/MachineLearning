package artificialNeuralNetworks.Demo;

import artificialNeuralNetworks.ANN.AnnLearner;

/**
 * FileName: TestIdentity.java
 * 
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 6, 2014 10:12:37 PM
 */
public class TestIdentity extends AnnTest {
    private static final String ATTR_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/identity-attr.txt";
    private static final String TRAIN_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/identity-train.txt";
    private static final String TEST_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/identity-train.txt";

    public TestIdentity() {
        DEF_NHIDDEN = new int[] { 3 };
        DEF_LEARN_RATE = 0.3;
        DEF_MOMENTUM = 0;

        learner = new AnnLearner(ATTR_FILE_URL, TRAIN_FILE_URL, TEST_FILE_URL);
        learner.nHidden = DEF_NHIDDEN;
        learner.learnRate = DEF_LEARN_RATE;
        learner.momentumRate = DEF_MOMENTUM;

        name = "Identity test";
    }
}