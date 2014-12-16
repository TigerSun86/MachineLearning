package artificialNeuralNetworks.ANN;

import java.util.ArrayList;
import java.util.List;

import artificialNeuralNetworks.ANN.NeuralNetwork.PredictAndConfidence;

import common.RawAttr;
import common.RawAttrList;

/**
 * FileName: FloatConverter.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 17, 2014 4:17:18 PM
 */
public class FloatConverter {
    public static final double HIGH_VALUE = 0.9;
    public static final double MID_VALUE = 0.5;
    public static final double LOW_VALUE = 0.1;

    public static ArrayList<Double> valuesToDouble (
            final ArrayList<String> values, final RawAttrList attrs) {
        assert values.size() == attrs.xList.size();
        final ArrayList<Double> newV = new ArrayList<Double>();
        for (int index = 0; index < values.size(); index++) {
            final String value = values.get(index); // Value in raw example.
            // Raw Attribute of the value.
            final RawAttr rAttr = attrs.xList.get(index);
            doubleOneValue(newV, value, rAttr);
        }
        return newV;
    }

    public static ArrayList<Double> targetToDouble (final String value,
            final RawAttrList attrs) {
        final ArrayList<Double> newV = new ArrayList<Double>();
        doubleOneValue(newV, value, attrs.t);
        return newV;
    }

    private static void doubleOneValue (final ArrayList<Double> newV,
            final String value, final RawAttr rAttr) {
        if (rAttr.isContinuous) {
            newV.add(Double.parseDouble(value));
        } else if (rAttr.valueList.size() == 2) { // Have 2 possible values.
            if (value.equals(rAttr.valueList.get(0))) {
                newV.add(HIGH_VALUE); // First value converted to 0.9.
            } else {
                newV.add(LOW_VALUE); // Second value converted to 0.1.
            }
        } else {
            // Have multiple values, so split the attribute into multiple
            // attributes, one of them is 0.9 and all others are 0.1.
            for (int attrIndex = 0; attrIndex < rAttr.valueList.size(); attrIndex++) {
                if (value.equals(rAttr.valueList.get(attrIndex))) {
                    // Value in example converted to 0.9.
                    newV.add(HIGH_VALUE);
                } else {
                    // Other values converted to 0.1.
                    newV.add(LOW_VALUE);
                }
            } // End of for (int attrIndex = 0;
        }
    }

    public static String targetBackString (final ArrayList<Double> values,
            final RawAttrList attrs) {
        return backOneValue(values, attrs.t);
    }

    public static ArrayList<String> valuesBackString (
            final ArrayList<Double> values, final RawAttrList attrs) {
        final ArrayList<String> newVs = new ArrayList<String>();
        for (int index = 0; index < attrs.xList.size(); index++) {
            // Get Ann values corresponding to the raw attribute.
            final List<Double> annValues = getAnnValuesAt(index, values, attrs);
            // Raw Attribute of the value.
            final RawAttr rAttr = attrs.xList.get(index);
            final String newV = backOneValue(annValues, rAttr);
            newVs.add(newV);
        }
        return newVs;
    }

    private static String backOneValue (final List<Double> annValues,
            final RawAttr rAttr) {
        if (rAttr.isContinuous) {
            return String.valueOf(annValues.get(0));
        } else if (rAttr.valueList.size() == 2) { // Have 2 possible values.
            // If higher equal than 0.5 is the first value in attribute,
            // otherwise is the second one.
            assert annValues.size() == 1;
            final double value = annValues.get(0);
            if (Double.compare(value, MID_VALUE) >= 0) {
                return rAttr.valueList.get(0);
            } else {
                return rAttr.valueList.get(1);
            }
        } else {
            // Find the max value. The value in raw attribute corresponding to
            // max Ann value is the new value.
            assert annValues.size() == rAttr.valueList.size();
            double maxV = Double.NEGATIVE_INFINITY;
            int maxVIndex = 0;
            for (int vIndex = 0; vIndex < annValues.size(); vIndex++) {
                final double value = annValues.get(vIndex);
                if (Double.compare(maxV, value) < 0) {
                    maxV = value;
                    maxVIndex = vIndex;
                }
            } // End of for (int vIndex = 0;
            return rAttr.valueList.get(maxVIndex);
        }
    }
    
    private static List<Double> getAnnValuesAt (final int indexOfRawAttr,
            final ArrayList<Double> values, final RawAttrList rAttrs) {
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
    
    private static int getLength (RawAttr rAttr) {
        if (rAttr.isContinuous) {
            return 1;
        } else if (rAttr.valueList.size() == 2) { // Have 2 possible values.
            return 1;
        } else {
            return rAttr.valueList.size();
        }
    }
    
    /* Multi ANN project begin */
    public static PredictAndConfidence targetBackStringWithConf (final ArrayList<Double> values,
            final RawAttrList attrs) {
        if (attrs.t.isContinuous) {
            return new PredictAndConfidence(String.valueOf(values.get(0)),values.get(0));
        } else if (attrs.t.valueList.size() == 2) { // Have 2 possible values.
            // If higher equal than 0.5 is the first value in attribute,
            // otherwise is the second one.
            assert values.size() == 1;
            final double value = values.get(0);
            // The confidence value should be between 0 and 1.
            final double conf = Math.abs(value-MID_VALUE)*2;
            if (Double.compare(value, MID_VALUE) >= 0) {
                return new PredictAndConfidence(attrs.t.valueList.get(0), conf);
            } else {
                return new PredictAndConfidence(attrs.t.valueList.get(1), conf);
            }
        } else {
            // Find the max value. The value in raw attribute corresponding to
            // max Ann value is the new value.
            assert values.size() == attrs.t.valueList.size();
            double maxV = Double.NEGATIVE_INFINITY;
            int maxVIndex = 0;
            for (int vIndex = 0; vIndex < values.size(); vIndex++) {
                final double value = values.get(vIndex);
                if (Double.compare(maxV, value) < 0) {
                    maxV = value;
                    maxVIndex = vIndex;
                }
            } // End of for (int vIndex = 0;
            return new PredictAndConfidence(attrs.t.valueList.get(maxVIndex),maxV);
        }
    }
    /* Multi ANN project end*/
}
