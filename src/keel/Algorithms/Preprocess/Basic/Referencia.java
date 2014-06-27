package keel.Algorithms.Preprocess.Basic;

public class Referencia implements Comparable {



  public int entero;

  public double real;



  public Referencia () {}



  public Referencia (int a, double b) {

    entero = a;

    real = b;

  }



  public int compareTo (Object o1) {

    if (this.real > ((Referencia)o1).real)

      return -1;

    else if (this.real < ((Referencia)o1).real)

      return 1;

    else return 0;

  }



  public String toString () {

    return new String ("{"+entero+", "+real+"}");

  }

}

