package dataset;

/**
 * FileName:     Enron2.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 12, 2014 3:43:04 PM 
 */
public class Enron2 implements GraphDataSet {

    @Override
    public String getName () {
        return "Enron2";
    }

    @Override
    public String getDataFileUrl () {
        return "http://cs.fit.edu/~pkc/classes/ml-internet/data/enron/enron2.txt";
    }

    @Override
    public boolean isDirected () {
        return true;
    }

}
