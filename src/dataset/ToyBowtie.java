package dataset;

/**
 * FileName:     ToyBowtie.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 12, 2014 3:43:04 PM 
 */
public class ToyBowtie implements GraphDataSet {

    @Override
    public String getName () {
        return "ToyBowtie";
    }

    @Override
    public String getDataFileUrl () {
        return "http://cs.fit.edu/~pkc/classes/ml-internet/data/toy-bowtie.txt";
    }

    @Override
    public boolean isDirected () {
        return false;
    }

}
