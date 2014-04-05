package genetic;

import java.util.ArrayList;

import util.MyMath;
import common.RawAttr;
import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName: BSAttrs.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Apr 4, 2014 12:16:21 AM
 */
public class BSAttrs {
    public RawAttrList rawAttrs;
    public ArrayList<Integer> condStart;
    public ArrayList<Integer> condLength;
    public ArrayList<Double> maxValue;
    public ArrayList<Double> minValue;
    public ArrayList<Integer> exp; // 10 based exponent.
    public int precondsLength;
    public int ruleLength;

    public BSAttrs(final RawExampleList exs, final RawAttrList attrs) {
        this.rawAttrs = attrs;
        initCondOffset(attrs);
        initMaxMinExp(exs, attrs);
        final int tarStart = condStart.get(condStart.size() - 1);
        final int tarOffset = condLength.get(condLength.size() - 1);
        precondsLength = tarStart;
        ruleLength = tarStart + tarOffset;
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

    private void initMaxMinExp (final RawExampleList exs,
            final RawAttrList attrs) {
        // Initialize default maxValue with negative infinite.
        maxValue = new ArrayList<Double>();
        for (int i = 0; i < attrs.xList.size(); i++) {
            maxValue.add(Double.NEGATIVE_INFINITY);
        }
        // Initialize default minValue with positive infinite.
        minValue = new ArrayList<Double>();
        for (int i = 0; i < attrs.xList.size(); i++) {
            minValue.add(Double.POSITIVE_INFINITY);
        }
        // Initialize default exponent (10 based) with 0.
        exp = new ArrayList<Integer>();
        for (int i = 0; i < attrs.xList.size(); i++) {
            exp.add(0);
        }
        for (RawExample ex : exs) {
            // Check each attribute x in example.
            for (int i = 0; i < attrs.xList.size(); i++) {
                if (!attrs.xList.get(i).isContinuous) {
                    continue; // Skip the discrete attribute.
                }
                final String x = ex.xList.get(i);
                final double value = Double.valueOf(x);
                // Set max value of this attribute.
                if (Double.compare(maxValue.get(i), value) < 0) {
                    maxValue.set(i, value);
                }
                // Set min value of this attribute.
                if (Double.compare(minValue.get(i), value) > 0) {
                    minValue.set(i, value);
                }
                // Set exponent of this attribute.
                final int fractionLength = MyMath.getFractionLength(x);
                if (exp.get(i) < fractionLength) {
                    exp.set(i, fractionLength);
                }
            }
        }
        // Make max and min value a little extended, because value in test
        // set maybe different with train set.
        for (int i = 0; i < maxValue.size(); i++) {
            final double extra = (maxValue.get(i) - minValue.get(i)) * 0.2;
            maxValue.set(i, maxValue.get(i) + extra);
            minValue.set(i, minValue.get(i) - extra);
        }
    }

    public long actualToMappedValue (final long x, final int index) {
        // Map actual long value (already has no exponent part) which is range
        // from lowest to highest of attribute, to the range from Long.MIN_VALUE
        // to Long.MAX_VALUE.
        final double xD = (double) x;
        final double low = minValue.get(index) * Math.pow(10, exp.get(index));
        final double high = maxValue.get(index) * Math.pow(10, exp.get(index));
        final double min = (double) Long.MIN_VALUE;
        final double max = (double) Long.MAX_VALUE;
        final double y = ((max - min) / (high - low)) * (xD - low) + min;
        return Math.round(y);
    }

    public long mappedToActualValue (final long y, final int index) {
        // Map back mapped long value (stored in the rule by the form 64 bits)
        // which is range from Long.MIN_VALUE to Long.MAX_VALUE, to the range
        // from lowest to highest of attribute.
        final double yD = (double) y;
        final double low = minValue.get(index) * Math.pow(10, exp.get(index));
        final double high = maxValue.get(index) * Math.pow(10, exp.get(index));
        final double min = (double) Long.MIN_VALUE;
        final double max = (double) Long.MAX_VALUE;
        final double x = ((high - low) / (max - min)) * (yD - min) + low;
        return Math.round(x);
    }
}
