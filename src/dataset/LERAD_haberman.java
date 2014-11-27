package dataset;

/**
 * FileName:     LERAD_haberman.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 23, 2014 10:41:26 PM 
 */
public class LERAD_haberman extends DataSet {
    @Override
    public String getName () {
        return "LERAD_haberman";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/LERAD_haberman-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/LERAD_haberman-train.txt";
    }

    @Override
    public String getTestFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/LERAD_haberman-test.txt";
    }

    @Override
    public String getDataFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/LERAD_haberman-attack.txt";
    }

    @Override
    protected String getKFoldBaseString () {
        // TODO Auto-generated method stub
        return null;
    }
}