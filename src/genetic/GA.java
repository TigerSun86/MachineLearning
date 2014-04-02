package genetic;

import java.util.BitSet;
import java.util.Collections;
import java.util.Random;

import util.MyMath;
import common.Evaluator;
import common.Hypothesis;
import common.RawAttrList;
import common.RawExample;
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
        for (Individual indi: p){
            System.out.println(indi);
        }
        evaluate(p, exs); // Evaluate and sort.
        while (Double.compare(p.get(0).accur, fitness_threshold) < 0) {
            // Select (1-r)* numP members to survive.
            Population ps = select(p, surviveNum);
            // Produce offspring.
            ps.addAll(crossOver(p, numOffspring));
            // Mutate.
            mutate(ps, m);
            p = ps;
            evaluate(p, exs); // Evaluate and sort.
        }
        return p.get(0);
    }

    private static Population initPopulation (RawExampleList exs,
            RawAttrList attr, int numP) {
        Population p = new Population();
        for (int i = 0; i < exs.size(); i++) {
            final Individual indi = new Individual(attr);
            RawExample ex = exs.get(i);
            BitSet rule = indi.geneRuleByEx(ex); // Convert example to rule.
            indi.addRule(rule);
            i++;
            if (i < exs.size()) { // Convert 2nd example to rule.
                ex = exs.get(i);
                rule = indi.geneRuleByEx(ex);
                indi.addRule(rule);
            }
            p.add(indi); // Add individual to population.
            if (p.size() == numP) {
                break; // Reached the number of population.
            }
        }
        while (p.size() != numP) { // Need more individuals.
            final Individual indi = new Individual(attr);
            BitSet rule = indi.geneRuleByRan(); // Generate random rule.
            indi.addRule(rule);
            rule = indi.geneRuleByRan();
            indi.addRule(rule);
            p.add(indi); // Add individual to population.
        }
        return p;
    }

    private static void evaluate (final Population p, final RawExampleList exs) {
        for (Individual ind : p) {
            final double accur = Evaluator.evaluate(ind, exs);
            ind.accur = accur;
        }
        Collections.sort(p, Collections.reverseOrder()); // Descending.
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
            final Individual mutatedInd = mutateInd(ind);
            ps.set(i, mutatedInd);
        }
    }

    private static Individual mutateInd (Individual ind) {
        // TODO Auto-generated method stub
        return null;
    }

    private static Population crossOver (Population p, int numOffspring) {
        final Population offspring = new Population();
        // Choose numOffspring parents to crossover.
        final Population parents = selectM(p, numOffspring);
        final Random ran = new Random();
        while (!parents.isEmpty()) {
            // Randomly choose two parents.
            final Individual p1 = parents.remove(ran.nextInt(parents.size()));
            final Individual p2 = parents.remove(ran.nextInt(parents.size()));
            // Produce two offspring.
            final Individual[] children = produce(p1, p2);
            for (Individual i : children) {
                offspring.add(i); // Add offspring.
            }
        }
        return offspring;
    }

    private static Individual[] produce (Individual p1, Individual p2) {
        // TODO Auto-generated method stub
        return null;
    }

    private static Population selectM (final Population p, final int m) {
        // Get a list with the indexes of individuals.
        final int[] iList = MyMath.mOutofNWithPriority(m, p.size());
        final Population ret = new Population();
        // Add individuals.
        for (int i = 0; i < iList.length; i++) {
            final int indexOfIndiv = iList[i];
            ret.add(p.get(indexOfIndiv));
        }
        return ret;
    }

    private static Population select (Population p, int surviveNum) {
        final Population ps = new Population();
        // Always keep the best one.
        ps.add(p.get(0));
        // Get a list with the indexes of survivors.
        final int[] iList =
                MyMath.mOutofNWithPriority(surviveNum, p.size() - 1);
        // Add survivors.
        for (int i = 0; i < iList.length; i++) {
            final int indexOfIndiv = iList[i] + 1; // 0 already added.
            ps.add(p.get(indexOfIndiv));
        }
        return ps;
    }
}
