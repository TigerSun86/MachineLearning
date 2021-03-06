package genetic;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
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
    public final BSAttrs attrs;
    public int numOfRules;

    public BitSet ruleSet;
    public String defaultPredict;

    /**
     * Initialize with ruleSet converted by examples.
     * */
    public BitStringRules(final BSAttrs attrs2, final RawExampleList exs) {
        this.attrs = attrs2;
        numOfRules = 0;
        ruleSet = new BitSet(); // No rule.

        // To generate default predict.
        final HashSet<Integer> targets = new HashSet<Integer>();
        // Generate rules by examples.
        for (RawExample ex : exs) {
            // Convert example to rule.
            final BitSet rule = generateRuleByEx(ex);
            addRule(rule);
            if (attrs.rawAttrs.t.valueList.contains(ex.t)) {
                final int tarIndex = attrs.rawAttrs.t.valueList.indexOf(ex.t);
                targets.add(tarIndex);
            }
        }
        // Generate default predict.
        final Random ran = new Random();
        int defPIndex;
        while (true) {
            defPIndex = ran.nextInt(attrs.rawAttrs.t.valueList.size());
            if ((attrs.rawAttrs.t.valueList.size() == targets.size())
                    || !targets.contains(defPIndex)) {
                // All targets have been covered by rules,
                // or get random target haven't been covered.
                break;
            }
        }
        final BitSet defP = new BitSet();
        defP.set(defPIndex);
        bitSetCopy(ruleSet, defP, 0, attrs.defPredictLength);
    }

    /**
     * Initialize with 2 random ruleSet
     * */
    public BitStringRules(final BSAttrs attrs2) {
        this.attrs = attrs2;
        numOfRules = 0;
        ruleSet = new BitSet(); // No rule.
        // Generate 2 random rules.
        BitSet rule = generateRuleByRan();
        addRule(rule);
        rule = generateRuleByRan();
        addRule(rule);

        // Generate default predict.
        final int defPIndex =
                new Random().nextInt(attrs.rawAttrs.t.valueList.size());
        final BitSet defP = new BitSet();
        defP.set(defPIndex);
        bitSetCopy(ruleSet, defP, 0, attrs.defPredictLength);
    }

    /**
     * Initialize with a clone of other BitStringRules.
     * */
    public BitStringRules(final BitStringRules other) {
        this.attrs = other.attrs;
        numOfRules = other.numOfRules;
        ruleSet = new BitSet();
        bitSetCopy(ruleSet, other.ruleSet, 0, other.getRuleSetLength());
    }

    public int numOfRules () {
        return numOfRules;
    }

    @Override
    public String toString () {
        final StringBuffer sb = new StringBuffer();
        sb.append("Default: " + getDefPredict() + Dbg.NEW_LINE);
        // Traverse all ruleSet.
        final int numRules = this.numOfRules();
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
        final int numRules = this.numOfRules();
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
            return getDefPredict();
        }
    }

    public boolean isValid () {
        // Check default predict.
        if (!checkTar(getDefPreCond())) {
            return false;
        }
        // Go through all rules
        for (int i = 0; i < numOfRules; i++) {
            final BitSet rule = getRule(i);
            // Go through all conditions.
            for (int j = 0; j < attrs.rawAttrs.xList.size(); j++) {
                // Check if condtion is valid.
                final BitSet cond = getCond(rule, j);
                final RawAttr attr = attrs.rawAttrs.xList.get(j);
                final boolean isValid;
                if (attr.isContinuous) {
                    isValid = checkCon(cond, j);
                } else {
                    isValid = checkDis(cond);
                }
                if (!isValid) {
                    return false;
                }
            }
            // Check target.
            if (!checkTar(getPostcond(rule))) {
                return false;
            }
        }
        return true;
    }

    public static void bitSetCopy (final BitSet des, final BitSet src,
            final int fromIndexOfDes, final int lengthOfSrc) {
        for (int i = 0; i < lengthOfSrc; i++) {
            final boolean v = src.get(i);
            des.set(fromIndexOfDes + i, v);
        }
    }

    public int getRuleSetLength () {
        return attrs.defPredictLength + (numOfRules * attrs.ruleLength);
    }

    /* basic methods begin ********************* */

    private BitSet getRule (final int index) {
        return ruleSet.get(attrs.defPredictLength + index * attrs.ruleLength,
                attrs.defPredictLength + (index + 1) * attrs.ruleLength);
    }

    private BitSet getCond (final BitSet rule, final int index) {
        final int start = attrs.condStart.get(index);
        final int offset = attrs.condLength.get(index);
        return rule.get(start, start + offset);
    }

    private BitSet getPostcond (final BitSet rule) {
        return getCond(rule, attrs.condStart.size() - 1); // Target is last cond
    }

    private BitSet getDefPreCond () {
        return ruleSet.get(0, attrs.defPredictLength);
    }

    private String getDefPredict () {
        final BitSet predictRule = getDefPreCond();
        assert predictRule.cardinality() == 1;
        final int index = predictRule.nextSetBit(0);
        assert index != -1;
        return attrs.rawAttrs.t.valueList.get(index);
    }

    private double getDoubleValue (final BitSet cond, final int fromIndex,
            final int attrIndex) {
        final BitSet x = cond.get(fromIndex, fromIndex + 64);
        final long[] xL = x.toLongArray();
        final long xMapped;
        if (xL.length != 0) {// All 64 bits are zeros.
            xMapped = xL[0];
        } else {
            xMapped = 0;
        }
        // Convert mapped value back to actual value.
        final long xActual = attrs.mappedToActualValue(xMapped, attrIndex);
        // Convert value of rule to (x / 10^exp).
        return (Math.round(xActual)) / (Math.pow(10, attrs.exp.get(attrIndex)));
    }

    private void setDoubleValue (final BitSet cond, final int fromIndex,
            final double x, final int attrIndex) {
        assert !Double.isNaN(x); // x should be a meaningful double value.
        // Convert double x to long (x * 10^exp).
        final long xActual =
                Math.round(x * (Math.pow(10, attrs.exp.get(attrIndex))));
        // Convert x to a reasonable mapped range in long.
        final long xMapped = attrs.actualToMappedValue(xActual, attrIndex);
        final long[] xA = { xMapped };
        final BitSet xB = BitSet.valueOf(xA); // Convert double to BitSet.
        // Copy the BitSet to specified position in cond.
        bitSetCopy(cond, xB, fromIndex, 64);
    }

    private void addRule (final BitSet rule) {
        // Append the rule to the end of the ruleSet.
        bitSetCopy(ruleSet, rule, getRuleSetLength(), attrs.ruleLength);
        numOfRules++;
    }

    /* basic methods end ********************* */

    /* generate rule by example begin ************************** */
    private BitSet generateRuleByEx (RawExample ex) {
        final BitSet rule = new BitSet();
        for (int i = 0; i < attrs.rawAttrs.xList.size(); i++) {
            final RawAttr attr = attrs.rawAttrs.xList.get(i);
            final String x = ex.xList.get(i);
            final BitSet cond;
            if (attr.isContinuous) {
                cond = geConByEx(x, attr, i);
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

    private BitSet geConByEx (String x, RawAttr attr, final int attrIndex) {
        final BitSet cond = new BitSet();
        // op = 10 : low <= x <= high.
        // In this case, low == high, means x == low.
        cond.set(0, false);
        cond.set(1, true);
        final double xD = Double.valueOf(x);
        final double low = xD;
        final double high = xD;
        setDoubleValue(cond, 2, low, attrIndex); // Set value low.
        setDoubleValue(cond, 2 + 64, high, attrIndex); // Set value high.
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
                cond = geConByRan(attr, i);
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

    private BitSet geConByRan (final RawAttr attr, final int attrIndex) {
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
        final double min = attrs.minValue.get(attrIndex);
        final double max = attrs.maxValue.get(attrIndex);
        while (true) {
            low = MyMath.randomDoubleBetween(min, max);
            high = MyMath.randomDoubleBetween(min, max);
            if (Double.compare(low, high) <= 0) {
                break; // Only legal when low <= high.
            }
        }
        // Set value low.
        setDoubleValue(cond, 2, low, attrIndex);
        // Set value high.
        setDoubleValue(cond, 2 + 64, high, attrIndex);
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
            final BitSet cond = getCond(rule, i); // Get precond.
            sb.append("(");
            sb.append(condToString(cond, i));
            sb.append(")");
            if (i != attrs.rawAttrs.xList.size() - 1) {
                sb.append(" && ");
            }
        }
        final BitSet cond = getPostcond(rule); // Get postcond.
        sb.append(" THEN ");
        sb.append(condToString(cond, -1)); // Target
        return sb.toString();
    }

    private String condToString (final BitSet cond, final int attrIndex) {
        final StringBuffer sb = new StringBuffer();
        final RawAttr attr;
        if (attrIndex == -1) {// Target attribute.
            attr = attrs.rawAttrs.t;
        } else { // Precondition attribute.
            attr = attrs.rawAttrs.xList.get(attrIndex);
        }
        if (attr.isContinuous) {
            // It's a continuous value.
            sb.append(continueToString(cond, attr, attrIndex));
        } else {
            sb.append(discreteToString(cond, attr));
        }
        return sb.toString();
    }

    private String continueToString (BitSet cond, final RawAttr attr,
            int attrIndex) {
        final BitSet op = cond.get(0, 2);
        final long opL;
        final long[] opA = op.toLongArray();
        if (opA.length != 0) {
            opL = opA[0];
        } else {
            opL = 0;
        }
        // To make double don't display redundant zero in the tail.
        final String fmt = String.format("%%.%df", attrs.exp.get(attrIndex));
        if (opL == 0) { // 00: x >= low
            final double low = getDoubleValue(cond, 2, attrIndex);
            final String ret = String.format("%s>=" + fmt, attr.name, low);
            return ret;
        } else if (opL == 1) { // 01: x <= high
            final double high = getDoubleValue(cond, 2 + 64, attrIndex);
            final String ret = String.format("%s<=" + fmt, attr.name, high);
            return ret;
        } else if (opL == 2) { // 10: low <= x <= high
            final double low = getDoubleValue(cond, 2, attrIndex);
            final double high = getDoubleValue(cond, 2 + 64, attrIndex);
            final String ret =
                    String.format(fmt + "<=%s<=" + fmt, low, attr.name,
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
                correct = contPredict(cond, xD, i);
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

    private boolean contPredict (BitSet cond, double x, int attrIndex) {
        final BitSet op = cond.get(0, 2);
        final long opL;
        final long[] opA = op.toLongArray();
        if (opA.length != 0) {
            opL = opA[0];
        } else {
            opL = 0;
        }
        if (opL == 0) { // 00: x >= low
            final double low = getDoubleValue(cond, 2, attrIndex);
            if (Double.compare(x, low) >= 0) {
                return true;
            } else {
                return false;
            }
        } else if (opL == 1) { // 01: x <= high
            final double high = getDoubleValue(cond, 2 + 64, attrIndex);
            if (Double.compare(x, high) <= 0) {
                return true;
            } else {
                return false;
            }
        } else if (opL == 2) { // 10: low <= x <= high
            final double low = getDoubleValue(cond, 2, attrIndex);
            final double high = getDoubleValue(cond, 2 + 64, attrIndex);
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

    /* isValid begin ******************************************* */
    private boolean checkCon (final BitSet cond, final int attrIndex) {
        // Check whether low <= high, only when op == 10, low <= x<= high.
        if (cond.get(0) == false && cond.get(1) == true) {
            final double low = getDoubleValue(cond, 2, attrIndex);
            final double high = getDoubleValue(cond, 2 + 64, attrIndex);
            if (Double.compare(low, high) > 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkDis (BitSet cond) {
        // Discrete condition is invalid only when all bits are zero.
        return !cond.isEmpty();
    }

    private static boolean checkTar (BitSet postcond) {
        // Target condition is valid if and only if it has one 1 bit in it.
        return postcond.cardinality() == 1;
    }
    /* isValid end ******************************************* */

}
