package genetic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import util.Dbg;
import util.MyMath;

import common.Evaluator;
import common.Hypothesis;
import common.RawAttrList;
import common.RawExampleList;

/**
 * FileName: GA.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Mar 31, 2014 10:15:19 PM
 */
public class GA {
    public static final String MODULE = "GAL";
    public static boolean DBG = false;

    public static final int SELECT_FIT_PRO = 0;
    public static final int SELECT_TOUR = 1;
    public static final int SELECT_RANK = 2;

    private static final int MAX_GENERATION = 10000;

    public static Hypothesis gaLearning (final RawExampleList exs,
            final RawAttrList attr, final double accuracyThreshold,
            final int maxGeneration, final int numP, final double r,
            final double m, final int selectWay) {
        int numOffspring = (int) Math.round(numP * r);
        if (numOffspring % 2 != 0) {
            numOffspring--; // numOffspring has to be even.
        }
        final int surviveNum = numP - numOffspring;

        // Init population.
        Population p = initPopulation(exs, attr, numP);
        Dbg.print(DBG, MODULE,
                "Initial population:" + Dbg.NEW_LINE + p.toString());
        int generationCount = 0;
        evaluate(p, exs); // Evaluate and sort.
        while ((Double.compare(p.get(0).accur, accuracyThreshold) < 0)
                && (generationCount < maxGeneration)
                && (generationCount < MAX_GENERATION)) {
            Dbg.print(DBG, MODULE,
                    "Individuals evaluated:" + Dbg.NEW_LINE + p.toString());
            // Select (1-r)* numP members to survive.
            final Population ps = select(p, surviveNum, selectWay);
            // Produce offspring.
            final Population offspring = crossOver(p, numOffspring, selectWay);
            ps.addAll(offspring);
            // Mutate.
            mutate(ps, m);
            p = ps;
            evaluate(p, exs); // Evaluate and sort.
            generationCount++;
            Dbg.print(DBG, MODULE, "Best individual:" + Dbg.NEW_LINE
                    + p.get(0).toString());

        }
        Dbg.print(DBG, MODULE, "Number of generation: " + generationCount);
        return p.get(0).rules;
    }

    private static Population initPopulation (RawExampleList exs,
            RawAttrList attrs, int numP) {
        final BSAttrs bsAttrs = new BSAttrs(exs, attrs);
        Population p = new Population();
        for (int i = 0; i < exs.size(); i++) {
            // Pick up 2 examples, convert them to individual, add to population
            final RawExampleList exs2 = new RawExampleList();
            exs2.add(exs.get(i));
            i++;
            if (i < exs.size()) {
                exs2.add(exs.get(i));
            }
            // Convert 2 examples to rule.
            final BitStringRules rules = new BitStringRules(bsAttrs, exs2);
            final Individual indi = new Individual(rules);
            p.add(indi); // Add individual to population.
            if (p.size() == numP) {
                break; // Reached the number of population.
            }
        }
        while (p.size() != numP) { // Need more individuals.
            final BitStringRules rules = new BitStringRules(bsAttrs);
            final Individual indi = new Individual(rules);
            p.add(indi); // Add individual to population.
        }
        return p;
    }

    private static void evaluate (final Population p, final RawExampleList exs) {
        for (Individual ind : p) {
            final double accur = Evaluator.evaluate(ind.rules, exs);
            ind.setAccuracy(accur);
        }
        Collections.sort(p, Collections.reverseOrder()); // Descending.
    }

    private static Population select (Population p, int num, int selectWay) {
        if (num == 0) {
            return new Population();
        }
        final Population ps;
        if (selectWay == SELECT_FIT_PRO) {
            ps = selectByFitPro(p, num);
        } else if (selectWay == SELECT_TOUR) {
            ps = selectByTourment(p, num);
        } else { // if (selectWay ==SELECT_RANK){
            ps = selectByRank(p, num);
        }
        Dbg.print(DBG, MODULE,
                "Individuals selected:" + Dbg.NEW_LINE + ps.toString());
        return ps;
    }

    private static Population selectByFitPro (Population p, int num) {
        // Generate the probability distribution.
        final double[] probDistribute = getProbDistribute(p);

        // Record the individual already been selected.
        final HashSet<Integer> selected = new HashSet<Integer>();
        final Population ps = new Population();
        // Always keep the best one.
        ps.add(p.get(0));
        selected.add(0);
        while (ps.size() < num) {
            // Select one individual by probability.
            final int index = MyMath.selectByProb(probDistribute);
            if (!selected.contains(index)) {
                ps.add(p.get(index)); // Add individual.
                selected.add(index); // Record the selected one.
            }
        }
        return ps;
    }

    // Predefined probability.
    private static final double PROB_HIGH = 0.8;

    private static Population selectByTourment (final Population p,
            final int num) {
        // Record the individual already been selected.
        final HashSet<Integer> selected = new HashSet<Integer>();
        final Population ps = new Population();
        // Always keep the best one.
        ps.add(p.get(0));
        selected.add(0);
        while (ps.size() < num) {
            // Pick two for tourment.
            final int[] indexes = MyMath.mOutofN(2, p.size());
            if (!selected.contains(indexes[0])
                    && !selected.contains(indexes[1])) {
                // Pick one out of two.
                final double[] probDistribute = new double[2];
                // Smooth the difference of probability. The higher fitness one
                // get PROB_HIGH.
                if (Double.compare(p.get(indexes[0]).fitness,
                        p.get(indexes[1]).fitness) > 0) {
                    probDistribute[0] = PROB_HIGH;
                    probDistribute[1] = 1 - PROB_HIGH;
                } else {
                    probDistribute[0] = 1 - PROB_HIGH;
                    probDistribute[1] = PROB_HIGH;
                }
                final int localIndex = MyMath.selectByProb(probDistribute);
                ps.add(p.get(indexes[localIndex])); // Add individual.
                selected.add(indexes[localIndex]); // Record selected
                                                   // individual.
            }
        }
        return ps;
    }

    private static Population selectByRank (final Population p, final int num) {
        // Generate the probability distribution.
        final double[] probDistribute = getProbDistributeByRank(p);
        // Record the individual already been selected.
        final HashSet<Integer> selected = new HashSet<Integer>();
        final Population ps = new Population();
        // Always keep the best one.
        ps.add(p.get(0));
        selected.add(0);
        while (ps.size() < num) {
            // Select one individual by probability.
            final int index = MyMath.selectByProb(probDistribute);
            if (!selected.contains(index)) {
                ps.add(p.get(index)); // Add individual.
                selected.add(index); // Record the selected one.
            }
        }
        return ps;
    }

    private static double[] getProbDistributeByRank (final Population p) {
        // Generate the probability distribution.
        final double[] probDistribute = new double[p.size()];
        final double sum = ((p.size() + 1) * p.size()) / 2;
        for (int i = 0; i < probDistribute.length; i++) {
            // Generate the probability by reversed rank number.
            probDistribute[i] = (p.size() - i) / sum;
        }
        return probDistribute;
    }

    private static double[] getProbDistribute (final Population p) {
        // Generate the probability distribution.
        final double[] probDistribute = new double[p.size()];
        double sum = 0;
        for (int i = 0; i < probDistribute.length; i++) {
            if (Double.compare(p.get(i).fitness, 0) <= 0) {
                // If fitness is negative, probability is zero.
                probDistribute[i] = 0;
            } else {
                probDistribute[i] = p.get(i).fitness;
            }
            sum += probDistribute[i];
        }
        if (Double.compare(sum, 0) == 0) {
            sum = 0.001;
        }
        for (int i = 0; i < probDistribute.length; i++) {
            probDistribute[i] /= sum;
        }

        return probDistribute;
    }

    private static Population crossOver (Population p, int numOffspring,
            final int selectWay) {
        final Population offspring = new Population();
        // Choose numOffspring parents to crossover.
        final Population parents = select(p, numOffspring, selectWay);
        final Random ran = new Random();
        while (!parents.isEmpty()) {
            // Randomly choose two parents.
            final Individual p1 = parents.remove(ran.nextInt(parents.size()));
            final Individual p2 = parents.remove(ran.nextInt(parents.size()));
            // Produce two offspring.
            final Individual[] children = OffspringProducer.produce(p1, p2);
            for (Individual i : children) {
                offspring.add(i); // Add offspring.
            }
        }
        return offspring;
    }

    private static void mutate (final Population ps, final double m) {
        int numMu = (int) Math.round(ps.size() * m);
        if (numMu == 0) {
            numMu = 1; // At least 1 mutation.
        }
        // Get a list with the indexes of individuals.
        int[] iList = MyMath.mOutofN(numMu, ps.size());
        for (int i : iList) {
            final Individual ind = ps.get(i);
            Dbg.print(DBG, MODULE, "Individual about to mutate:" + i
                    + Dbg.NEW_LINE + ind.toString());
            final Individual mutatedInd = Mutator.mutate(ind);
            ps.set(i, mutatedInd);
        }
    }
}
