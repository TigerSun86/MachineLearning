package artificialNeuralNetworks.ANN;

import instancereduction.FCNN;

import java.awt.geom.Point2D;
import java.util.Collections;

import util.MyMath;
import artificialNeuralNetworks.ANN.AnnLearner.AccurAndIter;

import common.DataGenerator;
import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;
import common.Region;
import common.Region.Ribbon;

/**
 * FileName: BorderOrCenter2.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Jun 17, 2014 4:20:13 PM
 */
public class BorderOrCenter2 {
    private static final double K = 1.0;
    // 0, class 1. 1, class 2.
    private static final double[][] B_FOR_CIRCLE;
    static {
        final int count = 10;
        B_FOR_CIRCLE = new double[2][count + 1];
        B_FOR_CIRCLE[0] = new double[count + 1];
        B_FOR_CIRCLE[1] = new double[count + 1];
        for (int i = 0; i < count + 1; i++) {
            B_FOR_CIRCLE[0][i] = -i / (double) count;
            B_FOR_CIRCLE[1][i] = i / (double) count;
        }
    }

    // 1st dimension. different ribbon area.
    // 2nd dimension. 0, class 1 region. 1, class 2 region.
    private static final Ribbon[][] regsForCircleTest;
    static {
        regsForCircleTest = new Ribbon[B_FOR_CIRCLE[0].length - 1][2];
        for (int i = 0; i < B_FOR_CIRCLE[0].length - 1; i++) {
            regsForCircleTest[i] = new Ribbon[2];
            regsForCircleTest[i][0] =
                    new Ribbon(K, B_FOR_CIRCLE[0][i], B_FOR_CIRCLE[0][i + 1]);
            regsForCircleTest[i][1] =
                    new Ribbon(K, B_FOR_CIRCLE[1][i], B_FOR_CIRCLE[1][i + 1]);
        }
    }

    // 0, class 1. 1, class 2.
    private static final double[][] B_FOR_RECT;
    static {
        final int count = 3;
        B_FOR_RECT = new double[2][count + 1];
        B_FOR_RECT[0] = new double[count + 1];
        B_FOR_RECT[1] = new double[count + 1];
        for (int i = 0; i < count + 1; i++) {
            B_FOR_RECT[0][i] = 0.75 * (-i / (double) count);
            B_FOR_RECT[1][i] = 0.75 * (i / (double) count);
        }
    }
    // 1st dimension. different ribbon area.
    // 2nd dimension. 0, class 1 region. 1, class 2 region.
    private static final Ribbon[][] regsForRectTest;
    static {
        regsForRectTest = new Ribbon[B_FOR_RECT[0].length - 1][2];
        for (int i = 0; i < B_FOR_RECT[0].length - 1; i++) {
            regsForRectTest[i] = new Ribbon[2];
            regsForRectTest[i][0] =
                    new Ribbon(K, B_FOR_RECT[0][i], B_FOR_RECT[0][i + 1]);
            regsForRectTest[i][1] =
                    new Ribbon(K, B_FOR_RECT[1][i], B_FOR_RECT[1][i + 1]);
        }
    }

    private static final String ATTR_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/toy-attr.txt";
    private static final String TRAIN_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/toyRect-train.txt";
    private static final String TEST_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/toyRect-test.txt";

    private static final RawAttrList RATTR = new RawAttrList(ATTR_FILE_URL);

    public static void main (String[] args) {
        final RawExampleList train = new RawExampleList(TRAIN_FILE_URL);
        final RawExampleList test = new RawExampleList(TEST_FILE_URL);
        /* testMultiPoints(train, test, regsForRectTest); */
        
        RawAttrList rawAttr = new RawAttrList(ATTR_FILE_URL);
        final FCNN red = new FCNN();

        final RawExampleList s = red.reduce(train, rawAttr);
        System.out.println(s);
    }

    private static void test1PairPoints (RawExampleList train,
            RawExampleList test, Region[][] regs) {
        final RawExampleList[][] exReg = splitSetByRegions(train, regs);

        printExReg(exReg);

        System.out.println("Region Accuracy Iteration");
        for (int i = 0; i < exReg.length; i++) {
            double[] accurAndIter = testRegion(exReg[i][0], exReg[i][1], test);
            System.out.println((i + 1) + " " + accurAndIter[0] + " "
                    + accurAndIter[1]);
        }
    }

    private static final int PAIR = 10;
    private static final int TIMES = 10;

    private static void testMultiPoints (RawExampleList train,
            RawExampleList test, Region[][] regs) {
        final RawExampleList[][] exReg = splitSetByRegions(train, regs);

        printExReg(exReg);
        System.out.println("Pairs " + PAIR + " Times " + TIMES);
        System.out.println("Region Accuracy Iteration");
        for (int i = 0; i < exReg.length; i++) {
            double[] accurAndIter =
                    testRegionByMultiPairs(exReg[i][0], exReg[i][1], test,
                            PAIR, TIMES);
            System.out.println((i + 1) + " " + accurAndIter[0] + " "
                    + accurAndIter[1]);
        }
    }

    private static RawExampleList[][] splitSetByRegions (RawExampleList s,
            Region[][] regs) {
        // 1st dimension. different areas.
        // 2nd dimension. 0, class 1 region. 1, class 2 region.
        final RawExampleList[][] exReg = new RawExampleList[regs.length][2];
        for (int i = 0; i < exReg.length; i++) {
            exReg[i] = new RawExampleList[2];
            for (int j = 0; j < exReg[i].length; j++) {
                exReg[i][j] = new RawExampleList();
            }
        }

        // Divide instances into different regions.
        for (RawExample e : s) {
            final int classIndex;
            if (e.t.equals(DataGenerator.CLASS[0])) {
                classIndex = 0;
            } else {
                classIndex = 1;
            }
            final Point2D.Double p =
                    new Point2D.Double(Double.parseDouble(e.xList.get(0)),
                            Double.parseDouble(e.xList.get(1)));
            for (int i = 0; i < regs.length; i++) {
                if (regs[i][classIndex].isInside(p)) {
                    exReg[i][classIndex].add(e);
                    break;
                }
            }
        }
        return exReg;
    }

    private static void printExReg (final RawExampleList[][] exReg) {
        for (int i = 0; i < exReg.length; i++) {
            for (int j = 0; j < exReg[i].length; j++) {
                System.out.println("Region " + (i + 1) + ", class " + (j + 1)
                        + " size " + exReg[i][j].size());
                System.out.println(exReg[i][j]);
            }
        }
    }

    private static double[] testRegion (RawExampleList class1Set,
            RawExampleList class2Set, RawExampleList test) {
        // No hidden nodes
        final AnnLearner annLearner = new AnnLearner(RATTR, 0.1, 0.1);
        annLearner.annAttr = new AnnAttrList(test, RATTR);
        annLearner.setRawTest(test);

        final double[] accurAndIter = new double[2];
        for (RawExample e1 : class1Set) {
            for (RawExample e2 : class2Set) {
                final RawExampleList trainSet = new RawExampleList();
                trainSet.add(e1);
                trainSet.add(e2);

                // Set data set for ANN learning.
                annLearner.setRawTrainWithNoise(trainSet);
                final AccurAndIter aai = annLearner.learnUntilConverge();
                accurAndIter[0] += aai.accur;
                accurAndIter[1] += aai.iter;
            }
        }
        final int count = class1Set.size() * class2Set.size();
        accurAndIter[0] /= count;
        accurAndIter[1] /= count;
        return accurAndIter;
    }

    private static double[] testRegionByMultiPairs (RawExampleList class1Set,
            RawExampleList class2Set, RawExampleList test, int numOfPairs,
            int times) {
        // No hidden nodes
        final AnnLearner annLearner = new AnnLearner(RATTR, 0.1, 0.1);
        annLearner.annAttr = new AnnAttrList(test, RATTR);
        annLearner.setRawTest(test);

        final double[] accurAndIter = new double[2];
        for (int t = 0; t < times; t++) {
            final RawExampleList trainSet = new RawExampleList();
            int[] selected = MyMath.mOutofN(numOfPairs, class1Set.size());
            for (int i : selected) {
                trainSet.add(class1Set.get(i));
            }
            selected = MyMath.mOutofN(numOfPairs, class2Set.size());
            for (int i : selected) {
                trainSet.add(class2Set.get(i));
            }
            Collections.shuffle(trainSet);

            // Set data set for ANN learning.
            annLearner.setRawTrainWithNoise(trainSet);
            final AccurAndIter aai = annLearner.learnUntilConverge();

            accurAndIter[0] += aai.accur;
            accurAndIter[1] += aai.iter;
        }

        accurAndIter[0] /= times;
        accurAndIter[1] /= times;

        return accurAndIter;
    }
}
