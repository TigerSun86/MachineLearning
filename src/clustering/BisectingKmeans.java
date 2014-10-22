package clustering;

import java.util.LinkedList;
import java.util.List;

public class BisectingKmeans {
    public enum WayToPick {
        LARGEST, LEASTSIM
    }

    public static ClusterList cluster(List<Vector> vecs, int k, int iter) {
        return cluster(vecs, k, WayToPick.LARGEST, iter);
    }

    public static ClusterList cluster(List<Vector> vecs, int k, WayToPick way,
            int iter) {
        assert vecs.size() >= k;
        // All as one cluster.
        final Cluster cluster = new Cluster();
        cluster.addAll(vecs);
        final LinkedList<Cluster> clusters = new LinkedList<Cluster>();
        clusters.add(cluster);

        while (clusters.size() < k) {
            final int cIdx = pickCluster(clusters, way);
            final Cluster cToSplit = clusters.get(cIdx);

            double maxSim = Double.NEGATIVE_INFINITY;
            List<Cluster> bestC = null;
            for (int i = 0; i < iter; i++) {
                List<Cluster> c = Kmeans.cluster(cToSplit, 2);
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
