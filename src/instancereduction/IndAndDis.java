package instancereduction;


/**
 * FileName:     IndAndDis.java
 * @Description: 
 *
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Jul 2, 2014 11:09:38 PM 
 */
public class IndAndDis implements Comparable<IndAndDis> {
    public int index;
    public double dis;

    public IndAndDis(int index, double dis) {
        this.index = index;
        this.dis = dis;
    }

    @Override
    public int compareTo (IndAndDis arg0) {
        return Double.compare(this.dis, arg0.dis);
    }
}
