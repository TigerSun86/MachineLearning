package dataset;


/**
 * FileName:     Haberman.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 27, 2014 9:51:36 PM 
 */
public class Haberman extends DataSet {
    @Override
    public String getName () {
        return "Haberman";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/haberman-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/haberman-train.txt";
    }

    @Override
    public String getTestFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/haberman-test.txt";
    }
    
    @Override
    public String getDataFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/haberman.txt";
    }

    @Override
    protected String getKFoldBaseString () {
        return "file://localhost/C:/WorkSpace/MachineLearning/10fold/Haberman/haberman";

    }
}
