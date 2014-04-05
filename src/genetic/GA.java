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
    public static final boolean DBG = true;

    public static Hypothesis gaLearning (final RawExampleList exs,
            final RawAttrList attr, final double fitness_threshold,
            final int numP, final double r, final double m) {
        int numOffspring = (int) Math.round(numP * r);
        if (numOffspring % 2 != 0) {
            numOffspring--; // numOffspring has to be even.
        }
        final int surviveNum = numP - numOffspring;

        // Init population.
        Population p = initPopulation(exs, attr, numP);
        Dbg.print(DBG, MODULE,
                "Initial population:" + Dbg.NEW_LINE + p.toString());

        evaluate(p, exs); // Evaluate and sort.
        while (Double.compare(p.get(0).accur, fitness_threshold) < 0) {
            // Select (1-r)* numP members to survive.
            final Population ps = select(p, surviveNum);
            // Produce offspring.
            final Population offspring = crossOver(p, numOffspring);
            ps.addAll(offspring);
            // Mutate.
            mutate(ps, m);
            p = ps;
            evaluate(p, exs); // Evaluate and sort.
        }
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
            final Individual indi = new Individual();
            indi.rules = rules;
            p.add(indi); // Add individual to population.
            if (p.size() == numP) {
                break; // Reached the number of population.
            }
        }
        while (p.size() != numP) { // Need more individuals.
            final BitStringRules rules = new BitStringRules(bsAttrs);
            final Individual indi = new Individual();
            indi.rules = rules;
            p.add(indi); // Add individual to population.
        }
        return p;
    }

    private static void evaluate (final Population p, final RawExampleList exs) {
        for (Individual ind : p) {
            final double accur = Evaluator.evaluate(ind.rules, exs);
            ind.accur = accur;
        }
        Collections.sort(p, Collections.reverseOrder()); // Descending.
        Dbg.print(DBG, MODULE,
                "Population evaluated:" + Dbg.NEW_LINE + p.toString());
    }

    private static Population select (Population p, int num) {
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
        Dbg.print(DBG, MODULE,
                "Population selected:" + Dbg.NEW_LINE + ps.toString());

        return ps;
    }

    private static double[] getProbDistribute (final Population p) {
        // Generate the probability distribution.
        final double[] probDistribute = new double[p.size()];
        double sum = 0;
        for (int i = 0; i < probDistribute.length; i++) {
            probDistribute[i] = p.get(i).accur;
            sum += p.get(i).accur;
        }
        for (int i = 0; i < probDistribute.length; i++) {
            probDistribute[i] /= sum;
        }

        return probDistribute;
    }

    private static Population crossOver (Population p, int numOffspring) {
        final Population offspring = new Population();
        // Choose numOffspring parents to crossover.
        final Population parents = select(p, numOffspring);
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
        final int[] iList = MyMath.mOutofN(numMu, ps.size());
        for (int i : iList) {
            final Individual ind = ps.get(i);
            Dbg.print(DBG, MODULE,
                    "Population mutated:" + i + Dbg.NEW_LINE + ind.toString());
            final Individual mutatedInd = mutateInd(ind);
            ps.set(i, mutatedInd);
        }
    }

    private static Individual mutateInd (Individual ind) {
        // TODO Auto-generated method stub
        return ind;
    }

}
