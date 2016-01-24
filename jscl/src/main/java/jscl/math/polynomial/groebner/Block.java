package jscl.math.polynomial.groebner;

import jscl.math.Debug;
import jscl.math.polynomial.Basis;
import jscl.math.polynomial.DegreeOrdering;
import jscl.math.polynomial.Ordering;
import jscl.math.polynomial.Polynomial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Block extends Standard {
    boolean degree;

    Block(Ordering ordering, int flags) {
        super(flags);
        degree = ordering instanceof DegreeOrdering;
    }

    void compute() {
        Debug.println("evaluate");
        int degree = 0;
        while (!pairs.isEmpty()) {
            List list = new ArrayList();
            Iterator it = pairs.keySet().iterator();
            while (it.hasNext()) {
                Pair pa = (Pair) it.next();
                int d = (flags & Basis.SUGAR) > 0 ? pa.sugar : pa.scm.degree();
                if (degree == 0) degree = d;
                else if (d > degree || !this.degree) break;
                list.add(pa);
            }
            process(list);
            remove(list);
            degree = 0;
        }
    }

    void add(List list) {
        super.add(ReducedRowEchelonForm.compute(list));
    }

    void process(List pairs) {
        List list = new ArrayList();
        Iterator it = pairs.iterator();
        while (it.hasNext()) {
            Pair pa = (Pair) it.next();
            if (criterion(pa)) continue;
            Polynomial p = reduce(pa, polys);
            if (p.signum() != 0) list.add(p);
            npairs++;
        }
        add(list);
    }

    void remove(List pairs) {
        Iterator it = pairs.iterator();
        while (it.hasNext()) remove((Pair) it.next());
    }
}
