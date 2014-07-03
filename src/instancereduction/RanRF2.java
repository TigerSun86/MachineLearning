package instancereduction;

import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName:     RanRF2.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jul 3, 2014 1:03:00 AM 
 */
public class RanRF2  implements Reducible {
    private static final double R = 0.5;

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        RawExampleList exs2 = new PureRF2().reduce(exs, attrs);
        final int num =(int)Math.round( exs.size() * R);
        if (exs2.size() > num) {
            exs2 = new RanR(((double)num)/exs2.size()).reduce(exs2, attrs);
        }
        return exs2;
    }

}