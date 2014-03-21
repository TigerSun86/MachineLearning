package learning.MoveMaker;

import java.awt.Point;
import java.util.Scanner;

import learning.Demo.TttState;
import learning.Game.GameProblem;
import learning.Game.PlayerManager;
import learning.Game.Record;

public class HumanPlayer implements MoveMaker {
    private final Scanner s;
    public HumanPlayer(final Scanner s2) {
        this.s = s2;
    }

    @Override
    public Object makeMove (final GameProblem gameProblem, final Record record) {
        TttState state = (TttState) record.state;
        displayGameBoard(state);
        final String p;
        if (record.nextPlayer == PlayerManager.P_1) {
            p = "X";
        } else {
            p = "O";
        }
        System.out
                .printf("Player %s, please make a move(two numbers seperated by blank space):",
                        p);

        final Point action = new Point();
        while (true) {
            final String[] in = s.nextLine().split(" ");

            final int r = Integer.parseInt(in[0]);
            final int c = Integer.parseInt(in[1]);
            if (r >= 0 && r <= 2 && c >= 0 && c <= 2) {
                if (state.board[r][c] == PlayerManager.P_EMPTY) {
                    action.setLocation(r, c);
                    break;
                }
            }
            System.out.printf("Illegal input.%n");
            System.out
                    .printf("Player %s, please make a move(two numbers seperated by blank space):",
                            p);
        }

        return (Object) action;
    }

    public static void displayGameBoard (final TttState state) {
        final int[][] b = state.board;
        System.out.printf("Column  0   1   2%n");
        System.out.printf("      -------------%n");
        for (int r = 0; r < b.length; r++) {
            System.out.printf("Row %d ", r);
            System.out.printf("| ");
            for (int c = 0; c < b[r].length; c++) {
                final String p;
                if (b[r][c] == PlayerManager.P_1) {
                    p = "X";
                } else if (b[r][c] == PlayerManager.P_2) {
                    p = "O";
                } else {
                    p = " ";
                }
                System.out.printf(p + " | ");
            }
            System.out.printf("%n      -------------%n");
        }
    }
}
