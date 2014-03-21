package learning.MoveMaker;

import java.util.ArrayDeque;

import learning.Game.GameProblem;
import learning.Game.Record;

public class RepeatedPlayer implements MoveMaker {
    private ArrayDeque<Object> actionQue = new ArrayDeque<Object>();

    @Override
    public Object makeMove (GameProblem gameProblem, Record record) {
        return actionQue.poll();
    }

    public void Add (final Object action) {
        actionQue.offer(action);
    }

}
