package jscl.math.polynomial.groebner;

import jscl.math.Generic;
import jscl.math.polynomial.Monomial;
import jscl.math.polynomial.Polynomial;

import java.util.ArrayList;
import java.util.List;

class ReducedRowEchelonForm {
    List content = new ArrayList();

    ReducedRowEchelonForm(List list) {
        content.addAll(list);
    }

    static List compute(List list) {
        ReducedRowEchelonForm f = new ReducedRowEchelonForm(list);
        f.compute();
        return f.content;
    }

    void compute() {
        int n = content.size();
        for (int i = 0; i < n; i++) reduce(i, false);
        for (int i = n - 1; i >= 0; i--) reduce(i, true);
    }

    void reduce(int pivot, boolean direction) {
        Polynomial p = polynomial(pivot);
        content.set(pivot, p = p.normalize().freeze());
        if (p.signum() == 0) return;
        Monomial m = p.head().monomial();
        int b = direction ? 0 : pivot + 1;
        int n = direction ? pivot : content.size();
        for (int i = b; i < n; i++) {
            Polynomial q = polynomial(i);
            Generic a = q.coefficient(m);
            if (a.signum() != 0) content.set(i, q.reduce(a, p));
        }
    }

    Polynomial polynomial(int n) {
        return (Polynomial) content.get(n);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        int n = content.size();
        for (int i = 0; i < n; i++) {
            Polynomial p = polynomial(i);
            buffer.append(i > 0 ? ", " : "").append(p);
        }
        buffer.append("}");
        return buffer.toString();
    }
}
