
package instancereduction;

import java.util.Arrays;
import java.util.Vector;

import keel.Algorithms.Preprocess.Basic.KNN;
import keel.Algorithms.Preprocess.Basic.KNN.Result;
import keel.Algorithms.Preprocess.Basic.Referencia;

import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

public class DROP3 implements Reducible {

  /*Own parameters of the algorithm*/
  private int k;
  public DROP3 () {
      this.k = 1;
  }
  
  public DROP3 (int k) {
    this.k = k;
  }
  
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
      final Result r = ejecutar(datosTrain, clasesTrain, k);

      final double[][] xs = r.xs; 
      final int[] classes = r.classes;
      final RawExampleList ret = new RawExampleList();
      for (int i = 0; i < xs.length;i++){
          final RawExample e = new RawExample();
          for (int j = 0; j < xs[i].length; j++){
              e.xList.add(String.valueOf(xs[i][j]));
          }
          e.t = attrs.t.valueList.get(classes[i]);
          ret.add(e);
      }
      return ret;
  }

  private static Result ejecutar (double datosTrain[][], int clasesTrain[], int k) { 
    int i, j, l, m, n, o;
    int nClases;
    int claseObt;
    boolean marcas[];
    int nSel;
    double conjS[][];
    int clasesS[];
    int vecinos[][];
    Vector<Referencia> asociados[];
    int aciertosSin;
    int vecinosTemp[];
    double distTemp[];
    double dist, bestD;
    boolean parar;
    Referencia orden[];
    int mayoria;

    /*Getting the number of different classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Inicialization of the instance flagged vector of the S set*/
    marcas = new boolean[datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      marcas[i] = true;
    }
    nSel = datosTrain.length;

    /*Do ENN before sorting*/
    for (i=0; i<datosTrain.length; i++) {
      claseObt = KNN.evaluacionKNN2 (k, datosTrain, clasesTrain, datosTrain[i],  nClases);
      if (claseObt != clasesTrain[i]) { //is included in the solution set if it is agree with your mayority
        marcas[i] = false;
        nSel--;
      }
    }

    /*Construction of an instance vector with distances to the nearest enemy*/
    orden = new Referencia[datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      bestD = Double.POSITIVE_INFINITY;
      for (j=0; j<datosTrain.length; j++) {
        if (clasesTrain[i] != clasesTrain[j]) {
          dist = KNN.distancia (datosTrain[i], datosTrain[j]);
          if (dist < bestD)
            bestD = dist;
        }
      }
      orden[i] = new Referencia (i, bestD);
    }

    /*Sorting the previous vector*/
    Arrays.sort(orden);

    /*Inicialization of the data structures of neighbors and associates*/
    distTemp = new double[k+1];
    vecinosTemp = new int[k+1];
    vecinos = new int[datosTrain.length][k+1];
    asociados = new Vector[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      asociados[i] = new Vector <Referencia>();

    /*Body of the algorithm DROP3 (same as DROP2).*/
    for (i=0; i<datosTrain.length; i++) {
      /*Get the k+1 nearest neighbors of each instance*/
      if (marcas[i]) {
        KNN.evaluacionKNN2 (k+1, datosTrain, clasesTrain, datosTrain[i], nClases, vecinos[i]);
        for (j=0; j<vecinos[i].length; j++) {
          if (vecinos[i][j] >= 0)
        	  asociados[vecinos[i][j]].addElement (new Referencia (i,0));
        }
      }
    }

    /*Check if delete or not the instances considering the WITH and WITHOUT sets*/
    for (o=0; o<datosTrain.length; o++){
      i = orden[o].entero;
      if (marcas[i]) { //only for instances haven´t noise filtered
        aciertosSin = 0;

        marcas[i] = false;
        nSel--;
        /*Construction of S set from the temporaly flags*/
        conjS = new double[nSel][datosTrain[0].length];
        clasesS = new int[nSel];
        for (m=0, l=0; m<datosTrain.length; m++) {
          if (marcas[m]) { //the instance will evaluate
            for (j=0; j<datosTrain[0].length; j++) {
              conjS[l][j] = datosTrain[m][j];
            }
            clasesS[l] = clasesTrain[m];
            l++;
          }
        }

        marcas[i] = true;
        nSel++;

        /*Evaluation of associates without the instance in T*/
        for (j=0; j<k+1; j++) {
          if (vecinos[i][j] >= 0) {
        	  claseObt = KNN.evaluacionKNN2 (k, conjS, clasesS, datosTrain[vecinos[i][j]], nClases);
        	  if (claseObt == clasesTrain[vecinos[i][j]])  //classify it correctly
        		  aciertosSin++;
          }
        }

        mayoria = (k+1) / 2;
        if (aciertosSin > mayoria) {
          /*Delete P from S*/
          marcas[i] = false;
          nSel--;

          /*For each associate of P, search a new nearest neighbor*/
          for (j=0; j<asociados[i].size(); j++) {
            for (l=0; l<k+1; l++) {
              vecinosTemp[l] = vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l];
              vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l] = -1;
              distTemp[l] = Double.POSITIVE_INFINITY;
            }
            for (l=0; l<datosTrain.length; l++) {
              if (marcas[l]) { //it is in S
                dist = KNN.distancia (datosTrain[((Referencia)(asociados[i].elementAt(j))).entero], datosTrain[l]);
                parar = false;

                /*Calculate the nearest neighbors in this situation again*/
                for (m=0; m<(k+1) && !parar; m++) {
                  if (dist < distTemp[m]) {
                    parar = true;
                    for (n=m+1; n<k+1; n++) {
                      distTemp[n] = distTemp[n-1];
                      vecinos[((Referencia)(asociados[i].elementAt(j))).entero][n] = vecinos[((Referencia)(asociados[i].elementAt(j))).entero][n-1];
                    }
                    distTemp[m] = dist;
                    vecinos[((Referencia)(asociados[i].elementAt(j))).entero][m] = l;
                  }
                }
              }
            }

            /*Add to the list of associates of the new neighbor this instance*/
            for (l=0; l<k+1; l++) {
              parar = false;
              for (m=0; m<asociados[vecinosTemp[l]].size() && !parar; m++) {
                if (((Referencia)(asociados[vecinosTemp[l]].elementAt(m))).entero == ((Referencia)(asociados[i].elementAt(j))).entero
                    && vecinosTemp[l] != i) {
                  asociados[vecinosTemp[l]].removeElementAt(m);
                  parar = true;
                }
              }
            }
            for (l=0; l<k+1; l++) {
              asociados[vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l]].addElement(new Referencia (((Referencia)(asociados[i].elementAt(j))).entero,0));
            }
          }
        }
      }
    }

    /*Construction of the S set from the flags*/
    conjS = new double[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (m=0, l=0; m<datosTrain.length; m++) {
      if (marcas[m]) { //the instance will evaluate
        for (j=0; j<datosTrain[0].length; j++) {
          conjS[l][j] = datosTrain[m][j];
        }
        clasesS[l] = clasesTrain[m];
        l++;
      }
    }

    return new Result(conjS, clasesS);
  }
}