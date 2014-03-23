package artificialNeuralNetworks.ANN;

import java.util.ArrayList;

/**
 * FileName: AnnProblem.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 19, 2014 2:05:18 AM
 */
public abstract class AnnProblem {
    public final String name;
    public final String attrUrl;
    public final String trainUrl;
    public final String testUrl;
    public final ArrayList<Integer> defNHidden;
    public final double defLearnRate;
    public final double defMomentum;

    public final AnnLearner learner;

    public AnnProblem() {
        this.name = getName();
        this.attrUrl = getAttrFileUrl();
        this.trainUrl = getTrainFileUrl();
        this.testUrl = getTestFileUrl();
        this.defNHidden = getDefaultNumberOfHiddenNodes();
        this.defLearnRate = getDefaultLearningRate();
        this.defMomentum = getDefaultMomentumRate();

        learner = new AnnLearner(attrUrl, trainUrl, testUrl);
        learner.nHidden = defNHidden;
        learner.learnRate = defLearnRate;
        learner.momentumRate = defMomentum;
    }

    public abstract String getName ();

    public abstract String getAttrFileUrl ();

    public abstract String getTrainFileUrl ();

    // Can be null
    public abstract String getTestFileUrl ();

    // Recommend new int[] { 3 }
    public abstract ArrayList<Integer> getDefaultNumberOfHiddenNodes ();

    public abstract double getDefaultLearningRate (); // Recommend 0.1

    public abstract double getDefaultMomentumRate (); // Recommend 0.2
}
