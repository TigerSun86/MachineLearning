package instancereduction;

import java.util.BitSet;

import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName: FCNNRDFI.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jul 9, 2014 9:11:34 PM
 */
public class FCNNRDFI implements Reducible {

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        final BitSet keptByFCNN = FCNN.reduceByFCNN(exs, attrs);
        final BitSet keptByRFI = PureRF.reduceFar(exs, attrs, PureRF.DEF_K);
        final BitSet keptByRDI = RDI.reduceByRDI(exs, attrs, RDI.DEF_K);
        final RawExampleList ret = new RawExampleList();
        for (int i = 0; i < exs.size(); i++) {
            if (keptByFCNN.get(i) || (keptByRDI.get(i) && keptByRFI.get(i))) {
                ret.add(exs.get(i));
            }
        }
        return ret;
    }
}