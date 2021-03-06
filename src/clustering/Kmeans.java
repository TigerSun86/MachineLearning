package clustering;

import java.util.ArrayList;
import java.util.List;

import common.RawAttrList;
import util.MyMath;

public class Kmeans implements ClusterAlg {
    public int k;
    public RawAttrList attrs;

    public Kmeans(int k, RawAttrList attrs) {
        this.k = k;
        this.attrs = attrs;
    }

    @Override
    public void setK (int k) {
        this.k = k;
    }

    @Override
    public ClusterList cluster (List<Vector> vecs) {
        return cluster2(vecs, this.k);
    }

    public static ClusterList cluster2 (List<Vector> vecs, int k) {
        final int realK = Math.min(k, vecs.size());
        // Initial random centroids.
        final int[] cenIdx = MyMath.mOutofN(realK, vecs.size());
        final List<Vector> centroids = new ArrayList<Vector>();
        for (int i = 0; i < realK; i++) {
            centroids.add(vecs.get(cenIdx[i]));
        }

        return cluster(vecs, centroids);
    }

    public static ClusterList cluster (List<Vector> vecs,
            List<Vector> centroidsIn) {
        // Make a copy of centroidsIn.
        final List<Vector> centroids = new ArrayList<Vector>();
        for (int i = 0; i < centroidsIn.size(); i++) {
            centroids.add(centroidsIn.get(i));
        }

        final int vecSize = vecs.get(0).size();
        ClusterList retClusters = null;
        boolean needMoreIter = true;
        while (needMoreIter) {
            final ClusterList clusters = new ClusterList();
            for (int i = 0; i < centroids.size(); i++) {
                final Cluster c = new Cluster();
                c.setCenter(new Vector(vecSize)); // To calculate new center.
                clusters.add(c);
            }

            // Assign all vectors to closest centroid.
            for (int i = 0; i < vecs.size(); i++) {
                final Vector vec = vecs.get(i);
                double minDis = Double.POSITIVE_INFINITY;
                int minCen = -1;
                for (int j = 0; j < centroids.size(); j++) {
                    final double dis = centroids.get(j).distanceTo(vec);
                    if (Double.compare(minDis, dis) > 0) {
                        minDis = dis;
                        minCen = j;
                    }
                }
                assert minCen != -1;
                clusters.get(minCen).add(vec);
                clusters.get(minCen).getCenter().accumulate(vec);
            }

            // Calculate new center, and check whether center changed.
            needMoreIter = false;
            for (int i = 0; i < clusters.size(); i++) {
                final Vector newCen = clusters.get(i).getCenter();
                final int numOfVecsInTheCluster = clusters.get(i).size();
                if (numOfVecsInTheCluster != 0) { // Calculate and store new cen
                    newCen.dividedBy(numOfVecsInTheCluster);
                }
                // Centroid changed.
                if (!newCen.equals(centroids.get(i))) {
                    needMoreIter = true;
                    // Set centroid as new centroid for next iteration.
                    centroids.set(i, newCen);
                }
            }

            if (!needMoreIter) {
                retClusters = clusters;
            }
        } // End of while (needMoreIter) {
        assert retClusters != null;
        return retClusters;
    }

}
