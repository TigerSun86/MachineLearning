package clustering;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Cluster extends ArrayList<Vector> {
    private static final long serialVersionUID = 1L;
    public Vector center = null;

    public double internalSim() {
        final Vector cen = this.getCenter();
        final double len = cen.vecLength();
        return len * len;
    }

    public void setCenter(Vector v) {
        this.center = v;
    }

    public Vector getCenter() {
        if (this.center == null) {
            assert this.size() > 0;
            final Vector cen = new Vector(this.get(0).size());
            for (Vector vec : this) {
                cen.accumulate(vec);
            }
            cen.dividedBy(this.size());
            this.center = cen;
        }
        // Return the pointer so the content of center is able to be modified
        // from outside.
        return this.center;
    }

    private static final int TOP = 3;

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        if (this.get(0).idxToWord != null) { // Construct top words.
            if (this.center == null) {
                this.getCenter();
            }

            // Get top 3.
            final LinkedList<Double> maxValues = new LinkedList<Double>();
            final LinkedList<Integer> maxIdx = new LinkedList<Integer>();
            for (int i = 0; i < center.size(); i++) {
                final double value = center.get(i);
                final int loop = Math.min(maxIdx.size(), TOP);
                int j = 0;
                while ((j < loop)
                        && (Double.compare(maxValues.get(j), value) >= 0)) {
                    j++;
                }
                if (j < TOP) {
                    maxValues.add(j, value);
                    maxIdx.add(j, i);
                }
            }

            sb.append("Top words: ");
            final List<String> idxToWord = this.get(0).idxToWord;
            final int loop = Math.min(maxIdx.size(), TOP);
            for (int i = 0; i < loop; i++) {
                final int idx = maxIdx.get(i);
                final String word = idxToWord.get(idx);
                sb.append(word);
                if (i == loop - 1) {
                    sb.append(". ");
                } else {
                    sb.append(" ");
                }
            }
        }

        sb.append("Article: ");
        for (int i = 0; i < this.size(); i++) {
            sb.append(this.get(i).id);
            sb.append(" ");
        }
        return sb.toString();
    }
}
