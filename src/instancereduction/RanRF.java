package instancereduction;

import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName: RanRF.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jul 1, 2014 11:49:54 PM
 */
public class RanRF implements Reducible {
    private static final double R = 0.5;

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        RawExampleList exs2 = new PureRF().reduce(exs, attrs);
        if (Double.compare(((double) exs2.size()) / exs.size(), R) > 0) {
            exs2 = new RanR(R).reduce(exs2, attrs);
        }
        return exs2;
    }

}
