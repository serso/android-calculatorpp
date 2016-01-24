package jscl.math.polynomial;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.Literal;
import jscl.util.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.*;

final class ListPolynomial extends Polynomial {
    final List content = new LinkedList();
    int degree;
    boolean mutable = true;

    ListPolynomial(Monomial monomialFactory, Generic coefFactory) {
        super(monomialFactory, coefFactory);
    }

    public int size() {
        return content.size();
    }

    public final Iterator iterator(boolean direction, Monomial current) {
        return new ContentIterator(direction, current);
    }

    int indexOf(Monomial monomial, boolean direction) {
        if (monomial == null) return direction ? content.size() : 0;
        int n = ArrayUtils.binarySearch(content, new Term(monomial, null));
        return n < 0 ? -n - 1 : direction ? n : n + 1;
    }

    @Nonnull
    public Polynomial subtract(@Nonnull Polynomial that) {
        if (that.signum() == 0) return this;
        if (mutable) {
            ListPolynomial q = (ListPolynomial) that;
            ListIterator it1 = content.listIterator(content.size());
            ListIterator it2 = q.content.listIterator(q.content.size());
            Term t1 = it1.hasPrevious() ? (Term) it1.previous() : null;
            Term t2 = it2.hasPrevious() ? (Term) it2.previous() : null;
            while (t2 != null) {
                int c = t1 == null ? 1 : (t2 == null ? -1 : -ordering.compare(t1.monomial(), t2.monomial()));
                if (c < 0) {
                    t1 = it1.hasPrevious() ? (Term) it1.previous() : null;
                } else {
                    if (c > 0) {
                        if (t1 != null) it1.next();
                        it1.add(t2.negate());
                    } else {
                        Term t = t1.subtract(t2);
                        if (t.signum() == 0) it1.remove();
                        else it1.set(t);
                    }
                    t1 = it1.hasPrevious() ? (Term) it1.previous() : null;
                    t2 = it2.hasPrevious() ? (Term) it2.previous() : null;
                }
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
            ListPolynomial q = (ListPolynomial) polynomial;
            ListIterator it1 = content.listIterator(content.size());
            ListIterator it2 = q.content.listIterator(q.content.size());
            Term t1 = it1.hasPrevious() ? (Term) it1.previous() : null;
            Term t2 = it2.hasPrevious() ? ((Term) it2.previous()).multiply(generic) : null;
            while (t2 != null) {
                int c = t1 == null ? 1 : (t2 == null ? -1 : -ordering.compare(t1.monomial(), t2.monomial()));
                if (c < 0) {
                    t1 = it1.hasPrevious() ? (Term) it1.previous() : null;
                } else {
                    if (c > 0) {
                        if (t1 != null) it1.next();
                        it1.add(t2.negate());
                    } else {
                        Term t = t1.subtract(t2);
                        if (t.signum() == 0) it1.remove();
                        else it1.set(t);
                    }
                    t1 = it1.hasPrevious() ? (Term) it1.previous() : null;
                    t2 = it2.hasPrevious() ? ((Term) it2.previous()).multiply(generic) : null;
                }
            }
            degree = degree(this);
            sugar = Math.max(sugar, q.sugar);
            normalized = false;
            return this;
        } else return copy().multiplyAndSubtract(generic, polynomial);
    }

    public Polynomial multiplyAndSubtract(Monomial monomial, Generic generic, Polynomial polynomial) {
        if (defined) throw new UnsupportedOperationException();
        if (generic.signum() == 0) return this;
        if (monomial.degree() == 0) return multiplyAndSubtract(generic, polynomial);
        if (mutable) {
            ListPolynomial q = (ListPolynomial) polynomial;
            ListIterator it1 = content.listIterator(content.size());
            ListIterator it2 = q.content.listIterator(q.content.size());
            Term t1 = it1.hasPrevious() ? (Term) it1.previous() : null;
            Term t2 = it2.hasPrevious() ? ((Term) it2.previous()).multiply(monomial, generic) : null;
            while (t2 != null) {
                int c = t1 == null ? 1 : (t2 == null ? -1 : -ordering.compare(t1.monomial(), t2.monomial()));
                if (c < 0) {
                    t1 = it1.hasPrevious() ? (Term) it1.previous() : null;
                } else {
                    if (c > 0) {
                        if (t1 != null) it1.next();
                        it1.add(t2.negate());
                    } else {
                        Term t = t1.subtract(t2);
                        if (t.signum() == 0) it1.remove();
                        else it1.set(t);
                    }
                    t1 = it1.hasPrevious() ? (Term) it1.previous() : null;
                    t2 = it2.hasPrevious() ? ((Term) it2.previous()).multiply(monomial, generic) : null;
                }
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
            ListIterator it = content.listIterator();
            while (it.hasNext()) it.set(((Term) it.next()).multiply(generic));
            normalized = false;
            return this;
        } else return copy().multiply(generic);
    }

    public Polynomial multiply(Monomial monomial) {
        if (defined) throw new UnsupportedOperationException();
        if (monomial.degree() == 0) return this;
        if (mutable) {
            ListIterator it = content.listIterator();
            while (it.hasNext()) it.set(((Term) it.next()).multiply(monomial));
            degree += monomial.degree();
            sugar += monomial.degree();
            return this;
        } else return copy().multiply(monomial);
    }

    public Polynomial divide(Generic generic) throws ArithmeticException {
        if (generic.compareTo(JsclInteger.valueOf(1)) == 0) return this;
        if (mutable) {
            ListIterator it = content.listIterator();
            while (it.hasNext()) it.set(((Term) it.next()).divide(generic));
            normalized = false;
            return this;
        } else return copy().divide(generic);
    }

    public Polynomial divide(Monomial monomial) throws ArithmeticException {
        if (monomial.degree() == 0) return this;
        if (mutable) {
            ListIterator it = content.listIterator();
            while (it.hasNext()) it.set(((Term) it.next()).divide(monomial));
            degree -= monomial.degree();
            sugar -= monomial.degree();
            return this;
        } else return copy().divide(monomial);
    }

    public Polynomial gcd(Polynomial polynomial) {
        throw new UnsupportedOperationException();
    }

    public int degree() {
        return degree;
    }

    public Polynomial valueOf(Polynomial polynomial) {
        ListPolynomial p = newinstance(0);
        p.init(polynomial);
        return p;
    }

    public Polynomial valueOf(Generic generic) {
        ListPolynomial p = newinstance(0);
        p.init(generic);
        return p;
    }

    public Polynomial valueOf(Monomial monomial) {
        ListPolynomial p = newinstance(0);
        p.init(monomial);
        return p;
    }

    public Polynomial freeze() {
        mutable = false;
        return this;
    }

    public Term head() {
        int size = content.size();
        return size > 0 ? (Term) content.get(size - 1) : null;
    }

    public Term tail() {
        int size = content.size();
        return size > 0 ? (Term) content.get(0) : null;
    }

    void init(Polynomial polynomial) {
        ListPolynomial q = (ListPolynomial) polynomial;
        content.addAll(q.content);
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
        int sugar = 0;
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            Monomial m = (Monomial) e.getKey();
            Generic a = (Generic) e.getValue();
            content.add(new Term(m, a));
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
                content.add(new Term(monomial(Literal.newInstance()), a));
            }
            degree = 0;
            sugar = 0;
        }
    }

    void init(Monomial monomial) {
        content.add(new Term(monomial, coefficient(JsclInteger.valueOf(1))));
        degree = monomial.degree();
        sugar = monomial.degree();
    }

    protected ListPolynomial newinstance(int n) {
        return new ListPolynomial(monomialFactory, coefFactory);
    }

    class ContentIterator implements ListIterator {
        final ListIterator iterator;
        final boolean direction;

        ContentIterator(boolean direction, Monomial current) {
            this.direction = direction;
            iterator = content.listIterator(indexOf(current, direction));
        }

        public boolean hasNext() {
            return direction ? iterator.hasPrevious() : iterator.hasNext();
        }

        public Object next() {
            return direction ? iterator.previous() : iterator.next();
        }

        public boolean hasPrevious() {
            return direction ? iterator.hasNext() : iterator.hasPrevious();
        }

        public Object previous() {
            return direction ? iterator.next() : iterator.previous();
        }

        public int nextIndex() {
            return direction ? iterator.previousIndex() : iterator.nextIndex();
        }

        public int previousIndex() {
            return direction ? iterator.nextIndex() : iterator.previousIndex();
        }

        public void remove() {
            iterator.remove();
        }

        public void set(Object o) {
            iterator.set(o);
        }

        public void add(Object o) {
            iterator.add(o);
        }
    }
}
