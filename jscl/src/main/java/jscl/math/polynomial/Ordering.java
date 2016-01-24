package jscl.math.polynomial;

import java.util.Comparator;

public abstract class Ordering implements Comparator {
    public abstract int compare(Monomial m1, Monomial m2);

    public int compare(Object o1, Object o2) {
        return compare((Monomial) o1, (Monomial) o2);
    }
}
