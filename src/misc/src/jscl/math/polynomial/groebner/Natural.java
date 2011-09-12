package jscl.math.polynomial.groebner;

import java.util.Comparator;

class Natural implements Comparator {
    public static final Comparator comparator=new Natural();

    private Natural() {}

    public int compare(Pair pa1, Pair pa2) {
        return pa1.compareTo(pa2);
    }

    public int compare(Object o1, Object o2) {
        return compare((Pair)o1,(Pair)o2);
    }
}
