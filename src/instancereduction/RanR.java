package instancereduction;

import common.RawAttrList;
import common.RawExampleList;
import common.TrainTestSplitter;

/**
 * FileName: RanR.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 25, 2014 10:40:42 PM
 */
public class RanR implements Reducible {
    private static final double RATIO_KEEPING = 0.5;

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        // Guarantee each class getting the same RATIO_KEEPING.
        final RawExampleList[] s =
                TrainTestSplitter.splitSetWithConsistentClassRatio(exs, attrs,
                        RATIO_KEEPING);
        return s[0];
    }
}
