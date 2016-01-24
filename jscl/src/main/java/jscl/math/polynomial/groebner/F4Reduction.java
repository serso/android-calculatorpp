package jscl.math.polynomial.groebner;

import jscl.math.Debug;
import jscl.math.polynomial.Basis;
import jscl.math.polynomial.Monomial;
import jscl.math.polynomial.Polynomial;
import jscl.math.polynomial.Term;

import java.util.*;

class F4Reduction {
    final Collection ideal;
    final List list;
    final int flags;
    final List polys = new ArrayList();
    final List content = new ArrayList();
    final Map considered = new TreeMap();
    final Map head = new TreeMap();
    final Map proj = new TreeMap();

    F4Reduction(Collection ideal, List list, int flags) {
        this.ideal = ideal;
        this.list = list;
        this.flags = flags;
    }

    static List compute(List pairs, Collection ideal, List list, int flags) {
        F4Reduction r = new F4Reduction(ideal, list, flags);
        r.compute(pairs);
        return r.content;
    }

    void compute(List pairs) {
        Iterator it = pairs.iterator();
        while (it.hasNext()) {
            Pair pa = (Pair) it.next();
            considered.put(pa.scm, null);
            add(pa);
        }
        process();
        if ((flags & Basis.F4_SIMPLIFY) > 0) list.add(this);
    }

    void add(Pair pair) {
        Debug.println(pair);
        Projection pr[] = new Projection[]{new Projection(pair, 0), new Projection(pair, 1)};
        for (int i = 0; i < pr.length; i++)
            if (!proj.containsKey(pr[i])) {
                add(pr[i].simplify(list));
                proj.put(pr[i], null);
            }
    }

    void add(Projection projection) {
        Polynomial p = projection.mult();
        Monomial scm = projection.scm();
        head.put(scm, null);
        Iterator it = p.iterator(scm);
        while (it.hasNext()) {
            Term t = (Term) it.next();
            Monomial m1 = t.monomial();
            if (considered.containsKey(m1)) continue;
            else considered.put(m1, null);
            Iterator iq = ideal.iterator();
            while (iq.hasNext()) {
                Polynomial q = (Polynomial) iq.next();
                Monomial m2 = q.head().monomial();
                if (m1.multiple(m2)) {
                    Monomial m = m1.divide(m2);
                    add(new Projection(m, q).simplify(list));
                    break;
                }
            }
        }
        content.add(p);
    }

    void process() {
        List list = ReducedRowEchelonForm.compute(content);
        content.clear();
        int n = list.size();
        for (int i = 0; i < n; i++) {
            Polynomial p = (Polynomial) list.get(i);
            if (p.signum() != 0) {
                Monomial m = p.head().monomial();
                if (!head.containsKey(m)) content.add(p);
                else {
                    if (p.index() != -1) p = p.copy();
                    p.setIndex(polys.size());
                    polys.add(p);
                }
            }
        }
    }
}
