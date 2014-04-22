package artificialNeuralNetworks.ANN;

import java.util.ArrayList;
import java.util.Iterator;

import util.Dbg;
import common.RawExample;
import common.RawExampleList;

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

    public AnnExList(final RawExampleList rawExSet, final AnnAttrList attrs) {
        super();
        for (RawExample re : rawExSet) {
            final AnnExample ae = exampleRawToANN(re, attrs);
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

    @Override
    public String toString () {
        Iterator<AnnExample> it = iterator();
        if (!it.hasNext()) return "";

        StringBuilder sb = new StringBuilder();
        for (;;) {
            AnnExample e = it.next();
            sb.append(e.toString());
            if (!it.hasNext()) return sb.toString();
            sb.append(Dbg.NEW_LINE);
        }
    }

    private static AnnExample exampleRawToANN (final RawExample re,
            final AnnAttrList attrs) {
        final AnnExample ae = new AnnExample();
        // Convert raw attribute to ANN version.
        ae.xList.addAll(FloatConverter.valuesToDouble(re.xList, attrs));
        // Convert raw target to ANN version.
        ae.tList.addAll(FloatConverter.targetToDouble(re.t, attrs));
        return ae;
    }
}
