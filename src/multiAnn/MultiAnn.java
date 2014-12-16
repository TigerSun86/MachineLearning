package multiAnn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.Dbg;
import artificialNeuralNetworks.ANN.NeuralNetwork;
import artificialNeuralNetworks.ANN.NeuralNetwork.PredictAndConfidence;

import common.Hypothesis;
import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName: MultiAnn.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 18, 2014 2:34:38 PM
 */
public class MultiAnn implements Hypothesis {
    public static final String MODULE = "MAnn";
    public static final boolean DBG = true;

    private final RawAttrList attrList;
    public final List<NeuralNetwork> nnlist = new ArrayList<NeuralNetwork>();
    private final List<RawExampleList> sets = new ArrayList<RawExampleList>();

    public MultiAnn(RawAttrList attrList) {
        this.attrList = attrList;
    }

    public void add (NeuralNetwork ann, RawExampleList dataSet) {
        nnlist.add(ann);
        sets.add(dataSet);
    }

    @Override
    public String predict (ArrayList<String> attrs) {
        // To calculate distance.
        final RawExample srcE = new RawExample();
        srcE.xList = attrs;
        srcE.t = null;

        // Decide use which nn to predict.
        double minDis = Double.POSITIVE_INFINITY;
        List<Integer> bestNNIdx = new ArrayList<Integer>();
        for (int i = 0; i < sets.size(); i++) {
            final RawExampleList set = sets.get(i);
            for (RawExample e : set) {
                final double dis = MAnnLearner.getDis(srcE, e);
                if (Double.compare(minDis, dis) > 0) {
                    minDis = dis;
                    bestNNIdx = new ArrayList<Integer>();
                    bestNNIdx.add(i);
                } else if (Double.compare(minDis, dis) == 0) {
                    bestNNIdx.add(i);
                }
            }
        }
        assert !bestNNIdx.isEmpty();
        boolean votePredict = true;
        if (votePredict) {
            // Weighted vote prediction,
            // weight is the (1-confidence of prediction).
            final List<String> classes = attrList.t.valueList;
            final double[] predictionCounts = new double[classes.size()];
            for (Integer idx : bestNNIdx) {
                final PredictAndConfidence prediction =
                        nnlist.get(idx).predictWithConf(attrs);
                final int indexOf = classes.indexOf(prediction.predict);
                assert indexOf != -1;
                predictionCounts[indexOf] += (1 - prediction.confidence);
            }

            double maxPre = Double.NEGATIVE_INFINITY;
            String pre = null;
            for (int i = 0; i < predictionCounts.length; i++) {
                if (Double.compare(maxPre, predictionCounts[i]) < 0) {
                    maxPre = predictionCounts[i];
                    pre = classes.get(i);
                }
            }
            Dbg.print(DBG, MODULE, Arrays.toString(predictionCounts) + " "
                    + pre);
            assert pre != null;
            return pre;
        } else {
            // Use the prediction with lowest confidence.
            double minConf = Double.POSITIVE_INFINITY;
            String bestPredict = null;
            for (Integer idx : bestNNIdx) {
                final PredictAndConfidence prediction =
                        nnlist.get(idx).predictWithConf(attrs);
                // System.out.println(idx+":"+prediction.predict+" "+prediction.confidence);
                if (Double.compare(minConf, prediction.confidence) > 0) {
                    minConf = prediction.confidence;
                    bestPredict = prediction.predict;
                }
            }
            // System.out.println("best "+bestPredict+minConf);
            assert bestPredict != null;
            return bestPredict;
        }
    }
}
