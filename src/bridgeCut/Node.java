package bridgeCut;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * FileName: Node.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 6, 2014 7:03:12 PM
 */
public class Node {
    public String name;
    private HashMap<String, Double> edges;

    public Node(String name) {
        this.name = name;
        this.edges = new HashMap<String, Double>();
    }

    public void addEdge (String n, double weight) {
        this.edges.put(n, weight);
    }

    public void addEdge (String n) {
        addEdge(n, 1);
    }

    public boolean hasEdge (String n) {
        return edges.get(n) != null;
    }

    public double getDistanceTo (String name2) {
        if (this.name.equals(name2)) {
            return 0;
        } else if (edges.get(name2) != null) {
            return edges.get(name2);
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    @Override
    public String toString () {
        final StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" ");
        sb.append("[");
        for (Entry<String, Double> e : edges.entrySet()) {
            sb.append(e.toString() + ", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
