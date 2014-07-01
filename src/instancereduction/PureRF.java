package instancereduction;

import instancereduction.ENN.Node;

import java.util.BitSet;
import java.util.PriorityQueue;

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

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        final BitSet kept = reduceFar(exs, attrs);
        if (kept == null) {
            return exs; // Only one class in exs.
        }
        final RawExampleList ret = new RawExampleList();
        for (int i = 0; i < exs.size(); i++) {
            if (kept.get(i)) {
                ret.add(exs.get(i));
            }
        }
        return ret;
    }

    public static BitSet reduceFar (RawExampleList exs, RawAttrList attrs) {
        // Measure distances between each examples.
        final double[][] diss = ENN.getDistances(exs, attrs);

        final double[] disToEnemy = new double[exs.size()];
        double sum = 0;
        for (int i = 0; i < exs.size(); i++) {
            final double dis = nearestEnemy(i, diss, exs);
            if (Double.isNaN(dis)) {
                return null; // Only one class in exs.
            }
            disToEnemy[i] = dis;
            sum += dis;
        }
        final double ave = sum / exs.size();
        double st = 0;
        for (double x : disToEnemy) {
            st += (x - ave) * (x - ave);
        }
        st /= exs.size();
        st = Math.sqrt(st);
        final double farThreshold = ave + st;
        final BitSet kept = new BitSet(exs.size());
        for (int i = 0; i < exs.size(); i++) {
            if (Double.compare(disToEnemy[i], farThreshold) <= 0) {
                kept.set(i);
            }
        }
        return kept;
    }

    private static double nearestEnemy (final int i, final double[][] diss,
            final RawExampleList exs) {
        final PriorityQueue<Node> que = new PriorityQueue<Node>(); // Ascending
        for (int j = 0; j < diss[i].length; j++) {
            if (j != i) {
                que.add(new Node(j, diss[i][j]));
            }
        }

        final String t = exs.get(i).t;
        double ret = Double.NaN;
        while (!que.isEmpty()) {
            final Node n = que.remove();
            if (!exs.get(n.index).t.equals(t)) {
                ret = n.dis;
                break;
            }
        }
        return ret;
    }
}
