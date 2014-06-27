package keel.Algorithms.Preprocess.Basic;

public class KNN {
    public static class Result{
        public double[][] xs; public int[] classes;
        public Result(double[][] xs, int[] classes) {
            this.xs = xs;
            this.classes = classes;
        }
    }
    
  public static int evaluacionKNN (int nvec, double conj[][], int clases[], double ejemplo[], int nClases) {
    return evaluacionKNN2 (nvec,conj, clases, ejemplo, nClases);
  }

  public static int evaluacionKNN2 (int nvec, double conj[][], int clases[], double ejemplo[], int nClases) {

    int i, j, l;
    boolean parar = false;
    int vecinosCercanos[];
    double minDistancias[];
    int votos[];
    double dist;
    int votada, votaciones;

    if (nvec > conj.length)
      nvec = conj.length;

    votos = new int[nClases];
    vecinosCercanos = new int[nvec];
    minDistancias = new double[nvec];
    for (i=0; i<nvec; i++) {
      vecinosCercanos[i] = -1;
      minDistancias[i] = Double.POSITIVE_INFINITY;
    }
    for (i=0; i<conj.length; i++) {
      dist = distancia(conj[i], ejemplo);
      if (dist > 0) {
        parar = false;
        for (j = 0; j < nvec && !parar; j++) {
          if (dist < minDistancias[j]) {
            parar = true;
            for (l = nvec - 1; l >= j+1; l--) {
                minDistancias[l] = minDistancias[l - 1];
                vecinosCercanos[l] = vecinosCercanos[l - 1];
            }
            minDistancias[j] = dist;
            vecinosCercanos[j] = i;
          }
        }
      }
    }

    for (j=0; j<nClases; j++) {
      votos[j] = 0;
    }
    for (j=0; j<nvec; j++) {
      if (vecinosCercanos[j] >= 0)
        votos[clases[vecinosCercanos[j]]] ++;
    }

    votada = 0;
    votaciones = votos[0];
    for (j=1; j<nClases; j++) {
      if (votaciones < votos[j]) {
        votaciones = votos[j];
        votada = j;
      }
    }

    return votada;
  }

  
  /**
   * To implement Depur Algorithm, we need the neighboor's vector, to decide what we must make
   * 
   * @param nvec
   * @param conj
   * @param clases
   * @param ejemplo
   * @param nClases
   * @return The neighboors' classes.
   * @author Isaac Triguero.
   */
  public static int[] evaluacionKNN3 (int nvec, double conj[][], int clases[], double ejemplo[], int nClases) {

	    int i, j, l;
	    boolean parar = false;
	    int vecinosCercanos[];
	    int clasesVecinosCercanos[];
	    double minDistancias[];
	    double dist;
	 
	    if (nvec > conj.length)
	      nvec = conj.length;

	    vecinosCercanos = new int[nvec];
	    clasesVecinosCercanos= new int[nvec];
	    minDistancias = new double[nvec];
	    for (i=0; i<nvec; i++) {
	      vecinosCercanos[i] = -1;
	      clasesVecinosCercanos[i]=-1;
	      minDistancias[i] = Double.POSITIVE_INFINITY;
	    }
	    for (i=0; i<conj.length; i++) {
	      dist = distancia(conj[i], ejemplo);
	      if (dist > 0) {
	        parar = false;
	        for (j = 0; j < nvec && !parar; j++) {
	          if (dist < minDistancias[j]) {
	            parar = true;
	            for (l = nvec - 1; l >= j+1; l--) {
	                minDistancias[l] = minDistancias[l - 1];
	                vecinosCercanos[l] = vecinosCercanos[l - 1];
	            }
	            minDistancias[j] = dist;
	            vecinosCercanos[j] = i;
	          }
	        }
	      }
	    }

	    for (j=0; j<vecinosCercanos.length; j++) {
	    	if(vecinosCercanos[j]!=-1)
	    	clasesVecinosCercanos[j] =clases[vecinosCercanos[j]];
	    }

	    return clasesVecinosCercanos;
	  }

  
  public static int evaluacionKNN2 (int nvec, double conj[][], int clases[], double ejemplo[], int nClases, Referencia nVotos) {

    int i, j, l;
    boolean parar = false;
    int vecinosCercanos[];
    double minDistancias[];
    int votos[];
    double dist;
    int votada, votaciones;

    if (nvec > conj.length)
      nvec = conj.length;

    votos = new int[nClases];
    vecinosCercanos = new int[nvec];
    minDistancias = new double[nvec];
    for (i=0; i<nvec; i++) {
      vecinosCercanos[i] = -1;
      minDistancias[i] = Double.POSITIVE_INFINITY;
    }

    for (i=0; i<conj.length; i++) {
      dist = distancia(conj[i], ejemplo);
      if (dist > 0) {
        parar = false;
        for (j = 0; j < nvec && !parar; j++) {
          if (dist < minDistancias[j]) {
            parar = true;
            for (l = nvec - 1; l >= j+1; l--) {
                minDistancias[l] = minDistancias[l - 1];
                vecinosCercanos[l] = vecinosCercanos[l - 1];
            }
            minDistancias[j] = dist;
            vecinosCercanos[j] = i;
          }
        }
      }
    }

    for (j=0; j<nClases; j++) {
      votos[j] = 0;
    }

    for (j=0; j<nvec; j++) {
      if (vecinosCercanos[j] >= 0)
        votos[clases[vecinosCercanos[j]]] ++;
    }

    votada = 0;
    votaciones = votos[0];
    for (j=1; j<nClases; j++) {
      if (votaciones < votos[j]) {
        votaciones = votos[j];
        votada = j;
      }
    }

    nVotos.entero = votaciones;
    return votada;
  }

  public static int evaluacionKNN2 (int nvec, double conj[][], int clases[], double ejemplo[], int nClases, int vecinos[]) {
    
	int i, j, l;
    boolean parar = false;
    int vecinosCercanos[];
    double minDistancias[];
    int votos[];
    double dist;
    int votada, votaciones;

    if (nvec > conj.length)
      nvec = conj.length;
    votos = new int[nClases];
    vecinosCercanos = new int[nvec];
    minDistancias = new double[nvec];
    for (i=0; i<nvec; i++) {
      vecinosCercanos[i] = -1;
      minDistancias[i] = Double.POSITIVE_INFINITY;
    }

    for (i=0; i<conj.length; i++) {
      dist = distancia(conj[i], ejemplo);
      if (dist > 0) {
        parar = false;
        for (j = 0; j < nvec && !parar; j++) {
          if (dist < minDistancias[j]) {
            parar = true;
            for (l = nvec - 1; l >= j+1; l--) {
              minDistancias[l] = minDistancias[l - 1];
              vecinosCercanos[l] = vecinosCercanos[l - 1];
            }
            minDistancias[j] = dist;
            vecinosCercanos[j] = i;
          }
        }
      }
    }

    for (j=0; j<nClases; j++) {
      votos[j] = 0;
    }
    for (j=0; j<nvec; j++) {
      if (vecinosCercanos[j] >= 0)
        votos[clases[vecinosCercanos[j]]] ++;
    }

    votada = 0;
    votaciones = votos[0];
    for (j=1; j<nClases; j++) {
      if (votaciones < votos[j]) {
        votaciones = votos[j];
        votada = j;
      }
    }

    for (i=0; i<vecinosCercanos.length; i++)
      vecinos[i] = vecinosCercanos[i];

    return votada;
  }
  
  public static double distancia (double ej1[], double ej2[]) {

    int i;
    double suma = 0;

    for (i=0; i<ej1.length; i++) {
      suma += (ej1[i]-ej2[i])*(ej1[i]-ej2[i]);
    }
    suma = Math.sqrt(suma);

    return suma;
  }

  public static double distancia2 (double ej1[], double ej2[]) {

    int i;
    double suma = 0;

    for (i=0; i<ej1.length; i++) {
      suma += (ej1[i]-ej2[i])*(ej1[i]-ej2[i]);
    }
    return suma;  
  } 
}



