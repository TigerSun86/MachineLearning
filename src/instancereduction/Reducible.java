package instancereduction;

import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName:     Reducible.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 15, 2014 3:10:25 PM 
 */
public interface Reducible {
    public RawExampleList reduce (final RawExampleList exs,
            final RawAttrList attrs);
}
