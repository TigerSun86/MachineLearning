package learning.MoveMaker;

import learning.Game.GameProblem;
import learning.Game.Record;

public interface MoveMaker {
    /** @return Action made by the MoveMaker. */
    Object makeMove (GameProblem gameProblem, Record record);
}
