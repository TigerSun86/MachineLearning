package clustering;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

import common.RawAttrList;

/**
 * FileName: RandomCluster.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 26, 2014 3:47:56 PM
 */
public class RandomCluster implements ClusterAlg {
    private final Mode m;
    public int k;
    public RawAttrList attrs;

    public enum Mode {
        EVEN, UNEVEN
    }

    public RandomCluster(Mode m, int k, RawAttrList attrs) {
        this.m = m;
        this.k = k;
        this.attrs = attrs;
    }

    @Override
    public void setK (int k) {
        this.k = k;
    }

    @Override
    public ClusterList cluster (List<Vector> vecs) {
        final int realK = Math.min(this.k, vecs.size());
        final ClusterList cs = new ClusterList();
        for (int i = 0; i < realK; i++) {
            cs.add(new Cluster());
        }

        final Random ran = new Random();
        final BitSet visited = new BitSet(vecs.size());
        if (m == Mode.EVEN) {
            int nextCluster = 0;
            while (visited.cardinality() < vecs.size()) {
                int idxEx = -1;
                while (idxEx == -1 || visited.get(idxEx)) {
                    // Already used.
                    idxEx = ran.nextInt(vecs.size());
                }
                visited.set(idxEx);
                cs.get(nextCluster).add(vecs.get(idxEx));
                nextCluster = (nextCluster + 1) % realK;
            }
        } else { // Uneven
            // At first assign one for each cluster to guarantee no cluster is
            // empty.
            for (int nextCluster = 0; nextCluster < realK; nextCluster++) {
                int idxEx = -1;
                while (idxEx == -1 || visited.get(idxEx)) {
                    // Already used.
                    idxEx = ran.nextInt(vecs.size());
                }
                visited.set(idxEx);
                cs.get(nextCluster).add(vecs.get(idxEx));
            }
            if (visited.cardinality() < vecs.size()) {
                // Assign remain examples.
                for (int idxEx = 0; idxEx < vecs.size(); idxEx++) {
                    if (!visited.get(idxEx)) {
                        final int idxC = ran.nextInt(realK);
                        visited.set(idxEx);
                        cs.get(idxC).add(vecs.get(idxEx));
                    }
                }
            }
        } // Uneven

        return cs;
    }

}
