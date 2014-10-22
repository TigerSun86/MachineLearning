package clustering;

import java.util.ArrayList;
import java.util.List;

import clustering.AHClustering.Mode;

public class ASeedKmeans implements ClusterAlg {
    public Mode m;

    public ASeedKmeans(Mode m) {
        this.m = m;
    }

    @Override
    public ClusterList cluster(List<Vector> vecs, int k) {
        ClusterTree c = AHClustering.cluster(vecs, this.m);
        List<Vector> centroidsIn = new ArrayList<Vector>();
        for (Cluster c2 : c.getKCluster(k)) {
            centroidsIn.add(c2.getCenter());
        }
        return Kmeans.cluster(vecs, centroidsIn);
    }

}
