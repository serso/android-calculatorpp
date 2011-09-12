package jscl.math.polynomial.groebner;

import java.util.Comparator;

class Sugar implements Comparator {
    public static final Comparator comparator=new Sugar();

    private Sugar() {}

    public int compare(Pair pa1, Pair pa2) {
        if(pa1.sugar<pa2.sugar) return -1;
        else if(pa1.sugar>pa2.sugar) return 1;
        else return pa1.compareTo(pa2);
    }

    public int compare(Object o1, Object o2) {
        return compare((Pair)o1,(Pair)o2);
    }
}
