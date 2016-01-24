package jscl.math.polynomial;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.Literal;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

class ArrayPolynomialGeneric extends Polynomial {
    Generic coef[];
    Monomial monomial[];
    int size;
    int degree;

    ArrayPolynomialGeneric(Monomial monomialFactory, Generic coefFactory) {
        super(monomialFactory, coefFactory);
    }

    ArrayPolynomialGeneric(int size, Monomial monomialFactory, Generic coefFactory) {
        this(monomialFactory, coefFactory);
        init(size);
    }

    public int size() {
        return size;
    }

    void init(int size) {
        monomial = new Monomial[size];
        coef = new Generic[size];
        this.size = size;
    }

    void resize(int size) {
        int length = monomial.length;
        if (size < length) {
            Monomial monomial[] = new Monomial[size];
            Generic coef[] = new Generic[size];
            System.arraycopy(this.monomial, length - size, monomial, 0, size);
            System.arraycopy(this.coef, length - size, coef, 0, size);
            this.monomial = monomial;
            this.coef = coef;
            this.size = size;
        }
    }

    public Iterator iterator(boolean direction, Monomial current) {
        return new ContentIterator(direction, current);
    }

    Term term(final int index) {
        return new Term(monomial[index], null) {
            public Generic coef() {
                return coef == null ? getCoef(index) : coef;
            }
        };
    }

    int indexOf(Monomial monomial, boolean direction) {
        if (monomial == null) return direction ? size : 0;
        int n = Arrays.binarySearch(this.monomial, monomial, ordering);
        return n < 0 ? -n - 1 : direction ? n : n + 1;
    }

    @Nonnull
    public Polynomial subtract(@Nonnull Polynomial that) {
        if (that.signum() == 0) return this;
        ArrayPolynomialGeneric q = (ArrayPolynomialGeneric) that;
        ArrayPolynomialGeneric p = newinstance(size + q.size);
        int i = p.size;
        int i1 = size;
        int i2 = q.size;
        Monomial m1 = i1 > 0 ? monomial[--i1] : null;
        Monomial m2 = i2 > 0 ? q.monomial[--i2] : null;
        while (m1 != null || m2 != null) {
            int c = m1 == null ? 1 : (m2 == null ? -1 : -ordering.compare(m1, m2));
            if (c < 0) {
                Generic a = getCoef(i1);
                --i;
                p.monomial[i] = m1;
                p.setCoef(i, a);
                m1 = i1 > 0 ? monomial[--i1] : null;
            } else if (c > 0) {
                Generic a = q.getCoef(i2).negate();
                --i;
                p.monomial[i] = m2;
                p.setCoef(i, a);
                m2 = i2 > 0 ? q.monomial[--i2] : null;
            } else {
                Generic a = getCoef(i1).subtract(q.getCoef(i2));
                if (a.signum() != 0) {
                    --i;
                    p.monomial[i] = m1;
                    p.setCoef(i, a);
                }
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
        if (generic.compareTo(JsclInteger.valueOf(1)) == 0) return subtract(polynomial);
        ArrayPolynomialGeneric q = (ArrayPolynomialGeneric) polynomial;
        ArrayPolynomialGeneric p = newinstance(size + q.size);
        int i = p.size;
        int i1 = size;
        int i2 = q.size;
        Monomial m1 = i1 > 0 ? monomial[--i1] : null;
        Monomial m2 = i2 > 0 ? q.monomial[--i2] : null;
        while (m1 != null || m2 != null) {
            int c = m1 == null ? 1 : (m2 == null ? -1 : -ordering.compare(m1, m2));
            if (c < 0) {
                Generic a = getCoef(i1);
                --i;
                p.monomial[i] = m1;
                p.setCoef(i, a);
                m1 = i1 > 0 ? monomial[--i1] : null;
            } else if (c > 0) {
                Generic a = q.getCoef(i2).multiply(generic).negate();
                --i;
                p.monomial[i] = m2;
                p.setCoef(i, a);
                m2 = i2 > 0 ? q.monomial[--i2] : null;
            } else {
                Generic a = getCoef(i1).subtract(q.getCoef(i2).multiply(generic));
                if (a.signum() != 0) {
                    --i;
                    p.monomial[i] = m1;
                    p.setCoef(i, a);
                }
                m1 = i1 > 0 ? monomial[--i1] : null;
                m2 = i2 > 0 ? q.monomial[--i2] : null;
            }
        }
        p.resize(p.size - i);
        p.degree = degree(p);
        p.sugar = Math.max(sugar, q.sugar);
        return p;
    }

    public Polynomial multiplyAndSubtract(Monomial monomial, Generic generic, Polynomial polynomial) {
        if (defined) throw new UnsupportedOperationException();
        if (generic.signum() == 0) return this;
        if (monomial.degree() == 0) return multiplyAndSubtract(generic, polynomial);
        ArrayPolynomialGeneric q = (ArrayPolynomialGeneric) polynomial;
        ArrayPolynomialGeneric p = newinstance(size + q.size);
        int i = p.size;
        int i1 = size;
        int i2 = q.size;
        Monomial m1 = i1 > 0 ? this.monomial[--i1] : null;
        Monomial m2 = i2 > 0 ? q.monomial[--i2].multiply(monomial) : null;
        while (m1 != null || m2 != null) {
            int c = m1 == null ? 1 : (m2 == null ? -1 : -ordering.compare(m1, m2));
            if (c < 0) {
                Generic a = getCoef(i1);
                --i;
                p.monomial[i] = m1;
                p.setCoef(i, a);
                m1 = i1 > 0 ? this.monomial[--i1] : null;
            } else if (c > 0) {
                Generic a = q.getCoef(i2).multiply(generic).negate();
                --i;
                p.monomial[i] = m2;
                p.setCoef(i, a);
                m2 = i2 > 0 ? q.monomial[--i2].multiply(monomial) : null;
            } else {
                Generic a = getCoef(i1).subtract(q.getCoef(i2).multiply(generic));
                if (a.signum() != 0) {
                    --i;
                    p.monomial[i] = m1;
                    p.setCoef(i, a);
                }
                m1 = i1 > 0 ? this.monomial[--i1] : null;
                m2 = i2 > 0 ? q.monomial[--i2].multiply(monomial) : null;
            }
        }
        p.resize(p.size - i);
        p.degree = degree(p);
        p.sugar = Math.max(sugar, q.sugar + monomial.degree());
        return p;
    }

    @Nonnull
    public Polynomial multiply(@Nonnull Polynomial that) {
        Polynomial p = newinstance(0);
        for (int i = 0; i < size; i++) p = p.multiplyAndSubtract(monomial[i], getCoef(i).negate(), that);
        return p;
    }

    public Polynomial multiply(Generic generic) {
        if (generic.signum() == 0) return valueOf(JsclInteger.valueOf(0));
        if (generic.compareTo(JsclInteger.valueOf(1)) == 0) return this;
        ArrayPolynomialGeneric p = newinstance(size);
        System.arraycopy(monomial, 0, p.monomial, 0, size);
        for (int i = 0; i < size; i++) p.setCoef(i, getCoef(i).multiply(generic));
        p.degree = degree;
        p.sugar = sugar;
        return p;
    }

    public Polynomial multiply(Monomial monomial) {
        if (defined) throw new UnsupportedOperationException();
        if (monomial.degree() == 0) return this;
        ArrayPolynomialGeneric p = newinstance(size);
        for (int i = 0; i < size; i++) {
            p.monomial[i] = this.monomial[i].multiply(monomial);
            p.setCoef(i, getCoef(i));
        }
        p.degree = degree + monomial.degree();
        p.sugar = sugar + monomial.degree();
        return p;
    }

    public Polynomial divide(Generic generic) throws ArithmeticException {
        if (generic.compareTo(JsclInteger.valueOf(1)) == 0) return this;
        ArrayPolynomialGeneric p = newinstance(size);
        System.arraycopy(monomial, 0, p.monomial, 0, size);
        for (int i = 0; i < size; i++) p.setCoef(i, getCoef(i).divide(generic));
        p.degree = degree;
        p.sugar = sugar;
        return p;
    }

    public Polynomial divide(Monomial monomial) throws ArithmeticException {
        if (monomial.degree() == 0) return this;
        ArrayPolynomialGeneric p = newinstance(size);
        for (int i = 0; i < size; i++) {
            p.monomial[i] = this.monomial[i].divide(monomial);
            p.setCoef(i, getCoef(i));
        }
        p.degree = degree - monomial.degree();
        p.sugar = sugar - monomial.degree();
        return p;
    }

    public Polynomial gcd(Polynomial polynomial) {
        throw new UnsupportedOperationException();
    }

    public Generic gcd() {
        if (field) return coefficient(tail());
        Generic a = coefficient(JsclInteger.valueOf(0));
        for (int i = size - 1; i >= 0; i--) a = a.gcd(getCoef(i));
        return a.signum() == signum() ? a : a.negate();
    }

    public Monomial monomialGcd() {
        Monomial m = monomial(tail());
        for (int i = 0; i < size; i++) m = m.gcd(monomial[i]);
        return m;
    }

    public int degree() {
        return degree;
    }

    public Polynomial valueOf(Polynomial polynomial) {
        ArrayPolynomialGeneric p = newinstance(0);
        p.init(polynomial);
        return p;
    }

    public Polynomial valueOf(Generic generic) {
        ArrayPolynomialGeneric p = newinstance(0);
        p.init(generic);
        return p;
    }

    public Polynomial valueOf(Monomial monomial) {
        ArrayPolynomialGeneric p = newinstance(0);
        p.init(monomial);
        return p;
    }

    public Polynomial freeze() {
        return this;
    }

    public Term head() {
        return size > 0 ? term(size - 1) : null;
    }

    public Term tail() {
        return size > 0 ? term(0) : null;
    }

    protected Generic getCoef(int n) {
        return coef[n];
    }

    protected void setCoef(int n, Generic generic) {
        coef[n] = generic;
    }

    public Generic genericValue() {
        Generic s = JsclInteger.valueOf(0);
        for (int i = 0; i < size; i++) {
            Monomial m = monomial[i];
            Generic a = getCoef(i).expressionValue();
            s = s.add(m.degree() > 0 ? a.multiply(Expression.valueOf(m.literalValue())) : a);
        }
        return s;
    }

    public Generic[] elements() {
        Generic a[] = new Generic[size];
        for (int i = 0; i < size; i++) a[i] = getCoef(i);
        return a;
    }

    public int compareTo(Polynomial polynomial) {
        ArrayPolynomialGeneric q = (ArrayPolynomialGeneric) polynomial;
        int i1 = size;
        int i2 = q.size;
        Monomial m1 = i1 == 0 ? null : monomial[--i1];
        Monomial m2 = i2 == 0 ? null : q.monomial[--i2];
        while (m1 != null || m2 != null) {
            int c = m1 == null ? -1 : (m2 == null ? 1 : ordering.compare(m1, m2));
            if (c < 0) return -1;
            else if (c > 0) return 1;
            else {
                c = getCoef(i1).compareTo(q.getCoef(i2));
                if (c < 0) return -1;
                else if (c > 0) return 1;
                m1 = i1 == 0 ? null : monomial[--i1];
                m2 = i2 == 0 ? null : q.monomial[--i2];
            }
        }
        return 0;
    }

    void init(Polynomial polynomial) {
        ArrayPolynomialGeneric q = (ArrayPolynomialGeneric) polynomial;
        init(q.size);
        System.arraycopy(q.monomial, 0, monomial, 0, size);
        for (int i = 0; i < size; i++) setCoef(i, q.getCoef(i));
        degree = q.degree;
        sugar = q.sugar;
    }

    void init(Expression expression) {
        Map map = new TreeMap(ordering);
        int n = expression.size();
        for (int i = 0; i < n; i++) {
            Literal l = expression.literal(i);
            JsclInteger en = expression.coef(i);
            Monomial m = monomial(l);
            l = l.divide(m.literalValue());
            Generic a2 = coefficient(l.degree() > 0 ? en.multiply(Expression.valueOf(l)) : en);
            Generic a1 = (Generic) map.get(m);
            Generic a = a1 == null ? a2 : a1.add(a2);
            if (a.signum() == 0) map.remove(m);
            else map.put(m, a);
        }
        init(map.size());
        int sugar = 0;
        Iterator it = map.entrySet().iterator();
        for (int i = 0; i < size; i++) {
            Map.Entry e = (Map.Entry) it.next();
            Monomial m = (Monomial) e.getKey();
            Generic a = (Generic) e.getValue();
            monomial[i] = m;
            setCoef(i, a);
            sugar = Math.max(sugar, m.degree());
        }
        degree = degree(this);
        this.sugar = sugar;
    }

    void init(Generic generic) {
        if (generic instanceof Expression) {
            init((Expression) generic);
        } else {
            Generic a = coefficient(generic);
            if (a.signum() != 0) {
                init(1);
                monomial[0] = monomial(Literal.newInstance());
                setCoef(0, a);
            } else init(0);
            degree = 0;
            sugar = 0;
        }
    }

    void init(Monomial monomial) {
        init(1);
        this.monomial[0] = monomial;
        setCoef(0, coefficient(JsclInteger.valueOf(1)));
        degree = monomial.degree();
        sugar = monomial.degree();
    }

    protected ArrayPolynomialGeneric newinstance(int n) {
        return new ArrayPolynomialGeneric(n, monomialFactory, coefFactory);
    }

    class ContentIterator implements Iterator {
        final boolean direction;
        int index;

        ContentIterator(boolean direction, Monomial current) {
            this.direction = direction;
            index = indexOf(current, direction);
        }

        public boolean hasNext() {
            return direction ? index > 0 : index < size;
        }

        public Object next() {
            return direction ? term(--index) : term(index++);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
