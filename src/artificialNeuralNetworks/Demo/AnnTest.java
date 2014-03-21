package artificialNeuralNetworks.Demo;

import artificialNeuralNetworks.ANN.AnnLearner;

/**
 * FileName:     AnnTest.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 19, 2014 2:05:18 AM 
 */
public abstract class AnnTest {
    public int[] DEF_NHIDDEN = new int[] { 3 };
    public double DEF_LEARN_RATE = 0.3;
    public double DEF_MOMENTUM = 0;
    
    public AnnLearner learner = null;
    
    public String name = "";
}
