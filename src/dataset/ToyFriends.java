package dataset;

/**
 * FileName: ToyFriends.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 12, 2014 3:43:04 PM
 */
public class ToyFriends implements GraphDataSet {

    @Override
    public String getName () {
        return "ToyFriends";
    }

    @Override
    public String getDataFileUrl () {
        return "http://cs.fit.edu/~pkc/classes/ml-internet/data/toy-friends.txt";
    }

    @Override
    public boolean isDirected () {
        return false;
    }

}
