package dataset;

/**
 * FileName:     GraphDataSet.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 12, 2014 3:39:28 PM 
 */
public interface GraphDataSet {
    public abstract String getName ();

    public abstract String getDataFileUrl ();
    
    public abstract boolean isDirected();
}
