package learning.LMS;

import java.util.ArrayList;

import util.Dbg;

public class Generalizer {
    private static final String MODULE = "Generalizer";
    private static final boolean DBG = false;

    public static final Hypothesis generalize (final Hypothesis h,
            final ArrayList<TrainExample> trainSet) {
        final Hypothesis newH = new Hypothesis(h);
        for (TrainExample t : trainSet) {
            newH.updateH(t.attrs, t.vTrain);
            Dbg.print(DBG, MODULE, "Learnt: " + t);
            Dbg.print(DBG, MODULE, "Updated: " + newH.toString());
        }
        return newH;
    }
}
