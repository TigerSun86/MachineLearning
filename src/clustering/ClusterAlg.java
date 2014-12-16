package clustering;

import java.util.List;

public interface ClusterAlg {
    public ClusterList cluster(List<Vector> vecs);
    public void setK(int k);
}
