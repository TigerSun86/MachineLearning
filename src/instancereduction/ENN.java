package instancereduction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.PriorityQueue;
import java.util.Random;

import util.Dbg;
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
public class ENN implements Reducible {
    public static final String MODULE = "ENN";
    public static final boolean DBG = true;

    public static final int K = 3;

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        final BitSet kept = reduceByEnn(exs, attrs);
        final RawExampleList ret = new RawExampleList();
        for (int i = 0; i < exs.size(); i++) {
            if (kept.get(i)) {
                ret.add(exs.get(i));
            }
        }
        Dbg.print(DBG, MODULE, "Reduced size: " + ret.size());
        return ret;
    }

    public static BitSet reduceByEnn (RawExampleList exs, RawAttrList attrs) {
        final IndAndDis[][] nns = getNeighborMatrix(exs, attrs);
        final String[] classDeterminedByNeighbors = new String[exs.size()];

        for (int i = 0; i < exs.size(); i++) {
            final ArrayList<Integer> neighbors = kNearestNeighbor(i, K, nns);
            final String majorityClass = majorityClass(exs, attrs, neighbors);
            classDeterminedByNeighbors[i] = majorityClass;
        }

        final BitSet kept = new BitSet(exs.size());
        for (int i = 0; i < exs.size(); i++) {
            final RawExample ex = exs.get(i);
            if (ex.t.equals(classDeterminedByNeighbors[i])) {
                // Keep example only when its neighbors agree with its class.
                kept.set(i);
            }
        }
        return kept;
    }

    public static String majorityClass (final RawExampleList exs,
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

    public static ArrayList<Integer> kNearestNeighbor (final int i,
            final int k, final IndAndDis[][] nns) {
        final ArrayList<Integer> ret = new ArrayList<Integer>();
        for (int j = 0; j < k; j++){
            ret.add(nns[i][j].index);
        }
        return ret;
    }
    
    public static IndAndDis[][] getNeighborMatrix(RawExampleList exs, RawAttrList attrs){
        // Measure distances between each examples.
        final double[][] diss = getDistances(exs, attrs);
        // Copy distances to nns.
        final IndAndDis[][] nns = new IndAndDis[diss.length][diss.length-1];
        for (int i = 0; i < diss.length;i++){
            nns[i] = new IndAndDis[diss.length-1];
            int count = 0;
            for(int j = 0; j < diss[i].length;j++){
                if (i != j){
                    nns[i][count] = new IndAndDis(j, diss[i][j]);
                    count++;
                }
            }
        }
        // Sort nns.
        for (int i =0; i < nns.length; i++){
            Arrays.sort(nns[i]); // Ascending.
        }
        return nns;
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
