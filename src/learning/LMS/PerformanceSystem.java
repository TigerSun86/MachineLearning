package learning.LMS;

import util.Dbg;
import learning.Game.GameProblem;
import learning.Game.GameRecorder;
import learning.Game.PlayerManager;
import learning.Game.Record;
import learning.MoveMaker.MoveMaker;

public class PerformanceSystem {
    private static final String MODULE = "PerformanceSystem";
    private static final boolean DBG = false;

    public static GameRecorder perform (final GameProblem prob,
            final GameRecorder gameRecorder) {
        final GameRecorder gr = new GameRecorder(gameRecorder);
        while (true) {
            final Record lastRecord = gr.getRecord();
            final int player = lastRecord.nextPlayer;

            final MoveMaker moveMaker = prob.pm.getMoveMaker(player);
            final Object action = moveMaker.makeMove(prob, lastRecord);
            final Record newRecord = prob.executeAction(lastRecord, action);
            gr.addRecord(newRecord);
            Dbg.print(DBG, MODULE, "Player: " + player + " made action: "
                    + action);

            final int winner = newRecord.winner;
            if (winner != PlayerManager.NOT_END) {
                // Game over.
                Dbg.print(DBG, MODULE, "Winner is: " + winner);
                break;
            }
        }
        Dbg.print(DBG, MODULE, "Game Records size: " + gr.size());
        return gr;
    }
}
