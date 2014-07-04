package instancereduction;

import keel.Algorithms.Preprocess.Basic.KNN;

import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName:     ENN2.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jul 3, 2014 2:44:57 PM 
 */
public class ENN2 implements Reducible {

    @Override
    public RawExampleList reduce (RawExampleList exs, RawAttrList attrs) {
        final double datosTrain[][] = new double[exs.size()][attrs.xList.size()];
        final int clasesTrain[] = new int[exs.size()];
        
        for (int i = 0; i < exs.size();i++){
            final RawExample e = exs.get(i);
            datosTrain[i] = new double[e.xList.size()];
            for (int j = 0; j < e.xList.size(); j++){
                datosTrain[i][j] = Double.parseDouble(e.xList.get(j));
            }
            clasesTrain[i] = attrs.t.valueList.indexOf(e.t);
        }
        final boolean[] kept = ejecutar(datosTrain, clasesTrain, 3);
        
        final RawExampleList ret = new RawExampleList();
        for (int i = 0; i < exs.size();i++){
            if (kept[i]){
                ret.add(exs.get(i));
            }
        }
        return ret;
    }

    private static boolean[] ejecutar (double datosTrain[][], int clasesTrain[], int k) { 

        int i;
        int nClases;
        int claseObt;
        boolean marcas[];

        /*Inicialization of the flagged instances vector for a posterior copy*/
        marcas = new boolean[datosTrain.length];
        for (i=0; i<datosTrain.length; i++)
          marcas[i] = false;

        /*Getting the number of differents classes*/
        nClases = 0;
        for (i=0; i<clasesTrain.length; i++)
          if (clasesTrain[i] > nClases)
            nClases = clasesTrain[i];
        nClases++;

        /*Body of the algorithm. For each instance in T, search the correspond class conform his mayority
         from the nearest neighborhood. Is it is positive, the instance is selected.*/
        for (i=0; i<datosTrain.length; i++) {
          /*Apply KNN to the instance*/
          claseObt = KNN.evaluacionKNN2 (k, datosTrain, clasesTrain, datosTrain[i],  nClases);
          if (claseObt == clasesTrain[i]) { //agree with your majority, it is included in the solution set
            marcas[i] = true;
          }
        }
        return marcas;
      }
}
