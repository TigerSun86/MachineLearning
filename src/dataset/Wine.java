package dataset;


/**
 * FileName: Wine.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Apr 21, 2014 9:48:58 PM
 */
public class Wine extends DataSet {
    @Override
    public String getName () {
        return "Wine";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/wine-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/wine-train.txt";
    }

    @Override
    public String getTestFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/wine-test.txt";
    }
    
    @Override
    public String getDataFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/wine.txt";
    }

    @Override
    protected String getKFoldBaseString () {
        return "file://localhost/C:/WorkSpace/MachineLearning/10fold/Wine/wine";
    }
}
