package jscl.math.polynomial;

import jscl.math.NotDivisibleException;
import jscl.math.Variable;

class BooleanMonomial extends SmallMonomial {
    static final int log2n = 1;
    static final int log2p = 5 - log2n;
    static final int nmask = (1 << (1 << log2n)) - 1;
    static final int pmask = (1 << log2p) - 1;

    BooleanMonomial(Variable unknown[], Ordering ordering) {
        this(((unknown.length - 1) >> log2p) + 1, unknown, ordering);
    }

    BooleanMonomial(int length, Variable unknown[], Ordering ordering) {
        super(length, unknown, ordering);
    }

    public Monomial multiply(Monomial monomial) {
        Monomial m = newinstance();
        for (int i = 0; i < unknown.length; i++) {
            int q = i >> log2p;
            int r = (i & pmask) << log2n;
            int a = (element[q] >> r) & nmask;
            int b = (monomial.element[q] >> r) & nmask;
            int c = a + b;
            if (c > nmask) throw new ArithmeticException();
            m.element[q] |= c << r;
            m.degree += c;
        }
        return m;
    }

    public boolean multiple(Monomial monomial, boolean strict) {
        boolean equal = true;
        for (int i = 0; i < unknown.length; i++) {
            int q = i >> log2p;
            int r = (i & pmask) << log2n;
            int a = (element[q] >> r) & nmask;
            int b = (monomial.element[q] >> r) & nmask;
            if (a < b) return false;
            equal &= a == b;
        }
        return !strict || !equal;
    }

    public Monomial divide(Monomial monomial) throws ArithmeticException {
        Monomial m = newinstance();
        for (int i = 0; i < unknown.length; i++) {
            int q = i >> log2p;
            int r = (i & pmask) << log2n;
            int a = (element[q] >> r) & nmask;
            int b = (monomial.element[q] >> r) & nmask;
            int c = a - b;
            if (c < 0) throw new NotDivisibleException();
            m.element[q] |= c << r;
        }
        m.degree = degree - monomial.degree;
        return m;
    }

    public Monomial gcd(Monomial monomial) {
        Monomial m = newinstance();
        for (int i = 0; i < unknown.length; i++) {
            int q = i >> log2p;
            int r = (i & pmask) << log2n;
            int a = (element[q] >> r) & nmask;
            int b = (monomial.element[q] >> r) & nmask;
            int c = Math.min(a, b);
            m.element[q] |= c << r;
            m.degree += c;
        }
        return m;
    }

    public Monomial scm(Monomial monomial) {
        Monomial m = newinstance();
        for (int i = 0; i < unknown.length; i++) {
            int q = i >> log2p;
            int r = (i & pmask) << log2n;
            int a = (element[q] >> r) & nmask;
            int b = (monomial.element[q] >> r) & nmask;
            int c = Math.max(a, b);
            m.element[q] |= c << r;
            m.degree += c;
        }
        return m;
    }

    public int element(int n) {
        if (reverse()) n = unknown.length - 1 - n;
        int q = n >> log2p;
        int r = (n & pmask) << log2n;
        return (element[q] >> r) & nmask;
    }

    void put(int n, int integer) {
        if (reverse()) n = unknown.length - 1 - n;
        int q = n >> log2p;
        int r = (n & pmask) << log2n;
        int a = (element[q] >> r) & nmask;
        int c = a + integer;
        if (c > nmask) throw new ArithmeticException();
        element[q] |= c << r;
        degree += c - a;
    }

    protected Monomial newinstance() {
        return new BooleanMonomial(element.length, unknown, ordering);
    }
}
