package bridgeCut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.Dbg;

/**
 * FileName: Graph.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 6, 2014 7:04:59 PM
 */
public class Graph extends HashMap<String, Node> {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<String, Node> e : this.entrySet()) {
            sb.append(e.getValue() + Dbg.NEW_LINE);
        }
        return sb.toString();
    }

    public static List<Graph> bridgeCut (Graph gBackup) {
        final Graph g = new Graph();
        for (java.util.Map.Entry<String, Node> e : gBackup.entrySet()) {
            g.put(e.getKey(), e.getValue());
        }
        final List<Graph> clusterList = new ArrayList<Graph>();

        while (!g.isEmpty()) {

        }

        return clusterList;
    }

    public static void allShortestPath (Graph g) {
        // FloydWarshallWithPathReconstruction .
        // http://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm

        final String[] nodeNames = g.keySet().toArray(new String[0]);
        final double[][] dist = new double[nodeNames.length][];
        final PossibleNext[][] next = new PossibleNext[nodeNames.length][];
        for (int i = 0; i < dist.length; i++) {
            dist[i] = new double[nodeNames.length];
            next[i] = new PossibleNext[nodeNames.length];
            for (int j = 0; j < dist[i].length; j++) {
                // Dist = 0, if i == j;
                // Dist = Double.POSITIVE_INFINIT, if no edge between i, j.
                final String ni = nodeNames[i];
                final String nj = nodeNames[j];
                final Node nodeI = g.get(ni);
                final double d = nodeI.getDistanceTo(nj);
                dist[i][j] = d;
                if (nodeI.hasEdge(nj)) {
                    next[i][j] = new PossibleNext(j);
                } else { // No edge between i, j.
                    next[i][j] = null;
                }
            }
        }

        for (int k = 0; k < dist.length; k++) {
            for (int i = 0; i < dist.length; i++) {
                for (int j = 0; j < dist.length; j++) {
                    if (!Double.isInfinite(dist[i][k])
                            && !Double.isInfinite(dist[k][j]) && i != j
                            && j != k && k != i) {
                        if (Double.compare(dist[i][k] + dist[k][j], dist[i][j]) < 0) {
                            // Shorter path.
                            dist[i][j] = dist[i][k] + dist[k][j];
                            // Delete all old nexts, add all new nexts.
                            next[i][j] = new PossibleNext(next[i][k]);
                        } else if (Double.compare(dist[i][k] + dist[k][j],
                                dist[i][j]) == 0) {
                            // Same distance, add all additional nexts.
                            next[i][j].addAll(next[i][k]);
                        }
                    } // if (!Double.isInfinite(dist[i][k])
                } // for (int j = 0; j < dist.length; j++) {
            } // for (int i = 0; i < dist.length; i++) {
        } // for (int k = 0; k < dist.length; k++) {

        for (int i = 0; i < dist.length; i++) {
            for (int j = 0; j < dist.length; j++) {
                System.out.println(nodeNames[i]+" ---- "+nodeNames[j]);
                final ArrayList<String> pathesOUT = path(i, j, next, nodeNames);
                if (pathesOUT != null) {
                    for (String s : pathesOUT) {
                        System.out.println(s);
                    }
                }else {
                    System.out.println("No path");
                }
            }
        }
    }

    private static ArrayList<String> path (int u, int v,
            final PossibleNext[][] next, String[] nodeNames) {
        if (next[u][v] == null) {
            return null; // No path.
        }
        final ArrayList<String> pathesOUT = new ArrayList<String>();
        pathCon(u, v, next, nodeNames, nodeNames[u], pathesOUT);
        return pathesOUT;
    }

    private static void pathCon (int u, int v, PossibleNext[][] next,
            String[] nodeNames, String path, ArrayList<String> pathesOUT) {
        if (u == v) { // Leaf.
            pathesOUT.add(path);
        } else {
            for (Integer i : next[u][v]) {
                pathCon(i, v, next, nodeNames, path + " " + nodeNames[i],
                        pathesOUT);
            }
        }
    }

    private static class Path extends ArrayList<String> {
        private static final long serialVersionUID = 1L;

        public Path(String s) {
            super();
            this.add(s);
        }

        public Path(Path p, String s) {
            super();
            this.addAll(p);
            this.add(s);
        }
    }

    private static class PossibleNext extends ArrayList<Integer> {
        private static final long serialVersionUID = 1L;

        public PossibleNext(int i) {
            super();
            this.add(i);
        }

        public PossibleNext(PossibleNext o) {
            super();
            this.addAll(o);
        }
    }
}
