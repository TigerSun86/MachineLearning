package artificialNeuralNetworks.ANN;

import instancereduction.Reducible;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import util.Dbg;
import util.SysUtil;
import common.DataCorrupter;
import common.Evaluator;
import common.RawAttrList;
import common.RawExampleList;
import common.TrainTestSplitter;

/**
 * FileName: AnnLearner.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 13, 2014 3:07:10 AM
 */
public class AnnLearner {
    public static final String MODULE = "ALN";
    public static final boolean DBG = true;

    private static final int MAX_ITER = 10000;

    public final RawAttrList rawAttr;
    public final AnnAttrList annAttr; // For nIn and nOut.
    public final RawExampleList rawTrain;
    public RawExampleList rawTest;

    public RawExampleList rawTrainWithNoise;

    public ArrayList<Integer> nHidden;
    public boolean hiddenHasThres;
    public boolean outHasThres;
    public double learnRate;
    public double momentumRate;

    public AnnLearner(final String attrFName, final String trainFName,
            final String testFName) {
        rawAttr = new RawAttrList(attrFName);
        rawTrain = new RawExampleList(trainFName);

        if (testFName != null) {
            rawTest = new RawExampleList(testFName);
        } else {
            rawTest = null;
        }

        rawTrainWithNoise = rawTrain;

        annAttr = new AnnAttrList(rawAttr);

        nHidden = new ArrayList<Integer>();
        hiddenHasThres = true;
        outHasThres = true;
        learnRate = 0.1;
        momentumRate = 0.1;
    }

    /* For instance reduction begin ********************* */
    public AnnLearner(final RawAttrList rawAttr, final double learnRate,
            final double momentumRate) {
        this.rawAttr = rawAttr;
        this.rawTrain = null;
        this.rawTest = null;
        this.rawTrainWithNoise = null;
        this.annAttr = new AnnAttrList(rawAttr);
        // If didn't set hidden nodes means no hidden nodes.
        this.nHidden = new ArrayList<Integer>();
        this.hiddenHasThres = true;
        this.outHasThres = true;
        this.learnRate = learnRate;
        this.momentumRate = momentumRate;
    }

    public void setRawTrainWithNoise (final RawExampleList rawTrainWithNoise) {
        this.rawTrainWithNoise = rawTrainWithNoise;
    }

    public void setRawTest (final RawExampleList rawTest) {
        this.rawTest = rawTest;
    }

    public void setNumOfHiddenNodes (final int nH) {
        this.nHidden = new ArrayList<Integer>();
        this.nHidden.add(nH);
    }

    public AccurAndIter kFoldLearning2 (final int k) {
        final AnnExList annSet = new AnnExList(rawTrainWithNoise, rawAttr);
        final AnnExList[] exArray = annSet.splitIntoMultiSets(k);
        if (exArray == null) {
            return null;
        }
        int sumIter = 0;
        for (int val = 0; val < exArray.length; val++) {
            final AnnExList valSet = exArray[val];
            final AnnExList trainSet = new AnnExList();
            for (int other = 0; other < exArray.length; other++) {
                if (other != val) { // All other set is train set.
                    trainSet.addAll(exArray[other]);
                }
            }
            sumIter += validation2(trainSet, valSet);
        }

        final int meanIter = sumIter / exArray.length;
        final NeuralNetwork net = iter(annSet, meanIter);
        final double accur = evalTest(net);
        return new AccurAndIter(accur, meanIter);
    }

    public AccurAndIter learnUntilConverge () {
        final NeuralNetwork net =
                new NeuralNetwork(rawAttr, annAttr.xList.size(), nHidden,
                        hiddenHasThres, annAttr.tList.size(), outHasThres,
                        learnRate, momentumRate);
        NeuralNetwork lastNet = new NeuralNetwork(net);

        final AnnExList annSet = new AnnExList(rawTrainWithNoise, rawAttr);
        int iter = 0;
        while (iter < MAX_ITER) {
            iter(net, annSet, 100);
            iter += 100;
            if (net.hasConverged(lastNet)) {
                Dbg.print(DBG, MODULE, "Network converged at iter " + iter);
                break;
            } else {
                lastNet = new NeuralNetwork(net);
            }
        }
        if (iter == MAX_ITER) {
            Dbg.print(DBG, MODULE, "Stop at iter: " + iter);
        }

        final double accur = evalTest(net);
        System.out.println(net);
        AnnVisualizer.show(net);
        return new AccurAndIter(accur, iter);
    }

    private int validation2 (final AnnExList trainSet, final AnnExList valSet) {
        final NeuralNetwork net =
                new NeuralNetwork(rawAttr, annAttr.xList.size(), nHidden,
                        hiddenHasThres, annAttr.tList.size(), outHasThres,
                        learnRate, momentumRate);

        double minError = Double.POSITIVE_INFINITY;
        int iter = 0;
        int bestIter = 0;
        while (iter < MAX_ITER) {
            iter(net, trainSet, BASIC_ITER);
            iter += BASIC_ITER;

            final double error = net.predictError(valSet);
            if (Double.compare(minError, error) > 0) {
                minError = error;
                bestIter = iter;
            }
        }

        Dbg.print(DBG, MODULE, "Best iter: " + bestIter);
        return bestIter;
    }

    public AcSizeItTime reductionLearningWith3Fold (
            final Reducible reductionMethod) {
        final long trainStartTime = SysUtil.getCpuTime();

        final RawExampleList[] exArray =
                TrainTestSplitter.splitSetInto3FoldWithConsistentClassRatio(
                        rawTrainWithNoise, rawAttr);

        long eTime = 0;
        int sumIter = 0;
        for (int val = 0; val < exArray.length; val++) {
            final RawExampleList valSet = exArray[val];
            final RawExampleList trainSet = new RawExampleList();
            for (int other = 0; other < exArray.length; other++) {
                if (other != val) { // All other set is train set.
                    trainSet.addAll(exArray[other]);
                }
            }
            final long startTime = SysUtil.getCpuTime();
            final RawExampleList reducedTrain =
                    reductionMethod.reduce(trainSet, rawAttr);
            eTime += (SysUtil.getCpuTime() - startTime);

            final AnnExList annTrain = new AnnExList(reducedTrain, rawAttr);
            final AnnExList annVal = new AnnExList(valSet, rawAttr);
            sumIter += validation2(annTrain, annVal);
        }

        final long startTime = SysUtil.getCpuTime();
        final RawExampleList reducedTrain =
                reductionMethod.reduce(rawTrainWithNoise, rawAttr);
        eTime += (SysUtil.getCpuTime() - startTime);
        // Convert from nano second to second.
        final double editTime = eTime / 1000.0;

        final AnnExList annTrain = new AnnExList(reducedTrain, rawAttr);

        final int meanIter = sumIter / exArray.length;
        final NeuralNetwork net = iter(annTrain, meanIter);
        final double accur = evalTest(net);

        // Convert from nano second to second.
        final double trainTime =
                (SysUtil.getCpuTime() - trainStartTime) / 1000.0;
        return new AcSizeItTime(accur, reducedTrain.size(), meanIter, editTime,
                trainTime);
    }

    public static class AccurAndIter {
        public final double accur;
        public final int iter;

        public AccurAndIter(double accur, int iter) {
            this.accur = accur;
            this.iter = iter;
        }
    }

    public static class AcSizeItTime {
        public final double accur;
        public final int size;
        public final int iter;
        public final double editTime;
        public final double trainTime;

        public AcSizeItTime(double accur, int size, int iter, double editTime,
                double trainTime) {
            this.accur = accur;
            this.size = size;
            this.iter = iter;
            this.editTime = editTime;
            this.trainTime = trainTime;
        }
    }

    /* For instance reduction end ********************* */

    public double evalTrain (final NeuralNetwork net) {
        return Evaluator.evaluate(net, rawTrain);
    }

    public double evalTest (final NeuralNetwork net) {
        if (rawTest != null) {
            return Evaluator.evaluate(net, rawTest);
        } else {
            return 0;
        }
    }

    public void corruptTrainList (final double ratio) {
        rawTrainWithNoise = DataCorrupter.corrupt(rawTrain, rawAttr, ratio);
    }

    public void resetTrainList () {
        rawTrainWithNoise = rawTrain;
    }

    public NeuralNetwork iterLearning (final int maxIter) {
        final AnnExList annSet = new AnnExList(rawTrainWithNoise, rawAttr);
        return iter(annSet, maxIter);
    }

    private static final double VAL_RATIO = 0.667;

    public NeuralNetwork validationLearning () {
        return validationLearning(VAL_RATIO);
    }

    public NeuralNetwork validationLearning (double ratio) {
        final AnnExList annSet = new AnnExList(rawTrainWithNoise, rawAttr);

        final AnnExList[] exArray = annSet.splitIntoTwoSets(ratio);
        final AnnExList trainSet = exArray[0];
        final AnnExList valSet = exArray[1];

        final NetAndIter nai = validation(trainSet, valSet);

        return nai.net;
    }

    public NeuralNetwork kFoldLearning (final int k) {
        final AnnExList annSet = new AnnExList(rawTrainWithNoise, rawAttr);
        final AnnExList[] exArray = annSet.splitIntoMultiSets(k);
        if (exArray == null) {
            return null;
        }
        int sumIter = 0;
        for (int val = 0; val < exArray.length; val++) {
            final AnnExList valSet = exArray[val];
            final AnnExList trainSet = new AnnExList();
            for (int other = 0; other < exArray.length; other++) {
                if (other != val) { // All other set is train set.
                    trainSet.addAll(exArray[other]);
                }
            }
            final NetAndIter nai = validation(trainSet, valSet);
            sumIter += nai.iter;
        }

        final int meanIter = sumIter / exArray.length;
        Dbg.print(DBG, MODULE, "Mean of iter: " + meanIter);
        final NeuralNetwork net = iter(annSet, meanIter);
        return net;
    }

    private NeuralNetwork iter (final AnnExList trainSet, final int maxIter) {
        final NeuralNetwork net =
                new NeuralNetwork(rawAttr, annAttr.xList.size(), nHidden,
                        hiddenHasThres, annAttr.tList.size(), outHasThres,
                        learnRate, momentumRate);
        int iter = 0;
        while (iter < maxIter) {
            net.update(trainSet);
            iter++;
        }
        return net;
    }

    private static void iter (final NeuralNetwork net,
            final AnnExList trainSet, final int maxIter) {
        int iter = 0;
        while (iter < maxIter) {
            net.update(trainSet);
            iter++;
        }
    }

    private static class NetAndIter {
        public NeuralNetwork net;
        public int iter;
    }

    private static final int BASIC_ITER = 100;
    private static final int MULTI_ITER = 20;

    private NetAndIter validation (final AnnExList trainSet,
            final AnnExList valSet) {
        final NeuralNetwork net =
                new NeuralNetwork(rawAttr, annAttr.xList.size(), nHidden,
                        hiddenHasThres, annAttr.tList.size(), outHasThres,
                        learnRate, momentumRate);
        NeuralNetwork lastNet = new NeuralNetwork(net);
        NeuralNetwork bestNet = null;

        double minError = Double.POSITIVE_INFINITY;
        int stopIter = BASIC_ITER * MULTI_ITER;
        int iter = 0;
        int bestIter = 0;
        while (iter < stopIter && iter < MAX_ITER) {
            iter(net, trainSet, BASIC_ITER);
            iter += BASIC_ITER;

            final double error = net.predictError(valSet);
            if (Double.compare(minError, error) > 0) {
                minError = error;
                bestIter = iter;
                bestNet = new NeuralNetwork(net);
                // Update stop iteration number so it can look further for
                // another minimum.
                stopIter = iter + BASIC_ITER * MULTI_ITER;
            }

            if (net.hasConverged(lastNet)) {
                Dbg.print(DBG, MODULE, "Network converged at iter " + iter);
                break;
            } else {
                lastNet = new NeuralNetwork(net);
            }
        }
        if (iter == MAX_ITER || iter == stopIter) {
            Dbg.print(DBG, MODULE, "Stop at iter: " + iter);
        }

        Dbg.print(DBG, MODULE, "Best iter: " + bestIter);

        final NetAndIter nai = new NetAndIter();
        nai.net = bestNet;
        nai.iter = bestIter;
        return nai;
    }

    public LinkedHashMap<String, LinkedHashMap<Double, Double>>
            errorVersusWeightsLearning (final int maxIter) {
        final AnnExList annSet = new AnnExList(rawTrainWithNoise, rawAttr);

        final AnnExList[] exArray = annSet.splitIntoTwoSets(VAL_RATIO);
        final AnnExList trainSet = exArray[0];
        final AnnExList valSet = exArray[1];

        final NeuralNetwork net =
                new NeuralNetwork(rawAttr, annAttr.xList.size(), nHidden,
                        hiddenHasThres, annAttr.tList.size(), outHasThres,
                        learnRate, momentumRate);
        NeuralNetwork lastNet = new NeuralNetwork(net);

        final LinkedHashMap<Double, Double> trainMap =
                new LinkedHashMap<Double, Double>();

        final LinkedHashMap<Double, Double> valMap =
                new LinkedHashMap<Double, Double>();

        double minError = Double.POSITIVE_INFINITY;
        int iter = 0;
        int bestIter = 0;

        while (iter < maxIter) { // Even converge don't quit.
            iter(net, trainSet, BASIC_ITER);
            iter += BASIC_ITER;

            double error = net.predictError(trainSet);
            trainMap.put((double) iter, error);
            error = net.predictError(valSet);
            valMap.put((double) iter, error);
            if (Double.compare(minError, error) > 0) {
                minError = error;
                bestIter = iter;
            }

            if (net.hasConverged(lastNet)) {
                Dbg.print(DBG, MODULE, "Network converged at iter: " + iter);
            }
            lastNet = new NeuralNetwork(net);
        }

        Dbg.print(DBG, MODULE, "Best iter: " + bestIter);

        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put("Train set error", trainMap);
        dataSet.put("Validation set error", valMap);
        return dataSet;
    }

    public LinkedHashMap<String, LinkedHashMap<Double, Double>>
            averErrorOfOutput (final AnnExList trainSet,
                    final AnnExList valSet, final int maxIter) {
        NeuralNetwork net =
                new NeuralNetwork(rawAttr, annAttr.xList.size(), nHidden,
                        hiddenHasThres, annAttr.tList.size(), outHasThres,
                        learnRate, momentumRate);
        NeuralNetwork lastNet = new NeuralNetwork(net);

        final LinkedHashMap<Double, Double> trainMap =
                new LinkedHashMap<Double, Double>();

        final LinkedHashMap<Double, Double> valMap =
                new LinkedHashMap<Double, Double>();

        double minError = Double.POSITIVE_INFINITY;
        int iter = 0;
        int bestIter = 0;
        boolean converged = false;
        while (iter < maxIter) {
            iter(net, trainSet, BASIC_ITER);
            iter += BASIC_ITER;

            double error = net.predictError(trainSet);
            trainMap.put((double) iter, error);
            error = net.predictError(valSet);
            valMap.put((double) iter, error);
            if (Double.compare(minError, error) > 0) {
                minError = error;
                bestIter = iter;
            }

            if (!converged && net.hasConverged(lastNet)) {
                System.out.println("Network converged at iter: " + iter);
                converged = true;
            } else {
                lastNet = new NeuralNetwork(net);
            }
        }

        Dbg.print(DBG, MODULE, "Best iter: " + bestIter);

        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put("Train set error", trainMap);
        dataSet.put("Validation set error", valMap);
        return dataSet;
    }

    public LinkedHashMap<String, LinkedHashMap<Double, Double>>
            errForEachOutput (final int maxIter) {
        final NeuralNetwork net =
                new NeuralNetwork(rawAttr, annAttr.xList.size(), nHidden,
                        hiddenHasThres, annAttr.tList.size(), outHasThres,
                        learnRate, momentumRate);

        final AnnExList train = new AnnExList(rawTrainWithNoise, rawAttr);

        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();

        final Layer outlayer = net.getLayerOut();
        for (int i = 0; i < outlayer.size(); i++) {
            final LinkedHashMap<Double, Double> unitMap =
                    new LinkedHashMap<Double, Double>();
            dataSet.put("Out " + String.valueOf(i), unitMap);
        }
        int iter = 0;
        while (iter < maxIter) {
            iter(net, train, BASIC_ITER);
            iter += BASIC_ITER;

            for (AnnExample ex : train) {
                final ArrayList<Double> predictOut = net.getV(ex.xList);
                for (int i = 0; i < predictOut.size(); i++) {
                    final String outUnitName = "Out " + String.valueOf(i);
                    final LinkedHashMap<Double, Double> unitMap =
                            dataSet.get(outUnitName);
                    Double error = unitMap.get((double) iter);
                    if (error == null) {
                        error = 0.0;
                    }
                    error +=
                            NeuralNetwork.oneSquareError(ex.tList, predictOut,
                                    i);
                    unitMap.put((double) iter, error);
                }
            }
            // Make average.
            for (int i = 0; i < outlayer.units.size(); i++) {
                final String outUnitName = "Out " + String.valueOf(i);
                final LinkedHashMap<Double, Double> unitMap =
                        dataSet.get(outUnitName);
                Double error = unitMap.get((double) iter);
                if (error == null) {
                    error = 0.0;
                }
                error = error / train.size();
                unitMap.put((double) iter, error);
            }
        }

        return dataSet;
    }

    public LinkedHashMap<String, LinkedHashMap<Double, Double>>
            outputForHidden (final int maxIter) {
        final NeuralNetwork net =
                new NeuralNetwork(rawAttr, annAttr.xList.size(), nHidden,
                        hiddenHasThres, annAttr.tList.size(), outHasThres,
                        learnRate, momentumRate);

        final AnnExList exSet = new AnnExList(rawTrainWithNoise, rawAttr);

        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();

        final Layer hLayer = net.getLayer(0);
        for (int i = 0; i < hLayer.size(); i++) {
            final LinkedHashMap<Double, Double> unitMap =
                    new LinkedHashMap<Double, Double>();
            dataSet.put("Unit " + String.valueOf(i), unitMap);
        }
        int iter = 0;
        while (iter < maxIter) {
            iter(net, exSet, BASIC_ITER);
            iter += BASIC_ITER;
            // The 2nd example
            final AnnExample ex = exSet.get(1);
            net.getV(ex.xList);
            for (int i = 0; i < hLayer.size(); i++) {
                final Unit hUnit = hLayer.units.get(i);
                final String outUnitName = "Unit " + String.valueOf(i);
                final LinkedHashMap<Double, Double> unitMap =
                        dataSet.get(outUnitName);
                unitMap.put((double) iter, hUnit.value);
            }
        }

        return dataSet;
    }

    public LinkedHashMap<String, LinkedHashMap<Double, Double>>
            weightsForOneHidden (final int maxIter) {
        final NeuralNetwork net =
                new NeuralNetwork(rawAttr, annAttr.xList.size(), nHidden,
                        hiddenHasThres, annAttr.tList.size(), outHasThres,
                        learnRate, momentumRate);

        final AnnExList exSet = new AnnExList(rawTrainWithNoise, rawAttr);

        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();

        final Layer hLayer = net.getLayer(0);
        final Unit u0 = hLayer.units.get(0);
        for (int i = 0; i < u0.weights.size(); i++) {
            final LinkedHashMap<Double, Double> unitMap =
                    new LinkedHashMap<Double, Double>();
            dataSet.put("W" + String.valueOf(i), unitMap);
        }
        int iter = 0;
        while (iter < maxIter) {
            iter(net, exSet, BASIC_ITER);
            iter += BASIC_ITER;

            for (int i = 0; i < u0.weights.size(); i++) {
                final double weight = u0.weights.get(i);
                final String outUnitName = "W" + String.valueOf(i);
                final LinkedHashMap<Double, Double> unitMap =
                        dataSet.get(outUnitName);
                unitMap.put((double) iter, weight);
            }
        }

        return dataSet;
    }
}
