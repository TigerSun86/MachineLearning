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
    public double accur;
    public double fitness;

    public Individual(final BitStringRules rules2) {
        this.rules = rules2;
        this.accur = 0;
        this.fitness = 0;
    }

    public void setAccuracy (final double accur2) {
        accur = accur2;
        fitness = accur - (0.001 * Math.pow(rules.numOfRules, 2));
    }

    @Override
    public int compareTo (Individual arg0) {
        return Double.compare(this.fitness, arg0.fitness);
    }

    @Override
    public String toString () {
        return rules.toString() + String.format("Accuracy: %.3f ", accur)
                + String.format("Fitness: %.3f", fitness);
    }
}
