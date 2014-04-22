package artificialNeuralNetworks.ANN;

import java.util.ArrayList;
import java.util.List;

import util.Dbg;
import common.RawAttr;
import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName: AnnAttrList.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 13, 2014 12:38:26 AM
 */
public class AnnAttrList {
    public final RawAttrList rAttrs;
    public final ArrayList<AnnAttr> xList;
    public final ArrayList<AnnAttr> tList;

    public AnnAttrList(final RawExampleList exs, final RawAttrList rAttrs) {
        final double[][] maxMin = getMaxMin(exs, rAttrs);
        final double[] max = maxMin[0];
        final double[] min = maxMin[1];

        // Make max and min value a little extended, because value in test
        // set maybe different with train set.
        for (int i = 0; i < max.length; i++) {
            final double extra = (max[i] - min[i]) * 0.2;
            max[i] = max[i] + extra;
            min[i] = min[i] - extra;
        }

        this.rAttrs = rAttrs;
        this.xList = new ArrayList<AnnAttr>();
        this.tList = new ArrayList<AnnAttr>();
        for (int i = 0; i < rAttrs.xList.size(); i++) {
            final RawAttr ra = rAttrs.xList.get(i);
            addOneAttr(this.xList, ra, max[i], min[i]);
        }
        // Target attribute.
        final RawAttr ra = rAttrs.t;
        addOneAttr(this.tList, ra, max[max.length - 1], min[min.length - 1]);
    }

    public List<AnnAttr> getAnnAttrsAt (final int indexOfRawAttr) {
        assert indexOfRawAttr >= 0 && indexOfRawAttr < rAttrs.xList.size();
        int fromIndex = 0;
        for (int i = 0; i < indexOfRawAttr; i++) {
            final int length = getLength(rAttrs.xList.get(i));
            fromIndex += length;
        }
        final int toIndex =
                fromIndex + getLength(rAttrs.xList.get(indexOfRawAttr));
        return xList.subList(fromIndex, toIndex);
    }

    public List<Double> getAnnValuesAt (final int indexOfRawAttr,
            final ArrayList<Double> values) {
        assert indexOfRawAttr >= 0 && indexOfRawAttr < rAttrs.xList.size();
        int fromIndex = 0;
        for (int i = 0; i < indexOfRawAttr; i++) {
            final int length = getLength(rAttrs.xList.get(i));
            fromIndex += length;
        }
        final int toIndex =
                fromIndex + getLength(rAttrs.xList.get(indexOfRawAttr));
        return values.subList(fromIndex, toIndex);
    }

    private int getLength (RawAttr rAttr) {
        if (rAttr.isContinuous) {
            return 1;
        } else if (rAttr.valueList.size() == 2) { // Have 2 possible values.
            return 1;
        } else {
            return rAttr.valueList.size();
        }
    }

    @Override
    public String toString () {
        final StringBuffer sb = new StringBuffer();
        sb.append("X:");
        sb.append(xList.toString());
        sb.append(Dbg.NEW_LINE + "T:");
        sb.append(tList.toString());
        return sb.toString();
    }

    private static void addOneAttr (final ArrayList<AnnAttr> attrList,
            final RawAttr ra, final double max, final double min) {
        if (ra.isContinuous) {
            attrList.add(new AnnAttr(ra.name, max, min));
        } else if (ra.valueList.size() == 2) {
            // One discrete RawAttr has 2 possible values, so converts to one
            // AnnAttr.
            attrList.add(new AnnAttr(ra.name, FloatConverter.HIGH_VALUE,
                    FloatConverter.LOW_VALUE));
        } else { // More than 2 values.
            // One discrete RawAttr has n possible values, so converts to n
            // separate AnnAttr.
            for (String valueName : ra.valueList) {
                final String newValueName = ra.name + " is " + valueName;
                attrList.add(new AnnAttr(newValueName,
                        FloatConverter.HIGH_VALUE, FloatConverter.LOW_VALUE));
            }
        }
    }

    private static double[][] getMaxMin (final RawExampleList exs,
            final RawAttrList attrs) {
        // Initialize default maxValue with negative infinite.
        // length = attributes+target.
        final double[] maxValue = new double[attrs.xList.size() + 1];
        for (int i = 0; i < maxValue.length; i++) {
            maxValue[i] = Double.NEGATIVE_INFINITY;
        }
        // Initialize default minValue with positive infinite.
        final double[] minValue = new double[attrs.xList.size() + 1];
        for (int i = 0; i < minValue.length; i++) {
            minValue[i] = Double.POSITIVE_INFINITY;
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
                if (Double.compare(maxValue[i], value) < 0) {
                    maxValue[i] = value;
                }
                // Set min value of this attribute.
                if (Double.compare(minValue[i], value) > 0) {
                    minValue[i] = value;
                }
            }
            // Check target.
            if (attrs.t.isContinuous) {
                final String x = ex.t;
                final double value = Double.valueOf(x);
                final int tarIndex = maxValue.length - 1;
                // Set max value of this attribute.
                if (Double.compare(maxValue[tarIndex], value) < 0) {
                    maxValue[tarIndex] = value;
                }
                // Set min value of this attribute.
                if (Double.compare(minValue[tarIndex], value) > 0) {
                    minValue[tarIndex] = value;
                }
            }
        }
        return new double[][] { maxValue, minValue };
    }
}
