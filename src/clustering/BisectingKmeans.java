package clustering;

import java.util.LinkedList;
import java.util.List;

import common.RawAttrList;

public class BisectingKmeans implements ClusterAlg {
    public enum WayToPick {
        LARGEST, LEASTSIM
    }

    public WayToPick way;
    public int iter;
    public int k;
    public RawAttrList attrs;

    public BisectingKmeans(WayToPick way, int iter ,int k, RawAttrList attrs) {
        this.iter = iter;
        this.way = way;
        this.k = k;
    }

    @Override
    public void setK (int k) {
        this.k = k;
    }
    
    @Override
    public ClusterList cluster(List<Vector> vecs) {
        return cluster(vecs, this.k, this.way, this.iter);
    }

    public static ClusterList cluster(List<Vector> vecs, int k, int iter) {
        return cluster(vecs, k, WayToPick.LARGEST, iter);
    }

    public static ClusterList cluster(List<Vector> vecs, int k, WayToPick way,
            int iter) {
        final int realK = Math.min(k, vecs.size());
        // All as one cluster.
        final Cluster cluster = new Cluster();
        cluster.addAll(vecs);
        final LinkedList<Cluster> clusters = new LinkedList<Cluster>();
        clusters.add(cluster);

        while (clusters.size() < realK) {
            final int cIdx = pickCluster(clusters, way);
            final Cluster cToSplit = clusters.get(cIdx);

            double maxSim = Double.NEGATIVE_INFINITY;
            List<Cluster> bestC = null;
            for (int i = 0; i < iter; i++) {
                List<Cluster> c = Kmeans.cluster2(cToSplit, 2);
                double simSum = 0.0;
                for (int j = 0; j < c.size(); j++) {
                    simSum += c.get(j).internalSim();
                }
                if (Double.compare(maxSim, simSum) < 0) {
                    maxSim = simSum;
                    bestC = c;
                }
            }
            assert bestC != null;
            clusters.remove(cIdx);
            clusters.addAll(bestC);
        }
        final ClusterList retcs = new ClusterList();
        retcs.addAll(clusters);
        return retcs;
    }

    private static int pickCluster(LinkedList<Cluster> clusters, WayToPick way) {
        if (way == WayToPick.LARGEST) {
            int idx = -1;
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < clusters.size(); i++) {
                final int size = clusters.get(i).size();
                if (max < size) {
                    max = size;
                    idx = i;
                }
            }
            assert idx != -1;
            return idx;
        } else { // LEASTSIM
            int idx = -1;
            double min = Double.POSITIVE_INFINITY;
            for (int i = 0; i < clusters.size(); i++) {
                final double sim = clusters.get(i).internalSim();
                if (Double.compare(min, sim) > 0) {
                    min = sim;
                    idx = i;
                }
            }
            assert idx != -1;
            return idx;
        }
    }
}
