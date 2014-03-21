package learning.Demo;

import java.awt.Point;

import learning.Game.GameState;
import learning.Game.PlayerManager;

public class TttState extends GameState {
    public static final int ROW_LEN = 3;
    public final int[][] board;
    public int moves;

    public TttState() {
        board = new int[ROW_LEN][ROW_LEN];
        for (int row = 0; row < board.length; row++) {
            board[row] = new int[ROW_LEN];
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = PlayerManager.P_EMPTY;
            }
        }

        moves = 0;
    }

    public TttState(final TttState state, final Point action, final int player) {
        board = new int[ROW_LEN][ROW_LEN];
        for (int row = 0; row < board.length; row++) {
            board[row] = new int[ROW_LEN];
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = state.board[row][col];
            }
        }
        board[action.x][action.y] = player;

        moves = state.moves + 1;
    }
}
