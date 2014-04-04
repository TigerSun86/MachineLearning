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
    public int ruleLength;

    public BSAttrs(final RawExampleList exs, final RawAttrList attrs) {
        this.rawAttrs = attrs;
        initCondOffset(attrs);
        initMaxMinExp(exs, attrs);
        final int tarStart = condStart.get(condStart.size() - 1);
        final int tarOffset = condLength.get(condLength.size() - 1);
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
    }
}
