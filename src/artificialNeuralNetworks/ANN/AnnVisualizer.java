package artificialNeuralNetworks.ANN;

import common.DataGenerator;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName: AnnVisualizer.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 23, 2014 8:42:26 PM
 */
public class AnnVisualizer {
    private static final int C = 40;

    public static void show (NeuralNetwork nn) {
        final RawExampleList s = new RawExampleList();
        for (int i = 0; i < C; i++) {
            for (int j = 0; j < C; j++) {
                final RawExample ex = new RawExample();
                final String x1 = String.valueOf(((double) i) / C);
                final String x2 = String.valueOf(((double) j) / C);
                ex.xList.add(x1);
                ex.xList.add(x2);
                final String predict = nn.predict(ex.xList);
                if (predict.equals(DataGenerator.CLASS[0])) {
                    ex.t = predict;
                    s.add(ex);
                }
            }
        }
        System.out.println(s);
    }

    public static void show (NeuralNetwork nn, RawExampleList test) {
        final RawExampleList s = new RawExampleList();
        for (RawExample e : test) {
            final RawExample ex = new RawExample();
            ex.xList.add(e.xList.get(0));
            ex.xList.add(e.xList.get(1));
            final String predict = nn.predict(e.xList);
            ex.t = predict;
            s.add(ex);
        }
        System.out.println(s);
    }
}
