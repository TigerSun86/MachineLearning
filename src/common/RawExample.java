package common;

import java.util.ArrayList;

/**
 * FileName: RawExample.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 13, 2014 12:55:25 AM
 */
public class RawExample {
    public ArrayList<String> xList = new ArrayList<String>();
    public String t = null;

    @Override
    public String toString () {
        final StringBuffer sb = new StringBuffer();
        sb.append("X: ");
        for (String x : xList) {
            sb.append(x);
            sb.append(" ");
        }
        sb.append("T: ");
        sb.append(t);
        return sb.toString();
    }

}
