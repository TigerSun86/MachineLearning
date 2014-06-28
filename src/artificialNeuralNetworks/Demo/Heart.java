package artificialNeuralNetworks.Demo;

import java.util.ArrayList;

import artificialNeuralNetworks.ANN.AnnProblem;

/**
 * FileName:     Heart.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 27, 2014 9:50:08 PM 
 */
public class Heart extends AnnProblem {
    @Override
    public String getName () {
        return "Heart";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/heart-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/heart-train.txt";
    }

    @Override
    public String getTestFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/heart-test.txt";
    }
    
    @Override
    public String getDataFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/heart.txt";
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
        return 0.1;
    }
}
