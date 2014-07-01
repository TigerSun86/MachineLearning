package instancereduction;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.PriorityQueue;

import util.MyMath;

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
        final int[] randRet =
                MyMath.mOutofN((int) Math.round(exs.size() * RATIO), exs.size());
        final BitSet kept = new BitSet(exs.size());
        for (int i : randRet) {
            kept.set(i);
        }

        // Measure distances between each examples.
        final double[][] diss = ENN.getDistances(exs, attrs);
        final BitSet ennMark = new BitSet(exs.size());
        for (int i = 0; i < exs.size(); i++) {
            final ArrayList<Integer> neighbors =
                    ENN.kNearestNeighbor(i, diss, ENN.K);
            final String majorityClass =
                    ENN.majorityClass(exs, attrs, neighbors);
            final RawExample e = exs.get(i);
            // The instance discarded by ENN but kept by random.
            if (!majorityClass.equals(e.t) && kept.get(i)) {
                kept.clear(i);
                ennMark.set(i);
                final int nn =
                        nearestNeighborHasntKept(i, diss, exs, kept, ennMark);
                if (nn != -1) {
                    kept.set(nn);
/*                    System.out.println(exs.get(i));
                    System.out.println("replaced by");
                    System.out.println(exs.get(nn));*/
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
            final double[][] diss, final RawExampleList exs, final BitSet kept,
            final BitSet ennMark) {
        final PriorityQueue<ENN.Node> que = new PriorityQueue<ENN.Node>(); // Ascending
        for (int j = 0; j < diss[i].length; j++) {
            if (j != i) {
                que.add(new ENN.Node(j, diss[i][j]));
            }
        }

        int ret = -1;
        final String t = exs.get(i).t;
        while (!que.isEmpty()) {
            final int j = que.remove().index;
            // Instance in same class, hasn't kept and wasn't marked by ENN.
            if (t.equals(exs.get(j).t) && !kept.get(j) && !ennMark.get(j)) {
                ret = j;
                break;
            }
        }

        return ret;
    }
}
