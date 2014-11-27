package dataset;

/**
 * FileName:     Restaurant.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 10, 2014 12:12:07 AM 
 */
public class Restaurant extends DataSet {
    @Override
    public String getName () {
        return "Restaurant";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/restaurant-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/restaurant-train.txt";
    }

    @Override
    public String getTestFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/restaurant-test.txt";
    }

    @Override
    public String getDataFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/restaurant.txt";
    }

    @Override
    protected String getKFoldBaseString () {
        // TODO Auto-generated method stub
        return null;
    }
}