package dataset;


/**
 * FileName:     Heart.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 27, 2014 9:50:08 PM 
 */
public class Heart extends DataSet {
    @Override
    public String getName () {
        return "Heart";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/heart-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/heart-train.txt";
    }

    @Override
    public String getTestFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/heart-test.txt";
    }
    
    @Override
    public String getDataFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/heart.txt";
    }

    @Override
    protected String getKFoldBaseString () {
        return "file://localhost/C:/WorkSpace/MachineLearning/10fold/Heart/heart";

    }
}
