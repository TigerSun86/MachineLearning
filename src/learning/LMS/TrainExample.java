package learning.LMS;

import java.util.ArrayList;

public class TrainExample {
    public final ArrayList<Double> attrs;
    public final double vTrain;

    public TrainExample(final ArrayList<Double> attrs2, final double vTrain2) {
        this.attrs = attrs2;
        this.vTrain = vTrain2;
    }

    @Override
    public String toString () {
        return String.format("Train example: Attributes: %s Train value: %f",
                attrs, vTrain);
    }
}
