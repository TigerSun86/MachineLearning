package artificialNeuralNetworks.ANN;

import java.awt.geom.Point2D;

import common.DataGenerator;
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
    private static final double[] BP = { 0, 1 / 3.0, 2 / 3.0, 1 };
    private static final double[] BN = { 0, -1 / 3.0, -2 / 3.0, -1 };
    // 0, class 1 region. 1, class 2 region.
    private static final Ribbon[] rBorder = { new Ribbon(K, BN[0], BN[1]),
            new Ribbon(K, BP[0], BP[1]) };
    private static final Ribbon[] rCenter = { new Ribbon(K, BN[1], BN[2]),
            new Ribbon(K, BP[1], BP[2]) };
    private static final Ribbon[] rFar = { new Ribbon(K, BN[2], BN[3]),
            new Ribbon(K, BP[2], BP[3]) };

    private static final String TRAIN_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/toy-train.txt";
    private static final String TEST_FILE_URL =
            "http://my.fit.edu/~sunx2013/MachineLearning/toy-test.txt";
    public static void main (String[] args) {
/*        final Ribbon rib = new Ribbon(K, B1, B2);
        System.out.println(rib.isInside(new Point2D.Double(0.4, 0.01)));
        System.out.println(rFar[0]);
        System.out.println(rFar[1]);
        System.out.println(rBorder[0].isInside(new Point2D.Double(0.0,
                -0.000001)));*/
        final RawExampleList train = new RawExampleList(TRAIN_FILE_URL);
        final RawExampleList test = new RawExampleList(TEST_FILE_URL);
        test(train, test);
    }

    public static void test (RawExampleList train, RawExampleList test) {
        // 1st dimension. 0, class 1 region. 1, class 2 region.
        // 2nd dimension. 0, border, 1 center, 2 far.
        final RawExampleList[][] exReg = new RawExampleList[2][3];
        for (int i = 0; i < exReg.length; i++) {
            exReg[i] = new RawExampleList[3];
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
            if (rBorder[classIndex].isInside(p)) {
                exReg[classIndex][0].add(e);
            } else if (rCenter[classIndex].isInside(p)) {
                exReg[classIndex][1].add(e);
            } else {
                exReg[classIndex][2].add(e);
            }
        }
        testRegion(exReg[0][1],exReg[1][1]);
        for (int i = 0; i < exReg.length; i++) {
            for (int j = 0; j < exReg[i].length; j++) {
                System.out.println(i+", "+j+" size "+ exReg[i][j].size());
                System.out.println(exReg[i][j]);
            }
        }
    }

    private static void testRegion (RawExampleList rawExampleList,
            RawExampleList rawExampleList2) {
        // TODO Auto-generated method stub
        
    }
}
