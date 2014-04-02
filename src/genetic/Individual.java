package genetic;

import java.util.BitSet;

import common.RawAttrList;
import common.RawExample;

/**
 * FileName: Individual.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Mar 31, 2014 10:23:25 PM
 */
public class Individual extends BitStringRules implements
        Comparable<Individual> {
    public Individual(RawAttrList attrList2) {
        super(attrList2);
        // TODO Auto-generated constructor stub
    }

    public double accur = 0;

    @Override
    public int compareTo (Individual arg0) {
        return Double.compare(this.accur, arg0.accur);
    }
}
