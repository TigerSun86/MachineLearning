package clustering;

import java.util.List;

public class AHClustering {

    public static List<CentralCluster> cluster(List<Vector> vecs, int k,
            int iter) {
        return null;
    }

    /**
     * Lower triangular matrix, only in cell RiCj, i > j has data.
     * c 0 1 2 3 4
     * 0
     * 1 1
     * 2 1 1
     * 3 1 1 1
     * 4 1 1 1 1
     * 
     * Pseudo code:
     * 1. Merge rows Ra and Rb to Ra (a<b).
     * 2. Update all columns (C0 to C(a-1)) in Ra. (Relationship between i and
     * a, i < a)
     * 3. For all rows Rj (a < j, j != b), update column Ca. (Relationship
     * between a and j)
     * 4. For all rows Rk (b < k), remove column Cb. (Relationship between b and
     * k)
     * 5. Remove Rb.
     * */
    public static void mergeTwoRow() {
    }
}
