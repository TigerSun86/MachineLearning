package dataset;


/**
 * FileName: Bupa.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Apr 24, 2014 5:29:53 AM
 */
public class Bupa extends DataSet {
    @Override
    public String getName () {
        return "Bupa";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/bupa-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/bupa-train.txt";
    }

    @Override
    public String getTestFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/bupa-test.txt";
    }
    
    @Override
    public String getDataFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/bupa.txt";
    }

    @Override
    protected String getKFoldBaseString () {
        return "file://localhost/C:/WorkSpace/MachineLearning/10fold/Bupa/bupa";
    }
}
