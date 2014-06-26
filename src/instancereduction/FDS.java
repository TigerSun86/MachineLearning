package instancereduction;

import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName:     FDS.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 25, 2014 10:22:11 PM 
 */
public class FDS implements Reducible {

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        return exs;
    }
}
