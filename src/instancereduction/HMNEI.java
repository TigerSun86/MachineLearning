package instancereduction;

import keel.Algorithms.Preprocess.Basic.KNN;
import keel.Algorithms.Preprocess.Basic.KNN.Result;

import common.RawAttrList;
import common.RawExample;
import common.RawExampleList;

public class HMNEI implements Reducible {
    /*Own parameters of the algorithm*/
    private final double epsilon;
    public HMNEI(){
        this.epsilon = 0.1;
    }
    public HMNEI(final double epsilon){
        this.epsilon = epsilon;
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
        
        final Result r = ejecutar(datosTrain, clasesTrain, epsilon);

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
    
    private static Result ejecutar (double datosTrain[][], int clasesTrain[], double epsilon) { 
    int i, j, k, l, m;
    int nClases;
    int claseObt;
    boolean marcas[];
    int nSel = 0;
    double conjS[][];
    int clasesS[];
    double conjS2[][];
    int clasesS2[];
    double dist, minDist;
    double acierto, aciertoAct = 0.0;
    int hit[], miss[];
    int pos, cont;
    double w[];
    int cc[];
    int seleccionadosAnt;

    /*Getting the number of differents classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;
    
	/*Building of the S set from the flags*/
	conjS2 = new double[datosTrain.length][datosTrain[0].length];
	clasesS2 = new int[datosTrain.length];
	for (m=0, l=0; m<datosTrain.length; m++) {
		for (j=0; j<datosTrain[0].length; j++) {
			conjS2[l][j] = datosTrain[m][j];
		}
		clasesS2[l] = clasesTrain[m];
		l++;
	}    

    nSel = datosTrain.length;

    do {
    	acierto = aciertoAct;
    	seleccionadosAnt = nSel;
    	
    	/*Building of the S set from the flags*/
    	conjS = new double[nSel][datosTrain[0].length];
    	clasesS = new int[nSel];
    	for (m=0, l=0; m<nSel; m++) {
    		for (j=0; j<datosTrain[0].length; j++) {
    			conjS[l][j] = conjS2[m][j];
    		}
    		clasesS[l] = clasesS2[m];
    		l++;
    	}

        /*Inicialization of the flagged instances vector from the S*/
        marcas = new boolean[nSel];
        for (i=0; i<nSel; i++) {
        	marcas[i] = true;
        }
    	
    	hit = new int[nSel];
    	miss = new int[nSel];
    	for (i=0; i<conjS.length; i++) {
    		for (j=0; j<nClases; j++) {
    			minDist = Double.POSITIVE_INFINITY;
    			pos = -1;
    			for (k=0; k<conjS.length; k++) {
    				if (i!=k && clasesS[k] == j) {
    					dist = KNN.distancia(conjS[i], conjS[k]);
    					if (dist < minDist) {
    						minDist = dist;    						
    						pos = k;
    					}
    				}
    			}
    			if (pos >= 0) {
    				if (clasesS[i] == j) {
    					hit[pos]++;
    				} else {
    					miss[pos]++;
    				}
    			}
    		}
    	}
    	
    	w = new double[nClases];
    	cc = new int[nClases];
    	for (i=0; i<w.length; i++) {
    		cont = 0;
    		for (j=0; j<clasesS.length; j++) {
    			if (clasesS[j] == i) {
    				cont++;
    			}
    		}
    		cc[i] = cont;
    		w[i] = (double)cont / (double)nSel;
    	}
    	
    	/*RULE R1*/
    	for (i=0; i<hit.length; i++) {
    		if ((w[clasesS[i]] * (double)miss[i] + epsilon) > ((1-w[clasesS[i]]) * (double)hit[i])) {
    			marcas[i] = false;
    			nSel--;
    		}
    	}
    	
    	/*RULE R2*/
    	for (i=0; i<nClases; i++) {
    		cont = 0;
    		for (j=0; j<hit.length && cont < 4; j++) {
    			if (clasesS[j] == i && marcas[j]) {
    				cont++;
    			}
    		}
    		if (cont < 4) {
    			for (j=0; j<hit.length; j++) {
    				if (clasesS[j] == i && !marcas[j] && (hit[j]+miss[j]) > 0) {
    					marcas[j] = true;
    					nSel++;
    				}
    			}
    		}
    	}

    	/*RULE R3*/
    	if (nClases > 3) {
    		for (i=0; i<hit.length; i++) {
    			if (!marcas[i] && (miss[i]+hit[i] > 0) && miss[i] < (nClases/2)) {
    				marcas[i] = true;
    				nSel++;
    			}
    		}
    	}
    	
   	
    	/*RULE R4*/
    	for (i=0; i<hit.length; i++) {
    		if (!marcas[i] && hit[i] >= (cc[clasesS[i]] / 4)) {
    			marcas[i] = true;
    			nSel++;
    		}
    	}

    	/*Building of the S set from the flags*/
    	conjS2 = new double[nSel][datosTrain[0].length];
    	clasesS2 = new int[nSel];
    	for (m=0, l=0; m<conjS.length; m++) {
    		if (marcas[m]) { //the instance will be evaluated
    			for (j=0; j<datosTrain[0].length; j++) {
    				conjS2[l][j] = conjS[m][j];
    			}
    			clasesS2[l] = clasesS[m];
    			l++;
    		}
    	}
    	
    	aciertoAct = 0;
    	for (i=0; i<datosTrain.length; i++) {
    		claseObt = KNN.evaluacionKNN2(1, conjS2, clasesS2, datosTrain[i], nClases);
    		if (claseObt == clasesTrain[i]) {
    			aciertoAct++;
    		}
    	}
    } while (aciertoAct >= acierto && nSel < seleccionadosAnt);
    return new Result(conjS2, clasesS2);
    
  }

}
