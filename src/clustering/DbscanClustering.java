package clustering;

import java.util.HashMap;
import java.util.List;

import common.RawAttrList;
import util.Dbg;
import clustering.dbscan.Gui;
import clustering.dbscan.Point;
import clustering.dbscan.dbscan;

/**
 * FileName: DbscanClustering.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 27, 2014 5:45:41 PM
 */
public class DbscanClustering implements ClusterAlg {
    public static final String MODULE = "DBS";
    public static final boolean DBG = true;

    public int minpoints;
    public double tdistance;
    public boolean addNoiseBack;
    public RawAttrList attrs;

    public DbscanClustering(int minpoints, double tdistance,
            boolean addNoiseBack, RawAttrList attrs) {
        this.minpoints = minpoints;
        this.tdistance = tdistance;
        this.addNoiseBack = addNoiseBack;
        this.attrs = attrs;
    }

    public DbscanClustering() {
        this(3, 0.1, true, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ClusterList cluster (List<Vector> vecs) {
        Gui.minpoints = minpoints;
        Gui.tdistance = tdistance;
        Gui.hset.clear();
        for (int id = 0; id < vecs.size(); id++) {
            Gui.hset.add(new Point(vecs.get(id)));
        }
        @SuppressWarnings("rawtypes")
        java.util.Vector<List> ret = dbscan.applyDbscan();

        final ClusterList clus = new ClusterList();
        for (List<Point> l : ret) {
            final Cluster exsClu = new Cluster();
            for (Point v : l) {
                exsClu.add(v.e);
            }
            Dbg.print(DBG, MODULE, "new cluster " + exsClu.toString());
            clus.add(exsClu);
        }

        if (clus.isEmpty()) {
            // If there is no cluster (all points are singleton clusters), just
            // return all points as a whole cluster.
            final Cluster singleCluster = new Cluster();
            singleCluster.addAll(vecs);
            final ClusterList cl = new ClusterList();
            cl.add(singleCluster);
            return cl;
        }

        if (!addNoiseBack) {
            return clus;
        } else {
            // Add back the points which were threw by dbscan as noise, so
            // dbscan will return the same amount of data points as inputed.
            final HashMap<Vector, Integer> map = new HashMap<Vector, Integer>();
            for (Vector v : vecs) {
                final int idxOfCluster = EnemyCluster.nearestCluster(v, clus);
                map.put(v, idxOfCluster);
            }
            // Use new ClusterList to store all vecs, to keep the original order
            // of the input points.
            final ClusterList finalCl = new ClusterList();
            for (int i = 0; i < clus.size(); i++) {
                finalCl.add(new Cluster());
            }
            for (Vector v : vecs) {
                final int idxOfCluster = map.get(v);
                finalCl.get(idxOfCluster).add(v);
            }
            return finalCl;
        }
    }

    @Override
    public void setK (int k) {
        // Do nothing.
    }

}
