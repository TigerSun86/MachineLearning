package artificialNeuralNetworks.ANN;

import java.util.ArrayList;
import java.util.List;

import common.Mapper;
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

    public static ArrayList<Double> valuesToDouble (final ArrayList<String> values,
            final AnnAttrList attrs) {
        assert values.size() == attrs.rAttrs.xList.size();
        final ArrayList<Double> newV = new ArrayList<Double>();
        for (int index = 0; index < values.size(); index++) {
            final String value = values.get(index); // Value in raw example.
            // Raw Attribute of the value.
            final RawAttr rAttr = attrs.rAttrs.xList.get(index);
            // Ann Attribute of the value.
            final List<AnnAttr> annAttr = attrs.getAnnAttrsAt(index);
            doubleOneValue(newV, value, rAttr, annAttr);
        }
        return newV;
    }

    public static ArrayList<Double> targetToDouble (final String value,
            final AnnAttrList attrs) {
        final ArrayList<Double> newV = new ArrayList<Double>();
        doubleOneValue(newV, value, attrs.rAttrs.t, attrs.tList);
        return newV;
    }

    private static void
            doubleOneValue (final ArrayList<Double> newV, final String value,
                    final RawAttr rAttr, final List<AnnAttr> annAttr) {
        if (rAttr.isContinuous) {
            assert annAttr.size() == 1;
            final double x = Double.parseDouble(value);
            final double y = Mapper.valueToMapped(x, annAttr.get(0));
            newV.add(y);
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
            final AnnAttrList attrs) {
        return backOneValue(values, attrs.rAttrs.t, attrs.tList);
    }

    public static ArrayList<String> valuesBackString (final ArrayList<Double> values,
            final AnnAttrList attrs) {
        final ArrayList<String> newVs = new ArrayList<String>();
        for (int index = 0; index < attrs.rAttrs.xList.size(); index++) {
            // Get Ann values corresponding to the raw attribute.
            final List<Double> annValues = attrs.getAnnValuesAt(index, values);
            // Raw Attribute of the value.
            final RawAttr rAttr = attrs.rAttrs.xList.get(index);
            // Ann Attribute of the value.
            final List<AnnAttr> annAttr = attrs.getAnnAttrsAt(index);
            final String newV = backOneValue(annValues, rAttr, annAttr);
            newVs.add(newV);
        }
        return newVs;
    }

    private static String backOneValue (final List<Double> annValues,
            final RawAttr rAttr, final List<AnnAttr> annAttr) {
        if (rAttr.isContinuous) {
            assert annAttr.size() == 1;
            final double y = annValues.get(0);
            final double x = Mapper.mappedToValue(y, annAttr.get(0));
            return String.valueOf(x);
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
}
