package multiAnn;

import artificialNeuralNetworks.ANN.AnnLearner;
import artificialNeuralNetworks.ANN.NeuralNetwork;
import common.Hypothesis;
import common.Learner;
import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName: AnnLearner2.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 27, 2014 4:51:46 PM
 */
public class AnnLearner2 implements Learner {
    private final int numOfHiddenNodes;
    private final double learnRate;
    private final double momentumRate;

    public AnnLearner2(final int numOfHiddenNodes, final double learnRate,
            final double momentumRate) {
        this.numOfHiddenNodes = numOfHiddenNodes;
        this.learnRate = learnRate;
        this.momentumRate = momentumRate;
    }

    public AnnLearner2() {
        this(3, 0.1, 0.1);
    }

    @Override
    public NeuralNetwork learn (RawExampleList dataSet, RawAttrList attrs) {
        final AnnLearner annLearner =
                new AnnLearner(attrs, learnRate, momentumRate);
        annLearner.setRawTrainWithNoise(dataSet);
        annLearner.setNumOfHiddenNodes(numOfHiddenNodes);
        final NeuralNetwork nn = annLearner.kFoldLearning(3);
        return nn;
    }

}
