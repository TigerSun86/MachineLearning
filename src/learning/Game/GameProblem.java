package learning.Game;

public abstract class GameProblem {
    public final PlayerManager pm;

    public GameProblem(final PlayerManager pm2) {
        this.pm = pm2;
    }

    public abstract int endTest (final GameState state);

    public abstract Record executeAction (final Record preRecord,
            final Object action);
}
