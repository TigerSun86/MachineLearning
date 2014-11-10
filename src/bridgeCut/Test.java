package bridgeCut;

import util.Dbg;
import common.DataReader;

/**
 * FileName: Test.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 6, 2014 7:33:34 PM
 */
public class Test {
    private static final String TOY1 =
            "http://cs.fit.edu/~pkc/classes/ml-internet/data/toy-bowtie.txt";
    private static final String TOY2 ="http://cs.fit.edu/~pkc/classes/ml-internet/data/toy-friends.txt";

    public static void main (String[] args) {
        Dbg.dbgSwitch = true;
        Dbg.defaultSwitch = true;
        final Graph g = readFile(TOY2);
        System.out.println(g.bridgeCut(0.7,true,false));
        //new ShortestPaths(g);
        //g.centralityOfEdges();
        Dbg.dbgSwitch = false;
        Dbg.defaultSwitch = false;
    }

    private static Graph readFile (String fileName) {
        final DataReader in = new DataReader(fileName);
        final Graph g = new Graph();
        while (true) {
            final String line = in.nextLine();
            if (line == null) {
                break;
            }
            if (line.length() <= 1) {
                continue; // Skip empty line.
            }
            final String[] str = line.split(" ");
            String n1name = str[0];
            String n2name = str[1];
            Node n1 = g.get(n1name);
            if (n1 == null) {
                n1 = new Node(n1name);
                g.put(n1name, n1);
            }
            n1.addNeighbor(n2name);

            Node n2 = g.get(n2name);
            if (n2 == null) {
                n2 = new Node(n2name);
                g.put(n2name, n2);
            }
            n2.addNeighbor(n1name);
        } // End of while (true) {
        in.close();
        return g;
    }
}
