package instancereduction;

import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName:     RDIRan.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Jul 4, 2014 2:49:40 PM
 */
public class RDIRan implements Reducible {
    private static final double R = 0.5;

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        RawExampleList exs2 = new PureRCI().reduce(exs, attrs);
        final int num =(int)Math.round( exs.size() * R);
        if (exs2.size() > num) {
            exs2 = new RanR(((double)num)/exs2.size()).reduce(exs2, attrs);
        }
        return exs2;
    }

}
