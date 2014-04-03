package genetic;

/**
 * FileName: Individual.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Mar 31, 2014 10:23:25 PM
 */
public class Individual implements Comparable<Individual> {
    public BitStringRules rules;
    public double accur = 0;

    @Override
    public int compareTo (Individual arg0) {
        return Double.compare(this.accur, arg0.accur);
    }

    @Override
    public String toString () {
        return rules.toString() + String.format("Accuracy: %.3f", accur);
    }
}
