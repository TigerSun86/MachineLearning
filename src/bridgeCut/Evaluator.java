package bridgeCut;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * FileName: Evaluator.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 11, 2014 3:12:40 PM
 */
public class Evaluator {

    public static double davisBouldinIndex (List<Graph> subGs, Graph originalG) {
        if (subGs.isEmpty() || subGs.size() == 1) {
            return 0;
        }
        double sum = 0.0;
        final ShortestPaths sps = new ShortestPaths(originalG);
        for (int i = 0; i < subGs.size(); i++) {
            final Graph gi = subGs.get(i);
            final double diamGi = diameter(gi);
            double maxValue = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < subGs.size(); j++) {
                if (i != j) {
                    final Graph gj = subGs.get(j);
                    final double diamGj = diameter(gj);
                    final double dis = distance(gi, gj, sps);
                    final double value;
                    if (Double.compare(dis, 0.0) == 0) {
                        // gi and gj are exactly the same (should not happen).
                        value = Double.POSITIVE_INFINITY;
                    } else {
                        value = (diamGi + diamGj) / dis;
                    }
                    if (Double.compare(maxValue, value) < 0) {
                        maxValue = value;
                    }
                }

            }
            assert Double.compare(maxValue, 0) > 0;
            sum += maxValue;
        }

        return sum / subGs.size();
    }

    /**
     * diameter = longest shortest path of g.
     * If g = singleton, diameter = 0
     * If g is a graph not fully connected, diameter = longest shortest path in
     * connected part.
     * @return 0 <= diameter < infinity
     * */
    private static double diameter (Graph g) {
        if (g.size() == 1) {
            return 0;
        } else {
            // Diameter is the longest shortest path of g.
            double maxLength = 0;// Max length is at least 0.
            final ShortestPaths sps = new ShortestPaths(g);
            for (Entry<Pair, Set<Path>> e : sps.entrySet()) {
                final Path path = e.getValue().iterator().next();
                if (Double.compare(maxLength, path.getLength()) < 0) {
                    maxLength = path.getLength();
                }
            }
            return maxLength;
        }
    }

    /**
     * The distance between 2 subgraph gi and gj in the original graph.
     * Distance could be infinity if gi and gj have no path between each other.
     * 
     * @return 1, 0 < distance <= infinity, if there is no overlapped nodes in
     *         gi, gj;
     *         2, 0 <= distance <= infinity, if there are overlapped nodes in
     *         gi, gj (should not happen).
     * */
    private static double distance (Graph gi, Graph gj, ShortestPaths sps) {
        double sum = 0;
        int count = 0;

        for (String ni : gi.getNodeNames()) {
            for (String nj : gj.getNodeNames()) {
                double dis = disBetNodes(ni, nj, sps);
                if (!Double.isInfinite(dis)) {
                    // Count only when there are paths between ni and nj, or ni
                    // nj are the same.
                    sum += dis;
                    count++;
                }
                if (gi.isDirected()) {
                    // Calculate the reverse direction too,
                    // if it's a directed graph.
                    dis = disBetNodes(nj, ni, sps);
                    if (!Double.isInfinite(dis)) {
                        sum += dis;
                        count++;
                    }
                }
            }
        }
        if (count == 0) {
            // No path between gi gj in original g (could not happen if the
            // original data is well defined.)
            return Double.POSITIVE_INFINITY;
        } else {
            return sum / count;
        }
    }

    private static double disBetNodes (String ni, String nj, ShortestPaths sps) {
        final double dis;
        if (ni.equals(nj)) {
            // distance is 0 if ni == nj. (should not happen)
            dis = 0;
        } else {
            final Set<Path> path = sps.get(ni, nj);
            if (path != null) { // There are paths between ni and nj.
                dis = path.iterator().next().getLength();
            } else { // No path.
                dis = Double.POSITIVE_INFINITY;
            }
        }
        return dis;
    }

    /**
     * @return -1<=sc<=1, -1 is worst, 1 is best
     */
    public static double silhouetteCoefficient (List<Graph> subGs,
            Graph originalG) {
        if (subGs.isEmpty() || subGs.size() == 1) {
            return 1;// Best situation (should not happen).
        }
        double sum = 0;
        int count = 0;
        final ShortestPaths sps = new ShortestPaths(originalG);
        for (int i = 0; i < subGs.size(); i++) {
            final Graph gi = subGs.get(i);
            for (String ni : gi.getNodeNames()) {
                final double sci;
                final double ai = getDistanceToGraph(ni, gi, sps);
                if (Double.isInfinite(ai)) { // (should not happen)
                    // ni disconnected with some nodes in original graph, the sc
                    // should be worse case: -1.
                    sci = -1;
                } else { // Normal case.
                    // Calculate b, minimum avgerage distance to other cluster.
                    double minDisToOther = Double.POSITIVE_INFINITY;
                    for (int j = 0; j < subGs.size(); j++) {
                        if (i != j) {
                            final Graph gj = subGs.get(j);
                            final double dis = getDistanceToGraph(ni, gj, sps);
                            if (Double.compare(minDisToOther, dis) > 0) {
                                minDisToOther = dis;
                            }
                        }
                    }
                    sci = (minDisToOther - ai) / Math.max(minDisToOther, ai);
                }
                sum += sci;
                count++;
            }
        }
        return sum / count;
    }

    /**
     * The average distance from node ni to sub graph g.
     * if g has only one node which is ni, dis = 0.
     * if g has any node is not accessable from ni, dis = infinity.
     * 
     * @return 0<=dis<=infinity
     */
    private static double getDistanceToGraph (String ni, Graph g,
            ShortestPaths sps) {
        double sum = 0;
        int count = 0;
        boolean hasInfinity = false;
        for (String nj : g.getNodeNames()) {
            if (!ni.equals(nj)) {
                final Set<Path> paths = sps.get(ni, nj);
                if (paths == null) {
                    // Node ni cannot access nj (should not happen in well
                    // defined undirected graph)
                    hasInfinity = true;
                    break;
                } else {
                    final double dis = paths.iterator().next().getLength();
                    sum += dis;
                    count++;
                }
            }
        }
        if (hasInfinity) {
            // Node ni cannot access nj (should not happen in well defined
            // undirected graph)
            return Double.POSITIVE_INFINITY;
        } else if (count == 0) {
            return 0;
        } else {
            return sum / count;
        }
    }
}
