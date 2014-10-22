package clustering;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import util.Dbg;
import util.DisplayChart;
import clustering.AHClustering.Mode;
import clustering.BisectingKmeans.WayToPick;

import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

import dataset.Iris;

public class Test {
    private static final String TOY = "http://cs.fit.edu/~pkc/classes/ml-internet/data/toy-topics.txt";
    private static final String TRAIN = "http://cs.fit.edu/~pkc/classes/ml-internet/data/news/news-topics.txt";

    private static final ClusterAlg[] ALGS = new ClusterAlg[] { new Kmeans(),
            new BisectingKmeans(WayToPick.LARGEST, 6),
            new BisectingKmeans(WayToPick.LEASTSIM, 6),
            new AHClustering(Mode.IST), new AHClustering(Mode.CST),
            new AHClustering(Mode.UPGMA), new ASeedKmeans(Mode.UPGMA) };
    private static final String[] ALGNAME = new String[] { "K-means",
            "Bisecting K-means with largest cluster to split",
            "Bisecting K-means with least overall similarity to split",
            "Aggolermerative Hierarchical Clustering with IST",
            "Aggolermerative Hierarchical Clustering with CST",
            "Aggolermerative Hierarchical Clustering with UPGMA",
            "Aggolermerative Hierarchical Clustering with UPGMA to seed K-means" };

    private static final int RE = 3;

    public static void main(String[] args) {
        final Scanner sc = new Scanner(System.in);
        System.out.println("Please input the mode to test: ");
        System.out.println("0, simple test");
        System.out.println("1, change iter");
        System.out.println("2, change k");
        final int in = getInt(sc);
        if (in == 0) {
            simpleTest(sc);
        } else if (in == 1) {
            changeIter(sc);
        } else {
            changeK(sc);
        }
        sc.close();
    }

    private static void simpleTest(final Scanner sc) {
        Dbg.dbgSwitch = true;
        Dbg.defaultSwitch = true;
        System.out.println("Please input the index of algorithm: ");
        for (int i = 0; i < ALGS.length; i++) {
            System.out.println(i + " " + ALGNAME[i]);
        }
        final int index = getInt(sc);
        final ClusterAlg alg = ALGS[index];
        if (alg instanceof BisectingKmeans) {
            System.out.println("Please input the ITER: ");
            final int iter = getInt(sc);
            ((BisectingKmeans) alg).iter = iter;

        }

        System.out
                .println("Please input the index of data set: 0, toy. 1, news. 2, Iris");
        final int ds = getInt(sc);
        final List<Vector> vecs;
        if (ds == 0) {
            vecs = getVecs(TOY);
        } else if (ds == 1) {
            vecs = getVecs(TRAIN);
        } else {
            vecs = getIrisVecs();
        }

        System.out.println("Please input the k: ");
        final int k = getInt(sc);

        ClusterList cl = alg.cluster(vecs, k);

        for (Cluster c : cl) {
            System.out.println(c);
        }
        System.out.println("entropy " + cl.entropy());
        System.out.println("fmeasure " + cl.fMeasure());
        System.out.println("similarity " + cl.overallSimilarity());
        System.out.println("silcoe " + cl.silhouetteCoefficient());
        Dbg.dbgSwitch = false;
        Dbg.defaultSwitch = false;
    }

    private static void changeIter(final Scanner sc) {
        System.out
                .println("Please input the index of data set: 0, toy. 1, news. 2, Iris");
        final int ds = getInt(sc);
        final String dsname;
        final List<Vector> vecs;
        if (ds == 0) {
            vecs = getVecs(TOY);
            dsname = "Toy";
        } else if (ds == 1) {
            vecs = getVecs(TRAIN);
            dsname = "News";
        } else {
            vecs = getIrisVecs();
            dsname = "Iris";
        }

        System.out.println("Please input the k: ");
        final int k = getInt(sc);
        System.out
                .println("Please input the way to split: 0, largest. 1, least similarity");
        int in = getInt(sc);
        BisectingKmeans.WayToPick way;
        if (in == 0) {
            way = BisectingKmeans.WayToPick.LARGEST;
        } else {
            way = BisectingKmeans.WayToPick.LEASTSIM;
        }
        final LinkedHashMap<Double, Double> eLine = new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> fLine = new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> simLine = new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> silLine = new LinkedHashMap<Double, Double>();
        for (int iter = 2; iter <= 10; iter += 2) {
            double e = 0;
            double f = 0;
            double sim = 0;
            double sil = 0;
            for (int j = 0; j < RE; j++) {
                ClusterList cl = BisectingKmeans.cluster(vecs, k, way, iter);
                e += cl.entropy();
                f += cl.fMeasure();
                sim += cl.overallSimilarity();
                sil += cl.silhouetteCoefficient();
            }
            e /= RE;
            f /= RE;
            sim /= RE;
            sil /= RE;
            eLine.put((double) iter, e);
            fLine.put((double) iter, f);
            simLine.put((double) iter, sim);
            silLine.put((double) iter, sil);
        }
        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet = new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put("Entropy", eLine);
        dataSet.put("FMeasure", fLine);
        dataSet.put("OverallSimilarity", simLine);
        dataSet.put("silhouetteCoefficient", silLine);

        DisplayChart.display(dataSet, dsname, "Change iteration test",
                "# of iteration", "Performance");
    }

    private static void changeK(final Scanner sc) {
        System.out
                .println("Please input the index of data set: 0, toy. 1, news. 2, Iris");
        final int ds = getInt(sc);
        final String dsname;
        final List<Vector> vecs;
        if (ds == 0) {
            vecs = getVecs(TOY);
            dsname = "Toy";
        } else if (ds == 1) {
            vecs = getVecs(TRAIN);
            dsname = "News";
        } else {
            vecs = getIrisVecs();
            dsname = "Iris";
        }

        final ArrayList<LinkedHashMap<Double, Double>> eline = new ArrayList<LinkedHashMap<Double, Double>>();
        for (int i = 0; i < ALGS.length; i++) {
            eline.add(new LinkedHashMap<Double, Double>());
        }
        final ArrayList<LinkedHashMap<Double, Double>> fline = new ArrayList<LinkedHashMap<Double, Double>>();
        for (int i = 0; i < ALGS.length; i++) {
            fline.add(new LinkedHashMap<Double, Double>());
        }
        final ArrayList<LinkedHashMap<Double, Double>> simline = new ArrayList<LinkedHashMap<Double, Double>>();
        for (int i = 0; i < ALGS.length; i++) {
            simline.add(new LinkedHashMap<Double, Double>());
        }
        final ArrayList<LinkedHashMap<Double, Double>> silline = new ArrayList<LinkedHashMap<Double, Double>>();
        for (int i = 0; i < ALGS.length; i++) {
            silline.add(new LinkedHashMap<Double, Double>());
        }
        for (int k = 2; k <= 10; k++) {
            for (int i = 0; i < ALGS.length; i++) {
                final ClusterAlg alg = ALGS[i];
                double e = 0;
                double f = 0;
                double sim = 0;
                double sil = 0;
                for (int j = 0; j < RE; j++) {
                    ClusterList cl = alg.cluster(vecs, k);
                    e += cl.entropy();
                    f += cl.fMeasure();
                    sim += cl.overallSimilarity();
                    sil += cl.silhouetteCoefficient();
                }
                e /= RE;
                f /= RE;
                sim /= RE;
                sil /= RE;
                eline.get(i).put((double) k, e);
                fline.get(i).put((double) k, f);
                simline.get(i).put((double) k, sim);
                silline.get(i).put((double) k, sil);
            }

        }
        final LinkedHashMap<String, LinkedHashMap<Double, Double>> edata = new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        for (int i = 0; i < ALGS.length; i++) {
            edata.put(ALGNAME[i], eline.get(i));
        }
        final LinkedHashMap<String, LinkedHashMap<Double, Double>> fdata = new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        for (int i = 0; i < ALGS.length; i++) {
            fdata.put(ALGNAME[i], fline.get(i));
        }
        final LinkedHashMap<String, LinkedHashMap<Double, Double>> simdata = new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        for (int i = 0; i < ALGS.length; i++) {
            simdata.put(ALGNAME[i], simline.get(i));
        }
        final LinkedHashMap<String, LinkedHashMap<Double, Double>> sildata = new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        for (int i = 0; i < ALGS.length; i++) {
            sildata.put(ALGNAME[i], silline.get(i));
        }

        DisplayChart.display(edata, dsname, "Change k test - Entropy", "k",
                "Entropy");
        DisplayChart.display(fdata, dsname, "Change k test - FMeasure", "k",
                "FMeasure");
        DisplayChart.display(simdata, dsname,
                "Change k test - OverallSimilarity", "k", "OverallSimilarity");
        DisplayChart.display(sildata, dsname,
                "Change k test - SilhouetteCoefficient", "k",
                "SilhouetteCoefficient");
    }

    private static List<Vector> getVecs(String file) {
        List<List<Article>> ret = ArticleReader.read(file);
        List<Article> arts = new ArrayList<Article>();
        for (List<Article> a : ret) {
            for (Article b : a) {
                arts.add(b);
            }
        }
        List<Vector> ret2 = TfidfVector.articlesToVectors(arts);
        return ret2;
    }

    private static List<Vector> getIrisVecs() {
        final RawAttrList rawAttr = new RawAttrList(new Iris().getAttrFileUrl());
        final RawExampleList rawTrain = new RawExampleList(
                new Iris().getTrainFileUrl());
        List<String> idxToWord = new ArrayList<String>();
        for (int i = 0; i < rawAttr.xList.size(); i++) {
            idxToWord.add(rawAttr.xList.get(i).name);
        }

        int scount = 0;
        int ecount = 0;
        int icount = 0;
        List<Vector> vecs = new ArrayList<Vector>();
        for (RawExample e : rawTrain) {
            Vector v = new Vector(4);
            for (int i = 0; i < 4; i++) {
                final double attr = Double.parseDouble(e.xList.get(i));
                v.set(i, attr);
            }
            v.normalize();
            final String target = e.t;
            if (target.equals(rawAttr.t.valueList.get(0))) {
                scount++;
                v.id = "S" + scount;
            } else if (target.equals(rawAttr.t.valueList.get(1))) {
                ecount++;
                v.id = "E" + ecount;
            } else {
                icount++;
                v.id = "I" + icount;
            }
            v.idxToWord = idxToWord;
            vecs.add(v);
        }
        return vecs;
    }

    private static int getInt(final Scanner s) {
        int ret = -1;
        while (ret < 0) {
            final String next = s.nextLine();
            try {
                ret = Integer.parseInt(next);
            } catch (NumberFormatException e) {
                ret = -1;
            }

            if (ret < 0) {
                System.out.println("Please reinput:");
            }
        }
        return ret;
    }
}
