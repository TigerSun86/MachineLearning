package genetic;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import util.Dbg;
import util.MyMath;

import common.Hypothesis;
import common.RawAttr;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName: BitStringRules.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Mar 31, 2014 10:25:58 PM
 */
public class BitStringRules implements Hypothesis {
    private final BSAttrs attrs;
    private int size;

    public BitSet ruleSet;
    public String defaultPredict;

    /**
     * Initialize with ruleSet converted by examplse.
     * */
    public BitStringRules(final BSAttrs attrs2, final RawExampleList exs) {
        this.attrs = attrs2;
        size = 0;
        ruleSet = new BitSet(); // No rule.

        for (RawExample ex : exs) {
            // Convert example to rule.
            final BitSet rule = generateRuleByEx(ex);
            addRule(rule);
        }

        defaultPredict = attrs.rawAttrs.t.valueList.get(0);
    }

    /**
     * Initialize with 2 random ruleSet
     * */
    public BitStringRules(final BSAttrs attrs2) {
        this.attrs = attrs2;
        size = 0;
        ruleSet = new BitSet(); // No rule.
        // Generate 2 random rules.
        BitSet rule = generateRuleByRan();
        addRule(rule);
        rule = generateRuleByRan();
        addRule(rule);

        defaultPredict = attrs.rawAttrs.t.valueList.get(0);
    }

    public int size () {
        return size;
    }

    @Override
    public String toString () {
        final StringBuffer sb = new StringBuffer();
        // Traverse all ruleSet.
        final int numRules = this.size();
        for (int i = 0; i < numRules; i++) {
            // Get the rule.
            final BitSet rule = getRule(i);
            sb.append("Rule " + i + " [");
            sb.append(ruleToString(rule));
            sb.append("]" + Dbg.NEW_LINE);
        }
        return sb.toString();
    }

    @Override
    public String predict (ArrayList<String> in) {
        String predict = null;
        // Traverse all ruleSet.
        final int numRules = this.size();
        for (int i = 0; i < numRules; i++) {
            // Get the rule.
            final BitSet rule = getRule(i);
            // Use rule to predict
            final String answer = rulePredict(rule, in);
            if (answer != null) { // The example is matched by the rule.
                predict = answer;
                break;
            }
        }
        if (predict != null) {
            return predict;
        } else {
            return defaultPredict;
        }
    }

    /* generate rule by example begin ************************** */
    private BitSet generateRuleByEx (RawExample ex) {
        final BitSet rule = new BitSet();
        for (int i = 0; i < attrs.rawAttrs.xList.size(); i++) {
            final RawAttr attr = attrs.rawAttrs.xList.get(i);
            final String x = ex.xList.get(i);
            final BitSet cond;
            if (attr.isContinuous) {
                cond = geConByEx(x, attr, attrs.exp.get(i));
            } else {
                cond = geDisByEx(x, attr);
            }
            final int start = attrs.condStart.get(i);
            final int length = attrs.condLength.get(i);
            bitSetCopy(rule, cond, start, length);
        }
        // Target.
        final RawAttr attr = attrs.rawAttrs.t;
        final String x = ex.t;
        final BitSet cond = geDisByEx(x, attr);
        final int start = attrs.condStart.get(attrs.condStart.size() - 1);
        final int length = attrs.condLength.get(attrs.condLength.size() - 1);
        bitSetCopy(rule, cond, start, length);
        return rule;
    }

    private static BitSet geConByEx (String x, RawAttr attr, final int exp) {
        final BitSet cond = new BitSet();
        // op = 10 : low <= x <= high.
        // In this case, low == high, means x == low.
        cond.set(0, false);
        cond.set(1, true);
        final double xD = Double.valueOf(x);
        final double low = xD;
        final double high = xD;
        setDoubleValue(cond, 2, low, exp); // Set value low.
        setDoubleValue(cond, 2 + 64, high, exp); // Set value high.
        return cond;
    }

    private static BitSet geDisByEx (String x, RawAttr attr) {
        final BitSet cond = new BitSet();
        final int index = attr.valueList.indexOf(x); // Find the index of x.
        assert index != -1;
        cond.set(index); // Set the bit corresponding of x to 1.
        return cond;
    }

    /* generate rule by example end ************************** */

    /* generate rule by random begin ************************** */
    private BitSet generateRuleByRan () {
        final BitSet rule = new BitSet();
        for (int i = 0; i < attrs.rawAttrs.xList.size(); i++) {
            final RawAttr attr = attrs.rawAttrs.xList.get(i);
            final BitSet cond;
            if (attr.isContinuous) {
                cond =
                        geConByRan(attr, attrs.exp.get(i),
                                attrs.minValue.get(i), attrs.maxValue.get(i));
            } else {
                cond = geDisByRan(attr);
            }
            final int start = attrs.condStart.get(i);
            final int length = attrs.condLength.get(i);
            bitSetCopy(rule, cond, start, length);
        }
        // Target.
        final RawAttr attr = attrs.rawAttrs.t;
        final BitSet cond = geneTar(attr);
        final int start = attrs.condStart.get(attrs.condStart.size() - 1);
        final int length = attrs.condLength.get(attrs.condLength.size() - 1);
        bitSetCopy(rule, cond, start, length);
        return rule;
    }

    private static BitSet geConByRan (final RawAttr attr, final int exp,
            final double min, final double max) {
        final BitSet cond = new BitSet();
        // op
        final int opN = new Random().nextInt(4);
        final boolean op0 = ((opN & 1) == 1) ? true : false;
        final boolean op1 = (((opN >> 1) & 1) == 1) ? true : false;
        cond.set(0, op0);
        cond.set(1, op1);
        // Generate low and high value.
        double low;
        double high;
        while (true) {
            low = MyMath.randomDoubleBetween(min, max);
            high = MyMath.randomDoubleBetween(min, max);
            if (Double.compare(low, high) <= 0) {
                break; // Only legal when low <= high.
            }
        }
        setDoubleValue(cond, 2, low, exp); // Set value low.
        setDoubleValue(cond, 2 + 64, high, exp); // Set value high.
        return cond;
    }

    private static BitSet geDisByRan (RawAttr attr) {
        final int length = attr.valueList.size();
        final int max = (int) Math.pow(2, length);
        final Random ran = new Random();
        int ranN = 0; // ranN should have 'length' bits.
        while (ranN == 0) {
            // Generate a random number except zero (zero is illegal for
            // discrete attribute).
            ranN = ran.nextInt(max);
        }

        final BitSet cond = new BitSet();
        // From low to high, assign each bit of ranN to cond.
        for (int i = 0; i < length; i++) {
            final int bit = (ranN >> i) & 1; // Filter needless bits.
            final boolean b = (bit == 1) ? true : false;
            cond.set(i, b); // Set the bit in cond.
        }
        return cond;
    }

    private static BitSet geneTar (RawAttr attr) {
        final Random ran = new Random();
        final int index = ran.nextInt(attr.valueList.size());
        final BitSet cond = new BitSet();
        cond.set(index);
        return cond;
    }

    /* generate rule by random end ************************** */

    /* toString begain ******************************************** */
    private String ruleToString (BitSet rule) {
        final StringBuffer sb = new StringBuffer();
        sb.append("IF ");
        for (int i = 0; i < attrs.rawAttrs.xList.size(); i++) {
            final RawAttr attr = attrs.rawAttrs.xList.get(i);
            final BitSet cond = getCond(rule, i); // Get precond.
            sb.append("(");
            sb.append(condToString(cond, attr, attrs.exp.get(i)));
            sb.append(")");
            if (i != attrs.rawAttrs.xList.size() - 1) {
                sb.append(" && ");
            }
        }
        final RawAttr attr = attrs.rawAttrs.t;
        final BitSet cond = getPostcond(rule); // Get postcond.
        sb.append(" THEN ");
        sb.append(condToString(cond, attr, 0)); // Target
        return sb.toString();
    }

    private String
            condToString (final BitSet cond, final RawAttr attr, int exp) {
        final StringBuffer sb = new StringBuffer();
        if (attr.isContinuous) {
            // It's a continuous value.
            sb.append(continueToString(cond, attr, exp));
        } else {
            sb.append(discreteToString(cond, attr));
        }
        return sb.toString();
    }

    private String continueToString (BitSet cond, final RawAttr attr, int exp) {
        final BitSet op = cond.get(0, 2);
        final long opL;
        final long[] opA = op.toLongArray();
        if (opA.length != 0) {
            opL = opA[0];
        } else {
            opL = 0;
        }
        // To make double don't display redundant zero in the tail.
        final String fmt = String.format("%%.%df", exp);
        if (opL == 0) { // 00: x >= low
            final double low = getDoubleValue(cond, 2, exp);
            final String ret = String.format("%s >= " + fmt, attr.name, low);
            return ret;
        } else if (opL == 1) { // 01: x <= low
            final double low = getDoubleValue(cond, 2, exp);
            final String ret = String.format("%s <= " + fmt, attr.name, low);
            return ret;
        } else if (opL == 2) { // 10: low <= x <= high
            final double low = getDoubleValue(cond, 2, exp);
            final double high = getDoubleValue(cond, 2 + 64, exp);
            final String ret =
                    String.format(fmt + " <= %s <= " + fmt, low, attr.name,
                            high);
            return ret;
        } else { // 11: doesn't matter.
            final String ret = String.format("%s doesn't matter", attr.name);
            return ret;
        }
    }

    private String discreteToString (BitSet cond, final RawAttr attr) {
        final StringBuffer sb = new StringBuffer();
        sb.append(attr.name);
        sb.append("=");
        for (int i = 0; i < attr.valueList.size(); i++) {
            if (cond.get(i)) { // The condition's been set.
                final String v = attr.valueList.get(i);
                sb.append(v);
                if ((i != attrs.rawAttrs.xList.size() - 1)
                        && (cond.nextSetBit(i + 1) != -1)) {
                    // There's still bit 1 later.
                    sb.append(" || ");
                }
            }
        }
        return sb.toString();
    }

    /* toString end ******************************************** */

    /* basic methods begin ********************* */

    private BitSet getRule (final int index) {
        return ruleSet.get(index * attrs.ruleLength, (index + 1)
                * attrs.ruleLength);
    }

    private BitSet getCond (final BitSet rule, final int index) {
        final int start = attrs.condStart.get(index);
        final int offset = attrs.condLength.get(index);
        return rule.get(start, start + offset);
    }

    private BitSet getPostcond (final BitSet rule) {
        return getCond(rule, attrs.condStart.size() - 1); // Target is last cond
    }

    private static double getDoubleValue (final BitSet cond,
            final int fromIndex, final int exp) {
        final BitSet low = cond.get(fromIndex, fromIndex + 64);
        final long[] lowL = low.toLongArray();
        final double lowD;
        if (lowL.length != 0) { // Convert value of rule to (x / 10^exp).
            lowD = (Math.round((double) lowL[0])) / (Math.pow(10, exp));
        } else { // All 64 bits are zeros.
            lowD = 0;
        }
        return lowD;
    }

    private static void setDoubleValue (final BitSet cond, final int fromIndex,
            final double x, final int exp) {
        assert !Double.isNaN(x); // x should be a meaningful double value.
        // Convert double x to long (x * 10^exp) and store in rule.
        final long xL = Math.round(x * (Math.pow(10, exp)));
        final long[] xA = { xL };
        final BitSet xB = BitSet.valueOf(xA); // Convert double to BitSet.
        // Copy the BitSet to specified position in cond.
        bitSetCopy(cond, xB, fromIndex, 64);
    }

    private static void bitSetCopy (final BitSet des, final BitSet src,
            final int fromIndexOfDes, final int lengthOfSrc) {
        for (int i = 0; i < lengthOfSrc; i++) {
            final boolean v = src.get(i);
            des.set(fromIndexOfDes + i, v);
        }
    }

    private void addRule (final BitSet rule) {
        // Append the rule to the end of the ruleSet.
        bitSetCopy(ruleSet, rule, size * attrs.ruleLength, attrs.ruleLength);
        size++;
    }

    /* basic methods end ********************* */

    /* predict begin ******************************************* */

    private String rulePredict (BitSet rule, ArrayList<String> in) {
        boolean isMatched = true;
        // Traverse all attrs in example.
        for (int i = 0; i < in.size(); i++) {
            // The x value in example.
            final String x = in.get(i);

            // Check the x with corresponding precond in rule.
            final BitSet cond = getCond(rule, i);
            final RawAttr attr = attrs.rawAttrs.xList.get(i);
            final boolean correct;
            if (attr.isContinuous) {// It's a continuous value.
                // Convert x to double.
                final double xD = Double.valueOf(x);
                // Check if the continuous value matches the condition.
                correct = contPredict(cond, xD, attrs.exp.get(i));
            } else {// Discrete value.
                // The index of x in value list of attribute.
                final int index = attr.valueList.indexOf(x);
                // Check the corresponding bit in condition.
                // If the bit is 1, it's correct.
                correct = cond.get(index);
            }
            if (!correct) { // One of the conditions is violated by example.
                isMatched = false;
                break;
            }
        }
        if (isMatched) { // All of the conditions of rule are matched by example
            final BitSet postcond = getPostcond(rule);
            final int index = postcond.nextSetBit(0);
            assert index != -1;
            return attrs.rawAttrs.t.valueList.get(index);
        } else { // The rule is not matched.
            return null;
        }
    }

    private static boolean contPredict (BitSet cond, double x, int exp) {
        final BitSet op = cond.get(0, 2);
        final long opL;
        final long[] opA = op.toLongArray();
        if (opA.length != 0) {
            opL = opA[0];
        } else {
            opL = 0;
        }
        if (opL == 0) { // 00: x >= low
            final double low = getDoubleValue(cond, 2, exp);
            if (Double.compare(x, low) >= 0) {
                return true;
            } else {
                return false;
            }
        } else if (opL == 1) { // 01: x <= low
            final double low = getDoubleValue(cond, 2, exp);
            if (Double.compare(x, low) <= 0) {
                return true;
            } else {
                return false;
            }
        } else if (opL == 2) { // 10: low <= x <= high
            final double low = getDoubleValue(cond, 2, exp);
            final double high = getDoubleValue(cond, 2 + 64, exp);
            if ((Double.compare(x, low) >= 0) && (Double.compare(x, high) <= 0)) {
                return true;
            } else {
                return false;
            }
        } else { // 11: doesn't matter.
            return true;
        }
    }

    /* predict end ******************************************* */

    public static class GeneBlock {
        public BitSet block;
        public int d1;
        public int d2;
        public int size;

        public GeneBlock(BitSet block, int d1, int d2, int size) {
            super();
            this.block = block;
            this.d1 = d1;
            this.d2 = d2;
            this.size = size;
        }
    }
/*
    public GeneBlock getGeneBlock () {
        // Select d1 position.
        // Randomly select a precond.
        final Random ran = new Random();
        final int indexOfCond = ran.nextInt(attrs.rawAttrs.xList.size());
        final boolean isLast =
                (indexOfCond == (attrs.rawAttrs.xList.size() - 1));
        final RawAttr cond = attrs.rawAttrs.xList.get(indexOfCond);
        int d1 = attrs.condStart.get(indexOfCond);
        if (cond.isContinuous) {
            d1 += d1FromCont(isLast);
        } else {
            d1 += d1FromDis(cond, isLast);
        }

        return null;
    }

    private int d1FromCont () {
        final int d1;
        // Continuous attribute has 3 parts: 2 bits op, 64 bits low, 64 bits
        // high.
        final Random ran = new Random();
        final int part = ran.nextInt(3);
        if (part == 0) {// op, d1 could be 3 possible position
            d1 = ran.nextInt(3);
        } else if (part == 1) { // low, d1 could be

        }
        return 0;
    }

    private int d1FromDis (RawAttr cond, final boolean isLast) {
        // d1 cannot be the position after last bit of cond, because there's a
        // situation that if d1 is at the end of all preconditions, d2 has to be
        // in the area of target bits, that's illegal.
        final int bitSize = cond.valueList.size();
        final int d1;
        if (isLast) {
            d1 = new Random().nextInt(bitSize);
        } else {
            d1 = new Random().nextInt(bitSize + 1);
        }

        return d1;
    }*/
}
