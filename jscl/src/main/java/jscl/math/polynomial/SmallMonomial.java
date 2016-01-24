package jscl.math.polynomial;

import jscl.math.NotDivisibleException;
import jscl.math.Variable;

class SmallMonomial extends Monomial {
    static final Ordering lexicographic = SmallLexicographic.ordering;
    static final Ordering totalDegreeLexicographic = SmallTotalDegreeLexicographic.ordering;
    static final Ordering degreeReverseLexicographic = SmallDegreeReverseLexicographic.ordering;
    static final int log2n = 3;
    static final int log2p = 5 - log2n;
    static final int nmask = (1 << (1 << log2n)) - 1;
    static final int pmask = (1 << log2p) - 1;

    SmallMonomial(Variable unknown[], Ordering ordering) {
        this(((unknown.length - 1) >> log2p) + 1, unknown, ordering);
    }

    SmallMonomial(int length, Variable unknown[], Ordering ordering) {
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
        return strict ? !equal : true;
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

    boolean reverse() {
        return ordering instanceof DegreeReverseLexicographic;
    }

    protected Monomial newinstance() {
        return new SmallMonomial(element.length, unknown, ordering);
    }
}

class SmallLexicographic extends Ordering {
    public static final Ordering ordering = new SmallLexicographic();

    SmallLexicographic() {
    }

    public int compare(Monomial m1, Monomial m2) {
        int c1[] = m1.element;
        int c2[] = m2.element;
        int n = c1.length;
        for (int i = n - 1; i >= 0; i--) {
            long l1 = c1[i] & 0xffffffffl;
            long l2 = c2[i] & 0xffffffffl;
            if (l1 < l2) return -1;
            else if (l1 > l2) return 1;
        }
        return 0;
    }
}

class SmallTotalDegreeLexicographic extends SmallLexicographic implements DegreeOrdering {
    public static final Ordering ordering = new SmallTotalDegreeLexicographic();

    SmallTotalDegreeLexicographic() {
    }


    public int compare(Monomial m1, Monomial m2) {
        if (m1.degree < m2.degree) return -1;
        else if (m1.degree > m2.degree) return 1;
        else return super.compare(m1, m2);
    }
}

class SmallDegreeReverseLexicographic extends Ordering implements DegreeOrdering {
    public static final Ordering ordering = new SmallDegreeReverseLexicographic();

    SmallDegreeReverseLexicographic() {
    }

    public int compare(Monomial m1, Monomial m2) {
        if (m1.degree < m2.degree) return -1;
        else if (m1.degree > m2.degree) return 1;
        else {
            int c1[] = m1.element;
            int c2[] = m2.element;
            int n = c1.length;
            for (int i = n - 1; i >= 0; i--) {
                long l1 = c1[i] & 0xffffffffl;
                long l2 = c2[i] & 0xffffffffl;
                if (l1 > l2) return -1;
                else if (l1 < l2) return 1;
            }
            return 0;
        }
    }
}
