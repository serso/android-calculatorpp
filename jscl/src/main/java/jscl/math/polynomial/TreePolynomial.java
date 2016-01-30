package jscl.math.polynomial;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.Literal;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

final class TreePolynomial extends Polynomial {
    final SortedMap content;
    int degree;
    boolean mutable = true;

    TreePolynomial(Monomial monomialFactory, Generic coefFactory) {
        super(monomialFactory, coefFactory);
        content = new TreeMap(ordering);
    }

    public int size() {
        return content.size();
    }

    public Iterator iterator(boolean direction, Monomial current) {
        return new ContentIterator(direction, current);
    }

    Term term(Map.Entry entry) {
        return new Term((Monomial) entry.getKey(), (Generic) entry.getValue());
    }

    Term term(Monomial monomial) {
        return new Term(monomial, null) {
            public Generic coef() {
                return coef == null ? coefficient(monomial) : coef;
            }
        };
    }

    SortedMap subContent(Monomial monomial, boolean direction) {
        if (monomial == null) return content;
        return direction ? content.headMap(monomial) : content.tailMap(monomial);
    }

    @Nonnull
    public Polynomial subtract(@Nonnull Polynomial that) {
        if (that.signum() == 0) return this;
        if (mutable) {
            TreePolynomial q = (TreePolynomial) that;
            Iterator it = q.content.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                Monomial m = (Monomial) e.getKey();
                Generic a = (Generic) e.getValue();
                Generic s = coefficient(m).subtract(a);
                if (s.signum() == 0) content.remove(m);
                else content.put(m, s);
            }
            degree = degree(this);
            sugar = Math.max(sugar, q.sugar);
            normalized = false;
            return this;
        } else return copy().subtract(that);
    }

    public Polynomial multiplyAndSubtract(Generic generic, Polynomial polynomial) {
        if (generic.signum() == 0) return this;
        if (generic.compareTo(JsclInteger.valueOf(1)) == 0) return subtract(polynomial);
        if (mutable) {
            TreePolynomial q = (TreePolynomial) polynomial;
            Iterator it = q.content.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                Monomial m = (Monomial) e.getKey();
                Generic a = ((Generic) e.getValue()).multiply(generic);
                Generic s = coefficient(m).subtract(a);
                if (s.signum() == 0) content.remove(m);
                else content.put(m, s);
            }
            degree = degree(this);
            sugar = Math.max(sugar, q.sugar);
            normalized = false;
            return this;
        } else return copy().multiplyAndSubtract(generic, polynomial);
    }

    public Polynomial multiplyAndSubtract(Monomial monomial, Generic generic, Polynomial polynomial) {
        if (generic.signum() == 0) return this;
        if (monomial.degree() == 0) return multiplyAndSubtract(generic, polynomial);
        if (mutable) {
            TreePolynomial q = (TreePolynomial) polynomial;
            Iterator it = q.content.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                Monomial m = ((Monomial) e.getKey()).multiply(monomial);
                Generic a = ((Generic) e.getValue()).multiply(generic);
                Generic s = coefficient(m).subtract(a);
                if (s.signum() == 0) content.remove(m);
                else content.put(m, s);
            }
            degree = degree(this);
            sugar = Math.max(sugar, q.sugar + monomial.degree());
            normalized = false;
            return this;
        } else return copy().multiplyAndSubtract(monomial, generic, polynomial);
    }

    public Polynomial multiply(Generic generic) {
        if (generic.signum() == 0) return valueOf(JsclInteger.valueOf(0));
        if (generic.compareTo(JsclInteger.valueOf(1)) == 0) return this;
        if (mutable) {
            Iterator it = content.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                e.setValue(((Generic) e.getValue()).multiply(generic));
            }
            normalized = false;
            return this;
        } else return copy().multiply(generic);
    }

    public Polynomial multiply(Monomial monomial) {
        if (defined) {
            TreePolynomial p = newinstance();
            Iterator it = content.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                Monomial m = ((Monomial) e.getKey()).multiply(monomial);
                Generic a = (Generic) e.getValue();
                Generic s = p.coefficient(m).add(a);
                if (s.signum() == 0) p.content.remove(m);
                else p.content.put(m, s);
            }
            p.degree = degree(p);
            p.sugar = sugar + monomial.degree();
            return p;
        } else {
            if (monomial.degree() == 0) return this;
            TreePolynomial p = newinstance();
            Iterator it = content.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                p.content.put(((Monomial) e.getKey()).multiply(monomial), e.getValue());
            }
            p.degree = degree + monomial.degree();
            p.sugar = sugar + monomial.degree();
            return p;
        }
    }

    public Polynomial divide(Generic generic) throws ArithmeticException {
        if (generic.compareTo(JsclInteger.valueOf(1)) == 0) return this;
        if (mutable) {
            Iterator it = content.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                e.setValue(((Generic) e.getValue()).divide(generic));
            }
            normalized = false;
            return this;
        } else return copy().divide(generic);
    }

    public Polynomial divide(Monomial monomial) throws ArithmeticException {
        if (monomial.degree() == 0) return this;
        TreePolynomial p = newinstance();
        Iterator it = content.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            p.content.put(((Monomial) e.getKey()).divide(monomial), e.getValue());
        }
        p.degree = degree + monomial.degree();
        p.sugar = sugar + monomial.degree();
        return p;
    }

    public Polynomial gcd(Polynomial polynomial) {
        throw new UnsupportedOperationException();
    }

    public int degree() {
        return degree;
    }

    public Polynomial valueOf(Polynomial polynomial) {
        TreePolynomial p = newinstance();
        p.init(polynomial);
        return p;
    }

    public Polynomial valueOf(Generic generic) {
        TreePolynomial p = newinstance();
        p.init(generic);
        return p;
    }

    public Polynomial valueOf(Monomial monomial) {
        TreePolynomial p = newinstance();
        p.init(monomial);
        return p;
    }

    public Polynomial freeze() {
        mutable = false;
        return this;
    }

    public Term head() {
        return content.size() > 0 ? term((Monomial) content.lastKey()) : null;
    }

    public Term tail() {
        return content.size() > 0 ? term((Monomial) content.firstKey()) : null;
    }

    public Generic coefficient(Monomial monomial) {
        Generic a = (Generic) content.get(monomial);
        return a == null ? coefficient(JsclInteger.valueOf(0)) : a;
    }

    void init(Polynomial polynomial) {
        TreePolynomial q = (TreePolynomial) polynomial;
        content.putAll(q.content);
        degree = q.degree;
        sugar = q.sugar;
    }

    void init(Expression expression) {
        int sugar = 0;
        int n = expression.size();
        for (int i = 0; i < n; i++) {
            Literal l = expression.literal(i);
            JsclInteger en = expression.coef(i);
            Monomial m = monomial(l);
            l = l.divide(m.literalValue());
            Generic a2 = coefficient(l.degree() > 0 ? en.multiply(Expression.valueOf(l)) : en);
            Generic a1 = coefficient(m);
            Generic a = a1.add(a2);
            if (a.signum() == 0) content.remove(m);
            else content.put(m, a);
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
            if (a.signum() != 0) content.put(monomial(Literal.newInstance()), a);
            degree = 0;
            sugar = 0;
        }
    }

    void init(Monomial monomial) {
        content.put(monomial, coefficient(JsclInteger.valueOf(1)));
        degree = monomial.degree();
        sugar = monomial.degree();
    }

    protected TreePolynomial newinstance() {
        return new TreePolynomial(monomialFactory, coefFactory);
    }

    class ContentIterator implements Iterator {
        final boolean direction;
        final Iterator iterator;
        SortedMap map;

        ContentIterator(boolean direction, Monomial current) {
            this.direction = direction;
            if (direction) {
                iterator = null;
                map = subContent(current, true);
            } else {
                iterator = (subContent(current, false)).entrySet().iterator();
                if (current != null && content.containsKey(current)) iterator.next();
            }
        }

        public boolean hasNext() {
            return direction ? map.size() > 0 : iterator.hasNext();
        }

        public Object next() {
            if (direction) {
                Monomial m = (Monomial) map.lastKey();
                map = content.headMap(m);
                return term(m);
            } else {
                return term((Map.Entry) iterator.next());
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
