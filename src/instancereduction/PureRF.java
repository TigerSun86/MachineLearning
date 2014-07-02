package instancereduction;

import instancereduction.ENN.Node;

import java.util.BitSet;

import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName: PureRF.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 30, 2014 10:04:36 PM
 */
public class PureRF implements Reducible {
    public static final int DEF_K = 3;
    private final int k;

    public PureRF() {
        this.k = DEF_K;
    }

    public PureRF(int k) {
        this.k = k;
    }

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        final BitSet kept = reduceFar(exs, attrs, k);
        final RawExampleList ret = new RawExampleList();
        for (int i = 0; i < exs.size(); i++) {
            if (kept.get(i)) {
                ret.add(exs.get(i));
            }
        }
        return ret;
    }

    public static BitSet reduceFar (RawExampleList exs, RawAttrList attrs,
            final int k) {
        final Node[][] nns = ENN.getNeighborMatrix(exs, attrs);
        return reduceFar(exs, attrs, k, nns);
    }

    public static BitSet reduceFar (RawExampleList exs, RawAttrList attrs,
            final int k, final Node[][] nns) {
        final BitSet kept = new BitSet(exs.size());
        final BitSet hasEnemyNearby = new BitSet(exs.size());

        final double[] disToEnemy = new double[exs.size()];
        double sum = 0;
        for (int i = 0; i < exs.size(); i++) {
            final DisAndNeiOrder dn = disToNearestEnemy(i, nns, exs);
            if (Double.isNaN(dn.dis)) {
                kept.set(0, exs.size());
                return kept; // Only one class in exs, so keep everyone.
            }

            if (dn.neighborOrder < k) {
                hasEnemyNearby.set(i); // This instance has enemy nearby.
            }

            disToEnemy[i] = dn.dis;
            sum += dn.dis;
        }
        final double ave = sum / exs.size();
        double st = 0;
        for (double x : disToEnemy) {
            st += (x - ave) * (x - ave);
        }
        st /= exs.size();
        st = Math.sqrt(st);
        final double farThreshold = ave + st;

        for (int i = 0; i < exs.size(); i++) {
            if ((Double.compare(disToEnemy[i], farThreshold) <= 0)
                    || hasEnemyNearby.get(i)) {
                // Keep instances which is not too far, or has enemy
                // among k nearest neighbors.
                kept.set(i);
            }
        }
        return kept;
    }

    private static class DisAndNeiOrder {
        public final double dis;
        public final int neighborOrder;

        public DisAndNeiOrder(double dis, int neighborOrder) {
            this.dis = dis;
            this.neighborOrder = neighborOrder;
        }
    }

    private static DisAndNeiOrder disToNearestEnemy (final int i,
            final Node[][] nns, final RawExampleList exs) {
        final String t = exs.get(i).t;
        double ret = Double.NaN;
        int neighborOrder = -1;
        for (int j = 0; j < nns[i].length; j++) {
            final Node n = nns[i][j];
            if (!exs.get(n.index).t.equals(t)) {
                ret = n.dis;
                neighborOrder = j;
                break;
            }
        }
        return new DisAndNeiOrder(ret, neighborOrder);
    }
}
