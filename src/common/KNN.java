package common;

/**
 * FileName:     KNN.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Jun 20, 2014 1:37:28 PM
 */
public class KNN {
    public static double distancia (double ej1[], double ej2[]) {

        int i;
        double suma = 0;

        for (i=0; i<ej1.length; i++) {
          suma += (ej1[i]-ej2[i])*(ej1[i]-ej2[i]);
        }
        suma = Math.sqrt(suma);

        return suma;
      }
}
