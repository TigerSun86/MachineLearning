package artificialNeuralNetworks.ANN;

import java.awt.geom.Point2D;

import artificialNeuralNetworks.ANN.AnnLearner.AccurAndIter;
import common.DataGenerator;
import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;
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
    private static final double[] BP;
    static {
        final int count = 10;
        BP = new double[count+1];
        for (int i = 0; i < count + 1; i++){
            BP[i] = i / (double)count;
        }
    }
    private static final double[] BN;
    static {
        final int count = 10;
        BN = new double[count+1];
        for (int i = 0; i < count + 1; i++){
            BN[i] = -i / (double)count;
        }
    }
    // 1st dimension. different ribbon area.
    // 2nd dimension. 0, class 1 region. 1, class 2 region.
    private static Ribbon[][] regs = {
            { new Ribbon(K, BN[0], BN[1]), new Ribbon(K, BP[0], BP[1]) },
            { new Ribbon(K, BN[1], BN[2]), new Ribbon(K, BP[1], BP[2]) },
            { new Ribbon(K, BN[2], BN[3]), new Ribbon(K, BP[2], BP[3]) } };
    static {
        regs = new Ribbon[BP.length - 1][2];
        for (int i = 0; i < BP.length - 1; i++) {
            regs[i] = new Ribbon[2];
            regs[i][0] = new Ribbon(K, BN[i], BN[i + 1]);
            regs[i][1] = new Ribbon(K, BP[i], BP[i + 1]);
        }
    }

    private static final String ATTR_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/toy-attr.txt";
    private static final String TRAIN_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/toy-train.txt";
    private static final String TEST_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/toy-test.txt";

    private static final RawAttrList RATTR = new RawAttrList(ATTR_FILE_URL);

    public static void main (String[] args) {
        final RawExampleList train = new RawExampleList(TRAIN_FILE_URL);
        final RawExampleList test = new RawExampleList(TEST_FILE_URL);
        test(train, test);
    }

    public static void test (RawExampleList train, RawExampleList test) {
        // 1st dimension. different ribbon area.
        // 2nd dimension. 0, class 1 region. 1, class 2 region.
        final RawExampleList[][] exReg = new RawExampleList[regs.length][2];
        for (int i = 0; i < exReg.length; i++) {
            exReg[i] = new RawExampleList[2];
            for (int j = 0; j < exReg[i].length; j++) {
                exReg[i][j] = new RawExampleList();
            }
        }

        // Divide instances into different regions.
        for (RawExample e : train) {
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
        for (int i = 0; i < exReg.length; i++) {
            for (int j = 0; j < exReg[i].length; j++) {
                System.out
                        .println("Region "+i + ", class " + j + " size " + exReg[i][j].size());
                System.out.println(exReg[i][j]);
            }
        }
        for (int i = 0; i < exReg.length; i++) {
            double[] accurAndIter = testRegion(exReg[i][0], exReg[i][1], test);
            System.out.println("Region " + i);
            System.out.println("Accuracy " + accurAndIter[0] + " Iteration "
                    + accurAndIter[1]);
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
}
