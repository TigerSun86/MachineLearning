package genetic;

import java.util.ArrayList;
import java.util.BitSet;

import common.Hypothesis;
import common.RawAttr;
import common.RawAttrList;
import debug.Dbg;

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
    private final String defaultPredict;
    private ArrayList<Integer> condStart;
    private ArrayList<Integer> condSize;
    private final int ruleSize;

    public BitSet rules = new BitSet();

    public BitStringRules(final RawAttrList attrList2) {
        attrList = attrList2;
        defaultPredict = attrList.t.valueList.get(0);
        initCondOffset(attrList2);
        final int tarStart = condStart.get(condStart.size() - 1);
        final int tarOffset = condSize.get(condSize.size() - 1);
        ruleSize = tarStart + tarOffset;

        rules = new BitSet(ruleSize); // Only one rule (all zero).
    }

    private void initCondOffset (final RawAttrList attrList2) {
        condStart = new ArrayList<Integer>();
        condSize = new ArrayList<Integer>();
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
            condSize.add(sizeOfCond);
            offset += sizeOfCond;
        }
        // The target attrbute.
        condStart.add(offset);
        condSize.add(attrList2.t.valueList.size());
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
        final long opL = op.toLongArray()[0];
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
        return rules.get(index * ruleSize, (index + 1) * ruleSize);
    }

    private BitSet getCond (final BitSet rule, final int index) {
        final int start = condStart.get(index);
        final int offset = condSize.get(index);
        return rule.get(start, start + offset);
    }

    private BitSet getPostcond (final BitSet rule) {
        return getCond(rule, condStart.size() - 1);
    }

    public int size () {
        return rules.length() / ruleSize;
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
        final long opL = op.toLongArray()[0];
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
        final double lowD = Double.longBitsToDouble(lowL[0]);
        return lowD;
    }
}
