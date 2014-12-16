package multiAnn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import util.Dbg;
import artificialNeuralNetworks.ANN.NeuralNetwork;
import clustering.Cluster;
import clustering.ClusterAlg;
import clustering.ClusterList;
import clustering.EnemyCluster;
import clustering.Vector;

import common.Learner;
import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;
import common.TrainTestSplitter;

/**
 * FileName: MAnnLearner.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 27, 2014 4:52:54 PM
 */
public class MAnnLearner implements Learner {
    public static final String MODULE = "MAL";
    public static final boolean DBG = true;

    private final ClusterAlg clusterAlg;

    public MAnnLearner(ClusterAlg alg) {
        this.clusterAlg = alg;
    }

    @Override
    public MultiAnn learn (RawExampleList dataSet, RawAttrList attrs) {
        // Clustering.
        if (clusterAlg instanceof EnemyCluster) {
            final EnemyCluster alg = (EnemyCluster) clusterAlg;
            final List<Vector> vecs = new ArrayList<Vector>();
            for (int id = 0; id < dataSet.size(); id++) {
                RawExample e = dataSet.get(id);
                vecs.add(new Vector(e, attrs));
            }
            alg.setFullSet(vecs);
        }

        final RawExampleList[] eachClass =
                TrainTestSplitter.splitSetbyClass(dataSet, attrs);
        final List<List<RawExampleList>> clusters =
                new ArrayList<List<RawExampleList>>();
        for (RawExampleList clas : eachClass) {
            // Split data of this class into clusters.
            final List<RawExampleList> cluster = clustering(clas, attrs);
            // final List<RawExampleList> cluster =
            // EnemyCluster.cluster(clas,exs);
            clusters.add(cluster);
        }

        // Pair generator.
        final List<DataAndAttr> pairs = pairGenerator(clusters, attrs);

        // Ann Combinator.
        final MultiAnn mAnn = new MultiAnn(attrs);
        for (DataAndAttr dataAndAttr : pairs) {
            final NeuralNetwork ann =
                    new AnnLearner2().learn(dataAndAttr.dataSet,
                            dataAndAttr.attrs);
            mAnn.add(ann, dataAndAttr.dataSet);
        }
        return mAnn;
    }

    /* pairGenerator begin ** */
    private static List<DataAndAttr> pairGenerator (
            List<List<RawExampleList>> clusters, RawAttrList attrs) {
        final HashSet<Pair> clusterPairSet = new HashSet<Pair>();
        // For each class i
        for (int i = 0; i < clusters.size(); i++) {
            final List<RawExampleList> srcClas = clusters.get(i);
            for (int j = 0; j < srcClas.size(); j++) {
                final RawExampleList srcCluster = srcClas.get(j);
                for (int k = 0; k < srcCluster.size(); k++) {
                    final RawExample srcE = srcCluster.get(k);
                    // Find nearest enemy point for srcE.
                    double minDis = Double.POSITIVE_INFINITY;
                    int nearestClas = -1;
                    int nearestCluster = -1;
                    for (int l = 0; l < clusters.size(); l++) {
                        if (l != i) {
                            final List<RawExampleList> enmClas =
                                    clusters.get(l);
                            for (int m = 0; m < enmClas.size(); m++) {
                                final RawExampleList enmCluster =
                                        enmClas.get(m);
                                for (int n = 0; n < enmCluster.size(); n++) {
                                    final RawExample enmE = enmCluster.get(n);
                                    final double dis = getDis(srcE, enmE);
                                    if (Double.compare(minDis, dis) > 0) {
                                        minDis = dis;
                                        nearestClas = l;
                                        nearestCluster = m;
                                    }
                                }
                            } // for (int m = 0; m < enmClas.size(); m++) {
                        }
                    } // for(int l = 0; l < clusters.size();l++){

                    assert nearestClas != -1;
                    final Pair pair =
                            new Pair(i, j, nearestClas, nearestCluster);
                    Dbg.print(DBG, MODULE, pair.toString());
                    clusterPairSet.add(pair);
                } // for (int k = 0; k < srcCluster.size(); k++) {
            } // for (int j = 0; j < srcClas.size(); j++) {
        } // for (int i = 0; i < clusters.size(); i++) {

        final List<DataAndAttr> ret = new ArrayList<DataAndAttr>();
        for (Pair pair : clusterPairSet) {
            final RawExampleList dataSet = new RawExampleList();
            dataSet.addAll(clusters.get(pair.clas1).get(pair.cluster1));
            dataSet.addAll(clusters.get(pair.clas2).get(pair.cluster2));
            Collections.shuffle(dataSet);

            Dbg.print(DBG, MODULE, pair.toString()
                    + clusters.get(pair.clas1).get(pair.cluster1).size() + ","
                    + clusters.get(pair.clas2).get(pair.cluster2).size());

            final RawAttrList newAttrs = new RawAttrList(attrs);
            // New Attribute only has 2 classes.
            newAttrs.t.valueList.clear();
            newAttrs.t.valueList.add(clusters.get(pair.clas1)
                    .get(pair.cluster1).get(0).t);
            newAttrs.t.valueList.add(clusters.get(pair.clas2)
                    .get(pair.cluster2).get(0).t);

            ret.add(new DataAndAttr(dataSet, newAttrs));
        }
        return ret;
    }

    public static double getDis (RawExample srcE, RawExample enmE) {
        final List<String> p1 = srcE.xList;
        final List<String> p2 = enmE.xList;
        double sum = 0.0;
        for (int i = 0; i < p1.size(); i++) {
            final double x1 = Double.parseDouble(p1.get(i));
            final double x2 = Double.parseDouble(p2.get(i));
            sum += (x1 - x2) * (x1 - x2);
        }
        return Math.sqrt(sum);
    }

    private static class DataAndAttr {
        public final RawExampleList dataSet;
        public final RawAttrList attrs;

        public DataAndAttr(RawExampleList dataSet, RawAttrList attrs) {
            super();
            this.dataSet = dataSet;
            this.attrs = attrs;
        }
    }

    private static class Pair {
        public int clas1;
        public int cluster1;
        public int clas2;
        public int cluster2;

        public Pair(int clas1, int cluster1, int clas2, int cluster2) {
            super();
            this.clas1 = clas1;
            this.cluster1 = cluster1;
            this.clas2 = clas2;
            this.cluster2 = cluster2;
        }

        @Override
        public int hashCode () {
            int hash = 3;
            hash = hash * 7 + clas1 + clas2;
            hash = hash * 7 + cluster1 + cluster2;
            return hash;
        }

        @Override
        public boolean equals (Object o) {
            if (!(o instanceof Pair)) {
                return false;
            } else {
                Pair o2 = (Pair) o;
                return (clas1 == o2.clas1 && cluster1 == o2.cluster1
                        && clas2 == o2.clas2 && cluster2 == o2.cluster2)
                        || (clas1 == o2.clas2 && cluster1 == o2.cluster2
                                && clas2 == o2.clas1 && cluster2 == o2.cluster1);
            }
        }

        @Override
        public String toString () {
            return "[" + clas1 + "-" + cluster1 + "," + clas2 + "-" + cluster2
                    + "]";
        }
    }

    /* pairGenerator end ** */

    private List<RawExampleList> clustering (RawExampleList dataSet,
            RawAttrList attrs) {
        List<Vector> vecs = new ArrayList<Vector>();
        for (int id = 0; id < dataSet.size(); id++) {
            RawExample e = dataSet.get(id);
            vecs.add(new Vector(e, attrs));
        }
        ClusterList cl = clusterAlg.cluster(vecs);
        // ClusterList cl = new Kmeans().cluster(vecs, 2);
        // ClusterList cl =
        // new BisectingKmeans(BisectingKmeans.WayToPick.LARGEST, 6, 2,
        // null).cluster(vecs);
        // ClusterList cl = new
        // AHClustering(AHClustering.Mode.CST).cluster(vecs, 2);
        final List<RawExampleList> retExsList = new ArrayList<RawExampleList>();
        for (Cluster c : cl) {
            final RawExampleList exsClu = new RawExampleList();
            for (Vector v : c) {
                exsClu.add(v.e);
            }
            Dbg.print(DBG, MODULE, exsClu.toString());
            retExsList.add(exsClu);
        }

        return retExsList;
    }

}
