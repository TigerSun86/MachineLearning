package instancereduction;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;

import util.Dbg;

import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName:     RDI.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jul 5, 2014 12:11:42 AM 
 */
public class RDI implements Reducible {
    public static final String MODULE = "RDI";
    public static final boolean DBG = true;

    public static final int DEF_K = 3;
    private final int k;

    public RDI() {
        this.k = DEF_K;
    }

    public RDI(final int k) {
        this.k = k;
    }

    public RawExampleList reduce (final RawExampleList exs,
            final RawAttrList attrs) {

        final BitSet kept = reduceByRDI(exs, attrs, k);

        final RawExampleList ret = new RawExampleList();
        for (int i = 0; i < exs.size(); i++) {
            if (kept.get(i)) {
                ret.add(exs.get(i));
            }
        }
        Dbg.print(DBG, MODULE, "Reduced size: " + ret.size());
        return ret;
    }
    
    public static BitSet reduceByRDI(final RawExampleList exs,
            final RawAttrList attrs, final int k) {
        assert !attrs.t.isContinuous;

        final BitSet reduced = new BitSet(exs.size());
        // Reduce instances of each class seperately.
        for (int classi = 0; classi < attrs.t.valueList.size(); classi++) {
            final String classv = attrs.t.valueList.get(classi);
            // Copy all examples with same class into a new example set.
            final RawExampleList newExs = new RawExampleList();
            // Map from new index to old index.
            final HashMap<Integer, Integer> indexMap =
                    new HashMap<Integer, Integer>();
            for (int oldIndex = 0; oldIndex < exs.size(); oldIndex++) {
                final RawExample ex = exs.get(oldIndex);
                if (ex.t.equals(classv)) {
                    indexMap.put(newExs.size(), oldIndex);
                    newExs.add(ex);
                }
            }

            // Measure distances between each examples.
            final double[][] diss = ENN.getDistances(newExs, attrs);
            final int[] reducedTemp = reduceCentral(diss, k);
            for (int newIndex : reducedTemp) {
                final Integer oldIndex = indexMap.get(newIndex);
                assert oldIndex != null;
                reduced.set(oldIndex);
            }
        }
        reduced.flip(0, exs.size()); // Become kept.
        return reduced;
    }
    private static int[] reduceCentral (double[][] diss, int k) {
        final int numOfNodes = diss.length;
        if (numOfNodes < k + 1) {
            return new int[0];
        }
        // For each node, store its distances to neighbors (neighbor list).
        final NodeTableAndAverDis naa = initNeighborLists(diss, k);
        final ArrayList<ArrayList<Node>> nodesTable = naa.nodesTable;
        final double averKDis = naa.averKDis;

        final BitSet reduced = new BitSet(numOfNodes);
        while (true) {
            if (numOfNodes - reduced.cardinality() < k + 1) {
                // Remain nodes is less than k + 1.
                break;
            }
            // Find the node with shortest distance to its kth neighbor.
            final int idToReduce =
                    indexOfNodeWithNearestKthNeihbor(nodesTable, reduced, k);
            // The kth neighbor's index is DEF_K - 1.
            final double kDis = nodesTable.get(idToReduce).get(k - 1).dis;
            if (Double.compare(kDis, averKDis) < 0) {
                // Delete the node.
                reduced.set(idToReduce);
                // Remove the node from neighbor lists of all other nodes.
                removeFromNeighborLists(idToReduce, nodesTable, reduced);
            } else {
                break;
            }
        }

        // Get the id of nodes to be reduced.
        final int[] ret = new int[reduced.cardinality()];
        int index = 0;
        for (int idToReduce = reduced.nextSetBit(0); idToReduce >= 0; idToReduce =
                reduced.nextSetBit(idToReduce + 1)) {
            ret[index] = idToReduce;
            index++;
        }
        assert reduced.cardinality() == index;
        return ret;
    }

    private static class Node implements Comparable<Node> {
        public int id;
        public double dis;

        public Node(int id, double dis) {
            super();
            this.id = id;
            this.dis = dis;
        }

        @Override
        public int compareTo (Node arg0) {
            return Double.compare(this.dis, arg0.dis);
        }

        @Override
        public String toString () {
            return "[" + id + "," + dis + "]";
        }
    }

    private static class NodeTableAndAverDis {
        public final ArrayList<ArrayList<Node>> nodesTable;
        public final double averKDis;

        public NodeTableAndAverDis(ArrayList<ArrayList<Node>> nodesTable,
                double averKDis) {
            super();
            this.nodesTable = nodesTable;
            this.averKDis = averKDis;
        }
    }

    private static NodeTableAndAverDis initNeighborLists (double[][] diss, int k) {
        final int numOfNodes = diss.length;
        // For each node, store its distances to neighbors (neighbor list).
        final ArrayList<ArrayList<Node>> nodesTable =
                new ArrayList<ArrayList<Node>>();
        double kDisSum = 0; // For getting average.
        for (int id = 0; id < numOfNodes; id++) {
            final ArrayList<Node> neighborList = new ArrayList<Node>();
            for (int neighborId = 0; neighborId < diss[id].length; neighborId++) {
                if (id != neighborId) { // Don't add itself.
                    neighborList
                            .add(new Node(neighborId, diss[id][neighborId]));
                }
            }
            Collections.sort(neighborList); // Ascending.
            kDisSum += neighborList.get(k - 1).dis; // For getting average.
            nodesTable.add(neighborList); // Store neighbors info in table.
        }
        final double averKDis = kDisSum / numOfNodes;
        return new NodeTableAndAverDis(nodesTable, averKDis);
    }

    private static void removeFromNeighborLists (int idToReduce,
            ArrayList<ArrayList<Node>> nodesTable, BitSet reduced) {
        // Check neighbor lists of all nodes.
        for (int id = 0; id < nodesTable.size(); id++) {
            if (reduced.get(id)) {
                continue; // Don't check reduced nodes.
            }
            final ArrayList<Node> neighborList = nodesTable.get(id);
            for (int j = 0; j < neighborList.size(); j++) {
                final int neighborId = neighborList.get(j).id;
                if (neighborId == idToReduce) {
                    // Remove the specific neighbor.
                    neighborList.remove(j);
                    break;
                }
            }
        }
    }

    private static int indexOfNodeWithNearestKthNeihbor (
            final ArrayList<ArrayList<Node>> nodesTable, final BitSet reduced, final int k) {
        // Find the node with shortest distance to its kth neighbor.
        double min = Double.POSITIVE_INFINITY;
        int minId = -1;
        for (int id = 0; id < nodesTable.size(); id++) {
            if (reduced.get(id)) {
                continue; // Don't check reduced nodes.
            }
            final ArrayList<Node> neighborList = nodesTable.get(id);
            assert neighborList.size() >= k;
            final double dis = neighborList.get(k - 1).dis;
            if (Double.compare(min, dis) > 0) {
                min = dis;
                minId = id;
            }
        }
        assert minId != -1;
        return minId;
    }
}
