package dataset;

/**
 * FileName: DataSet.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 27, 2014 10:33:06 PM
 */
public abstract class DataSet {
    public abstract String getName ();

    public abstract String getAttrFileUrl ();

    public abstract String getTrainFileUrl ();

    // Can be null
    public abstract String getTestFileUrl ();

    // Whole data file before splitting into train and test.
    public abstract String getDataFileUrl ();

    protected abstract String getKFoldBaseString ();

    private static final int K = 10;
    private static final String STR_TRAIN = "-train-";
    private static final String STR_TEST = "-test-";
    private static final String STR_EXT = ".txt";

    public String[] getKFoldCVTrains () {
        final String[] ret = new String[K];
        final String base = getKFoldBaseString();
        for (int i = 0; i < K; i++) {
            ret[i] = base + STR_TRAIN + i + STR_EXT;
        }
        return ret;

    }

    public String[] getKFoldCVTests () {
        final String[] ret = new String[K];
        final String base = getKFoldBaseString();
        for (int i = 0; i < K; i++) {
            ret[i] = base + STR_TEST + i + STR_EXT;
        }
        return ret;
    }
}
