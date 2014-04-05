package genetic;

import java.util.BitSet;
import java.util.Random;

import util.Dbg;

/**
 * FileName: OffspringProducer.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Apr 4, 2014 10:16:38 PM
 */
public class OffspringProducer {
    public static final String MODULE = "OSP";
    public static boolean DBG = true;

    public static Individual[] produce (Individual p1, Individual p2) {
        final Individual c1 = new Individual();
        final Individual c2 = new Individual();
        c1.rules = new BitStringRules(p1.rules);
        c2.rules = new BitStringRules(p2.rules);
        cross(c1.rules, c2.rules);
        c1.accur = 0;
        c2.accur = 0;
        Dbg.print(DBG, MODULE, "Parent 1:" + Dbg.NEW_LINE + p1.toString());
        Dbg.print(DBG, MODULE, "Parent 2:" + Dbg.NEW_LINE + p2.toString());
        Dbg.print(DBG, MODULE, "Child 1:" + Dbg.NEW_LINE + c1.toString());
        Dbg.print(DBG, MODULE, "Child 2:" + Dbg.NEW_LINE + c2.toString());
        return new Individual[] { c1, c2 };
    }

    private static void cross (final BitStringRules indi1,
            final BitStringRules indi2) {
        final GeneBlock gb1 = getGeneBlock(indi1);
        final GeneBlock gb2 = getGeneBlock(indi2, gb1.d1, gb1.d2);
        exchangeGeneBlock(gb1, gb2);
        // Set gene should use the origin gb, because d1 and d2 are the gap for
        // the corresponding rule set.
        setGeneBlock(indi1, gb1);
        setGeneBlock(indi2, gb2);
    }

    private static class GeneBlock {
        public BitSet block;
        public int size;
        public int d1;
        public int d2;

        public GeneBlock(BitSet block, int d1, int d2, int size) {
            this.block = block;
            this.size = size;
            this.d1 = d1;
            this.d2 = d2;
        }
    }

    private static GeneBlock getGeneBlock (final BitStringRules indi) {
        // Select cross cover positions.
        // Positions are legal only when d1+d2 <= precondsLength -1;
        final Random ran = new Random();
        // d1 cannot be the position after last bit of cond, because there's a
        // situation that if d1 is at the end of all preconditions, d2 has to be
        // in the area of target bits, that's illegal.
        final int d1 = ran.nextInt(indi.attrs.precondsLength);
        int d2 = ran.nextInt(indi.attrs.precondsLength - d1);
        // d2 changes to distance from the end of the rule (include target) to a
        // random position in preconds.
        d2 += indi.attrs.condLength.get(indi.attrs.condLength.size() - 1);
        assert (d1 + d2) <= indi.attrs.ruleLength - 1;

        final int blockSize =
                (indi.numOfRules * indi.attrs.ruleLength) - d2 - d1;
        final BitSet block = indi.ruleSet.get(d1, d1 + blockSize);
        final GeneBlock gb = new GeneBlock(block, d1, d2, blockSize);
        return gb;
    }

    private static GeneBlock getGeneBlock (final BitStringRules indi,
            final int d1, final int d2) {
        // If numOfRules is 3 (index 0,1,2), if d1p picked up index 1, d2p could
        // pick up index 1 or 2.
        final Random ran = new Random();
        final int ruleIndexForD1p = ran.nextInt(indi.numOfRules);
        final int reversedRuleIndexForD2p =
                ran.nextInt(indi.numOfRules - ruleIndexForD1p);

        final int d1p = d1 + (ruleIndexForD1p * indi.attrs.ruleLength);
        final int d2p = d2 + (reversedRuleIndexForD2p * indi.attrs.ruleLength);

        final int blockSize =
                (indi.numOfRules * indi.attrs.ruleLength) - d2p - d1p;
        final BitSet block = indi.ruleSet.get(d1p, d1p + blockSize);
        final GeneBlock gb = new GeneBlock(block, d1p, d2p, blockSize);
        return gb;
    }

    private static void exchangeGeneBlock (final GeneBlock gb1,
            final GeneBlock gb2) {
        final BitSet blockT = gb1.block;
        final int sizeT = gb1.size;
        gb1.block = gb2.block;
        gb1.size = gb2.size;
        gb2.block = blockT;
        gb2.size = sizeT;
    }

    private static void setGeneBlock (final BitStringRules indi,
            final GeneBlock gb) {
        // The info in the rule set after the position of d2 need to be backed
        // up first.
        final int ruleSetLength = indi.numOfRules * indi.attrs.ruleLength;
        final BitSet backup =
                indi.ruleSet.get(ruleSetLength - gb.d2, ruleSetLength);
        // Replace gene block in rule set with gb.
        BitStringRules.bitSetCopy(indi.ruleSet, gb.block, gb.d1, gb.size);
        // Recovery the tail of the rule set.
        BitStringRules.bitSetCopy(indi.ruleSet, backup, gb.d1 + gb.size, gb.d2);
        final int newRuleSetLength = gb.d1 + gb.size + gb.d2;
        assert (newRuleSetLength % indi.attrs.ruleLength) == 0;
        // Update number of rules.
        indi.numOfRules = newRuleSetLength / indi.attrs.ruleLength;
    }

}
