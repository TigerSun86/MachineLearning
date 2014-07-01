package instancereduction;

import java.util.BitSet;

import common.PureRF;
import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName:     RF.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 30, 2014 10:41:01 PM 
 */
public class RF implements Reducible {

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        final BitSet keptByENN = ENN.reduceByEnn(exs, attrs);
        final BitSet keptByRF = PureRF.reduceFar(exs, attrs);
        final RawExampleList ret = new RawExampleList();
        for (int i = 0; i < exs.size(); i++) {
            if (keptByENN.get(i)&&keptByRF.get(i)) {
                ret.add(exs.get(i));
            }
        }
        return ret;
    }

}
