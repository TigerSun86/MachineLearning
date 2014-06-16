package instancereduction;

import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName:     RPOCNN.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 15, 2014 3:11:51 PM 
 */
public class RPOCNN implements Reducible {

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        return POCNN.rPocNN(exs, attrs);
    }
}
