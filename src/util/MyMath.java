package util;

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
}
