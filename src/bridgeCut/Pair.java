package bridgeCut;

/**
 * FileName: Pair.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 8, 2014 8:44:08 PM
 */

public class Pair {
    public String n1;
    public String n2;
    private final boolean isDirected;

    public Pair(String n1, String n2, boolean isDirected) {
        this.n1 = n1;
        this.n2 = n2;
        this.isDirected = isDirected;
    }

    public Pair(String n1, String n2) {
        this(n1, n2, false);
    }

    @Override
    public int hashCode () {
        if (isDirected) {
            return (3 + n1.hashCode()) * 7 + n2.hashCode();
        } else {
            return n1.hashCode() + n2.hashCode();
        }

    }

    @Override
    public boolean equals (Object o) {
        if (!(o instanceof Pair)) {
            return false;
        } else {
            Pair o2 = (Pair) o;
            if (this.isDirected != o2.isDirected) {
                return false;
            }
            if (isDirected) {
                return n1.equals(o2.n1) && n2.equals(o2.n2);
            } else {
                return (n1.equals(o2.n1) && n2.equals(o2.n2))
                        || (n2.equals(o2.n1) && n1.equals(o2.n2));
            }

        }
    }

    @Override
    public String toString () {
        if (isDirected) {
            return n1 + "->" + n2;
        } else {
            return n1 + "<->" + n2;
        }
    }
}
