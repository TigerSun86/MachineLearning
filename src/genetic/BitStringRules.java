package genetic;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import util.Dbg;

import common.Hypothesis;
import common.RawAttr;
import common.RawAttrList;
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
    private final RawAttrList attrList;
    private ArrayList<Integer> condStart;
    private ArrayList<Integer> condLength;
    private final int ruleLength;
    private int size;

    public BitSet rules;
    public String defaultPredict;

    /**
     * Initialize with rules converted by examplse.
     * */
    public BitStringRules(final RawAttrList attrList2, final RawExampleList exs) {
        attrList = attrList2;
        initCondOffset(attrList2);
        final int tarStart = condStart.get(condStart.size() - 1);
        final int tarOffset = condLength.get(condLength.size() - 1);
        ruleLength = tarStart + tarOffset;

        size = 0;
        rules = new BitSet(); // No rule.

        for (RawExample ex : exs) {
            final BitSet rule = geneRuleByEx(ex); // Convert example to rule.
            addRule(rule);
        }

        defaultPredict = attrList.t.valueList.get(0);
    }

    /**
     * Initialize with 2 random rules
     * */
    public BitStringRules(final RawAttrList attrList2) {
        attrList = attrList2;
        initCondOffset(attrList2);
        final int tarStart = condStart.get(condStart.size() - 1);
        final int tarOffset = condLength.get(condLength.size() - 1);
        ruleLength = tarStart + tarOffset;

        size = 0;
        rules = new BitSet(); // No rule.

        BitSet rule = geneRuleByRan(); // Generate 2 random rules.
        addRule(rule);
        rule = geneRuleByRan();
        addRule(rule);

        defaultPredict = attrList.t.valueList.get(0);
    }

    private void initCondOffset (final RawAttrList attrList2) {
        condStart = new ArrayList<Integer>();
        condLength = new ArrayList<Integer>();
        int offset = 0;
        for (int i = 0; i < attrList2.xList.size(); i++) {
            final RawAttr attr = attrList2.xList.get(i);
            condStart.add(offset);
            final int sizeOfCond;
            if (attr.isContinuous) {// It's a continuous value.
                sizeOfCond = 2 + 64 + 64;
            } else {// Discrete value.
                sizeOfCond = attr.valueList.size();
            }
            condLength.add(sizeOfCond);
            offset += sizeOfCond;
        }
        // The target attrbute.
        condStart.add(offset);
        condLength.add(attrList2.t.valueList.size());
    }

    public BitSet geneRuleByEx (RawExample ex) {
        final BitSet rule = new BitSet();
        for (int i = 0; i < attrList.xList.size(); i++) {
            final RawAttr attr = attrList.xList.get(i);
            final String x = ex.xList.get(i);
            final BitSet cond;
            if (attr.isContinuous) {
                cond = convertCon(x, attr);
            } else {
                cond = convertDis(x, attr);
            }
            final int start = condStart.get(i);
            final int length = condLength.get(i);
            bitSetCopy(rule, cond, start, length);
        }
        // Target.
        final RawAttr attr = attrList.t;
        final String x = ex.t;
        final BitSet cond = convertDis(x, attr);
        final int start = condStart.get(condStart.size() - 1);
        final int length = condLength.get(condLength.size() - 1);
        bitSetCopy(rule, cond, start, length);
        return rule;
    }

    private static BitSet convertCon (String x, RawAttr attr) {
        final BitSet cond = new BitSet();
        // op = 10 : low < x < high
        cond.set(0, false);
        cond.set(1, true);
        final double xD = Double.valueOf(x);
        final double low = xD - 1;
        final double high = xD + 1;
        setDoubleValue(cond, 2, low); // Set value low.
        setDoubleValue(cond, 2 + 64, high); // Set value high.
        return cond;
    }

    private static BitSet convertDis (String x, RawAttr attr) {
        final BitSet cond = new BitSet();
        final int index = attr.valueList.indexOf(x); // Find the index of x.
        assert index != -1;
        cond.set(index); // Set the bit corresponding of x to 1.
        return cond;
    }

    private static void bitSetCopy (final BitSet des, final BitSet src,
            final int fromIndexOfDes, final int lengthOfSrc) {
        for (int i = 0; i < lengthOfSrc; i++) {
            final boolean v = src.get(i);
            des.set(fromIndexOfDes + i, v);
        }
    }

    public void addRule (final BitSet rule) {
        // Append the rule to the end of the rules.
        bitSetCopy(rules, rule, size * ruleLength, ruleLength);
        size++;
    }

    public BitSet geneRuleByRan () {
        final BitSet rule = new BitSet();
        for (int i = 0; i < attrList.xList.size(); i++) {
            final RawAttr attr = attrList.xList.get(i);
            final BitSet cond;
            if (attr.isContinuous) {
                cond = geneCon(attr);
            } else {
                cond = geneDis(attr);
            }
            final int start = condStart.get(i);
            final int length = condLength.get(i);
            bitSetCopy(rule, cond, start, length);
        }
        // Target.
        final RawAttr attr = attrList.t;
        final BitSet cond = geneTar(attr);
        final int start = condStart.get(condStart.size() - 1);
        final int length = condLength.get(condLength.size() - 1);
        bitSetCopy(rule, cond, start, length);
        return rule;
    }

    private static BitSet geneCon (RawAttr attr) {
        final BitSet cond = new BitSet();
        final Random ran = new Random();
        // op
        final int opN = ran.nextInt(4);
        final boolean op1 = ((opN & 1) == 1) ? true : false;
        final boolean op2 = (((opN >> 1) & 1) == 1) ? true : false;
        cond.set(0, op1);
        cond.set(1, op2);

        final double low = ran.nextDouble();
        final double high = ran.nextDouble();
        setDoubleValue(cond, 2, low); // Set value low.
        setDoubleValue(cond, 2 + 64, high); // Set value high.
        return cond;
    }

    private static BitSet geneDis (RawAttr attr) {
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

    @Override
    public String toString () {
        final StringBuffer sb = new StringBuffer();
        // Traverse all rules.
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

    private String ruleToString (BitSet rule) {
        final StringBuffer sb = new StringBuffer();
        sb.append("IF ");
        for (int i = 0; i < attrList.xList.size(); i++) {
            final RawAttr attr = attrList.xList.get(i);
            final BitSet cond = getCond(rule, i); // Get precond.
            sb.append("(");
            sb.append(condToString(cond, attr));
            sb.append(")");
            if (i != attrList.xList.size() - 1) {
                sb.append(" && ");
            }
        }
        final RawAttr attr = attrList.t;
        final BitSet cond = getPostcond(rule); // Get postcond.
        sb.append(" THEN ");
        sb.append(condToString(cond, attr));
        return sb.toString();
    }

    private String condToString (final BitSet cond, final RawAttr attr) {
        final StringBuffer sb = new StringBuffer();
        if (attr.isContinuous) {
            // It's a continuous value.
            sb.append(continueToString(cond, attr));
        } else {
            sb.append(discreteToString(cond, attr));
        }
        return sb.toString();
    }

    private String continueToString (BitSet cond, final RawAttr attr) {
        final BitSet op = cond.get(0, 2);
        final long opL;
        final long[] opA = op.toLongArray();
        if (opA.length != 0) {
            opL = opA[0];
        } else {
            opL = 0;
        }
        if (opL == 0) { // 00: x > low
            final double low = getDoubleValue(cond, 2);
            final String ret = String.format("%s > %.2f", attr.name, low);
            return ret;
        } else if (opL == 1) { // 01: x < low
            final double low = getDoubleValue(cond, 2);
            final String ret = String.format("%s < %.2f", attr.name, low);
            return ret;
        } else if (opL == 2) { // 10: low < x < high
            final double low = getDoubleValue(cond, 2);
            final double high = getDoubleValue(cond, 2 + 64);
            final String ret =
                    String.format("%.2f < %s < %.2f", low, attr.name, high);
            return ret;
        } else { // 11: don't matter.
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
                if ((i != attrList.xList.size() - 1)
                        && (cond.nextSetBit(i + 1) != -1)) {
                    // There's still bit 1 later.
                    sb.append(" || ");
                }
            }
        }
        return sb.toString();
    }

    private BitSet getRule (final int index) {
        return rules.get(index * ruleLength, (index + 1) * ruleLength);
    }

    private BitSet getCond (final BitSet rule, final int index) {
        final int start = condStart.get(index);
        final int offset = condLength.get(index);
        return rule.get(start, start + offset);
    }

    private BitSet getPostcond (final BitSet rule) {
        return getCond(rule, condStart.size() - 1);
    }

    public int size () {
        return size;
    }

    @Override
    public String predict (ArrayList<String> in) {
        String predict = null;
        // Traverse all rules.
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

    private String rulePredict (BitSet rule, ArrayList<String> in) {
        boolean isMatched = true;
        // Traverse all attrs in example.
        for (int i = 0; i < in.size(); i++) {
            // The x value in example.
            final String x = in.get(i);

            // Check the x with corresponding precond in rule.
            final BitSet cond = getCond(rule, i);
            final RawAttr attr = attrList.xList.get(i);
            final boolean correct;
            if (attr.isContinuous) {// It's a continuous value.
                // Convert x to double.
                final double xD = Double.valueOf(x);
                // Check if the continuous value matches the condition.
                correct = contPredict(cond, xD);
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
            return attrList.t.valueList.get(index);
        } else { // The rule is not matched.
            return null;
        }
    }

    private boolean contPredict (BitSet cond, double x) {
        final BitSet op = cond.get(0, 2);
        final long opL;
        final long[] opA = op.toLongArray();
        if (opA.length != 0) {
            opL = opA[0];
        } else {
            opL = 0;
        }
        if (opL == 0) { // 00: x > low
            final double low = getDoubleValue(cond, 2);
            if (Double.compare(x, low) > 0) {
                return true;
            } else {
                return false;
            }
        } else if (opL == 1) { // 01: x < low
            final double low = getDoubleValue(cond, 2);
            if (Double.compare(x, low) < 0) {
                return true;
            } else {
                return false;
            }
        } else if (opL == 2) { // 10: low < x < high
            final double low = getDoubleValue(cond, 2);
            final double high = getDoubleValue(cond, 2 + 64);
            if ((Double.compare(x, low) > 0) && (Double.compare(x, high) < 0)) {
                return true;
            } else {
                return false;
            }
        } else { // 11: don't matter.
            return true;
        }
    }

    private static double
            getDoubleValue (final BitSet cond, final int fromIndex) {
        final BitSet low = cond.get(fromIndex, fromIndex + 64);
        final long[] lowL = low.toLongArray();
        final double lowD;
        if (lowL.length != 0) {
            lowD = Double.longBitsToDouble(lowL[0]);
        } else { // All 64 bits are zeros.
            lowD = 0;
        }
        return lowD;
    }

    private static void setDoubleValue (final BitSet cond, final int fromIndex,
            final double x) {
        assert !Double.isNaN(x); // x should be a meaningful double value.
        final long xL = Double.doubleToRawLongBits(x);
        final long[] xA = { xL };
        final BitSet xB = BitSet.valueOf(xA); // Convert double to BitSet.
        // Copy the BitSet to specified position in cond.
        bitSetCopy(cond, xB, fromIndex, 64);
    }

}
