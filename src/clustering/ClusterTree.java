package clustering;

import java.util.PriorityQueue;

public class ClusterTree extends Cluster implements Comparable<ClusterTree> {
    // Store the whole cluster at each node, to save time by using space.
    private static final long serialVersionUID = 1L;

    public ClusterTree left;
    public ClusterTree right;
    public double simOfTwoChildren;

    /**
     * Constructor for leaf.
     */
    public ClusterTree(Vector v) {
        super();
        this.left = null;
        this.right = null;
        // simOfTwoChildren should no be a value for leaf node, but here assign
        // positive infinity to it for compareTo().
        this.simOfTwoChildren = Double.POSITIVE_INFINITY;
        this.add(v);
    }

    /**
     * Constructor for internal node.
     */
    public ClusterTree(ClusterTree c1, ClusterTree c2, double sim) {
        super();
        this.left = c1;
        this.right = c2;
        this.simOfTwoChildren = sim;
        this.addAll(c1);
        this.addAll(c2);
    }

    public ClusterList getKCluster(final int k) {
        final PriorityQueue<ClusterTree> que = new PriorityQueue<ClusterTree>();
        que.add(this);
        boolean hasMore = !this.isLeaf();
        while (que.size() < k && hasMore) {
            final ClusterTree ct = que.remove();
            hasMore = !ct.isLeaf();
            if (hasMore) { // Still is a internal node can be split.
                que.add(ct.left);
                que.add(ct.right);
            } else { // All nodes in queue are leaves, cannot split any more.
                // Add it back.
                que.add(ct);
            }
        }
        final ClusterList ret = new ClusterList();
        while (!que.isEmpty()) {
            ret.add(que.remove());
        }
        return ret;
    }

    @Override
    public int compareTo(ClusterTree c) {
        return Double.compare(this.simOfTwoChildren, c.simOfTwoChildren);
    }

    private boolean isLeaf() {
        return Double.isInfinite(this.simOfTwoChildren);
    }
}
