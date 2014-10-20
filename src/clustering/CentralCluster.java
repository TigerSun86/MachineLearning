package clustering;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CentralCluster extends ArrayList<Vector> {
    private static final long serialVersionUID = 1L;
    public Vector center = null;

    public double similarity() {
        if (this.center == null) {
            this.calCenter();
        }
        final double len = this.center.vecLength();
        return len * len;
    }

    public void calCenter() {
        assert this.size() > 0;
        final Vector cen = new Vector(this.get(0).size());
        for (Vector vec : this) {
            cen.accumulate(vec);
        }
        cen.dividedBy(this.size());
        this.center = cen;
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
                this.calCenter();
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
