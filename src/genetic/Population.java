package genetic;

import java.util.ArrayList;

import util.Dbg;

/**
 * FileName: Population.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Mar 31, 2014 10:21:57 PM
 */
public class Population extends ArrayList<Individual> {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.size(); i++) {
            final Individual indi = this.get(i);
            sb.append("Individual " + i + ":" + Dbg.NEW_LINE
                    + indi.toString()+ Dbg.NEW_LINE);
        }
        return sb.toString();
    }
}
