package instancereduction;

import java.util.BitSet;

import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName:     RevENNRDFI.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jul 9, 2014 9:26:37 PM 
 */
public class RevENNRDFI implements Reducible {

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        final BitSet keptByRevENN = ENN.reduceByEnn(exs, attrs);
        keptByRevENN.flip(0, exs.size());
        final BitSet keptByRFI = PureRF.reduceFar(exs, attrs, PureRF.DEF_K);
        final BitSet keptByRDI = RDI.reduceByRDI(exs, attrs, RDI.DEF_K);
        final RawExampleList ret = new RawExampleList();
        for (int i = 0; i < exs.size(); i++) {
            if (keptByRevENN.get(i) || (keptByRDI.get(i) && keptByRFI.get(i))) {
                ret.add(exs.get(i));
            }
        }
        return ret;
    }
}