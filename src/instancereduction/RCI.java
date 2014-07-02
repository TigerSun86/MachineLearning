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
    private final int k;

    public RCI() {
        this.k = PureRCI.DEF_K;
    }

    public RCI(final int k) {
        this.k = k;
    }

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        final RawExampleList exs2 = new ENN().reduce(exs, attrs);
        return new PureRCI(k).reduce(exs2, attrs);
    }
}
