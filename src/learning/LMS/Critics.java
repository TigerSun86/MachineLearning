package learning.LMS;

import java.util.ArrayList;

import util.Dbg;
import learning.Game.GameAnalyser;
import learning.Game.GameProblem;
import learning.Game.GameRecorder;
import learning.Game.Record;

public class Critics {
    private static final String MODULE = "Critics";
    private static final boolean DBG = false;

    public static ArrayList<TrainExample> criticize (final GameProblem prob,
            final GameRecorder recorder, final GameAnalyser analyser) {
        final ArrayList<TrainExample> trainSet = new ArrayList<TrainExample>();

        for (int player : prob.pm.getPlayerSet()) {
            Dbg.print(DBG, MODULE, "Player: " + player);
            // Find the first move made by this player.
            int index = recorder.nextIndexOf(player, 0);
            while (index != -1) {
                // Get attributes.
                final Record r = recorder.getRecord(index);
                final ArrayList<Double> attrs =
                        analyser.getAttributes(r.state, player);

                // Get Training value.
                // Find the successor of current record.
                final Record nextR;
                final int index2 = recorder.nextIndexOf(player, index + 1);
                if (index2 == -1) {
                    // There is no further move made by the player, use the
                    // hypothesis value of the last record to represent training
                    // value.
                    nextR = recorder.getRecord();
                } else {
                    // Use hypothesis value of successor record to represent
                    // training value.
                    nextR = recorder.getRecord(index2);
                }

                final double vTrain = analyser.getUtility(prob, nextR, player);

                // Add train example to train set.
                final TrainExample eg = new TrainExample(attrs, vTrain);
                trainSet.add(eg);
                Dbg.print(DBG, MODULE, "Added " + eg);

                // For next loop.
                index = index2;
            }
        }
        Dbg.print(DBG, MODULE, "Train set size: " + trainSet.size());
        return trainSet;
    }
}
