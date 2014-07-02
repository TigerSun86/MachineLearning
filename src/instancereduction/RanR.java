package instancereduction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import util.MyMath;

import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName: RanR.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 25, 2014 10:40:42 PM
 */
public class RanR implements Reducible {
    private final double ratioKeeping;

    public RanR() {
        this.ratioKeeping = 0.5;
    }

    public RanR(double ratioKeeping) {
        this.ratioKeeping = ratioKeeping;
    }

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        // Guarantee each class getting the same ratioKeeping.
        final BitSet kept = reduceByRandom(exs, attrs, ratioKeeping);
        final RawExampleList ret = new RawExampleList();
        for (int i = 0; i < exs.size(); i++) {
            if (kept.get(i)) {
                ret.add(exs.get(i));
            }
        }
        return ret;
    }

    public static BitSet reduceByRandom (RawExampleList exs, RawAttrList attrs,
            double ratioKeeping) {
        final ArrayList<String> classList = attrs.t.valueList;

        // Count number of instances for each class.
        final int[] numForEachClass = new int[classList.size()];
        for (RawExample e : exs) {
            final int classI = classList.indexOf(e.t);
            numForEachClass[classI]++;
        }

        // Get selected indexes for each class.
        final int[][] selectedOfEachClass = new int[classList.size()][];
        for (int i = 0; i < classList.size(); i++) {
            selectedOfEachClass[i] =
                    MyMath.mOutofN(
                            (int) Math.round(numForEachClass[i] * ratioKeeping),
                            numForEachClass[i]);
            // Sort it ascendingly to make the picking later easier.
            Arrays.sort(selectedOfEachClass[i]);
        }

        // Get the final kept indexes by selected of class.
        final BitSet kept = new BitSet(exs.size());
        final int[] counterForEachClass = new int[classList.size()];
        final int[] counterForSelected = new int[classList.size()];
        for (int i = 0; i < exs.size(); i++) {
            final RawExample e = exs.get(i);
            final int classI = classList.indexOf(e.t);
            // This e is in selectedOfEachClass[classI].
            if ((selectedOfEachClass[classI].length != 0)
                    && (selectedOfEachClass[classI].length != counterForSelected[classI])
                    && (selectedOfEachClass[classI][counterForSelected[classI]] == counterForEachClass[classI])) {
                kept.set(i);
                counterForSelected[classI]++;
            }
            counterForEachClass[classI]++;
        }

        return kept;
    }
}
