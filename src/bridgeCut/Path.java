package bridgeCut;

import java.util.ArrayList;
import java.util.Collections;

/**
 * FileName: Path.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 8, 2014 4:06:12 PM
 */
public class Path extends ArrayList<String> {
    private static final long serialVersionUID = 1L;

    public Path(String p) {
        super();
        Collections.addAll(this, p.split(" "));
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String s : this) {
            sb.append(s);
            sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
    }
}
