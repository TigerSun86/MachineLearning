package util;

import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * FileName: MyMath.java
 * @Description: Math methods.
 * 
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Feb 25, 2014
 */
public class MyMath {
    public static double doubleRound (final double n, final int maxFLen) {
        final int RoundFactor = (int) Math.pow(10, maxFLen);
        return Math.round(n * RoundFactor) / (double) RoundFactor;
    }

    public static int getFractionLength (final String s) {
        int dotPos = s.indexOf('.');
        if (dotPos == -1) {
            return 0; // No fraction.
        }
        final String result = s.substring(dotPos + 1, s.length());
        assert (result.length() != 0);
        return result.length();
    }

    public static int[] mOutofN (final int m, final int n) {
        // Descending.
        final PriorityQueue<NumAndValue> que =
                new PriorityQueue<NumAndValue>(n, Collections.reverseOrder());
        // Every number in n rolls a value.
        final Random ran = new Random();
        for (int i = 0; i < n; i++) {
            final int value = ran.nextInt();
            que.add(new NumAndValue(i, value));
        }

        final int[] ret = new int[m];
        for (int i = 0; i < m; i++) {
            ret[i] = que.remove().num;
        }
        return ret;
    }

    private static class NumAndValue implements Comparable<NumAndValue> {
        final int num;
        final int value;

        public NumAndValue(int i, int value2) {
            num = i;
            value = value2;
        }

        @Override
        public int compareTo (NumAndValue o) {
            // TODO Auto-generated method stub
            return this.value - o.value;
        }
    }

    public static int selectByProb (final double[] probDistribute) {
        final double[] prob = new double[probDistribute.length + 1];
        prob[0] = 0;
        for (int i = 0; i < probDistribute.length; i++) {
            prob[i + 1] = prob[i] + probDistribute[i];
        }

        final double ran = new Random().nextDouble();

        int index = Arrays.binarySearch(prob, ran);
        if (index < 0) { // Didn't find the value equals with ran.
            // Make index back to 'insertion point'.
            index = -index;
            index -= 1;
            if (index == prob.length) { // Ran greater than all elements in prob
                index -= 1; // Back 'insertion point' to last element.
            }
            // The selected index is 'insertion point' - 1;
            index -= 1;
        } else if (index == prob.length - 1) {
            // Ran just equals with the last value.
            index -= 1;
        }
        return index;
    }

    public static double
            randomDoubleBetween (final double min, final double max) {
        return (Math.random() * (max - min)) + min;
    }
}
