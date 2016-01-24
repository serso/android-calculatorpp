package jscl.math.polynomial;

import jscl.math.Generic;
import jscl.math.JsclBoolean;
import jscl.math.JsclInteger;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

class ArrayPolynomialBoolean extends ArrayPolynomialModular {
    ArrayPolynomialBoolean(Monomial monomialFactory) {
        super(monomialFactory, JsclBoolean.factory);
    }

    ArrayPolynomialBoolean(int size, Monomial monomialFactory) {
        this(monomialFactory);
        init(size);
    }

    void init(int size) {
        monomial = new Monomial[size];
        this.size = size;
    }

    void resize(int size) {
        int length = monomial.length;
        if (size < length) {
            Monomial monomial[] = new Monomial[size];
            System.arraycopy(this.monomial, length - size, monomial, 0, size);
            this.monomial = monomial;
            this.size = size;
        }
    }

    @Nonnull
    public Polynomial subtract(@Nonnull Polynomial that) {
        if (that.signum() == 0) return this;
        ArrayPolynomialBoolean q = (ArrayPolynomialBoolean) that;
        ArrayPolynomialBoolean p = (ArrayPolynomialBoolean) newinstance(size + q.size);
        int i = p.size;
        int i1 = size;
        int i2 = q.size;
        Monomial m1 = i1 > 0 ? monomial[--i1] : null;
        Monomial m2 = i2 > 0 ? q.monomial[--i2] : null;
        while (m1 != null || m2 != null) {
            int c = m1 == null ? 1 : (m2 == null ? -1 : -ordering.compare(m1, m2));
            if (c < 0) {
                p.monomial[--i] = m1;
                m1 = i1 > 0 ? monomial[--i1] : null;
            } else if (c > 0) {
                p.monomial[--i] = m2;
                m2 = i2 > 0 ? q.monomial[--i2] : null;
            } else {
                m1 = i1 > 0 ? monomial[--i1] : null;
                m2 = i2 > 0 ? q.monomial[--i2] : null;
            }
        }
        p.resize(p.size - i);
        p.degree = degree(p);
        p.sugar = Math.max(sugar, q.sugar);
        return p;
    }

    public Polynomial multiplyAndSubtract(Generic generic, Polynomial polynomial) {
        if (generic.signum() == 0) return this;
        return subtract(polynomial);
    }

    public Polynomial multiplyAndSubtract(Monomial monomial, Generic generic, Polynomial polynomial) {
        if (generic.signum() == 0) return this;
        return multiplyAndSubtract(generic, polynomial.multiply(monomial));
    }

    public Polynomial multiply(Generic generic) {
        if (generic.signum() == 0) return valueOf(JsclInteger.valueOf(0));
        return this;
    }

    public Polynomial multiply(Monomial monomial) {
        if (defined) {
            Map map = new TreeMap(ordering);
            for (int i = 0; i < size; i++) {
                Monomial m = this.monomial[i].multiply(monomial);
                if (map.containsKey(m)) map.remove(m);
                else map.put(m, null);
            }
            ArrayPolynomialBoolean p = (ArrayPolynomialBoolean) newinstance(map.size());
            Iterator it = map.keySet().iterator();
            for (int i = 0; i < p.size; i++) p.monomial[i] = (Monomial) it.next();
            p.degree = degree(p);
            p.sugar = sugar + monomial.degree();
            return p;
        } else {
            if (monomial.degree() == 0) return this;
            ArrayPolynomialBoolean p = (ArrayPolynomialBoolean) newinstance(size);
            for (int i = 0; i < size; i++) p.monomial[i] = this.monomial[i].multiply(monomial);
            p.degree = degree + monomial.degree();
            p.sugar = sugar + monomial.degree();
            return p;
        }
    }

    protected Generic getCoef(int n) {
        return new JsclBoolean(1);
    }

    protected void setCoef(int n, Generic generic) {
    }

    protected ArrayPolynomialGeneric newinstance(int n) {
        return new ArrayPolynomialBoolean(n, monomialFactory);
    }
}
