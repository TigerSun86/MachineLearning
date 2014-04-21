package instancereduction;

import common.DataCorrupter;
import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName:     Test.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Apr 19, 2014 8:57:06 PM
 */
public class Test {
    private static final String ATTR_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/iris-attr.txt";
    private static final String TRAIN_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/iris-train.txt";
    private static final String TEST_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/iris-test.txt";
    
/*    private static final String ATTR_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/rcitest-attr.txt";
    private static final String TRAIN_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/rcitest-train.txt";
    private static final String TEST_FILE_URL =
            "http://cs.fit.edu/~pkc/classes/ml/data/iris-test.txt";*/

    public static void main(String[] args){
        RawAttrList rawAttr = new RawAttrList(ATTR_FILE_URL);

        RawExampleList rawTrain = new RawExampleList(TRAIN_FILE_URL);
        System.out.println("a");
        //rawTrain = DataCorrupter.corrupt(rawTrain, rawAttr, 0.1);
        //System.out.println(rawTrain);
        rawTrain = ENN.reduce(rawTrain, rawAttr);
        System.out.println("Reduced, size: "+ rawTrain.size());
        //System.out.println(rawTrain);
        rawTrain = RCI.reduce(rawTrain, rawAttr);
        System.out.println("Reduced, size: "+ rawTrain.size());
        //System.out.println(rawTrain);
    }
}
