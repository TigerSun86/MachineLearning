package instancereduction;

import java.util.BitSet;

import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName: RDFI.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jul 5, 2014 12:20:16 AM
 */
public class RDFI implements Reducible {
    @Override
    public RawExampleList reduce (final RawExampleList exs,
            final RawAttrList attrs) {

        final BitSet keptByRDI = RDI.reduceByRDI(exs, attrs, RDI.DEF_K);
        final BitSet keptByRFI = PureRF.reduceFar(exs, attrs, 0);

        final RawExampleList ret = new RawExampleList();
        for (int i = 0; i < exs.size(); i++) {
            if (keptByRDI.get(i) && keptByRFI.get(i)) {
                ret.add(exs.get(i));
            }
        }
        return ret;
    }
}