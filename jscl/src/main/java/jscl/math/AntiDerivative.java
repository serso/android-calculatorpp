package jscl.math;

import jscl.math.function.*;
import jscl.math.polynomial.Monomial;
import jscl.math.polynomial.Polynomial;
import jscl.math.polynomial.UnivariatePolynomial;

import javax.annotation.Nonnull;

public class AntiDerivative {
    UnivariatePolynomial factory;
    PolynomialWithSyzygy syzygy;
    Generic result;

    AntiDerivative(Variable variable) {
        factory = (UnivariatePolynomial) Polynomial.factory(variable);
        syzygy = (PolynomialWithSyzygy) PolynomialWithSyzygy.factory(variable);
    }

    public static Generic compute(Fraction fraction, Variable variable) {
        AntiDerivative s = new AntiDerivative(variable);
        s.compute(fraction);
        return s.getValue();
    }

    public static Generic compute(Root root, Variable variable) throws NotIntegrableException {
        int d = root.degree();
        Generic a[] = root.getParameters();
        boolean b = d > 0;
        b = b && a[0].negate().isIdentity(variable);
        for (int i = 1; i < d; i++) b = b && a[i].signum() == 0;
        b = b && a[d].compareTo(JsclInteger.valueOf(1)) == 0;
        if (b) {
            return new Pow(
                    a[0].negate(),
                    new Inverse(JsclInteger.valueOf(d)).selfExpand()
            ).antiDerivative(0);
        } else {
            throw new NotIntegrableException();
        }
    }

    void compute(Fraction fraction) {
        Debug.println("antiDerivative");
        Debug.increment();
        Generic g[] = fraction.getParameters();
        Generic r[] = reduce(g[0], g[1]);
        r = divideAndRemainder(r[0], r[1]);
        Generic s = new Inverse(r[2]).selfExpand();
        Generic p = r[0].multiply(s);
        Generic a = r[1].multiply(s);
        result = p.antiDerivative(factory.variable()).add(hermite(a, g[1]));
        Debug.decrement();
    }

    Generic[] reduce(Generic n, Generic d) {
        Debug.println("reduce(" + n + ", " + d + ")");
        Polynomial pn = factory.valueOf(n);
        Polynomial pd = factory.valueOf(d);
        Polynomial gcd = pn.gcd(pd);
        return new Generic[]{
                pn.divide(gcd).genericValue(),
                pd.divide(gcd).genericValue()
        };
    }

    Generic[] divideAndRemainder(Generic n, Generic d) {
        Debug.println("divideAndRemainder(" + n + ", " + d + ")");
        Polynomial pn = syzygy.valueof(n, 0);
        Polynomial pd = syzygy.valueof(d, 1);
        PolynomialWithSyzygy pr = (PolynomialWithSyzygy) pn.remainderUpToCoefficient(pd);
        return new Generic[]{
                pr.syzygy[1].genericValue().negate(),
                pr.genericValue(),
                pr.syzygy[0].genericValue()
        };
    }

    Generic[] bezout(Generic a, Generic b) {
        Debug.println("bezout(" + a + ", " + b + ")");
        Polynomial pa = syzygy.valueof(a, 0);
        Polynomial pb = syzygy.valueof(b, 1);
        PolynomialWithSyzygy gcd = (PolynomialWithSyzygy) pa.gcd(pb);
        return new Generic[]{
                gcd.syzygy[0].genericValue(),
                gcd.syzygy[1].genericValue(),
                gcd.genericValue()
        };
    }

    Generic hermite(Generic a, Generic d) {
        Debug.println("hermite(" + a + ", " + d + ")");
        UnivariatePolynomial sd[] = ((UnivariatePolynomial) factory.valueOf(d)).squarefreeDecomposition();
        int m = sd.length - 1;
        if (m < 2) return trager(a, d);
        else {
            Generic u = sd[0].genericValue();
            for (int i = 1; i < m; i++) {
                u = u.multiply(sd[i].genericValue().pow(i));
            }
            Generic v = sd[m].genericValue();
            Generic vprime = sd[m].derivative().genericValue();
            Generic uvprime = u.multiply(vprime);
            Generic r[] = bezout(uvprime, v);
            Generic b = r[0].multiply(a);
            Generic c = r[1].multiply(a);
            Generic s = r[2];
            r = divideAndRemainder(b, v);
            b = r[1];
            c = c.multiply(r[2]).add(r[0].multiply(uvprime));
            s = new Inverse(s.multiply(r[2]).multiply(JsclInteger.valueOf(1 - m))).selfExpand();
            b = b.multiply(s);
            c = c.multiply(s);
            Generic bprime = ((UnivariatePolynomial) factory.valueOf(b)).derivative().genericValue();
            return new Fraction(b, v.pow(m - 1)).selfExpand().add(hermite(JsclInteger.valueOf(1 - m).multiply(c).subtract(u.multiply(bprime)), u.multiply(v.pow(m - 1))));
        }
    }

    Generic trager(Generic a, Generic d) {
        Debug.println("trager(" + a + ", " + d + ")");
        Variable t = new TechnicalVariable("t");
        UnivariatePolynomial pd = (UnivariatePolynomial) factory.valueOf(d);
        UnivariatePolynomial pa = (UnivariatePolynomial) factory.valueOf(a).subtract(pd.derivative().multiply(t.expressionValue()));
        UnivariatePolynomial rs[] = pd.remainderSequence(pa);
        Polynomial fact = UnivariatePolynomial.factory(t);
        for (int i = 0; i < rs.length; i++)
            if (rs[i] != null)
                rs[i] = (UnivariatePolynomial) fact.valueOf((i > 0 ? rs[i].normalize() : rs[i]).genericValue());
        UnivariatePolynomial q[] = rs[0].squarefreeDecomposition();
        int m = q.length - 1;
        Generic s = JsclInteger.valueOf(0);
        for (int i = 1; i <= m; i++) {
            for (int j = 0; j < q[i].degree(); j++) {
                Generic a2 = new Root(q[i], j).selfExpand();
                s = s.add(a2.multiply(new Ln(i == pd.degree() ? d : rs[i].substitute(a2)).selfExpand()));
            }
        }
        return s;
    }

    Generic getValue() {
        return result;
    }
}

class PolynomialWithSyzygy extends UnivariatePolynomial {
    Polynomial syzygy[] = new Polynomial[2];

    PolynomialWithSyzygy(Variable variable) {
        super(variable);
    }

    public static Polynomial factory(Variable variable) {
        return new PolynomialWithSyzygy(variable);
    }

    @Nonnull
    public Polynomial subtract(@Nonnull Polynomial that) {
        PolynomialWithSyzygy p2 = (PolynomialWithSyzygy) that;
        PolynomialWithSyzygy p = (PolynomialWithSyzygy) super.subtract(p2);
        for (int i = 0; i < syzygy.length; i++) p.syzygy[i] = syzygy[i].subtract(p2.syzygy[i]);
        return p;
    }

    public Polynomial multiply(Generic generic) {
        PolynomialWithSyzygy p = (PolynomialWithSyzygy) super.multiply(generic);
        for (int i = 0; i < syzygy.length; i++) p.syzygy[i] = syzygy[i].multiply(generic);
        return p;
    }

    public Polynomial multiply(Monomial monomial, Generic generic) {
        PolynomialWithSyzygy p = (PolynomialWithSyzygy) super.multiply(monomial, generic);
        for (int i = 0; i < syzygy.length; i++) p.syzygy[i] = syzygy[i].multiply(monomial).multiply(generic);
        return p;
    }

    public Polynomial divide(Generic generic) throws ArithmeticException {
        PolynomialWithSyzygy p = (PolynomialWithSyzygy) super.divide(generic);
        for (int i = 0; i < syzygy.length; i++) p.syzygy[i] = syzygy[i].divide(generic);
        return p;
    }

    public Polynomial remainderUpToCoefficient(Polynomial polynomial) throws ArithmeticException {
        PolynomialWithSyzygy p = this;
        PolynomialWithSyzygy q = (PolynomialWithSyzygy) polynomial;
        if (p.signum() == 0) return p;
        int d = p.degree();
        int d2 = q.degree();
        for (int i = d - d2; i >= 0; i--) {
            Generic c1 = p.get(i + d2);
            Generic c2 = q.get(d2);
            Generic c = c1.gcd(c2);
            c1 = c1.divide(c);
            c2 = c2.divide(c);
            p = (PolynomialWithSyzygy) p.multiply(c2).subtract(q.multiply(monomial(Literal.valueOf(variable, i)), c1)).normalize();
        }
        return p;
    }

    public Polynomial gcd(Polynomial polynomial) {
        Polynomial p = this;
        Polynomial q = polynomial;
        while (q.signum() != 0) {
            Polynomial r = p.remainderUpToCoefficient(q);
            p = q;
            q = r;
        }
        return p;
    }

    public Generic gcd() {
        Generic a = super.gcd();
        for (int i = 0; i < syzygy.length; i++) a = a.gcd(syzygy[i].gcd());
        return a.signum() == signum() ? a : a.negate();
    }

    public PolynomialWithSyzygy valueof(Generic generic, int n) {
        PolynomialWithSyzygy p = (PolynomialWithSyzygy) newinstance();
        p.init(generic, n);
        return p;
    }

    void init(Generic generic, int n) {
        init(generic);
        for (int i = 0; i < syzygy.length; i++)
            syzygy[i] = Polynomial.factory(variable).valueOf(JsclInteger.valueOf(i == n ? 1 : 0));
    }

    protected UnivariatePolynomial newinstance() {
        return new PolynomialWithSyzygy(variable);
    }
}
