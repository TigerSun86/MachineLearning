package lerad;

import java.util.Scanner;

import util.Dbg;

import common.DataCorrupter;
import common.Evaluator;
import common.Hypothesis;
import common.RawAttrList;
import common.RawExampleList;

import dataset.DataSet;
import dataset.Iris;
import dataset.LERAD_Toy;
import dataset.LERAD_ids;

/**
 * FileName:     Test.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 23, 2014 9:43:37 PM 
 */
public class Test {
    private static final DataSet[] DATA_SOURCE = { new LERAD_Toy(),
        new LERAD_ids(), new Iris() };
    public static void main(String[] args){
        simpleTest(1, null);
    }
    private static void simpleTest (final int dsetindex, final Scanner sc) {
        Dbg.dbgSwitch = true;
        Dbg.defaultSwitch = true;
        // Read data from files.
        final RawAttrList rawAttr =
                new RawAttrList(DATA_SOURCE[dsetindex].getAttrFileUrl());
        final RawExampleList rawTrain =
                new RawExampleList(DATA_SOURCE[dsetindex].getTrainFileUrl());
        final RawExampleList rawTest =
                new RawExampleList(DATA_SOURCE[dsetindex].getTestFileUrl());
/*        System.out
                .println("Please input the noise rate, need pruning or not, and k. "
                        + "Eg: \"0.2 1 1\" means 0.2 noise rate, need pruning, and k = 1.");*/
        final double noiseRate = 0;
        /*final boolean needPruning = (sc.nextInt() == 1);*/
/*        final int k = sc.nextInt();
        System.out
                .printf("Data set is %s, noise rate is %.2f, needPruning = %s k = %d%n",
                        DATA_SOURCE[dsetindex].getName(), noiseRate,
                        Boolean.toString(needPruning), k);*/
        
        final RawExampleList noiseTrain =
                DataCorrupter.corrupt(rawTrain, rawAttr, noiseRate);
        final Hypothesis h = LERAD.learn(noiseTrain, rawAttr);
        LERAD.predict(rawTest, rawAttr, (RuleList)h);
        System.out.println("Learnt hypothesis: ");
        System.out.println(h);
        double accur = Evaluator.evaluate(h, noiseTrain);
        //System.out.println("train" + accur);
        accur = Evaluator.evaluate(h, rawTest);
        // Get accuracy of predictor on test set.
       // System.out.println("test" + accur);
        Dbg.dbgSwitch = false;
        Dbg.defaultSwitch = false;
    }
}
