package bridgeCut;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
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
    public static final String MODULE = "GRAPH";
    public static final boolean DBG = true;

    private final boolean isDirected;

    public Graph(boolean isDirected) {
        this.isDirected = isDirected;
    }

    public Graph() {
        this(false);
    }

    public Set<String> getNodeNames () {
        return this.keySet();
    }
    
    public boolean isDirected(){
        return this.isDirected;
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<String, Node> e : this.entrySet()) {
            sb.append(e.getValue() + Dbg.NEW_LINE);
        }
        return sb.toString();
    }

    public static String graphListToString (List<Graph> gs) {
        // Print the graph with maximum size first.
        final StringBuilder sb = new StringBuilder();
        final Sorter<Graph> sorter = new Sorter<Graph>();
        for (Graph g : gs) {
            sorter.add(g, g.size());
        }
        final List<Graph> gSorted = sorter.sortDescend();
        for (int i = 0; i < gSorted.size(); i++) {
            sb.append("Graph " + i + Dbg.NEW_LINE);
            sb.append(gSorted.get(i).toString());
        }
        return sb.toString();
    }

    public List<Graph> bridgeCut (double dThreshold, boolean isNode,
            boolean isCentrality) {
        return bridgeCut(this, dThreshold, isNode, isCentrality);
    }

    public static List<Graph> bridgeCut (Graph gBackup, double dThreshold,
            boolean isNode, boolean isCentrality) {
        final Graph g = new Graph();
        for (java.util.Map.Entry<String, Node> e : gBackup.entrySet()) {
            g.put(e.getKey(), new Node(e.getValue()));
        }
        final List<Graph> clusterList = new ArrayList<Graph>();

        final HashSet<Set<String>> oldIsolatedGraphs =
                new HashSet<Set<String>>();
        oldIsolatedGraphs.add(gBackup.getNodeNames());

        while (!g.isEmpty()) {
            final Object topCut = g.oneCut(isNode, isCentrality);

            if (isNode) { // The cut node is a single cluster, add it.
                final Node tempN = new Node((String) topCut);
                final Graph temp = new Graph(g.isDirected);
                temp.put(tempN.name, tempN);
                clusterList.add(temp);
                Dbg.print(DBG, MODULE, "Cut: " + topCut + " added");
            } else { // Cut edge.
                Dbg.print(DBG, MODULE, "Cut: " + topCut);
            }
            
            final Set<Set<String>> isolatedGs = g.isolatedGraphs();
            for (Set<String> nodeNames : isolatedGs) {
                if (!oldIsolatedGraphs.contains(nodeNames)) {
                    // New isolated graph.
                    final double density = gBackup.density(nodeNames);
                    if (Double.compare(density, dThreshold) > 0) {
                        // Add the sub graph with original edges in gBackup.
                        clusterList.add(gBackup.subGraph(nodeNames));
                        for (String n : nodeNames) {
                            g.cut(n); // Remove isolated graph from g.
                        }
                        Dbg.print(DBG, MODULE, "New sub graph: " + nodeNames
                                + " added");
                    } else { // The sub graph still is there.
                        oldIsolatedGraphs.add(nodeNames);
                        Dbg.print(DBG, MODULE, "New sub graph: " + nodeNames);
                    }
                } // if (!oldIsolatedGraphs.contains(nodeNames)) {
            } // for (Graph ig : isolatedGs) {
        } // while (!g.isEmpty()) {

        return clusterList;
    }

    private Object oneCut (boolean isNode, boolean isCentrality) {
        if (isNode) { // Cut node.
            final List<String> nodes;
            if (isCentrality) {
                nodes = this.centralityOfNodes();
            } else { // Betweenness.
                nodes = this.betweennessOfNodes();
            }
            final String topNode = nodes.get(0);
            this.cut(topNode);
            return topNode;
        } else { // Cut edge.
            final List<Pair> edges;
            if (isCentrality) {
                edges = this.centralityOfEdges();
            } else { // Betweenness.
                edges = this.betweennessOfEdges();
            }
            final Pair topEdge = edges.get(0);
            this.cut(topEdge);
            return topEdge;
        }
    }

    private double density (Set<String> nodeNames) {
        if (nodeNames.size() == 1) {
            return Double.POSITIVE_INFINITY;
        }
        double sum = 0;
        for (String n : nodeNames) {
            final Set<String> neiNames = this.get(n).getNeighborNames();
            for (String nei : neiNames) {
                if (nodeNames.contains(nei)) {
                    sum++;
                }
            }
        }
        sum /= (nodeNames.size() * (nodeNames.size() - 1));
        return sum;
    }

    private Set<Set<String>> isolatedGraphs () {
        final String[] nodeNames = this.getNodeNames().toArray(new String[0]);
        final HashMap<String, Integer> name2Idx =
                new HashMap<String, Integer>();
        for (int i = 0; i < nodeNames.length; i++) {
            name2Idx.put(nodeNames[i], i);
        }
        final BitSet total = new BitSet(nodeNames.length);
        Set<BitSet> subGs = new HashSet<BitSet>();
        while (total.cardinality() < nodeNames.length) {
            final BitSet subG = traversal(nodeNames, name2Idx, total);
            total.or(subG);
            subGs = merge(subGs, subG);
        }
        // Convert bitsets to name sets
        final Set<Set<String>> nameSets = new HashSet<Set<String>>();
        for (BitSet subG : subGs) {
            final Set<String> names = new HashSet<String>();
            for (int i = 0; i < nodeNames.length; i++) {
                if (subG.get(i)) {
                    names.add(nodeNames[i]);
                }
            }
            nameSets.add(names);
        }
        return nameSets;
    }

    private Graph subGraph (Set<String> names) {
        final Graph g = new Graph(isDirected);
        for (java.util.Map.Entry<String, Node> e : this.entrySet()) {
            final Node n1 = e.getValue();
            if (names.contains(n1.name)) {
                final Node n2 = new Node(n1.name);
                for (java.util.Map.Entry<String, Double> e2 : n1.getNeighbors()
                        .entrySet()) {
                    if (names.contains(e2.getKey())) {
                        // Only add neighbors existing in subgraph.
                        n2.addNeighbor(e2.getKey(), e2.getValue());
                    }
                }
                g.put(n2.name, n2);
            }
        }
        return g;
    }

    private static Set<BitSet>
            merge (final Set<BitSet> subGs, final BitSet newG) {
        // a and b have no intersect, but after a = a+c, a and b could has some
        // intersect, so they can be merged.
        final Set<BitSet> newSubGs = new HashSet<BitSet>();
        for (BitSet oldG : subGs) {
            if (newG.intersects(oldG)) {
                // New sub graph is connected to old graph.
                // Append old sub graph to new, and add to newSubGs later.
                newG.or(oldG);
            } else {
                // The oldG is a single graph not related to newG.
                newSubGs.add(oldG);
            }
        }
        newSubGs.add(newG); // Add the big single graph a+b+c.
        return newSubGs;
    }

    private BitSet traversal (String[] nodeNames,
            HashMap<String, Integer> name2Idx, BitSet total) {
        // Breadth first search.
        final int startIdx = total.nextClearBit(0);
        assert startIdx >= 0 && startIdx < nodeNames.length;
        final PriorityQueue<Integer> que = new PriorityQueue<Integer>();
        que.add(startIdx);
        final BitSet visited = new BitSet(nodeNames.length);
        visited.set(startIdx);
        while (!que.isEmpty()) {
            final Integer idx = que.remove();
            final String name = nodeNames[idx];
            final Node node = this.get(name);
            for (String nei : node.getNeighborNames()) {
                final int idxOfNei = name2Idx.get(nei);
                if (!visited.get(idxOfNei)) {
                    if (!total.get(idxOfNei)) {
                        // Haven't visited by previous sub graph traversal.
                        que.add(idxOfNei);
                        visited.set(idxOfNei);
                    } else {
                        // Already visited by other sub graph, no need to expand
                        // it again, but need to mark it as visited, to prove
                        // this sub graph have intersection with other sub
                        // graph.
                        visited.set(idxOfNei);
                    }
                }
            }
        }
        return visited;
    }

    private void cut (Pair edge) {
        if (isDirected) {
            this.get(edge.n1).removeNeighbor(edge.n2);
        } else { // Undirected.
            this.get(edge.n1).removeNeighbor(edge.n2);
            this.get(edge.n2).removeNeighbor(edge.n1);
        }
    }

    private void cut (String n) {
        // Remove node.
        this.remove(n);
        for (java.util.Map.Entry<String, Node> e : this.entrySet()) {
            final Node node = e.getValue();
            node.removeNeighbor(n);
        }
    }

    public List<String> centralityOfNodes () {
        List<String> bet = betweennessOfNodes();
        List<String> bridge = bridgeCoefficientOfNodes();
        HashMap<String, Integer> cen = new HashMap<String, Integer>();
        for (int rank = 0; rank < bet.size(); rank++) {
            cen.put(bet.get(rank), rank + 1);
        }
        for (int rank = 0; rank < bridge.size(); rank++) {
            cen.put(bridge.get(rank), cen.get(bridge.get(rank)) * (rank + 1));
        }
        final Sorter<String> sorter = new Sorter<String>();
        for (java.util.Map.Entry<String, Integer> e : cen.entrySet()) {
            sorter.add(e.getKey(), e.getValue());
        }
        List<String> list = sorter.sortAscend();
        Dbg.print(DBG, MODULE, "Centrality of nodes");
        for (String s : list) {
            Dbg.print(DBG, MODULE, s + " " + cen.get(s));
        }
        return list;
    }

    public List<Pair> centralityOfEdges () {
        List<Pair> bet = betweennessOfEdges();
        List<Pair> bridge = bridgeCoefficientOfEdges();
        HashMap<Pair, Integer> cen = new HashMap<Pair, Integer>();
        for (int rank = 0; rank < bet.size(); rank++) {
            cen.put(bet.get(rank), rank + 1);
        }
        for (int rank = 0; rank < bridge.size(); rank++) {
            cen.put(bridge.get(rank), cen.get(bridge.get(rank)) * (rank + 1));
        }
        final Sorter<Pair> sorter = new Sorter<Pair>();
        for (java.util.Map.Entry<Pair, Integer> e : cen.entrySet()) {
            sorter.add(e.getKey(), e.getValue());
        }
        List<Pair> list = sorter.sortAscend();
        Dbg.print(DBG, MODULE, "Centrality of edges");
        for (Pair s : list) {
            Dbg.print(DBG, MODULE, s + " " + cen.get(s));
        }
        return list;
    }

    /* Betweenness begin ** */
    private List<String> betweennessOfNodes () {
        HashMap<Object, Double> bet = betweenness(true);
        final Sorter<String> sorter = new Sorter<String>();
        for (java.util.Map.Entry<Object, Double> e : bet.entrySet()) {
            sorter.add((String) e.getKey(), e.getValue());
        }
        List<String> list = sorter.sortDescend();

        Dbg.print(DBG, MODULE, "Betweenness of nodes");
        for (String s : list) {
            Dbg.print(DBG, MODULE, s + " " + bet.get(s));
        }
        return list;
    }

    private List<Pair> betweennessOfEdges () {
        HashMap<Object, Double> bet = betweenness(false);
        final Sorter<Pair> sorter = new Sorter<Pair>();
        for (java.util.Map.Entry<Object, Double> e : bet.entrySet()) {
            sorter.add((Pair) e.getKey(), e.getValue());
        }
        List<Pair> list = sorter.sortDescend();

        Dbg.print(DBG, MODULE, "Betweenness of edges");
        for (Pair s : list) {
            Dbg.print(DBG, MODULE, s + " " + bet.get(s));
        }
        return list;
    }

    private HashMap<Object, Double> betweenness (boolean isNode) {
        final ShortestPaths sps = new ShortestPaths(this, this.isDirected);
        final HashMap<Object, Double> betweenness =
                new HashMap<Object, Double>();

        // For each pair of nodes n1, n2.
        final String[] nodeNames = this.getNodeNames().toArray(new String[0]);
        if (isNode) { // Initialize all nodes.
            for (String n : nodeNames) {
                betweenness.put(n, 0.0);
            }
        }

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
                        } // for (java.util.Map.Entry<String, Double> e
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

    /* Betweenness end ** */

    /* BridgeCoefficient begin ** */
    private List<String> bridgeCoefficientOfNodes () {
        final Sorter<String> sorter = new Sorter<String>();
        final HashMap<String, Double> bcs = new HashMap<String, Double>();
        for (String node : this.keySet()) {
            final double bc = bcOfNode(node);
            bcs.put(node, bc);
            sorter.add(node, bc);
        }
        List<String> list = sorter.sortDescend();

        Dbg.print(DBG, MODULE, "Bridge coefficient of nodes");
        for (String s : list) {
            Dbg.print(DBG, MODULE, s + " " + bcs.get(s));
        }
        return list;
    }

    private List<Pair> bridgeCoefficientOfEdges () {
        final Sorter<Pair> sorter = new Sorter<Pair>();
        final HashMap<Pair, Double> bcs = new HashMap<Pair, Double>();
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
                if (this.get(n1).hasNeighbor(n2)) {
                    final Pair pair = new Pair(n1, n2, isDirected);
                    final double bc = bcOfEdge(pair);
                    bcs.put(pair, bc);
                    sorter.add(pair, bc);
                }
            }
        }

        List<Pair> list = sorter.sortDescend();

        Dbg.print(DBG, MODULE, "Bridge coefficient of edges");
        for (Object s : list) {
            Dbg.print(DBG, MODULE, s + " " + bcs.get(s));
        }
        return list;
    }

    private double bcOfNode (String node) {
        int degreeOfNode = this.get(node).getDegree();
        if (degreeOfNode == 0) {
            return 0;
        }
        double sum = 0;
        for (String neighbor : this.get(node).getNeighborNames()) {
            int degree = this.get(neighbor).getDegree();
            if (degree > 1) { // If degree == 1, don't count it (value = 0).
                int delta = delta(node, neighbor);
                sum += delta / (degree - 1.0);
            }
        }
        return sum / degreeOfNode;
    }

    private double bcOfEdge (Pair edge) {
        final String i = edge.n1;
        final String j = edge.n2;
        final int degreeI = this.get(i).getDegree();
        final int degreeJ = this.get(j).getDegree();
        final double bcI = bcOfNode(i);
        final double bcJ = bcOfNode(j);

        final Set<String> neisOfI = this.get(i).getNeighborNames();
        final Set<String> neisOfJ = this.get(j).getNeighborNames();
        final HashSet<String> allNeis = new HashSet<String>();
        allNeis.addAll(neisOfI);
        allNeis.addAll(neisOfJ);
        final int commonNei = neisOfI.size() + neisOfJ.size() - allNeis.size();
        double sum = degreeI * bcI + degreeJ * bcJ;
        sum /= (degreeI + degreeJ);
        sum /= (commonNei + 1);
        return sum;
    }

    private int delta (String src, String nei) {
        // Neighborhood of src and src itself.
        final HashSet<String> neighborhood = new HashSet<String>();
        neighborhood.addAll(this.get(src).getNeighborNames());
        neighborhood.add(src);
        int sum = 0;
        for (String neiOfNei : this.get(nei).getNeighborNames()) {
            if (!neighborhood.contains(neiOfNei)) {
                // The edge doesn't come back to the neighborhood subgraph.
                sum++;
            }
        }
        return sum;
    }

    /* BridgeCoefficient end ** */

    /* BridgeCoefficient2 begin ** */
    private List<String> bridgeCoefficientOfNodes2 () {
        final Sorter<String> sorter = new Sorter<String>();
        final HashMap<String, Double> bcs = new HashMap<String, Double>();
        final HashMap<String, Integer> deltaMemo =
                new HashMap<String, Integer>();
        for (String node : this.keySet()) {
            final double bc = bcOfNode2(node, deltaMemo);
            bcs.put(node, bc);
            sorter.add(node, bc);
        }
        List<String> list = sorter.sortDescend();

        Dbg.print(DBG, MODULE, "Bridge coefficient of nodes");
        for (String s : list) {
            Dbg.print(DBG, MODULE, s + " " + bcs.get(s));
        }
        return list;
    }

    private List<Pair> bridgeCoefficientOfEdges2 () {
        final Sorter<Pair> sorter = new Sorter<Pair>();
        final HashMap<Pair, Double> bcs = new HashMap<Pair, Double>();
        final String[] nodeNames = this.keySet().toArray(new String[0]);
        for (int i = 0; i < nodeNames.length; i++) {
            final String n1 = nodeNames[i];
            final int length;
            if (isDirected) {
                length = nodeNames.length;
            } else { // Undirected graph, only visit lower triangle.
                length = i;
            }
            final HashMap<String, Integer> deltaMemo =
                    new HashMap<String, Integer>();
            for (int j = 0; j < length; j++) {
                final String n2 = nodeNames[j];
                if (this.get(n1).hasNeighbor(n2)) {
                    final Pair pair = new Pair(n1, n2, isDirected);
                    final double bc = bcOfEdge2(pair, deltaMemo);
                    bcs.put(pair, bc);
                    sorter.add(pair, bc);
                }
            }
        }

        List<Pair> list = sorter.sortDescend();

        Dbg.print(DBG, MODULE, "Bridge coefficient of edges");
        for (Object s : list) {
            Dbg.print(DBG, MODULE, s + " " + bcs.get(s));
        }
        return list;
    }

    private double bcOfNode2 (String node, HashMap<String, Integer> deltaMemo) {
        int degreeOfNode = this.get(node).getDegree();
        if (degreeOfNode == 0) {
            return 0;
        }
        double sum = 0;
        for (String neighbor : this.get(node).getNeighborNames()) {
            int degree = this.get(neighbor).getDegree();
            if (degree > 1) { // If degree == 1, don't count it.
                Integer delta = deltaMemo.get(neighbor);
                if (delta == null) {
                    delta = delta2(neighbor);
                    deltaMemo.put(neighbor, delta);
                }
                sum += delta / (degree - 1.0);
            }
        }
        return sum / degreeOfNode;
    }

    private double bcOfEdge2 (Pair edge, HashMap<String, Integer> deltaMemo) {
        final String i = edge.n1;
        final String j = edge.n2;
        final int degreeI = this.get(i).getDegree();
        final int degreeJ = this.get(j).getDegree();
        final double bcI = bcOfNode2(i, deltaMemo);
        final double bcJ = bcOfNode2(j, deltaMemo);

        final Set<String> neisOfI = this.get(i).getNeighborNames();
        final Set<String> neisOfJ = this.get(j).getNeighborNames();
        final HashSet<String> allNeis = new HashSet<String>();
        allNeis.addAll(neisOfI);
        allNeis.addAll(neisOfJ);
        final int commonNei = neisOfI.size() + neisOfJ.size() - allNeis.size();
        double sum = degreeI * bcI + degreeJ * bcJ;
        sum /= (degreeI + degreeJ);
        sum /= (commonNei + 1);
        return sum;
    }

    private int delta2 (String node) {
        // Neighborhood of node.
        final HashSet<String> neis = new HashSet<String>();
        neis.addAll(this.get(node).getNeighborNames());
        int sum = 0;
        for (String neighbor : neis) {
            for (String edge : this.get(neighbor).getNeighborNames()) {
                if (!neis.contains(edge) && !edge.equals(node)) {
                    // The edge doesn't come back to the subgraph: node's
                    // neighbors and node it self.
                    sum++;
                }
            }
        }
        return sum;
    }
    /* BridgeCoefficient2 end ** */
}
