package clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class ClusterList extends ArrayList<Cluster> {
    private static final long serialVersionUID = 1L;

    public double entropy() {
        double ecs = 0.0;
        for (Cluster c : this) {
            final double e = entropyOfCluster(c);
            ecs += (e * c.size());
        }

        int totalCount = 0;
        for (Cluster c : this) {
            totalCount += c.size();
        }
        ecs /= totalCount;
        return ecs;
    }

    private static double entropyOfCluster(Cluster c) {
        final HashMap<String, Integer> classes = new HashMap<String, Integer>();
        for (Vector v : c) {
            final String cla = v.getClassName();
            Integer count = classes.get(cla);
            if (count != null) {
                count += 1;
                classes.put(cla, count);
            } else {
                classes.put(cla, 1);
            }
        }
        double e = 0.0;
        for (Entry<String, Integer> cla : classes.entrySet()) {
            final double p = ((double) cla.getValue()) / c.size();
            final double logp = Math.log(p);
            e += p * logp;
        }
        e = -e;
        return e;
    }

    public double fMeasure() {
        final HashMap<String, Integer> classes = new HashMap<String, Integer>();
        int totalCount = 0;
        for (Cluster c : this) {
            for (Vector v : c) {
                final String cla = v.getClassName();
                Integer count = classes.get(cla);
                if (count != null) {
                    count += 1;
                    classes.put(cla, count);
                } else {
                    classes.put(cla, 1);
                }
                totalCount++;
            }
        }

        double fcs = 0.0;
        for (Entry<String, Integer> classI : classes.entrySet()) {
            final int ni = classI.getValue();
            double maxF = Double.NEGATIVE_INFINITY;
            for (Cluster clusterJ : this) {
                final int nj = clusterJ.size();
                final int nij = getNij(clusterJ, classI.getKey());
                if (nij>0){
                    final double recall = ((double) nij) / ni;
                    final double pre = ((double) nij) / nj;
                    final double fij = (2 * recall * pre) / (recall + pre);
                    if (Double.compare(maxF, fij) < 0) {
                        maxF = fij;
                    }
                }
            }
            assert !Double.isInfinite(maxF);
            fcs += ni * maxF;
        }
        fcs /= totalCount;
        return fcs;
    }

    private static int getNij(Cluster c, String cla) {
        int count = 0;
        for (Vector v : c) {
            if (cla.equals(v.getClassName())) {
                count++;
            }
        }
        return count;
    }

    public double overallSimilarity() {
        double sum = 0;
        for (Cluster c : this) {
            sum += c.internalSim() * c.size();
        }

        int totalCount = 0;
        for (Cluster c : this) {
            totalCount += c.size();
        }
        sum /= totalCount;
        return sum;
    }

    public double silhouetteCoefficient() {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < this.size(); i++) {
            for (int j = 0; j < this.get(i).size(); j++) {
                final double sc = getSC(i, j);
                sum += sc;
                count++;
            }
        }
        sum /= count;
        return sum;
    }

    private double getSC(int i, int j) {
        final Vector cur = this.get(i).get(j);
        final Cluster c = this.get(i);
        double a = 0;
        if (c.size() > 1) { // if only one vector, avg dis is 0.
            for (int k = 0; k < c.size(); k++) {
                if (k != j) {
                    final double dis = cur.distanceTo(c.get(k));
                    a += dis;
                }
            }
            a /= c.size();
        }

        double b = Double.POSITIVE_INFINITY;
        for (int k = 0; k < this.size(); k++) {
            if (k != i) {
                final Cluster c2 = this.get(k);
                double avgdis = 0;
                for (Vector v : c2) {
                    avgdis += cur.distanceTo(v);
                }
                avgdis /= c2.size();
                if (Double.compare(b, avgdis) > 0) {
                    b = avgdis;
                }
            }
        }
        assert !Double.isInfinite(b);
        return (b - a) / Math.max(a, b);
    }
}
