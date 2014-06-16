package instancereduction;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

import util.Dbg;

import common.MappedAttrList;
import common.RawAttr;
import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName: ENN.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Apr 19, 2014 7:54:26 PM
 */
public class ENN {
    public static final String MODULE = "ENN";
    public static final boolean DBG = true;

    public static final int K = 3;

    public static RawExampleList reduce (final RawExampleList exs,
            final RawAttrList attrs) {
        // Map all attributes in range 0 to 1.
        final MappedAttrList mAttr = new MappedAttrList(exs, attrs);
        final RawExampleList exs2 = mAttr.mapExs(exs, attrs);

        // Measure distances between each examples.
        final double[][] diss = getDistances(exs2, attrs);
        final String[] classDeterminedByNeighbors = new String[exs2.size()];

        for (int i = 0; i < exs2.size(); i++) {
            final ArrayList<Integer> neighbors = kNearestNeighbor(i, diss, K);
            final String majorityClass = majorityClass(exs2, attrs, neighbors);
            classDeterminedByNeighbors[i] = majorityClass;
        }
        
        // Reduce original exs.
        final RawExampleList ret = new RawExampleList();
        for (int i = 0; i < exs.size(); i++) {
            final RawExample ex = exs.get(i);
            if (ex.t.equals(classDeterminedByNeighbors[i])) {
                // Keep example only when all its neighbors agree with its
                // class.
                ret.add(ex);
            }
        }
        Dbg.print(DBG, MODULE, "Reduced size: " + ret.size());
        return ret;
    }

    private static String majorityClass (final RawExampleList exs,
            final RawAttrList attrs, final ArrayList<Integer> neighbors) {
        assert !attrs.t.isContinuous;
        final ArrayList<String> classes = attrs.t.valueList;
        final int[] count = new int[classes.size()];
        for (int nei : neighbors) {
            final RawExample exNei = exs.get(nei);
            final int index = classes.indexOf(exNei.t);
            assert index != -1;
            count[index]++;
        }
        int maxCount = Integer.MIN_VALUE;
        ArrayList<Integer> maxIndex = null;
        for (int index = 0; index < count.length; index++) {
            if (maxCount < count[index]) {
                maxCount = count[index];
                maxIndex = new ArrayList<Integer>();
                maxIndex.add(index);
            } else if (maxCount == count[index]) { // Tie.
                assert maxIndex != null;
                maxIndex.add(index);
            }
        }
        // If tie, choose randomly.
        final int majIndex =
                maxIndex.get(new Random().nextInt(maxIndex.size()));
        return classes.get(majIndex);
    }

    private static class Node implements Comparable<Node> {
        public int index;
        public double dis;

        public Node(int index, double dis) {
            super();
            this.index = index;
            this.dis = dis;
        }

        @Override
        public int compareTo (Node arg0) {
            return Double.compare(this.dis, arg0.dis);
        }
    }

    private static ArrayList<Integer> kNearestNeighbor (final int i,
            final double[][] diss, final int k) {
        final PriorityQueue<Node> que = new PriorityQueue<Node>(); // Ascending
        for (int j = 0; j < diss[i].length; j++) {
            if (j != i) {
                que.add(new Node(j, diss[i][j]));
            }
        }
        final ArrayList<Integer> ret = new ArrayList<Integer>();
        int count = 0;
        while (count < k && !que.isEmpty()) {
            ret.add(que.remove().index);
            count++;
        }
        return ret;
    }

    public static double[][] getDistances (final RawExampleList exs,
            final RawAttrList attrs) {
        final double[][] diss = new double[exs.size()][exs.size()];
        // Only calculate the upper triangle.
        for (int i = 0; i < exs.size(); i++) {
            for (int j = i; j < exs.size(); j++) {
                if (j == i) {
                    diss[i][j] = 0; // Itself.
                } else {
                    final double d = getDistance(exs.get(i), exs.get(j), attrs);
                    diss[i][j] = d;
                    diss[j][i] = d;
                }
            }
        }

        return diss;
    }

    private static double getDistance (final RawExample ex1,
            final RawExample ex2, final RawAttrList attrs) {
        double sum = 0;
        for (int i = 0; i < attrs.xList.size(); i++) {
            final RawAttr attr = attrs.xList.get(i);
            final String v1 = ex1.xList.get(i);
            final String v2 = ex2.xList.get(i);
            if (attr.isContinuous) {
                final double temp = Double.valueOf(v1) - Double.valueOf(v2);
                sum += temp * temp;
            } else {
                // Discrete value: distance equals to 1 if values are different;
                // 0 if values are the same.
                if (!v1.equals(v2)) {
                    sum += 1;
                }
            }
        }
        assert Double.compare(sum, 0) >= 0;
        return Math.sqrt(sum);
    }
}
