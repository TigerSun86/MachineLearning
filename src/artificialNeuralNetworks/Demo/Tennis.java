package artificialNeuralNetworks.Demo;

import java.util.ArrayList;

import artificialNeuralNetworks.ANN.AnnProblem;


/**
 * FileName: Tennis.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 13, 2014 1:52:52 AM
 */
public class Tennis extends AnnProblem {
    @Override
    public String getName () {
        return "Tennis test";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://cs.fit.edu/~pkc/classes/ml/data/tennis-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://cs.fit.edu/~pkc/classes/ml/data/tennis-train.txt";
    }

    @Override
    public String getTestFileUrl () {
        return "http://cs.fit.edu/~pkc/classes/ml/data/tennis-test.txt";
    }
    
    @Override
    public String getDataFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/tennis.txt";
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