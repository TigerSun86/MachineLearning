package clustering;

import java.util.ArrayList;
import java.util.List;

import common.RawAttrList;

import clustering.AHClustering.Mode;

public class ASeedKmeans implements ClusterAlg {
    public Mode m;
    public int k;
    public RawAttrList attrs;

    public ASeedKmeans(Mode m, int k, RawAttrList attrs) {
        this.m = m;
        this.k = k;
    }

    @Override
    public ClusterList cluster (List<Vector> vecs) {
        ClusterTree c = AHClustering.cluster(vecs, this.m);
        List<Vector> centroidsIn = new ArrayList<Vector>();
        for (Cluster c2 : c.getKCluster(this.k)) {
            centroidsIn.add(c2.getCenter());
        }
        return Kmeans.cluster(vecs, centroidsIn);
    }

    @Override
    public void setK (int k) {
        this.k = k;
    }

}
