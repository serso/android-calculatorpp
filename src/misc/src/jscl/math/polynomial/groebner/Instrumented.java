package jscl.math.polynomial.groebner;

import java.util.ArrayList;
import java.util.List;
import jscl.math.Generic;
import jscl.math.polynomial.Basis;
import jscl.math.polynomial.Polynomial;

class Instrumented extends Standard {
    final List aux=new ArrayList();

    Instrumented(int flags) {
        super(flags);
    }

    void populate(Basis basis) {
        Basis aux=basis.modulo(2147483647);
        Generic a[]=basis.elements();
        for(int i=0;i<a.length;i++) {
            Polynomial p=basis.polynomial(a[i]);
            Polynomial x=aux.polynomial(a[i]);
            if(x.signum()!=0 && p.signum()!=0) add(p,x);
        }
    }

    void process(Pair pair) {
        if(criterion(pair)) return;
        Polynomial x=reduce(new Pair(new Polynomial[] {auxiliary(pair.polynomial[0]),auxiliary(pair.polynomial[1])}),aux);
        if(x.signum()!=0) {
            Polynomial p=reduce(pair,polys);
            if(p.signum()!=0) add(p,x);
        }
        npairs++;
    }

    void add(Polynomial polynomial, Polynomial auxiliary) {
        add(polynomial);
        aux.add(auxiliary);
    }

    Polynomial auxiliary(Polynomial polynomial) {
        return (Polynomial)aux.get(polynomial.index());
    }
}
