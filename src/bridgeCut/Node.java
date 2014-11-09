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
    // Name should not contain space, because shortest path is using space to
    // split nodes.
    public String name;
    private HashMap<String, Double> neighbors;

    public Node(String name) {
        this.name = name;
        this.neighbors = new HashMap<String, Double>();
    }

    public void addNeighbor (String n, double weight) {
        this.neighbors.put(n, weight);
    }

    public void addNeighbor (String n) {
        addNeighbor(n, 1);
    }

    public boolean hasEdge (String n) {
        return neighbors.get(n) != null;
    }

    public double getDistanceTo (String name2) {
        if (this.name.equals(name2)) {
            return 0;
        } else if (neighbors.get(name2) != null) {
            return neighbors.get(name2);
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
        for (Entry<String, Double> e : neighbors.entrySet()) {
            sb.append(e.toString() + ", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
