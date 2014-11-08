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

    public double betweenness () {
        final ShortestPaths sps = new ShortestPaths(this);
        final HashMap<String, Double> betweenness =
                new HashMap<String, Double>();
        for (String n : this.keySet()) { // Initialize betweenness to 0.
            betweenness.put(n, 0.0);
        }
        for (String n1 : this.keySet()) {
            for (String n2 : this.keySet()) {
                if (!n1.equals(n2)) {
                    final List<Path> ps = sps.get(n1, n2);
                    if (ps != null) {
                        // Count the times of occurring of all internal nodes in
                        // paths between n1 and n2.
                        final HashMap<String, Double> counter =
                                new HashMap<String, Double>();
                        for (Path p : ps) {
                            for (int i = 1; i <= p.size() - 2; i++) {
                                // Count for all nodes internal the path.
                                final String nInternal = p.get(i);
                                Double count = counter.get(nInternal);
                                if (count == null) {
                                    count = 0.0;
                                }
                                counter.put(nInternal, count + 1.0);
                            }
                        } // for (Path p : ps) {

                        // Update the betweenness of all internal nodes in
                        // paths between n1 and n2.
                        for (java.util.Map.Entry<String, Double> e : counter
                                .entrySet()) {
                            String n = e.getKey();
                            Double count = e.getValue();
                            // Times n occurred over # of shortest paths between
                            // n1 and n2.
                            Double bNew = count / ps.size();
                            Double bOld = betweenness.get(n);
                            betweenness.put(n, bOld + bNew);
                        }
                    }
                }

            }
        }
        return 0;
    }
}
