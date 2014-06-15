package util;

/**
 * FileName: Combination2OutOfN.java
 * @Description:
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jun 14, 2014 1:42:48 PM
 */
public class Combination2OutOfN {
    private final int n;
    private int i;
    private int j;
    public Combination2OutOfN(int n) {
        this.n = n;
        i = 1;
        j = 2;
    }

    public int[] next () {
        if (n < 2) {
            return null;
        }
        
        final int[] ret = new int[2];
        ret[0] = i;
        ret[1] = j;

        // For next.
        if (j < n) {
            j++;
        } else { // j == n
            i++;
            j = i + 1;
        }
        if (ret[0] == n) { // Already gone through all.
            return null;
        } else {
            return ret;
        }
    }
}
