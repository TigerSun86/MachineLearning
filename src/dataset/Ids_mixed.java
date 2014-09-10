package dataset;

/**
 * FileName: Ids_mixed.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 10, 2014 12:14:13 AM
 */
public class Ids_mixed implements DataSet {
    @Override
    public String getName () {
        return "Ids-mixed";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/ids-mixed-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/ids-mixed-train.txt";
    }

    @Override
    public String getTestFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/ids-mixed-test.txt";
    }

    @Override
    public String getDataFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/ids-mixed.txt";
    }
}