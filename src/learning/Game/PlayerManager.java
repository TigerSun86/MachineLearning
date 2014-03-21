package learning.Game;

import java.util.HashMap;
import java.util.Set;

import learning.MoveMaker.MoveMaker;

public class PlayerManager {
    public static final int P_1 = 1;
    public static final int P_2 = 2;
    public static final int P_EMPTY = 0;
    public static final int P_INVALID = -1;
    public static final int NOT_END = -2;
    public static final int TIE = -3;

    private final HashMap<Integer, MoveMaker> players;

    public PlayerManager() {
        players = new HashMap<Integer, MoveMaker>();
    }

    public void setPlayer (final int player, final MoveMaker moveMaker) {
        players.put(player, moveMaker);
    }

    public MoveMaker getMoveMaker (final int player) {
        return players.get(player);
    }

    public Set<Integer> getPlayerSet () {
        return players.keySet();
    }
}
