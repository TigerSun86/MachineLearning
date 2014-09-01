package ripperk;

import common.Evaluator;
import common.Hypothesis;
import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName: Test.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 1, 2014 1:36:40 PM
 */
public class Test {
    private static final String ATTR_FILE_URL =
            "http://my.fit.edu/~sunx2013/DataMining/restaurant-attr.txt";
    private static final String TRAIN_FILE_URL =
            "http://my.fit.edu/~sunx2013/DataMining/restaurant-train.txt";
    private static final String TEST_FILE_URL =
            "http://my.fit.edu/~sunx2013/DataMining/restaurant-test.txt";

    public static void main (String[] args) {
        // Read data from files.
        final RawAttrList rawAttr = new RawAttrList(ATTR_FILE_URL);
        final RawExampleList rawTrain = new RawExampleList(TRAIN_FILE_URL);
        final RawExampleList rawTest = new RawExampleList(TEST_FILE_URL);
        System.out.println(rawAttr);
        System.out.println(rawTrain);
        System.out.println(rawTest);
        
        // Normalize data.
        // Split data set into train and test.
        
        
        // Predictor = RIPPERK_learner(training set)
        final Hypothesis h =RIPPERk.learn(rawTrain, rawAttr);
        final double accur = Evaluator.evaluate(h, rawTest);
        // Get accuracy of predictor on test set.
        System.out.println(accur);

    }
}
