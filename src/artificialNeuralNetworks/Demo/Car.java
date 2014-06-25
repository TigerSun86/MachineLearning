package artificialNeuralNetworks.Demo;

import java.util.ArrayList;

import artificialNeuralNetworks.ANN.AnnProblem;

/**
 * FileName: Car.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Apr 24, 2014 5:38:07 AM
 */
public class Car extends AnnProblem {
    @Override
    public String getName () {
        return "Car Evaluation";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/car-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/car-train.txt";
    }

    @Override
    public String getTestFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/car-test.txt";
    }
    
    @Override
    public String getDataFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/car.txt";
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
