package common;

import java.util.ArrayList;

import artificialNeuralNetworks.ANN.FloatConverter;

/**
 * FileName: MapTool.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Apr 22, 2014 11:11:05 AM
 */
public class MapTool implements Mappable {
    private final double valueMax;
    private final double valueMin;
    private final double mappedMax = FloatConverter.HIGH_VALUE;
    private final double mappedMin = FloatConverter.LOW_VALUE;

    public MapTool(double valueMax, double valueMin) {
        this.valueMax = valueMax;
        this.valueMin = valueMin;
    }

    @Override
    public double getValueMax () {
        return valueMax;
    }

    @Override
    public double getValueMin () {
        return valueMin;
    }

    @Override
    public double getMappedMax () {
        return mappedMax;
    }

    @Override
    public double getMappedMin () {
        return mappedMin;
    }

    public static double[][] getMaxMin (final RawExampleList exs,
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

    public static ArrayList<String> mapValues (final ArrayList<String> values,
            final ArrayList<RawAttr> xList, final ArrayList<MapTool> ms) {
        assert values.size() == xList.size();
        final ArrayList<String> newV = new ArrayList<String>();
        for (int index = 0; index < values.size(); index++) {
            final String value = values.get(index); // Value in raw example.
            // Raw Attribute of the value.
            final RawAttr rAttr = xList.get(index);
            final MapTool m = ms.get(index);
            newV.add(mapOneValue(value, rAttr, m));
        }
        return newV;
    }

    public static String mapOneValue (final String value, final RawAttr rAttr,
            final MapTool m) {
        if (rAttr.isContinuous) {
            final double x = Double.parseDouble(value);
            final double y = Mapper.valueToMapped(x, m);
            return Double.toString(y);
        } else {
            return value;
        }
    }

    public static RawExampleList mapExs (final RawExampleList exs,
            final RawAttrList attrs) {
        final double[][] maxMin = MapTool.getMaxMin(exs, attrs);
        final double[] max = maxMin[0];
        final double[] min = maxMin[1];

        // Make max and min value a little extended, because value in test
        // set maybe different with train set.
        for (int i = 0; i < max.length; i++) {
            final double extra = (max[i] - min[i]) * 0.1;
            max[i] = max[i] + extra;
            min[i] = min[i] - extra;
        }

        final ArrayList<MapTool> ms = new ArrayList<MapTool>();
        for (int i = 0; i < attrs.xList.size(); i++) {
            ms.add(new MapTool(max[i], min[i]));
        }
        ms.add(new MapTool(max[max.length - 1], min[min.length - 1]));
        
        final RawExampleList nExs = new RawExampleList();
        for (RawExample ex : exs) {
            final ArrayList<String> nVs = mapValues(ex.xList, attrs.xList, ms);
            final String nT = mapOneValue(ex.t, attrs.t, ms.get(ms.size() - 1));
            final RawExample nEx = new RawExample();
            nEx.xList = nVs;
            nEx.t = nT;
            nExs.add(nEx);
        }
        return nExs;
    }
}
