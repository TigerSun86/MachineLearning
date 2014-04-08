package genetic;

import java.util.Random;

import util.Dbg;

/**
 * FileName: Mutator.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Apr 5, 2014 3:03:08 AM
 */
public class Mutator {
    public static final String MODULE = "MTR";
    public static boolean DBG = false;

    public static Individual mutate (Individual indi) {
        final Random ran = new Random();
        BitStringRules newIndi = null;
        int ruleIndex = 0;
        int positionInPrecond = 0;
        int positionInPrecondStart = 0;
        while (true) {
            newIndi = new BitStringRules(indi.rules);
            // Randomly select the rule index to mutate.
            ruleIndex = ran.nextInt(newIndi.numOfRules);
            // Randomly select a position among preconditions to mutate.
            // Don't mutate the target condition.
            positionInPrecond = ran.nextInt(newIndi.attrs.precondsLength);
            positionInPrecondStart =
                    positionInPrecond
                            - (int) (newIndi.attrs.precondsLength * 0.1);
            if (positionInPrecondStart < 0) {
                positionInPrecondStart = 0;
            }
            final int positionToMutate =
                    ruleIndex * newIndi.attrs.ruleLength + positionInPrecond;
            final int positionToMutateStart =
                    ruleIndex * newIndi.attrs.ruleLength + positionInPrecondStart;
            // Mutate.
            if (positionToMutate == positionToMutateStart){
                newIndi.ruleSet.flip(positionToMutate); // At least flip one bit.
            } else {
                newIndi.ruleSet.flip(positionToMutateStart, positionToMutate);
            }

            if (newIndi.isValid()) {
                break;
            } else { // If new individual is not valid, remutate it.
                Dbg.print(DBG, MODULE, "Mutated invalid child:" + Dbg.NEW_LINE
                        + newIndi.toString());
            }
        }

        final Individual indi2 = new Individual(newIndi);
        Dbg.print(DBG, MODULE,
                "Before mutation:" + Dbg.NEW_LINE + indi.toString());
        Dbg.print(DBG, MODULE,
                "After mutation:" + Dbg.NEW_LINE + indi2.toString());
        Dbg.print(DBG, MODULE, "Mutated bit from rule " + ruleIndex
                + " postion from " + positionInPrecondStart +" to "+positionInPrecond);
        return indi2;
    }
}
