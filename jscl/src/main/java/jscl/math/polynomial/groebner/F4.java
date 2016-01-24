package jscl.math.polynomial.groebner;

import jscl.math.polynomial.Ordering;
import jscl.math.polynomial.Polynomial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class F4 extends Block {
    List reduction = new ArrayList();

    F4(Ordering ordering, int flags) {
        super(ordering, flags);
    }

    void add(List list) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Polynomial p = (Polynomial) it.next();
            if (p.signum() != 0) add(p);
        }
    }

    void process(List pairs) {
        List list = new ArrayList();
        Iterator it = pairs.iterator();
        while (it.hasNext()) {
            Pair pa = (Pair) it.next();
            if (criterion(pa)) continue;
            list.add(pa);
        }
        add(F4Reduction.compute(list, polys, reduction, flags));
        npairs += list.size();
    }
}
