package clustering;

import java.util.List;

public interface ClusterAlg {
    public ClusterList cluster(List<Vector> vecs, int k);
}
