package bridgeCut;

import java.util.ArrayList;
import java.util.Collections;

/**
 * FileName: Path.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 8, 2014 4:06:12 PM
 */
public class Path extends ArrayList<String> {
    private static final long serialVersionUID = 1L;
    private final boolean isDirected;
    private final double length;

    public Path(String p) {
        this(p, 1);
    }

    public Path(String p, double length) {
        this(p, false, length);
    }

    public Path(String p, boolean isDirected, double length) {
        super();
        this.isDirected = isDirected;
        this.length = length;
        Collections.addAll(this, p.split(" "));
    }

    public double getLength () {
        return this.length;
    }

    @Override
    public int hashCode () {
        if (isDirected) {
            int hash = 3;
            for (int i = 0; i < this.size(); i++) {
                hash = hash * 7 + this.get(i).hashCode();
            }
            return hash;
        } else { // Undirected path.
            int hash = 3;
            int length = (this.size() + 1) / 2;
            for (int i = 0; i < length; i++) {
                hash =
                        hash * 7 + this.get(i).hashCode()
                                + this.get(this.size() - 1 - i).hashCode();
            }
            return hash;
        }
    }

    @Override
    public boolean equals (Object o) {
        if (!(o instanceof Path)) {
            return false;
        } else if (this.size() != ((Path) o).size()) {
            return false;
        } else {
            Path p = (Path) o;
            if (this.isDirected != p.isDirected) {
                return false;
            }
            boolean eq = true;
            for (int i = 0; i < this.size(); i++) { // Order.
                if (!this.get(i).equals(p.get(i))) {
                    eq = false;
                    break;
                }
            }
            if (!this.isDirected) { // Undirected path.
                if (!eq) { // Reverse order.
                    eq = true;
                    for (int i = 0; i < this.size(); i++) {
                        if (!this.get(i).equals(p.get(p.size() - 1 - i))) {
                            eq = false;
                            break;
                        }
                    }
                }
            }

            return eq;
        }
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < this.size(); i++) {
            sb.append(this.get(i));
            if (i != this.size() - 1) {
                if (isDirected) {
                    sb.append("->");
                } else {
                    sb.append("<->");
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
