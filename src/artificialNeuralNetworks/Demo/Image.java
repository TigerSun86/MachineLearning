package artificialNeuralNetworks.Demo;

import java.util.ArrayList;

import artificialNeuralNetworks.ANN.AnnProblem;

/**
 * FileName: Image.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Apr 19, 2014 10:07:25 PM
 */
public class Image extends AnnProblem {
    @Override
    public String getName () {
        return "Image test";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/image-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://archive.ics.uci.edu/ml/machine-learning-databases/statlog/segment/segment.dat";
    }

    @Override
    public String getTestFileUrl () {
        return "http://archive.ics.uci.edu/ml/machine-learning-databases/statlog/segment/segment.dat";
    }

    @Override
    public ArrayList<Integer> getDefaultNumberOfHiddenNodes () {
        final ArrayList<Integer> nH = new ArrayList<Integer>();
        nH.add(7);
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
