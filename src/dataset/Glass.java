package dataset;


/**
 * FileName:     Glass.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 27, 2014 9:50:52 PM 
 */
public class Glass  extends DataSet {
    @Override
    public String getName () {
        return "Glass";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/glass-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/glass-train.txt";
    }

    @Override
    public String getTestFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/glass-test.txt";
    }
    
    @Override
    public String getDataFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/glass.txt";
    }

    @Override
    protected String getKFoldBaseString () {
        return "file://localhost/C:/WorkSpace/MachineLearning/10fold/Glass/glass";

    }
}
