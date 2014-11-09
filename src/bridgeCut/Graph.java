package bridgeCut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import util.Dbg;
import util.Sorter;

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
    private final boolean isDirected;

    public Graph(boolean isDirected) {
        this.isDirected = isDirected;
    }

    public Graph() {
        this(false);
    }

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

    public void getB () {
        HashMap<Object, Double> bet = betweenness(false);
        final Sorter<Object> sorter = new Sorter<Object>();
        for (java.util.Map.Entry<Object, Double> e : bet.entrySet()) {
            sorter.add(e.getKey(), e.getValue());
        }
        List<Object> list = sorter.sortDescend();

        for (Object s : list) {
            System.out.println(s + " " + bet.get(s));
        }
    }

    public HashMap<Object, Double> betweenness (boolean isNode) {
        final ShortestPaths sps = new ShortestPaths(this, this.isDirected);
        final HashMap<Object, Double> betweenness =
                new HashMap<Object, Double>();
        // For each pair of nodes n1, n2.
        final String[] nodeNames = this.keySet().toArray(new String[0]);
        for (int i = 0; i < nodeNames.length; i++) {
            final String n1 = nodeNames[i];
            final int length;
            if (isDirected) {
                length = nodeNames.length;
            } else { // Undirected graph, only visit lower triangle.
                length = i;
            }

            for (int j = 0; j < length; j++) {
                final String n2 = nodeNames[j];
                if (!n1.equals(n2)) { // Not same node
                    final Set<Path> ps = sps.get(n1, n2);
                    if (ps != null) {
                        // Count the times of occurring of all internal nodes
                        // (edges) in paths between n1 and n2.
                        final HashMap<Object, Double> counter =
                                countWithin2Points(ps, isNode);

                        // Update the betweenness of all internal nodes (edges)
                        // in paths between n1 and n2.
                        for (java.util.Map.Entry<Object, Double> e : counter
                                .entrySet()) {
                            Object n = e.getKey();
                            Double count = e.getValue();
                            // Times n occurred over # of shortest paths between
                            // n1 and n2.
                            Double bNew = count / ps.size();
                            Double bOld = betweenness.get(n);
                            if (bOld == null) {
                                bOld = 0.0;
                            }
                            betweenness.put(n, bOld + bNew);
                        }// for (java.util.Map.Entry<String, Double> e : counter
                    } // if (ps != null) {
                } // if (!n1.equals(n2)) {
            } // for (int j = 0; j < length; j++) {
        } // for (int i = 0; i < nodeNames.length; i++) {
        return betweenness;
    }

    private HashMap<Object, Double> countWithin2Points (final Set<Path> ps,
            final boolean isNode) {
        // Count the times of occurring of all internal nodes (edges) in
        // paths between n1 and n2.
        final HashMap<Object, Double> counter = new HashMap<Object, Double>();
        for (Path p : ps) {
            if (isNode) {
                for (int i = 1; i <= p.size() - 2; i++) {
                    // Count for all nodes internal the path.
                    final String nInternal = p.get(i);
                    Double count = counter.get(nInternal);
                    if (count == null) {
                        count = 0.0;
                    }
                    counter.put(nInternal, count + 1.0);
                }
            } else { // is edge.
                for (int i = 0; i <= p.size() - 2; i++) {
                    // / Count for all edges.
                    final Pair pair =
                            new Pair(p.get(i), p.get(i + 1), isDirected);
                    Double count = counter.get(pair);
                    if (count == null) {
                        count = 0.0;
                    }
                    counter.put(pair, count + 1.0);
                }
            }
        } // for (Path p : ps) {
        return counter;
    }
}
