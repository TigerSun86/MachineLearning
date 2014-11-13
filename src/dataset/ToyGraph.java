package dataset;

/**
 * FileName:     ToyGraph.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 12, 2014 3:43:04 PM 
 */
public class ToyGraph implements GraphDataSet {

    @Override
    public String getName () {
        return "ToyGraph";
    }

    @Override
    public String getDataFileUrl () {
        return "http://cs.fit.edu/~pkc/classes/ml-internet/data/toy-graph.txt";
    }

    @Override
    public boolean isDirected () {
        return false;
    }

}
