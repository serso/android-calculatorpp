package jscl.math.polynomial.groebner;

import jscl.math.polynomial.Monomial;
import jscl.math.polynomial.Polynomial;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

class Projection implements Comparable {
    final Monomial monomial;
    final Polynomial polynomial;

    Projection(Pair pair, int index) {
        this(pair.scm.divide(pair.monomial[index]), pair.polynomial[index]);
    }

    Projection(Monomial monomial, Polynomial polynomial) {
        this.monomial = monomial;
        this.polynomial = polynomial;
    }

    Monomial scm() {
        return polynomial.head().monomial().multiply(monomial);
    }

    Polynomial mult() {
        return polynomial.multiply(monomial);
    }

    Projection simplify(List list) {
        Monomial t = monomial;
        if (t.degree() > 0) {
            Monomial m = polynomial.head().monomial();
            int n = list.size();
            for (int i = 0; i < n; i++) {
                Collection ideal = ((F4Reduction) list.get(i)).polys;
                Iterator it = ideal.iterator();
                while (it.hasNext()) {
                    Polynomial p = (Polynomial) it.next();
                    Monomial u = p.head().monomial();
                    if (u.multiple(m, true)) {
                        u = u.divide(m);
                        if (t.multiple(u, true)) {
                            Projection pr = new Projection(t.divide(u), p).simplify(list);
                            return pr;
                        }
                    }
                }
            }
        }
        return this;
    }

    public int compareTo(Projection proj) {
        int c = monomial.compareTo(proj.monomial);
        if (c < 0) return -1;
        else if (c > 0) return 1;
        else {
            c = polynomial.index() - proj.polynomial.index();
            if (c < 0) return -1;
            else if (c > 0) return 1;
            else return 0;
        }
    }

    public int compareTo(Object o) {
        return compareTo((Projection) o);
    }

    public String toString() {
        return "{" + monomial + ", " + polynomial.head().monomial() + "}";
    }
}
