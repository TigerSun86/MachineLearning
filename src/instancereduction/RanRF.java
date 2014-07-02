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
        RawExampleList exs2 = new PureRF(0).reduce(exs, attrs);
        final int num =(int)Math.round( exs.size() * R);
        if (exs2.size() > num) {
            exs2 = new RanR(((double)num)/exs2.size()).reduce(exs2, attrs);
        }
        return exs2;
    }

}
