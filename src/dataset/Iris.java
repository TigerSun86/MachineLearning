package dataset;



/**
 * FileName: Iris.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 9, 2014 8:15:11 AM
 */
public class Iris implements DataSet {
    @Override
    public String getName () {
        return "Iris";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/iris-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://cs.fit.edu/~pkc/classes/ml/data/iris-train.txt";
    }

    @Override
    public String getTestFileUrl () {
        return "http://cs.fit.edu/~pkc/classes/ml/data/iris-test.txt";
    }
    
    @Override
    public String getDataFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/iris.txt";
    }
}