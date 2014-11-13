package bridgeCut;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import util.Dbg;
import util.DisplayChart;

import common.DataReader;

import dataset.Enron2;
import dataset.Enron5;
import dataset.GraphDataSet;
import dataset.ToyBowtie;
import dataset.ToyFriends;
import dataset.ToyGraph;

/**
 * FileName: Test.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 6, 2014 7:33:34 PM
 */
public class Test {
    private static final GraphDataSet[] DATA_SET = new GraphDataSet[] {
            new ToyBowtie(), new ToyFriends(), new ToyGraph(), new Enron2(),
            new Enron5() };

    public static void main (String[] args) {
        final Scanner sc = new Scanner(System.in);
        System.out.println("Please input the index of data set:");
        for (int i = 0; i < DATA_SET.length; i++) {
            System.out.println(i + ", " + DATA_SET[i].getName());
        }
        System.out.println("Other, quit");
        final int ds = getInt(sc);
        if (ds >= DATA_SET.length) {
            return;
        }
        final GraphDataSet dataSet = DATA_SET[ds];
        final Graph g =
                readFile(dataSet.getDataFileUrl(), dataSet.isDirected());
        System.out.println(g);
        System.out
                .println("Please input the test mode: 0, simpleTest. 1, varyDt. 2, varyCluster. 3, singleton. other, quit.");
        final int mode = getInt(sc);
        if (mode == 0) {
            simpleTest(sc, g);
        } else if (mode == 1) {
            varyDt(sc, g);
        } else if (mode == 2) {
            varyCluster(sc, g);
        } else if (mode == 3) {
            singleton(sc, g);
        } else {
            return;
        }

    }
    private static void simpleTest (Scanner sc, Graph g) {
        Dbg.dbgSwitch = true;
        Dbg.defaultSwitch = true;
        System.out.println("Please input the density threshold (double 0-1):");
        final double dt = getDouble(sc);
        System.out.println("Please input to cut node or edge "
                + "(0 for node, 1 for edge):");
        final boolean isNode = (getInt(sc) == 0) ? true : false;
        System.out
                .println("Please input to use which way to calculate centrality"
                        + " (0 for bridge centrality, 1 for betweenness)");
        final boolean isBC = (getInt(sc) == 0) ? true : false;
        List<Graph> glist = g.bridgeCut(dt, isNode, isBC);
        System.out.println("Cluster list is:");
        System.out.println(Graph.graphListToString(glist));
        System.out.println("Davis Bouldin Index is "
                + Evaluator.davisBouldinIndex(glist, g));
        System.out.println("Silhouette Coefficient is "
                + Evaluator.silhouetteCoefficient(glist, g));
        // new ShortestPaths(g);
        // g.centralityOfEdges();
        Dbg.dbgSwitch = false;
        Dbg.defaultSwitch = false;
    }

    private static void varyDt (Scanner sc, Graph g) {
        System.out.println("Please input to cut node or edge "
                + "(0 for node, 1 for edge):");
        final boolean isNode = (getInt(sc) == 0) ? true : false;
        String disstr = (isNode) ? "Node" : "Edge";

        System.out
                .println("Please input to use which way to calculate centrality"
                        + " (0 for bridge centrality, 1 for betweenness)");
        final boolean isBC = (getInt(sc) == 0) ? true : false;
        disstr += ", " + ((isBC) ? "bridge centrality" : "betweenness");
        final LinkedHashMap<Double, Double> eLine =
                new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> fLine =
                new LinkedHashMap<Double, Double>();
        for (double dt = 0.0; dt <= 1.0; dt += 0.1) {
            List<Graph> glist = g.bridgeCut(dt, isNode, isBC);
            Iterator<Graph> iter = glist.iterator();
            while (iter.hasNext()) {
                if (iter.next().size() == 1) {
                    iter.remove();
                }
            }
            double dbi = Evaluator.davisBouldinIndex(glist, g);
            double sil = Evaluator.silhouetteCoefficient(glist, g);
            System.out.println("Density threshold: " + dt);
            System.out.println("Davis Bouldin Index is " + dbi);
            System.out.println("Silhouette Coefficient is " + sil);
            eLine.put(dt, dbi);
            fLine.put(dt, sil);
        }
        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put("Davis Bouldin Index", eLine);
        dataSet.put("Silhouette Coefficient", fLine);
        DisplayChart.display(dataSet, "Change density threshold test",
                "Change density threshold test -- " + disstr,
                "Density threshold", "Performance");
    }

    private static void varyCluster (Scanner sc, Graph g) {
        System.out.println("Please input to cut node or edge "
                + "(0 for node, 1 for edge):");
        final boolean isNode = (getInt(sc) == 0) ? true : false;
        String disstr = (isNode) ? "Node" : "Edge";

        System.out
                .println("Please input to use which way to calculate centrality"
                        + " (0 for bridge centrality, 1 for betweenness)");
        final boolean isBC = (getInt(sc) == 0) ? true : false;
        disstr += ", " + ((isBC) ? "bridge centrality" : "betweenness");
        final LinkedHashMap<Double, Double> eLine =
                new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> fLine =
                new LinkedHashMap<Double, Double>();
        for (double dt = 0.0; dt <= 1.0; dt += 0.1) {
            List<Graph> glist = g.bridgeCut(dt, isNode, isBC);
            Iterator<Graph> iter = glist.iterator();
            while (iter.hasNext()) {
                if (iter.next().size() == 1) {
                    iter.remove();
                }
            }
            double dbi = Evaluator.davisBouldinIndex(glist, g);
            double sil = Evaluator.silhouetteCoefficient(glist, g);
            System.out.println("Density threshold: " + dt);
            System.out.println("# of clusters is " + glist.size());
            System.out.println("Davis Bouldin Index is " + dbi);
            System.out.println("Silhouette Coefficient is " + sil);
            eLine.put((double) glist.size(), dbi);
            fLine.put((double) glist.size(), sil);
        }
        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put("Davis Bouldin Index", eLine);
        dataSet.put("Silhouette Coefficient", fLine);
        DisplayChart.display(dataSet, "Change # of clusters test",
                "Change # of clusters test -- " + disstr, "# of clusters",
                "Performance");
    }

    private static void singleton (Scanner sc, Graph g) {
        System.out.println("Please input to cut node or edge "
                + "(0 for node, 1 for edge):");
        final boolean isNode = (getInt(sc) == 0) ? true : false;
        String disstr = (isNode) ? "Node" : "Edge";

        final LinkedHashMap<Double, Double> eLine =
                new LinkedHashMap<Double, Double>();
        final LinkedHashMap<Double, Double> fLine =
                new LinkedHashMap<Double, Double>();

        HashMap<Integer, Integer> ret =
                Graph.singletonTest(g, 0.3, isNode, true, 20);
        for (Entry<Integer, Integer> e : ret.entrySet()) {
            eLine.put((double) e.getKey().intValue(), (double) e.getValue()
                    .intValue());
        }
        ret = Graph.singletonTest(g, 0.3, isNode, false, 20);
        for (Entry<Integer, Integer> e : ret.entrySet()) {
            fLine.put((double) e.getKey().intValue(), (double) e.getValue()
                    .intValue());
        }

        final LinkedHashMap<String, LinkedHashMap<Double, Double>> dataSet =
                new LinkedHashMap<String, LinkedHashMap<Double, Double>>();
        dataSet.put("bridge centrality", eLine);
        dataSet.put("betweenness", fLine);
        DisplayChart.display(dataSet, "Singleton test", "Singleton test -- "
                + disstr, "# of " + disstr + " deleted", "Singleton produced");
    }

    private static Graph readFile (String fileName, boolean isDirected) {
        final DataReader in = new DataReader(fileName);
        final Graph g = new Graph(isDirected);
        while (true) {
            final String line = in.nextLine();
            if (line == null) {
                break;
            }
            if (line.length() <= 1) {
                continue; // Skip empty line.
            }
            final String[] str = line.split("\\s");
            String n1name = str[0];
            String n2name = str[1];
            Node n1 = g.get(n1name);
            if (n1 == null) {
                n1 = new Node(n1name);
                g.put(n1name, n1);
            }
            n1.addNeighbor(n2name); // Edge n1 to n2

            Node n2 = g.get(n2name);
            if (n2 == null) {
                n2 = new Node(n2name);
                g.put(n2name, n2);
            }
            if (!isDirected) {
                n2.addNeighbor(n1name); // Edge n2 to n1
            }
        } // End of while (true) {
        in.close();
        return g;
    }

    private static int getInt (final Scanner s) {
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

    private static double getDouble (final Scanner s) {
        double ret = Double.NaN;
        while (Double.isNaN(ret)) {
            final String next = s.nextLine();
            try {
                ret = Double.parseDouble(next);
            } catch (NumberFormatException e) {
                ret = Double.NaN;
            }
            // Only accept 0 <= ret <= 1.
            if (Double.compare(ret, 0) < 0 || Double.compare(ret, 1) > 0) {
                System.out.println("Please reinput:");
                ret = Double.NaN;
            }
        }
        return ret;
    }
}
