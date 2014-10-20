package clustering;

import java.util.ArrayList;

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
}
