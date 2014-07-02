package instancereduction;

import instancereduction.ENN.Node;

import java.util.ArrayList;
import java.util.BitSet;

import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName:     RanENNRF.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jul 1, 2014 9:02:51 PM 
 */
public class RanENNRF implements Reducible {
    private static final double RATIO = 0.7;
    
    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        // Ran.
        final BitSet kept = RanR.reduceByRandom(exs, attrs, RATIO);
        // ENN.
        final BitSet ennMark = new BitSet(exs.size());
        final BitSet ennReplaced = new BitSet(exs.size());
        final Node[][] nns = ENN.getNeighborMatrix(exs, attrs);
        for (int i = 0; i < exs.size(); i++) {
            final ArrayList<Integer> neighbors =
                    ENN.kNearestNeighbor(i, ENN.K, nns);
            final String majorityClass =
                    ENN.majorityClass(exs, attrs, neighbors);
            final RawExample e = exs.get(i);
            // The instance discarded by ENN but kept by random.
            if (!majorityClass.equals(e.t) && kept.get(i)) {
                kept.clear(i);
                ennMark.set(i);
                final int nn =
                        RanENN.nearestNeighborHasntKept(i, nns, exs, kept, ennMark);
                if (nn != -1) {
                    kept.set(nn);
                    ennReplaced.set(nn);
                    /* System.out.println(exs.get(i));
                     * System.out.println("replaced by");
                     * System.out.println(exs.get(nn)); */
                }
            }
        }
        // RF.
        final BitSet keptByRF = PureRF.reduceFar(exs, attrs, PureRF.DEF_K, nns);
        // Save the one got enn replaced.
        keptByRF.or(ennReplaced);

        // Remove the one didn't get kept by RF.
        kept.and(keptByRF);
        
        final RawExampleList ret = new RawExampleList();
        for (int i = 0; i < exs.size(); i++) {
            if (kept.get(i)) {
                ret.add(exs.get(i));
            }
        }
        return ret;
    }

}
