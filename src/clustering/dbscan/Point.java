package clustering.dbscan;

import java.util.ArrayList;

public class Point 
{
	private double x;

	private double y;

	public ArrayList<Double> v = new ArrayList<Double>();
	public clustering.Vector e = null;
	Point(double a, double b)
	 {
		x=a;
		y=b;
	 }
	public Point(clustering.Vector vector){
	    this.e = vector;
	}

	public double getX ()
	 {

		return x;

	 }


	public double getY () 
	{

		return y;

	}

	

}

