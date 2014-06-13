package artificialNeuralNetworks.ANN;

import java.util.ArrayList;

import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName: BorderOrCenter.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 12, 2014 1:43:36 PM
 */
public class BorderOrCenter {
    public RawAttrList rawAttr;
    public AnnAttrList annAttr; // For nIn and nOut.
    public RawExampleList rawTrain;
    public RawExampleList rawTest;

    public RawExampleList rawTrainWithNoise;

    public ArrayList<Integer> nHidden;
    public boolean hiddenHasThres;
    public boolean outHasThres;
    public double learnRate;
    public double momentumRate;
    public String attrFName =
            "http://my.fit.edu/~sunx2013/MachineLearning/boc-attr.txt";
    public String trainFNme =
            "http://my.fit.edu/~sunx2013/MachineLearning/boc-train51-49.txt";
    public String testFName = null;

    private NeuralNetwork iter () {
        rawAttr = new RawAttrList(attrFName);
        rawTrain = new RawExampleList(trainFNme);

        if (testFName != null) {
            rawTest = new RawExampleList(testFName);
        } else {
            rawTest = null;
        }

        rawTrainWithNoise = rawTrain;

        annAttr = new AnnAttrList(rawTrain, rawAttr);
        
        nHidden = new ArrayList<Integer>();
        hiddenHasThres = true;
        outHasThres = true;
        learnRate = 0.1;
        momentumRate = 0;

        final AnnExample ex1 = new AnnExample();
        ex1.xList.add(0.75);
        ex1.xList.add(0.25);
        ex1.tList.add(FloatConverter.HIGH_VALUE);
        final AnnExample ex2 = new AnnExample();
        ex2.xList.add(0.25);
        ex2.xList.add(0.75);
        ex2.tList.add(FloatConverter.LOW_VALUE);
        final AnnExList trainSet = new AnnExList();
        trainSet.add(ex1);
        trainSet.add(ex2);
        
        final double x1 = trainSet.get(0).xList.get(0);
        final double x2 = trainSet.get(0).xList.get(1);
        final double d = Math.abs(x1-x2) / Math.sqrt(2.0);
        System.out.printf("distance %.3f x1 %.2f x2 %.2f%n", d, x1, x2);
        
        final NeuralNetwork net =
                new NeuralNetwork(annAttr, annAttr.xList.size(), nHidden,
                        hiddenHasThres, annAttr.tList.size(), outHasThres,
                        learnRate, momentumRate);
        System.out.println("iterations w0 w1 w2");
        int iter = 0;
        while (iter < 10000) {
            net.update(trainSet);
            iter++;
            if (iter % 1000 == 0){
                Unit u = net.layers.get(0).units.get(0);
                final double w0 = u.weights.get(0);
                final double w1 = u.weights.get(1);
                final double w2 = u.weights.get(2);
                System.out.printf("%d %.4f %.4f %.4f%n",iter, w0, w1,w2);
            }
        }
        return net;
    }
    
    private void iter2 () {
        rawAttr = new RawAttrList(attrFName);
        rawTrain = new RawExampleList(trainFNme);

        if (testFName != null) {
            rawTest = new RawExampleList(testFName);
        } else {
            rawTest = null;
        }

        rawTrainWithNoise = rawTrain;

        annAttr = new AnnAttrList(rawTrain, rawAttr);

        nHidden = new ArrayList<Integer>();
        hiddenHasThres = true;
        outHasThres = true;
        learnRate = 0.1;
        momentumRate = 0;
        
        final AnnExample ex1 = new AnnExample();
        ex1.xList.add(0.51);
        ex1.xList.add(0.49);
        ex1.tList.add(FloatConverter.HIGH_VALUE);
        final AnnExample ex2 = new AnnExample();
        ex2.xList.add(0.49);
        ex2.xList.add(0.51);
        ex2.tList.add(FloatConverter.LOW_VALUE);
        final AnnExList trainSet = new AnnExList();
        trainSet.add(ex1);
        trainSet.add(ex2);
        
        System.out.println("distance x1 x2 w0 w1 w2");
        for (int i = 0; i < 49; i++){
            
            final NeuralNetwork net =
                    new NeuralNetwork(annAttr, annAttr.xList.size(), nHidden,
                            hiddenHasThres, annAttr.tList.size(), outHasThres,
                            learnRate, momentumRate);
            int iter = 0;
            while (iter < 10000) {
                net.update(trainSet);
                iter++;
            }
            final double x1 = trainSet.get(0).xList.get(0);
            final double x2 = trainSet.get(0).xList.get(1);
            final double d = Math.abs(x1-x2) / Math.sqrt(2.0);
            System.out.printf("%.3f %.2f %.2f ", d, x1, x2);
            Unit u = net.layers.get(0).units.get(0);
            final double w0 = u.weights.get(0);
            final double w1 = u.weights.get(1);
            final double w2 = u.weights.get(2);
            System.out.printf("%.4f %.4f %.4f%n",w0, w1,w2);
            
            changeData(trainSet, 0.01);            
        }

    }
    private static void changeData(AnnExList trainSet, final double delta){
        AnnExample ex1 = trainSet.get(0);
        AnnExample ex2 = trainSet.get(1);
        ex1.xList.set(0, ex1.xList.get(0) + delta);
        ex1.xList.set(1, ex1.xList.get(1) - delta);
        ex2.xList.set(0, ex2.xList.get(0) - delta);
        ex2.xList.set(1, ex2.xList.get(1) + delta);
    }
    
    private void iter3 () {
        rawAttr = new RawAttrList(attrFName);
        rawTrain = new RawExampleList(trainFNme);

        if (testFName != null) {
            rawTest = new RawExampleList(testFName);
        } else {
            rawTest = null;
        }

        rawTrainWithNoise = rawTrain;

        annAttr = new AnnAttrList(rawTrain, rawAttr);

        
        nHidden = new ArrayList<Integer>();
        hiddenHasThres = true;
        outHasThres = true;
        learnRate = 0.1;
        momentumRate = 0;
        

        final AnnExample ex1 = new AnnExample();
        ex1.xList.add(0.51);
        ex1.xList.add(0.01);
        ex1.tList.add(FloatConverter.HIGH_VALUE);
        final AnnExample ex2 = new AnnExample();
        ex2.xList.add(0.01);
        ex2.xList.add(0.51);
        ex2.tList.add(FloatConverter.LOW_VALUE);
        final AnnExList trainSet = new AnnExList();
        trainSet.add(ex1);
        trainSet.add(ex2);
        System.out.println("distance x1 x2 w0 w1 w2");
        for (int i = 0; i < 49; i++){
           
            final NeuralNetwork net =
                    new NeuralNetwork(annAttr, annAttr.xList.size(), nHidden,
                            hiddenHasThres, annAttr.tList.size(), outHasThres,
                            learnRate, momentumRate);
            int iter = 0;
            while (iter < 10000) {
                net.update(trainSet);
                iter++;
            }
            
            final double x1 = trainSet.get(0).xList.get(0);
            final double x2 = trainSet.get(0).xList.get(1);
            final double d = Math.abs(x1-x2) / Math.sqrt(2.0);
            System.out.printf("%.3f %.2f %.2f ", d, x1, x2);
            Unit u = net.layers.get(0).units.get(0);
            final double w0 = u.weights.get(0);
            final double w1 = u.weights.get(1);
            final double w2 = u.weights.get(2);
            System.out.printf("%.4f %.4f %.4f%n",w0, w1,w2);
            
            changeData2(trainSet, 0.01);            
        }

    }
    private static void changeData2(AnnExList trainSet, final double delta){
        AnnExample ex1 = trainSet.get(0);
        AnnExample ex2 = trainSet.get(1);
        ex1.xList.set(0, ex1.xList.get(0) + delta);
        ex1.xList.set(1, ex1.xList.get(1) + delta);
        ex2.xList.set(0, ex2.xList.get(0) + delta);
        ex2.xList.set(1, ex2.xList.get(1) + delta);
    }
    
    public static void main(String[] args){
        new BorderOrCenter().iter();
    }
}
