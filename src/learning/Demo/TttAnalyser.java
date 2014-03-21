package learning.Demo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;

import learning.Game.GameAnalyser;
import learning.Game.GameProblem;
import learning.Game.GameState;
import learning.Game.PlayerManager;
import learning.Game.Record;
import learning.LMS.Hypothesis;

public class TttAnalyser extends GameAnalyser {
    private static final int ATTRIBUTE_COUNT = 6;

    public TttAnalyser(final Hypothesis h2) {
        super(h2);
    }

    @Override
    public ArrayList<Double> getAttributes (final GameState state,
            final int player) {
        final ArrayList<Double> attrs = new ArrayList<Double>();

        final TttState s = (TttState) state;

        final int[][] features = getFeatures(s);
        for (int i = 1; i < 4; i++) {
            attrs.add((double) features[player][i]);

        }
        final int anthP;
        if (player == PlayerManager.P_1) {
            anthP = PlayerManager.P_2;
        } else {
            anthP = PlayerManager.P_1;
        }
        for (int i = 1; i < 3; i++) {
            attrs.add((double) features[anthP][i]);

        }

        if (player == PlayerManager.P_1) {
            attrs.add((double) 2);
        } else {
            attrs.add((double) -2);
        }

        return attrs;
    }

    private static final Point DIRECT_RIGHT = new Point(0, +1);
    private static final Point DIRECT_DOWN = new Point(+1, 0);
    private static final Point DIRECT_RD = new Point(+1, +1);
    private static final Point DIRECT_RU = new Point(-1, +1);

    public int[][] getFeatures (final GameState state) {
        final int[][] statistic = new int[3][4];
        final TttState s = (TttState) state;
        final int[][] board = s.board;
        // Horizontal.
        for (int row = 0; row < board.length; row++) {
            final Point start = new Point(row, 0);
            PlayerAndNum pan = checkConect(board, start, DIRECT_RIGHT);
            if (pan.player != PlayerManager.P_EMPTY) {
                statistic[pan.player][pan.num]++;
            }
        }
        // Vertical.
        for (int col = 0; col < board[0].length; col++) {
            final Point start = new Point(0, col);
            PlayerAndNum pan = checkConect(board, start, DIRECT_DOWN);
            if (pan.player != PlayerManager.P_EMPTY) {
                statistic[pan.player][pan.num]++;
            }
        }
        // Diagonal.
        Point start = new Point(0, 0);
        PlayerAndNum pan = checkConect(board, start, DIRECT_RD);
        if (pan.player != PlayerManager.P_EMPTY) {
            statistic[pan.player][pan.num]++;
        }

        // Diagonal.
        start = new Point(2, 0);
        pan = checkConect(board, start, DIRECT_RU);
        if (pan.player != PlayerManager.P_EMPTY) {
            statistic[pan.player][pan.num]++;
        }

        return statistic;
    }

    private static PlayerAndNum checkConect (final int[][] board,
            final Point s, final Point direct) {
        int[] result = new int[3];
        Point tmpP = new Point(s);
        for (int i = 0; i < 3; i++) {
            result[board[tmpP.x][tmpP.y]]++;
            tmpP = new Point(tmpP.x + direct.x, tmpP.y + direct.y);
        }
        final PlayerAndNum ret = new PlayerAndNum();
        if (result[PlayerManager.P_1] != 0 && result[PlayerManager.P_2] == 0) {
            ret.player = PlayerManager.P_1;
            ret.num = result[PlayerManager.P_1];
        } else if (result[PlayerManager.P_2] != 0
                && result[PlayerManager.P_1] == 0) {
            ret.player = PlayerManager.P_2;
            ret.num = result[PlayerManager.P_2];
        } else {
            ret.player = PlayerManager.P_EMPTY;
        }
        return ret;
    }

    private static class PlayerAndNum {
        int player;
        int num;
    }

    private static final double END_SCORE_WIN = 100.0;
    private static final double END_SCORE_TIE = 0;
    private static final double END_SCORE_LOSE = -100.0;

    @Override
    public double endScore (final int player, final int winner) {
        if (winner == player) {
            return END_SCORE_WIN;
        } else if (winner == PlayerManager.TIE) {
            return END_SCORE_TIE;
        } else {
            return END_SCORE_LOSE;
        }
    }

    @Override
    public HashSet<Object>
            getActions (final GameProblem gp, final Record record) {
        final HashSet<Object> actions = new HashSet<Object>();
        final TttState state = (TttState) record.state;
        final int[][] board = state.board;
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == PlayerManager.P_EMPTY) {
                    actions.add(new Point(row, col));
                }
            }
        }
        return actions;
    }

    public static int attributesCount () {
        return ATTRIBUTE_COUNT;
    }
}
