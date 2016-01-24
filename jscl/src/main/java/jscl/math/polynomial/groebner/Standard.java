package jscl.math.polynomial.groebner;

import jscl.math.Debug;
import jscl.math.Generic;
import jscl.math.polynomial.Basis;
import jscl.math.polynomial.Monomial;
import jscl.math.polynomial.Ordering;
import jscl.math.polynomial.Polynomial;
import jscl.util.ArrayUtils;

import java.util.*;

public class Standard {
    final int flags;
    final Comparator comparator;
    final Map pairs;
    final List polys = new ArrayList();
    final Map removed = new TreeMap();
    int npairs;
    int npolys;

    Standard(int flags) {
        this.flags = flags;
        pairs = new TreeMap(comparator = (flags & Basis.SUGAR) > 0 ? Sugar.comparator : Natural.comparator);
    }

    public static Basis compute(Basis basis) {
        return compute(basis, 0);
    }

    public static Basis compute(Basis basis, int flags) {
        return compute(basis, flags, (flags & Basis.INSTRUMENTED) > 0);
    }

    static Basis compute(Basis basis, int flags, boolean instrumented) {
        Standard a = instrumented ? new Instrumented(flags) : algorithm(basis.ordering(), flags);
        a.computeValue(basis);
        basis = basis.valueof(a.elements());
        if (instrumented) return compute(basis, flags, false);
        return basis;
    }

    static Standard algorithm(Ordering ordering, int flags) {
        switch (flags & Basis.ALGORITHM) {
            case Basis.F4:
                return new F4(ordering, flags);
            case Basis.BLOCK:
                return new Block(ordering, flags);
            default:
                return new Standard(flags);
        }
    }

    static Polynomial reduce(Pair pair, Collection ideal) {
        Debug.println(pair);
        return s_polynomial(pair.polynomial[0], pair.polynomial[1]).reduce(ideal, false).normalize().freeze();
    }

    static Polynomial s_polynomial(Polynomial p1, Polynomial p2) {
        Monomial m1 = p1.head().monomial();
        Monomial m2 = p2.head().monomial();
        Monomial m = m1.gcd(m2);
        m1 = m1.divide(m);
        m2 = m2.divide(m);
        return p1.multiply(m2).reduce(p1.head().coef(), m1, p2);
    }

    void computeValue(Basis basis) {
        Debug.println(basis);
        populate(basis);
        npolys = 0;
        compute();
        remove();
        reduce();
        Debug.println("signature = (" + npairs + ", " + npolys + ", " + polys.size() + ")");
    }

    void populate(Basis basis) {
        List list = new ArrayList();
        Generic a[] = basis.elements();
        for (int i = 0; i < a.length; i++) list.add(basis.polynomial(a[i]));
        add(list);
    }

    void add(List list) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Polynomial p = (Polynomial) it.next();
            if (p.signum() != 0) add(p);
        }
    }

    void compute() {
        Debug.println("evaluate");
        while (!pairs.isEmpty()) {
            Pair pa = (Pair) pairs.keySet().iterator().next();
            process(pa);
            remove(pa);
        }
    }

    void process(Pair pair) {
        if (criterion(pair)) return;
        Polynomial p = reduce(pair, polys);
        if (p.signum() != 0) add(p);
        npairs++;
    }

    void remove(Pair pair) {
        pairs.remove(pair);
        if (pair.reduction) removed.put(pair.principal, null);
    }

    void add(Polynomial polynomial) {
        polynomial.setIndex(polys.size());
        Debug.println("(" + polynomial.head().monomial() + ", " + polynomial.index() + ")");
        if ((flags & Basis.GM_SETTING) > 0) makePairsGM(polynomial);
        else makePairs(polynomial);
        polys.add(polynomial);
        npolys++;
    }

    boolean criterion(Pair pair) {
        return (flags & Basis.GM_SETTING) > 0 ? false : b_criterion(pair);
    }

    void makePairs(Polynomial polynomial) {
        Iterator it = polys.iterator();
        while (it.hasNext()) {
            Polynomial p = (Polynomial) it.next();
            Pair pa = new Pair(p, polynomial);
            if (!pa.coprime) pairs.put(pa, null);
        }
    }

    boolean b_criterion(Pair pair) {
        Iterator it = polys.iterator();
        while (it.hasNext()) {
            Polynomial p = (Polynomial) it.next();
            if (pair.scm.multiple(p.head().monomial())) {
                Pair pa1 = new Pair(sort(pair.polynomial[0], p));
                Pair pa2 = new Pair(sort(pair.polynomial[1], p));
                if (considered(pa1) && considered(pa2)) return true;
            }
        }
        return false;
    }

    boolean considered(Pair pair) {
        return !pairs.containsKey(pair);
    }

    Polynomial[] sort(Polynomial p1, Polynomial p2) {
        return p1.index() < p2.index() ? new Polynomial[]{p1, p2} : new Polynomial[]{p2, p1};
    }

    void makePairsGM(Polynomial polynomial) {
        List list = new ArrayList();
        Iterator it = pairs.keySet().iterator();
        while (it.hasNext()) {
            Pair pa = (Pair) it.next();
            Pair p1 = new Pair(new Polynomial[]{pa.polynomial[0], polynomial});
            Pair p2 = new Pair(new Polynomial[]{pa.polynomial[1], polynomial});
            if (multiple(pa, p1) && multiple(pa, p2)) list.add(pa);
        }
        int n = list.size();
        for (int i = 0; i < n; i++) {
            Pair pa = (Pair) list.get(i);
            remove(pa);
        }
        Map map = new TreeMap((flags & Basis.SUGAR) > 0 && (flags & Basis.FUSSY) > 0 ? Sugar.comparator : Natural.comparator);
        it = polys.iterator();
        while (it.hasNext()) {
            Polynomial p = (Polynomial) it.next();
            Pair pa = new Pair(p, polynomial);
            pairs.put(pa, null);
            map.put(pa, null);
        }
        list = ArrayUtils.toList(map.keySet());
        n = list.size();
        for (int i = 0; i < n; i++) {
            Pair pa = (Pair) list.get(i);
            for (int j = i + 1; j < n; j++) {
                Pair pa2 = (Pair) list.get(j);
                if (pa2.scm.multiple(pa.scm)) remove(pa2);
            }
            if (pa.coprime) remove(pa);
        }
    }

    boolean multiple(Pair p1, Pair p2) {
        return p1.scm.multiple(p2.scm, true) && ((flags & Basis.SUGAR) > 0 && (flags & Basis.FUSSY) > 0 ? Sugar.comparator.compare(p1, p2) > 0 : true);
    }

    void remove() {
        Iterator it = polys.iterator();
        while (it.hasNext()) if (removed.containsKey(it.next())) it.remove();
    }

    void reduce() {
        Debug.println("reduce");
        Map map = new TreeMap();
        int size = polys.size();
        for (int i = 0; i < size; i++) {
            Polynomial p = (Polynomial) polys.get(i);
            polys.set(i, p = p.reduce(polys, true).normalize().freeze());
            Debug.println("(" + p.head().monomial() + ")");
            map.put(p, null);
        }
        polys.clear();
        polys.addAll(map.keySet());
    }

    Generic[] elements() {
        int size = polys.size();
        Generic a[] = new Generic[size];
        for (int i = 0; i < size; i++) {
            a[i] = ((Polynomial) polys.get(i)).genericValue();
        }
        return a;
    }
}
