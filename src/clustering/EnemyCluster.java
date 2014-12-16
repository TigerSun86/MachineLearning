package clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import util.Dbg;

/**
 * FileName: EnemyCluster.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 25, 2014 2:42:08 PM
 */
public class EnemyCluster implements ClusterAlg {
    public static final String MODULE = "ENC";
    public static final boolean DBG = true;

    public final ClusterAlg clusterAlg;
    private final boolean clusterByBorder;
    private List<Vector> fullSet = null;

    public EnemyCluster(final ClusterAlg clusterAlg,
            final boolean clusterByBorder) {
        this.clusterAlg = clusterAlg;
        this.clusterByBorder = clusterByBorder;
    }

    public EnemyCluster(final ClusterAlg clusterAlg) {
        this(clusterAlg, true);
    }

    @Override
    public ClusterList cluster (List<Vector> vecs) {
        final List<Vector> centerSet = new ArrayList<Vector>();
        final List<Vector> borderSet = new ArrayList<Vector>();
        for (Vector v : vecs) {
            if (isBorder(v)) {
                borderSet.add(v);
            } else {
                centerSet.add(v);
            }
        }
        Dbg.print(DBG, MODULE, "Border: " + borderSet.size());
        Dbg.print(DBG, MODULE, "Center: " + centerSet.size());

        final List<Vector> setToCluster;
        if (clusterByBorder) {
            setToCluster = borderSet;
        } else {
            setToCluster = centerSet;
        }

        if (setToCluster.isEmpty()) {
            // If there is no points need to do clustering, just return all
            // points as a whole cluster.
            final Cluster c = new Cluster();
            c.addAll(vecs);
            final ClusterList finalCl = new ClusterList();
            finalCl.add(c);
            return finalCl;
        }

        final ClusterList cl = clusterAlg.cluster(setToCluster);
        final HashMap<Vector, Integer> map = new HashMap<Vector, Integer>();
        for (Vector v : vecs) {
            final int idxOfCluster = nearestCluster(v, cl);
            map.put(v, idxOfCluster);
        }

        // Use new ClusterList to store all vecs, to keep the original order
        // of the input points.
        final ClusterList finalCl = new ClusterList();
        for (int i = 0; i < cl.size(); i++) {
            finalCl.add(new Cluster());
        }
        for (Vector v : vecs) {
            final int idxOfCluster = map.get(v);
            finalCl.get(idxOfCluster).add(v);
        }
        return finalCl;
    }

    @Override
    public void setK (int k) {
        clusterAlg.setK(k);
    }

    public void setFullSet (List<Vector> fullSet) {
        this.fullSet = fullSet;
    }

    public static int nearestCluster (Vector bv, ClusterList cl) {
        double min = Double.POSITIVE_INFINITY;
        int minIdxOfCluster = -1;
        for (int i = 0; i < cl.size(); i++) {
            final Cluster cluster = cl.get(i);
            for (Vector v : cluster) {
                final double dis = v.distanceTo(bv);
                if (Double.compare(dis, 0) == 0) {
                    // It's the vector itself.
                    return i;
                } else if (Double.compare(min, dis) > 0) {
                    min = dis; // Find the cluster has vector with min dis.
                    minIdxOfCluster = i;
                }
            }
        }
        assert minIdxOfCluster != -1;
        return minIdxOfCluster;
    }

    private boolean isBorder (Vector v) {
        final LinkedList<Vector> neighbors = new LinkedList<Vector>();
        final LinkedList<Double> minDiss = new LinkedList<Double>();
        for (Vector o : fullSet) {
            final double dis = v.distanceTo(o);
            if (dis > 0) { // Don't compare with e itself.
                boolean inserted = false;
                for (int i = 0; i < Math.min(3, minDiss.size()); i++) {
                    // Insert the neighbor into top 3
                    if (Double.compare(minDiss.get(i), dis) > 0) {
                        minDiss.add(i, dis);
                        neighbors.add(i, o);
                        inserted = true;
                        break;
                    }
                }
                if (!inserted && minDiss.size() < 3) {
                    // Add o if it's not smaller than any one in the neighbors,
                    // but there're only 0/1/2 elements in the neighbors.
                    minDiss.add(dis);
                    neighbors.add(o);
                }
            }
        }
        int enemyCount = 0;
        for (int i = 0; i < Math.min(3, neighbors.size()); i++) {
            if (!v.e.t.equals(neighbors.get(i).e.t)) {
                enemyCount++;
            }
        }
        if (enemyCount >= 2) {
            // E is a border if 2 out of 3 neighbors are enemy. (ENN alg)
            return true;
        } else {
            return false;
        }
    }
}
