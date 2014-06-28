package dataset;

/**
 * FileName: DataSet.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 27, 2014 10:33:06 PM
 */
public interface DataSet {
    public abstract String getName ();

    public abstract String getAttrFileUrl ();

    public abstract String getTrainFileUrl ();

    // Can be null
    public abstract String getTestFileUrl ();
    
    // Whole data file before splitting into train and test.
    public abstract String getDataFileUrl ();

}
