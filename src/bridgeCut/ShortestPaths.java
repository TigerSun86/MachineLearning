package bridgeCut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import util.Dbg;

/**
 * FileName: ShortestPaths.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 8, 2014 4:21:40 PM
 */
public class ShortestPaths {
    public static final String MODULE = "SPA";
    public static final boolean DBG = false;
    // Always calculate shortest path as directed path.
    private final boolean isDirected;
    private final HashMap<Pair, Set<Path>> paths;

    public ShortestPaths(Graph g) {
        this(g, false);
    }

    public ShortestPaths(Graph g, boolean isDirected) {
        this.isDirected = isDirected;
        paths = new HashMap<Pair, Set<Path>>();
        this.allShortestPath(g);
        Dbg.print(DBG, MODULE, Dbg.NEW_LINE + this.toString());
    }

    public void add (String n1, String n2, Path p) {
        Set<Path> ps = paths.get(new Pair(n1, n2, isDirected));
        if (ps == null) {
            ps = new HashSet<Path>();
            paths.put(new Pair(n1, n2, isDirected), ps);
        }
        ps.add(p);
    }

    public Set<Path> get (String n1, String n2) {
        return paths.get(new Pair(n1, n2, isDirected));
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder();
        for (Entry<Pair, Set<Path>> e : paths.entrySet()) {
            Pair pair = e.getKey();
            sb.append(pair + Dbg.NEW_LINE);
            for (Path p : e.getValue()) {
                sb.append(p + Dbg.NEW_LINE);
            }
        }
        return sb.toString();
    }

    public void allShortestPath (Graph g) {
        // Modified from FloydWarshallWithPathReconstruction .
        // http://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm

        // Initialize.
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
                if (nodeI.hasNeighbor(nj)) {
                    next[i][j] = new PossibleNext(j);
                } else { // No edge between i, j.
                    next[i][j] = null;
                }
            }
        }
        // FloydWarshall
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

        // Path reconstruction.
        for (int i = 0; i < dist.length; i++) {
            for (int j = 0; j < dist.length; j++) {
                final ArrayList<String> pathsOUT =
                        pathConstruct(i, j, next, nodeNames);
                if (pathsOUT != null) { // Has shortest path.
                    for (String oneP : pathsOUT) {
                        this.add(nodeNames[i], nodeNames[j], new Path(oneP,
                                this.isDirected));
                    }
                }
            }
        }
    }

    private static ArrayList<String> pathConstruct (int u, int v,
            final PossibleNext[][] next, String[] nodeNames) {
        if (next[u][v] == null) {
            return null; // No path.
        }
        final ArrayList<String> pathsOUT = new ArrayList<String>();
        pathConRecur(u, v, next, nodeNames, nodeNames[u], pathsOUT);
        return pathsOUT;
    }

    private static void pathConRecur (int u, int v, PossibleNext[][] next,
            String[] nodeNames, String path, ArrayList<String> pathsOUT) {
        if (u == v) { // Leaf.
            pathsOUT.add(path);
        } else {
            for (Integer i : next[u][v]) {
                pathConRecur(i, v, next, nodeNames, path + " " + nodeNames[i],
                        pathsOUT);
            }
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
