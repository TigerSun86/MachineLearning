package instancereduction;

import java.util.Arrays;
import java.util.Vector;

import common.KNN;
import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

/**
 * FileName:     FCNN.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: TigerSun86@gmail.com
 * @date Jun 20, 2014 1:27:43 PM
 */
public class FCNN implements Reducible {
    private final int k;
    public FCNN () {
        this.k = 1;
      }
    public FCNN (int k) {
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
        
        final int[] s = ejecutar(datosTrain, clasesTrain, k);
        
        final RawExampleList ret = new RawExampleList();
        for (int i : s){
            ret.add(exs.get(i));
        }
        return ret;
    }

    private static int[] ejecutar (double datosTrain[][], int clasesTrain[], int k) { 
        int S[];
        int i, j, l, m;
        int nClases;
        int pos;
        int tamS;
        int nearest[][];
        Vector <Integer> deltaS = new Vector <Integer> ();
        double centroid[];
        int nCentroid;
        double dist, minDist;
        int rep[];
        boolean insert;
        int votes[];
        int max;

        /*Getting the number of different classes*/
        nClases = 0;
        for (i=0; i<clasesTrain.length; i++)
            if (clasesTrain[i] > nClases)
                nClases = clasesTrain[i];
        nClases++;

        if (nClases < 2) {
            System.err.println("Input dataset has only one class");
            nClases = 0;
        }
    
        nearest = new int[datosTrain.length][k];
        for (i=0; i<datosTrain.length; i++) {
            Arrays.fill(nearest[i],-1);
        }

        /*Inicialization of the candidates set*/
        S = new int[datosTrain.length];
        for (i=0; i<S.length; i++)
            S[i] = Integer.MAX_VALUE;
        tamS = 0;    
    
        /*Inserting an element of each class*/
        centroid = new double[datosTrain[0].length];
        for (i=0; i<nClases; i++) {
            nCentroid = 0;
            Arrays.fill(centroid, 0);
            for (j=0; j<datosTrain.length; j++) {
                if (clasesTrain[j] == i) {
                    for (l=0; l<datosTrain[j].length; l++) {
                        centroid[l] += datosTrain[j][l];
                    }
                    nCentroid++;
                }
            }
            for (j=0; j<centroid.length; j++) {
                centroid[j] /= (double)nCentroid;
            }
            pos = -1;
            minDist = Double.POSITIVE_INFINITY;
            for (j=0; j<datosTrain.length; j++) {
                if (clasesTrain[j] == i) {
                    dist = KNN.distancia(centroid, datosTrain[j]);
                    if (dist < minDist) {
                        minDist = dist;
                        pos = j;
                    }
                }
            }
            if (pos >= 0)
                deltaS.add(pos);
        }

        /*Algorithm body*/
        rep = new int[datosTrain.length];
        votes = new int[nClases];
        while (deltaS.size() > 0) {
        
            for (i=0; i<deltaS.size(); i++) {
                S[tamS] = deltaS.elementAt(i);
                tamS++;
            }
            Arrays.sort(S);
            
            Arrays.fill(rep, -1);
        
            for (i=0; i<datosTrain.length; i++) {
                if (Arrays.binarySearch(S, i) < 0) {
                    for (j=0; j<deltaS.size(); j++) {
                        insert = false;
                        for (l=0; l<nearest[i].length && !insert; l++) {
                            if (nearest[i][l] < 0) {
                                nearest[i][l] = deltaS.elementAt(j);
                                insert = true;
                            } else {
                                if (KNN.distancia(datosTrain[nearest[i][l]], datosTrain[i]) > KNN.distancia(datosTrain[i], datosTrain[deltaS.elementAt(j)])) {
                                    for (m = k - 1; m >= l+1; m--) {
                                        nearest[i][m] = nearest[i][m-1];
                                    }
                                    nearest[i][l] = deltaS.elementAt(j);
                                    insert = true;
                                }
                            }
                        } 
                    }
                
                    Arrays.fill(votes, 0);
                    for (j=0; j<nearest[i].length; j++) {
                        if (nearest[i][j] >= 0) {
                            votes[clasesTrain[nearest[i][j]]]++;
                        }
                    }
                    max = votes[0];
                    pos = 0;
                    for (j=1; j<votes.length; j++) {
                        if (votes[j] > max) {
                            max = votes[j];
                            pos = j;
                        }
                    }
                    if (clasesTrain[i] != pos) {
                        for (j=0; j<nearest[i].length; j++) {
                            if (nearest[i][j] >= 0) {
                                if (rep[nearest[i][j]] < 0) {
                                    rep[nearest[i][j]] = i;
                                } else {
                                    if (KNN.distancia(datosTrain[nearest[i][j]], datosTrain[i]) < KNN.distancia(datosTrain[nearest[i][j]], datosTrain[rep[nearest[i][j]]])) {
                                        rep[nearest[i][j]] = i;                                 
                                    }
                                }                               
                            }
                        }
                    }
                }
            }
        
            deltaS.removeAllElements();

            for (i=0; i<tamS; i++) {
                if (rep[S[i]] >= 0 && !deltaS.contains(rep[S[i]]))
                    deltaS.add(rep[S[i]]);
            }
        }
        return Arrays.copyOfRange(S, 0, tamS);
    }   


}
