package dataset;

/**
 * FileName:     LERAD_ids.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Sep 23, 2014 10:41:26 PM 
 */
public class LERAD_ids extends DataSet {
    @Override
    public String getName () {
        return "LERAD_ids";
    }

    @Override
    public String getAttrFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/LERAD_ids-attr.txt";
    }

    @Override
    public String getTrainFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/LERAD_ids-train.txt";
    }

    @Override
    public String getTestFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/LERAD_ids-test.txt";
    }

    @Override
    public String getDataFileUrl () {
        return "http://my.fit.edu/~sunx2013/MachineLearning/LERAD_ids-attack.txt";
    }

    @Override
    protected String getKFoldBaseString () {
        // TODO Auto-generated method stub
        return null;
    }
}