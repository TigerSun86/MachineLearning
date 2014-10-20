package clustering;

import java.util.ArrayList;

public class Vector extends ArrayList<Double> {
    private static final long serialVersionUID = 1L;

    /**
     * Create a vector with length of vecLength.
     * */
    public Vector(int vecLength) {
        super();
        for (int i = 0; i < vecLength; i++) {
            this.add(0.0);
        }
    }

    public double distanceTo(Vector vec) {
        if (this.size() != vec.size()) {
            return Double.NaN;
        } else {
            double sum = 0.0;
            for (int i = 0; i < this.size(); i++) {
                sum += (this.get(i) - vec.get(i)) * (this.get(i) - vec.get(i));
            }
            return Math.sqrt(sum);
        }
    }

    public void accumulate(Vector vec) {
        assert this.size() == vec.size();
        for (int i = 0; i < this.size(); i++) {
            this.set(i, this.get(i) + vec.get(i));
        }
    }

    public void dividedBy(double v) {
        assert v != 0;
        for (int i = 0; i < this.size(); i++) {
            this.set(i, this.get(i) / v);
        }
    }

    public void normalize() {
        double norm = 0.0;
        for (int i = 0; i < this.size(); i++) {
            norm += this.get(i) * this.get(i);
        }
        norm = Math.sqrt(norm);
        this.dividedBy(norm);
    }

    @Override
    public boolean equals(Object vec) {
        if (!(vec instanceof Vector)) {
            return false;
        }
        final double dis = this.distanceTo((Vector) vec);

        if (Double.isNaN(dis) || Double.compare(dis, 0.0) != 0) {
            return false;
        } else {// dis == 0;
            return true;
        }
    }

    @Override
    public int hashCode() {
        double hash = 3.0;
        for (int i = 0; i < this.size(); i++) {
            hash = 101 * hash + this.get(i);
        }
        return Double.valueOf(hash).hashCode();
    }

}
