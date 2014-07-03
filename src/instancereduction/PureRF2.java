package instancereduction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName: PureRF2.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jul 2, 2014 10:09:40 PM
 */
public class PureRF2 implements Reducible {
    public static final int DEF_K = 3;
    public static final double DEF_R = 0.85;
    private final int k;
    private final double r;

    public PureRF2() {
        this.k = DEF_K;
        this.r = DEF_R;
    }

    public PureRF2(int k, double r) {
        this.k = k;
        this.r = r;
    }

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        final BitSet kept = reduceFar(exs, attrs, k, r);
        final RawExampleList ret = new RawExampleList();
        for (int i = 0; i < exs.size(); i++) {
            if (kept.get(i)) {
                ret.add(exs.get(i));
            }
        }
        return ret;
    }

    public static BitSet reduceFar (RawExampleList exs, RawAttrList attrs,
            final int k, final double r) {
        final IndAndDis[][] nns = ENN.getNeighborMatrix(exs, attrs);
        return reduceFar(exs, attrs, k, r, nns);
    }

    public static BitSet reduceFar (RawExampleList exs, RawAttrList attrs,
            final int k, final double r, final IndAndDis[][] nns) {
        if ((Double.compare(r, 1) >= 0) || (Double.compare(r, 0) < 0)) {
            final BitSet kept = new BitSet(exs.size());
            kept.set(0, exs.size());
            return kept; // Keep all.
        }
        // Here usage of IndAndDis is different than before. The index is for my
        // index; the dis is for distance to my nearest enemy.
        final IndAndDis[] disToEnemy = new IndAndDis[exs.size()];
        for (int i = 0; i < exs.size(); i++) {
            final IndAndDis dis = disToNearestEnemy(i, nns, exs);
            if (dis == null) {
                final BitSet kept = new BitSet(exs.size());
                kept.set(0, exs.size());
                return kept; // Only one class in exs, so keep everyone.
            }
            disToEnemy[i] = dis;
        }

        // Sort ascendingly by distance to nearest enemy.
        Arrays.sort(disToEnemy);

        final int[] numToRemove = getNumToRemoveForEachClass(exs, attrs, r);
        final BitSet kept = new BitSet(exs.size());
        kept.set(0, exs.size());
        // Start from the farthest distance one.
        for (int i = disToEnemy.length - 1; i >= 0; i--) {
            final int index = disToEnemy[i].index;
            final int classIndex = attrs.t.valueList.indexOf(exs.get(index).t);
            if (numToRemove[classIndex] != 0) {
                // Still need to remove instance of this class.
                if (isSafeToRemove(index, nns, exs, kept, k)) {
                    kept.clear(index);
                    numToRemove[classIndex]--;
                }
            }
        }
        return kept;
    }

    private static int[] getNumToRemoveForEachClass (RawExampleList exs,
            RawAttrList attrs, final double r) {
        final ArrayList<String> classList = attrs.t.valueList;
        final int[] numToRemove = new int[classList.size()];
        // Count number of instances for each class.
        for (RawExample e : exs) {
            final int classI = classList.indexOf(e.t);
            numToRemove[classI]++;
        }
        for (int i = 0; i < numToRemove.length; i++) {
            // Do not use round(num*(1-r)) here, because if num is 1, r is 0.5,
            // I want numToRemove to be 0.
            final int nToKeep = (int) Math.round(numToRemove[i] * r);
            numToRemove[i] -= nToKeep;
        }
        return numToRemove;
    }

    /**
     * Return true only when k nearest neighbors are all in the same class as
     * the index one.
     * The neighbor which has already been removed won't count as these k
     * nearest neighbors.
     * */
    private static boolean isSafeToRemove (int index, IndAndDis[][] nns,
            RawExampleList exs, final BitSet kept, int k) {
        final String t = exs.get(index).t;

        int count = 0;
        boolean isSafe = true;
        for (int j = 0; j < nns[index].length; j++) {
            if (count >= k) {
                // Check count at first because I want it quit immediately when
                // k is set to 0.
                isSafe = true;
                break;
            }

            final int neighborIndex = nns[index][j].index;
            if (!exs.get(neighborIndex).t.equals(t)) { // Is an enemy.
                // Has enemy within k neighbors, not safe to be removed.
                // Do not check whether this enemy has been removed or not,
                // because even this enemy is removed, it's still not safe.
                isSafe = false;
                break;
            } else { // In the same class.
                if (kept.get(neighborIndex)) {
                    // Increase the count only when this neighbor is still kept.
                    count++;
                }
            }
        }
        return isSafe;
    }

    private static IndAndDis disToNearestEnemy (final int i,
            final IndAndDis[][] nns, final RawExampleList exs) {
        final String t = exs.get(i).t;
        IndAndDis ret = null;
        for (int j = 0; j < nns[i].length; j++) {
            final IndAndDis n = nns[i][j];
            if (!exs.get(n.index).t.equals(t)) {
                // Here usage of IndAndDis is different than before. The index
                // is for my index; the dis is for distance to my nearest enemy.
                ret = new IndAndDis(i, n.dis);
                break;
            }
        }
        return ret;
    }
}
