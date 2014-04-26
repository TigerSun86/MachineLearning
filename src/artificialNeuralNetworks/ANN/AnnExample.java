package artificialNeuralNetworks.ANN;

import java.util.ArrayList;

/**
 * FileName: AnnExample.java
 * 
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 7, 2014 4:16:57 PM
 */
public class AnnExample {
    // Attributes value.
    public final ArrayList<Double> xList = new ArrayList<Double>();
    // Target value.
    public final ArrayList<Double> tList = new ArrayList<Double>();

    @Override
    public String toString () {
        final StringBuffer sb = new StringBuffer();
        sb.append("X: ");
        for (Double x : xList) {
            sb.append(String.format("%.3f", x));
            sb.append(" ");
        }
        sb.append("T: ");
        for (Double t : tList) {
            sb.append(String.format("%.3f", t));
            sb.append(" ");
        }
        return sb.toString();
    }
}
