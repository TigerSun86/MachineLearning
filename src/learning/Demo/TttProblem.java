package learning.Demo;

import java.awt.Point;

import learning.Game.GameProblem;
import learning.Game.GameState;
import learning.Game.PlayerManager;
import learning.Game.Record;

public class TttProblem extends GameProblem {
    public TttProblem(final PlayerManager pm2) {
        super(pm2);
    }

    @Override
    public Record executeAction (final Record preRecord, final Object action) {
        final int player = preRecord.nextPlayer;
        final int nextPlayer;
        if (player == PlayerManager.P_1) {
            nextPlayer = PlayerManager.P_2;
        } else {
            nextPlayer = PlayerManager.P_1;
        }

        final TttState newState =
                new TttState((TttState) preRecord.state, (Point) action, player);
        final int winner = endTest(newState);

        return new Record(newState, (Point) action, player, nextPlayer, winner);
    }

    private static final Point DIRECT_RIGHT = new Point(0, +1);
    private static final Point DIRECT_DOWN = new Point(+1, 0);
    private static final Point DIRECT_RD = new Point(+1, +1);
    private static final Point DIRECT_RU = new Point(-1, +1);

    @Override
    public int endTest (final GameState state) {
        final TttState s = (TttState) state;
        final int[][] board = s.board;
        // Horizontal.
        for (int row = 0; row < board.length; row++) {
            final Point start = new Point(row, 0);
            if (checkConect(board, start, DIRECT_RIGHT)) {
                return board[start.x][start.y]; // Found winner;
            }
        }
        // Vertical.
        for (int col = 0; col < board[0].length; col++) {
            final Point start = new Point(0, col);
            if (checkConect(board, start, DIRECT_DOWN)) {
                return board[start.x][start.y]; // Found winner;
            }
        }
        // Diagonal.
        Point start = new Point(0, 0);
        if (checkConect(board, start, DIRECT_RD)) {
            return board[start.x][start.y]; // Found winner;
        }
        
        // Diagonal.
        start = new Point(2, 0);
        if (checkConect(board, start, DIRECT_RU)) {
            return board[start.x][start.y]; // Found winner;
        }


        if (s.moves == 9) {
            return PlayerManager.TIE;
        }
        return PlayerManager.NOT_END;
    }

    private static boolean checkConect (final int[][] board, final Point s,
            final Point direct) {
        if (board[s.x][s.y] == PlayerManager.P_EMPTY) {
            return false;
        }
        Point tmpP = new Point(s);
        final int player = board[tmpP.x][tmpP.y];
        for (int i = 0; i < 2; i++) {
            tmpP = new Point(tmpP.x + direct.x, tmpP.y + direct.y);
            if (player != board[tmpP.x][tmpP.y]) {
                return false;
            }
        }
        return true;
    }
}
