package instancereduction;

import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName: RCI.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 25, 2014 10:10:49 PM
 */
public class RCI implements Reducible {
    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        final RawExampleList exs2 = new ENN().reduce(exs, attrs);
        return PureRCI.reduce(exs2, attrs);
    }
}
