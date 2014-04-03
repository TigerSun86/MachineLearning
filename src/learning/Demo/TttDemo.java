package learning.Demo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Scanner;

import util.Dbg;
import learning.Game.GameRecorder;
import learning.Game.PlayerManager;
import learning.Game.Record;
import learning.LMS.Critics;
import learning.LMS.Generalizer;
import learning.LMS.Hypothesis;
import learning.LMS.PerformanceSystem;
import learning.LMS.TrainExample;
import learning.MoveMaker.AlphaBetaPlayer;
import learning.MoveMaker.HumanPlayer;
import learning.MoveMaker.MoveMaker;
import learning.MoveMaker.RepeatedPlayer;

public class TttDemo {
    private static final String MODULE = "TttDemo";
    private static final boolean DBG = true;

    private static final String TRAIN_EGS_FILE = "examplesSep.txt";

    private static final Record INIT_RECORD = new Record(new TttState(), null,
            PlayerManager.P_INVALID, PlayerManager.P_1, PlayerManager.NOT_END);
    // Order: Separate
     private static final Hypothesis H_SAMPLE = new Hypothesis(14.285996,
      19.348214, 6.452740, 60.349739, 0.156545, -48.852905, -1.727931); 
    // Order: lose tie win
/*    private static final Hypothesis H_SAMPLE = new Hypothesis(0.038943,
            -0.152388, 1.855290, 2.472602, -0.544517, -2.943407, 48.907703);*/
    // Order: win lose tie
/*     private static final Hypothesis H_SAMPLE = new Hypothesis(0.885353,
     -0.040837, 0.351693, 4.634941, -0.531713, -1.222138, 0.095312);*/

    // All weights are 1
/*     private static final Hypothesis H_SAMPLE = new Hypothesis(
     TttAnalyser.attributesCount());*/

    private static final Hypothesis H_INIT = new Hypothesis(
            TttAnalyser.attributesCount());

    public static void main (final String[] args) {
        final Scanner s = new Scanner(System.in);
        Hypothesis h = H_INIT;
        while (true) {
            System.out.println("Please choose mode:");
            System.out.println("tList: teacher mode.");
            System.out.println("n: no teacher mode.");
            System.out.println("p: player mode.");
            System.out.println("c: championship mode.");
            System.out.println("q: quit.");
            final String cmd = s.nextLine();
            if (cmd.equalsIgnoreCase("tList")) {
                h = teacherMode(h);
            } else if (cmd.equalsIgnoreCase("n")) {
                h = noTeacherMode(h, s);
            } else if (cmd.equalsIgnoreCase("p")) {
                h = playerMode(h, s);
            } else if (cmd.equalsIgnoreCase("c")) {
                championshipMode(h);
            } else {
                break;
            }
        }
        s.close();
    }

    private static Hypothesis teacherMode (final Hypothesis hIn) {
        final ArrayList<GameRecorder> grList = new ArrayList<GameRecorder>();
        final Scanner s =
                new Scanner(TttDemo.class.getClassLoader().getResourceAsStream(
                        TRAIN_EGS_FILE));
        while (s.hasNextLine()) {
            final String[] str = s.nextLine().split(" ");
            final RepeatedPlayer rp1 = new RepeatedPlayer();
            final RepeatedPlayer rp2 = new RepeatedPlayer();
            for (int i = 0; i <= str.length - 2; i++) { // Get rid of last one.
                final String[] act = str[i].split(",");
                final Point action =
                        new Point(Integer.parseInt(act[0]),
                                Integer.parseInt(act[1]));
                if (i % 2 == 0) {
                    rp1.Add(action);
                } else {
                    rp2.Add(action);
                }
            }

            final PlayerManager pm = new PlayerManager();
            pm.setPlayer(PlayerManager.P_1, rp1);
            pm.setPlayer(PlayerManager.P_2, rp2);
            final TttProblem prob = new TttProblem(pm);
            final GameRecorder newGr =
                    PerformanceSystem.perform(prob, new GameRecorder(
                            INIT_RECORD));
            HumanPlayer.displayGameBoard((TttState) newGr.getRecord().state);
            grList.add(newGr);
        }
        s.close();

        Hypothesis h = new Hypothesis(hIn);
        final TttAnalyser analyser = new TttAnalyser(h);
        final PlayerManager pm = new PlayerManager();
        pm.setPlayer(PlayerManager.P_1, null);
        pm.setPlayer(PlayerManager.P_2, null);
        final TttProblem prob = new TttProblem(pm);
        for (int i = 0; i < 5; i++) {
            for (GameRecorder gr : grList) {
                final ArrayList<TrainExample> trainSet =
                        Critics.criticize(prob, gr, analyser);
                h = Generalizer.generalize(h, trainSet);
                analyser.setHypothesis(h);
            }
            Dbg.print(DBG, MODULE, h.toString());
        }

        return h;
    }

    private static void championshipMode (final Hypothesis hTrained) {
        System.out.println("Testing with weak opponent.");
        final Hypothesis weak = H_SAMPLE;
        final PlayerManager pm = new PlayerManager();
        final TttProblem prob = new TttProblem(pm);

        final TttAnalyser analyser1 = new TttAnalyser(hTrained);
        final TttAnalyser analyser2 = new TttAnalyser(weak);

        final MoveMaker player1 = new AlphaBetaPlayer(analyser1, 1);
        final MoveMaker player2 = new AlphaBetaPlayer(analyser2, 1);

        int win = 0;
        int tie = 0;
        int lose = 0;
        for (int i = 0; i < 10; i++) {
            pm.setPlayer(PlayerManager.P_1, player1);
            pm.setPlayer(PlayerManager.P_2, player2);

            // Initialize game recorder.
            GameRecorder initGr = new GameRecorder(INIT_RECORD);
            GameRecorder newGr = PerformanceSystem.perform(prob, initGr);

            HumanPlayer.displayGameBoard((TttState) newGr.getRecord().state);
            String winner;
            if (newGr.getRecord().winner == PlayerManager.P_1) {
                winner = "Trained";
                win++;
            } else if (newGr.getRecord().winner == PlayerManager.P_2) {
                winner = "Weak";
                lose++;
            } else {
                winner = "Tie";
                tie++;
            }
            System.out.println("Game Over, winner is: " + winner);

            // Switch player.
            pm.setPlayer(PlayerManager.P_2, player1);
            pm.setPlayer(PlayerManager.P_1, player2);

            // Initialize game recorder.
            initGr = new GameRecorder(INIT_RECORD);
            newGr = PerformanceSystem.perform(prob, initGr);

            HumanPlayer.displayGameBoard((TttState) newGr.getRecord().state);
            if (newGr.getRecord().winner == PlayerManager.P_2) {
                winner = "Trained";
                win++;
            } else if (newGr.getRecord().winner == PlayerManager.P_1) {
                winner = "Weak";
                lose++;
            } else {
                winner = "Tie";
                tie++;
            }
            System.out.println("Game Over, winner is: " + winner);
        }
        System.out.printf("Win %d, Tie %d, Lose %d.%n", win, tie, lose);
    }

    private static Hypothesis
            playerMode (final Hypothesis hIn, final Scanner s) {
        Hypothesis h = new Hypothesis(hIn);

        final PlayerManager pm = new PlayerManager();
        final TttProblem prob = new TttProblem(pm);

        // Initialize game recorder.
        final GameRecorder initGr = new GameRecorder(INIT_RECORD);

        while (true) {
            final TttAnalyser analyser = new TttAnalyser(h);

            final MoveMaker player1;
            final MoveMaker player2;

            System.out.println("Please choose player(X or O):");

            if (s.nextLine().equalsIgnoreCase("X")) {
                player1 = new HumanPlayer(s);
                player2 = new AlphaBetaPlayer(analyser, 2);
            } else {
                player1 = new AlphaBetaPlayer(analyser, 2);
                player2 = new HumanPlayer(s);
            }

            pm.setPlayer(PlayerManager.P_1, player1);
            pm.setPlayer(PlayerManager.P_2, player2);

            final GameRecorder newGr = PerformanceSystem.perform(prob, initGr);
            Dbg.print(DBG, MODULE, "Game length: " + newGr.size()
                    + ", winner is: " + newGr.getRecord().winner);

            HumanPlayer.displayGameBoard((TttState) newGr.getRecord().state);
            final String winner;
            if (newGr.getRecord().winner == PlayerManager.P_1) {
                winner = "X";
            } else if (newGr.getRecord().winner == PlayerManager.P_2) {
                winner = "O";
            } else {
                winner = "Tie";
            }
            System.out.println("Game Over, winner is: " + winner);

            for (int i = 0; i < 5; i++) {
                final ArrayList<TrainExample> trainSet =
                        Critics.criticize(prob, newGr, new TttAnalyser(h));

                h = Generalizer.generalize(h, trainSet);
                Dbg.print(DBG, MODULE, h.toString());
            }

            System.out.println("Want another game? (Y or N):");
            if (!s.nextLine().equalsIgnoreCase("Y")) {
                break;
            }
        }
        return h;
    }

    private static Hypothesis noTeacherMode (final Hypothesis hIn,
            final Scanner s) {
        System.out.println("Please input the number of Games:");
        final int num = Integer.parseInt(s.nextLine());

        Hypothesis h = new Hypothesis(hIn);
        Dbg.print(DBG, MODULE, h.toString());

        final TttAnalyser analyser1 = new TttAnalyser(h);
        final TttAnalyser analyser2 = new TttAnalyser(h);
        final TttAnalyser analyser3 = new TttAnalyser(h);

        final AlphaBetaPlayer player1 = new AlphaBetaPlayer(analyser1, 2);
        final AlphaBetaPlayer player2 = new AlphaBetaPlayer(analyser2, 2);

        final PlayerManager pm = new PlayerManager();
        pm.setPlayer(PlayerManager.P_1, player1);
        pm.setPlayer(PlayerManager.P_2, player2);

        final TttProblem prob = new TttProblem(pm);

        final GameRecorder initGr = new GameRecorder(INIT_RECORD);

        for (int i = 0; i < num; i++) {
            final GameRecorder newGr = PerformanceSystem.perform(prob, initGr);
            Dbg.print(DBG, MODULE, "Game length: " + newGr.size()
                    + ", winner is: " + newGr.getRecord().winner);

            final ArrayList<TrainExample> trainSet =
                    Critics.criticize(prob, newGr, analyser3);
            Dbg.print(DBG, MODULE, "Train set size: " + trainSet.size());

            h = Generalizer.generalize(h, trainSet);
            Dbg.print(DBG, MODULE, h.toString());

            analyser1.setHypothesis(h);
            analyser2.setHypothesis(h);
            analyser3.setHypothesis(h);
        }
        return h;
    }
}
