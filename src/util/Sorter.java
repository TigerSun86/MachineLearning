package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FileName: Sorter.java
 * @Description: To sort elements with given weights.
 * 
 * @author Xunhu(Tiger) Sun
 *         email: sunx2013@my.fit.edu
 * @date Nov 8, 2014 7:43:34 PM
 */
public class Sorter<T> {
    private final ArrayList<Element<T>> list;

    public Sorter() {
        this.list = new ArrayList<Element<T>>();
    }

    public void add (T e, double weight) {
        list.add(new Element<T>(e, weight));
    }

    public List<T> sortAscend () {
        return sort(true);
    }

    public List<T> sortDescend () {
        return sort(false);
    }

    private List<T> sort (boolean isAscend) {
        if (list.isEmpty()) {
            return null;
        }
        if (isAscend) {
            Collections.sort(list);
        } else {
            Collections.sort(list, Collections.reverseOrder());
        }

        final ArrayList<T> ret = new ArrayList<T>();
        for (Element<T> e : list) {
            ret.add(e.e);
        }
        return ret;
    }

    private static class Element<T> implements Comparable<Element<T>> {
        public final T e;
        private final double weight;

        public Element(T e, double weight) {
            this.e = e;
            this.weight = weight;
        }

        @Override
        public int compareTo (Element<T> o) {
            return Double.compare(this.weight, o.weight);
        }
    }
}
