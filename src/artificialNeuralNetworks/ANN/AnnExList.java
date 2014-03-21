package artificialNeuralNetworks.ANN;

import java.util.ArrayList;

import common.RawAttr;
import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;
import debug.Dbg;

/**
 * FileName: AnnExList.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Mar 10, 2014 10:19:01 PM
 */
public class AnnExList extends ArrayList<AnnExample> {
    private static final long serialVersionUID = 1L;
    public static final String MODULE = "AEL";
    public static final boolean DBG = false;

    public AnnExList() {
        super();
    }

    public AnnExList(final RawExampleList rawExSet, final RawAttrList ral) {
        super();
        for (RawExample re : rawExSet) {
            final AnnExample ae = exampleRawToANN(re, ral);
            this.add(ae);
        }
    }

    /**
     * public AnnExList[] splitIntoMultiSets(int numOfSets)
     * 
     * Splits given ExampleSet into 2 ExampleSets by given ratio in percentage.
     * The first output ExampleSet has the number of examples of the ratio, the
     * second one has remaining examples. Splits examples randomly.
     * 
     * @return: An array with 2 ExampleSets; null, if given ratio is not between
     *          0 and 1.
     */
    public AnnExList[] splitIntoMultiSets (final int numOfSets) {
        if (numOfSets <= 0) {
            return null;
        }
        final int num;
        if (numOfSets > this.size()) {
            num = this.size();
        } else {
            num = numOfSets;
        }

        final AnnExList[] exArray = new AnnExList[num];
        for (int i = 0; i < num; i++) {
            exArray[i] = new AnnExList();
        }
        int count = 0;
        for (AnnExample e : this) {
            final int index = count % num;
            exArray[index].add(e);
            count++;
        }

        Dbg.print(DBG, MODULE, "Splitted examples into " + num + " sub sets.");
        for (int i = 0; i < num; i++) {
            Dbg.print(DBG, MODULE, "Set " + i + " size " + exArray[i].size());
        }

        return exArray;
    }

    /**
     * public AnnExList[] splitIntoTwoSets(double ratio)
     * 
     * Splits given ExampleSet into 2 ExampleSets by given ratio in percentage.
     * The first output ExampleSet has the number of examples of the ratio, the
     * second one has remaining examples. Splits examples randomly.
     * 
     * @return: An array with 2 ExampleSets; null, if given ratio is not between
     *          0 and 1.
     */
    public AnnExList[] splitIntoTwoSets (final double ratio) {
        if (ratio > 1.0 || ratio < 0) {
            return null;
        }

        final AnnExList[] exArray = new AnnExList[2];
        exArray[0] = new AnnExList();
        exArray[1] = new AnnExList();

        final int numOfFirst = (int) (this.size() * ratio);
        int countFirst = 0;
        for (AnnExample e : this) {
            if (countFirst < numOfFirst) {
                exArray[0].add(e);
                countFirst++;
            } else {
                exArray[1].add(e);
            }
        }

        return exArray;
    }

    private static AnnExample exampleRawToANN (final RawExample re,
            final RawAttrList ral) {
        final AnnExample ae = new AnnExample();
        // Convert raw attribute to ANN version.
        final ArrayList<Double> annExX =
                FloatConverter.toDouble(re.xList, ral.xList);
        ae.xList.addAll(annExX);
        final String value = re.t; // Value in raw example.
        final RawAttr ra = ral.t; // Attribute of the value.
        // Convert raw target to ANN version.
        FloatConverter.doubleOneValue(ae.tList, value, ra);
        return ae;
    }
}
