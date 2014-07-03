package instancereduction;

import java.util.ArrayList;
import java.util.BitSet;

import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName: RanENN.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 30, 2014 8:02:02 PM
 */
public class RanENN implements Reducible {
    private static final double RATIO = 0.5;

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        final BitSet kept = RanR.reduceByRandom(exs, attrs, RATIO);

        final BitSet ennMark = new BitSet(exs.size());
        final IndAndDis[][] nns = ENN.getNeighborMatrix(exs, attrs);
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
                        nearestNeighborHasntKept(i, nns, exs, kept, ennMark);
                if (nn != -1) {
                    kept.set(nn);
                    /* System.out.println(exs.get(i));
                     * System.out.println("replaced by");
                     * System.out.println(exs.get(nn)); */
                }
            }
        }

        final RawExampleList ret = new RawExampleList();
        for (int i = 0; i < exs.size(); i++) {
            if (kept.get(i)) {
                ret.add(exs.get(i));
            }
        }
        return ret;
    }

    public static int nearestNeighborHasntKept (final int i,
            final IndAndDis[][] nns, final RawExampleList exs, final BitSet kept,
            final BitSet ennMark) {
        int ret = -1;
        final String t = exs.get(i).t;
        for (int j = 0; j < nns[i].length; j++) {
            final int index = nns[i][j].index;
            // Instance in same class, hasn't kept and wasn't marked by ENN.
            if (t.equals(exs.get(index).t) && !kept.get(index)
                    && !ennMark.get(index)) {
                ret = j;
                break;
            }
        }
        return ret;
    }
}
