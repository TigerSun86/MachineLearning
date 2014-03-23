package artificialNeuralNetworks.Demo;

import java.util.ArrayList;

import artificialNeuralNetworks.ANN.AnnProblem;


/**
 * FileName: Iris.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 9, 2014 8:15:11 AM
 */
public class Iris extends AnnProblem {
    @Override
    public String getName () {
        return "Iris test";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://cs.fit.edu/~pkc/classes/ml/data/iris-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://cs.fit.edu/~pkc/classes/ml/data/iris-train.txt";
    }

    @Override
    public String getTestFileUrl () {
        return "http://cs.fit.edu/~pkc/classes/ml/data/iris-test.txt";
    }

    @Override
    public ArrayList<Integer> getDefaultNumberOfHiddenNodes () {
        final ArrayList<Integer> nH = new ArrayList<Integer>();
        nH.add(3);
        return nH;
    }

    @Override
    public double getDefaultLearningRate () {
        return 0.1;
    }

    @Override
    public double getDefaultMomentumRate () {
        return 0.2;
    }
}