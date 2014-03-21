package artificialNeuralNetworks.ANN;

import java.util.ArrayList;

import common.RawAttr;

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

    public static ArrayList<Double> toDouble (final ArrayList<String> values,
            final ArrayList<RawAttr> attrs) {
        final ArrayList<Double> newV = new ArrayList<Double>();
        for (int index = 0; index < attrs.size(); index++) {
            final String value = values.get(index); // Value in raw example.
            final RawAttr attr = attrs.get(index); // Attribute of the value.
            doubleOneValue(newV, value, attr);
        }
        return newV;
    }

    public static void doubleOneValue (final ArrayList<Double> newV,
            final String value, final RawAttr attr) {
        if (attr.isContinuous) {
            newV.add(Double.parseDouble(value));
        } else if (attr.valueList.size() == 2) { // Have 2 possible values.
            if (value.equals(attr.valueList.get(0))) {
                newV.add(HIGH_VALUE); // First value converted to 0.9.
            } else {
                newV.add(LOW_VALUE); // Second value converted to 0.1.
            }
        } else {
            // Have multiple values, so split the attribute into multiple
            // attributes, one of them is 0.9 and all others are 0.1.
            for (int attrIndex = 0; attrIndex < attr.valueList.size(); attrIndex++) {
                if (value.equals(attr.valueList.get(attrIndex))) {
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
            final RawAttr attr) {
        final String newV;
        if (attr.isContinuous) {
            final double value = values.get(0);
            newV = String.valueOf(value);
        } else if (attr.valueList.size() == 2) { // Have 2 possible values.
            // If higher equal than 0.5 is the first value in attribute,
            // otherwise is the second one.
            final double value = values.get(0);
            if (Double.compare(value, MID_VALUE) >= 0) {
                newV = attr.valueList.get(0);
            } else {
                newV = attr.valueList.get(1);
            }
        } else {
            // Find the max value in values,
            // the attribute value corresponding to max value is the new value.
            double maxV = Double.NEGATIVE_INFINITY;
            int maxAttrIndex = 0;

            for (int attrIndex = 0; attrIndex < attr.valueList.size(); attrIndex++) {
                final double value = values.get(attrIndex);
                if (Double.compare(maxV, value) < 0) {
                    maxV = value;
                    maxAttrIndex = attrIndex;
                }
            } // End of for (int attrIndex = 0;
            newV = attr.valueList.get(maxAttrIndex);
        }
        return newV;
    }

    public static ArrayList<String> backString (final ArrayList<Double> values,
            final ArrayList<RawAttr> attrs) {
        final ArrayList<String> newV = new ArrayList<String>();
        int lastIndex = 0;
        for (int index = 0; index < attrs.size(); index++) {
            final RawAttr attr = attrs.get(index); // Attribute of the value.
            lastIndex = backOneValue(newV, values, lastIndex, attr);
        }
        return newV;
    }

    private static int backOneValue (final ArrayList<String> newV,
            final ArrayList<Double> values, final int lastIndex,
            final RawAttr attr) {
        final int retIndex;
        if (attr.isContinuous) {
            final double value = values.get(lastIndex);
            newV.add(String.valueOf(value));
            retIndex = lastIndex + 1;
        } else if (attr.valueList.size() == 2) { // Have 2 possible values.
            // If higher equal than 0.5 is the first value in attribute,
            // otherwise is the second one.
            final double value = values.get(lastIndex);
            if (Double.compare(value, MID_VALUE) >= 0) {
                newV.add(attr.valueList.get(0));
            } else {
                newV.add(attr.valueList.get(1));
            }
            retIndex = lastIndex + 1;
        } else {
            // Find the max value from lastIndex to
            // lastIndex+attr.valueList.size(),
            // the attribute value corresponding to max value is the new value.
            double maxV = Double.NEGATIVE_INFINITY;
            int maxAttrIndex = 0;

            for (int attrIndex = 0; attrIndex < attr.valueList.size(); attrIndex++) {
                final int curIndex = lastIndex + attrIndex;
                final double value = values.get(curIndex);
                if (Double.compare(maxV, value) < 0) {
                    maxV = value;
                    maxAttrIndex = attrIndex;
                }
            } // End of for (int attrIndex = 0;
            newV.add(attr.valueList.get(maxAttrIndex));
            retIndex = lastIndex + attr.valueList.size();
        }
        return retIndex;
    }
}
