package clustering;

import java.util.LinkedList;
import java.util.List;

public class AHClustering {
    public enum Mode {
        IST, CST, UPGMA
    }

    public static ClusterTree cluster(List<Vector> vecs, Mode m) {
        final List<ClusterTree> listCT = new LinkedList<ClusterTree>();
        for (Vector v : vecs) { // Initialize.
            listCT.add(new ClusterTree(v));
        }
        // Lower triangular matrix.
        final List<List<Double>> matrix = new LinkedList<List<Double>>();
        matrix.add(new LinkedList<Double>()); // Leave Row 0 empty.
        for (int i = 1; i < vecs.size(); i++) {
            final List<Double> row = new LinkedList<Double>();
            for (int j = 0; j < i; j++) {
                final double sim = getSim(listCT.get(i), listCT.get(j), m);
                row.add(sim);
            }
            matrix.add(row);
        }
        for (int i = 0; i < matrix.size(); i++) {
            //System.out.println(i + " " + matrix.get(i));
        }
        while (listCT.size() > 1) {
            // Find the highest similarity pair.
            double max = Double.NEGATIVE_INFINITY;
            int a = -1;
            int b = -1;
            for (int i = 1; i < matrix.size(); i++) {
                final List<Double> row = matrix.get(i);
                for (int j = 0; j < i; j++) {
                    final double sim = row.get(j);
                    if (Double.compare(max, sim) < 0) {
                        max = sim;
                        // a < b.
                        a = j;
                        b = i;
                    }
                }
            }
            assert a != -1;
            // Merge cluster a and b.
            mergeTwoRow(matrix, listCT, a, b, m);
            for (int i = 0; i < matrix.size(); i++) {
                //System.out.println(i + " " + matrix.get(i));
            }
        }

        return listCT.get(0);
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
     * 
     * @param b
     * @param a
     * @param listCT
     * @param matrix
     * */
    public static void mergeTwoRow(List<List<Double>> matrix,
            List<ClusterTree> listCT, int a, int b, Mode m) {
        // 1. Merge rows Ra and Rb to Ra (a<b).
        final ClusterTree cla = listCT.get(a);
        final ClusterTree clb = listCT.get(b);
        final double simab = matrix.get(b).get(a);
        final ClusterTree newcla = new ClusterTree(cla, clb, simab);

        listCT.set(a, newcla);
        // 2. Update all columns (C0 to C(a-1)) in Ra. (Relationship between i
        // and a, i < a)
        final List<Double> ra = matrix.get(a);
        for (int i = 0; i < a; i++) {
            final ClusterTree cli = listCT.get(i);
            final double sim = getSim(cli, cla, m);
            ra.set(i, sim);
        }
        // 3. For all rows Rj (a < j, j != b), update column Ca. (Relationship
        // between a and j)
        for (int j = a + 1; j < matrix.size(); j++) {
            if (j != b) {
                final ClusterTree clj = listCT.get(j);
                final double sim = getSim(cla, clj, m);
                matrix.get(j).set(a, sim);
            }
        }
        // 4. For all rows Rk (b < k), remove column Cb. (Relationship between b
        // and * k)
        for (int k = b + 1; k < matrix.size(); k++) {
            matrix.get(k).remove(b);
        }
        // 5. Remove Rb.
        matrix.remove(b);
        listCT.remove(b);
    }

    private static double getSim(ClusterTree c1, ClusterTree c2, Mode m) {
        if (m == Mode.IST) {
            return getSimIST(c1, c2);
        } else if (m == Mode.CST) {
            return getSimCST(c1, c2);
        } else {
            return getSimUPGMA(c1, c2);
        }
    }

    private static double getSimIST(ClusterTree c1, ClusterTree c2) {
        final double sim1 = istSim(c1);
        final double sim2 = istSim(c2);
        final Cluster total = new Cluster();
        total.addAll(c1);
        total.addAll(c2);
        final double simTotal = istSim(total);
        final double sim = simTotal - (sim1 + sim2);
        assert sim <= 0;
        return sim;
    }

    private static double istSim(Cluster c) {
        double sim = 0.0;
        final Vector cen = c.getCenter();
        for (Vector v : c) {
            sim += cen.cosine(v);
        }
        return sim;
    }

    private static double getSimCST(ClusterTree c1, ClusterTree c2) {
        final Vector cen1 = c1.getCenter();
        final Vector cen2 = c2.getCenter();
        return cen1.cosine(cen2);
    }

    private static double getSimUPGMA(ClusterTree c1, ClusterTree c2) {
        double sim = 0.0;
        for (Vector v1 : c1) {
            for (Vector v2 : c2) {
                sim += v1.cosine(v2);
            }
        }
        sim /= c1.size();
        sim /= c2.size();
        return sim;
    }
}
