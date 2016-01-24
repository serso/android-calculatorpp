package jscl.math.function;

import jscl.math.*;
import jscl.math.polynomial.Polynomial;
import jscl.math.polynomial.UnivariatePolynomial;
import jscl.mathml.MathML;
import jscl.util.ArrayComparator;

import javax.annotation.Nonnull;

public class Root extends Algebraic {

    protected Generic subscript;

    public Root(Generic parameters[], Generic subscript) {
        super("root", parameters);
        this.subscript = subscript;
    }

    public Root(Generic parameters[], int s) {
        this(parameters, JsclInteger.valueOf(s));
    }

    public Root(@Nonnull UnivariatePolynomial polynomial, int s) {
        this(polynomial.normalize().elements(), s);
    }

    static Generic nth(Generic parameter[]) {
        int degree = parameter.length - 1;
        Generic a = new Fraction(parameter[0], parameter[degree]).selfSimplify();
        return new Pow(
                a.negate(),
                new Inverse(JsclInteger.valueOf(degree)).selfSimplify()
        ).selfSimplify();
    }

    static Generic linear(Generic parameter[]) {
        Generic a = new Fraction(parameter[0], parameter[1]).selfSimplify();
        return a.negate();
    }

    static Generic quadratic(Generic parameter[], int subscript) {
        Generic a = new Fraction(parameter[1], parameter[2]).selfSimplify();
        Generic b = new Fraction(parameter[0], parameter[2]).selfSimplify();
        Generic y = new Sqrt(
                a.pow(2).subtract(JsclInteger.valueOf(4).multiply(b))
        ).selfSimplify();
        switch (subscript) {
            case 0:
                return new Fraction(
                        a.subtract(y),
                        JsclInteger.valueOf(2)
                ).selfSimplify().negate();
            default:
                return new Fraction(
                        a.add(y),
                        JsclInteger.valueOf(2)
                ).selfSimplify().negate();
        }
    }

    static Generic cubic(Generic parameter[], int subscript) {
        Generic a = new Fraction(parameter[2], parameter[3]).selfSimplify();
        Generic b = new Fraction(parameter[1], parameter[3]).selfSimplify();
        Generic c = new Fraction(parameter[0], parameter[3]).selfSimplify();
        Generic y[] = new Generic[2];
        for (int i = 0; i < y.length; i++) {
            y[i] = new Cubic(
                    new Root(
                            new Generic[]{
                                    a.pow(6).subtract(JsclInteger.valueOf(9).multiply(a.pow(4)).multiply(b)).add(JsclInteger.valueOf(27).multiply(a.pow(2)).multiply(b.pow(2))).subtract(JsclInteger.valueOf(27).multiply(b.pow(3))),
                                    JsclInteger.valueOf(2).multiply(a.pow(3)).subtract(JsclInteger.valueOf(9).multiply(a).multiply(b)).add(JsclInteger.valueOf(27).multiply(c)),
                                    JsclInteger.valueOf(1)
                            },
                            i
                    ).selfSimplify()
            ).selfSimplify();
        }
        switch (subscript) {
            case 0:
                return new Fraction(
                        a.subtract(y[0]).subtract(y[1]),
                        JsclInteger.valueOf(3)
                ).selfSimplify().negate();
            case 1:
                return new Fraction(
                        a.subtract(Constants.Generic.J.multiply(y[0])).subtract(Constants.Generic.J_BAR.multiply(y[1])),
                        JsclInteger.valueOf(3)
                ).selfSimplify().negate();
            default:
                return new Fraction(
                        a.subtract(Constants.Generic.J_BAR.multiply(y[0])).subtract(Constants.Generic.J.multiply(y[1])),
                        JsclInteger.valueOf(3)
                ).selfSimplify().negate();
        }
    }

    static Generic quartic(Generic parameter[], int subscript) {
        Generic a = new Fraction(parameter[3], parameter[4]).selfSimplify();
        Generic b = new Fraction(parameter[2], parameter[4]).selfSimplify();
        Generic c = new Fraction(parameter[1], parameter[4]).selfSimplify();
        Generic d = new Fraction(parameter[0], parameter[4]).selfSimplify();
        Generic y[] = new Generic[3];
        for (int i = 0; i < y.length; i++) {
            y[i] = new Sqrt(
                    new Root(
                            new Generic[]{
                                    a.pow(6).subtract(JsclInteger.valueOf(8).multiply(a.pow(4)).multiply(b)).add(JsclInteger.valueOf(16).multiply(a.pow(2)).multiply(b.pow(2))).add(JsclInteger.valueOf(16).multiply(a.pow(3)).multiply(c)).subtract(JsclInteger.valueOf(64).multiply(a).multiply(b).multiply(c)).add(JsclInteger.valueOf(64).multiply(c.pow(2))),
                                    JsclInteger.valueOf(-3).multiply(a.pow(4)).add(JsclInteger.valueOf(16).multiply(a.pow(2)).multiply(b)).subtract(JsclInteger.valueOf(16).multiply(b.pow(2))).subtract(JsclInteger.valueOf(16).multiply(a).multiply(c)).add(JsclInteger.valueOf(64).multiply(d)),
                                    JsclInteger.valueOf(3).multiply(a.pow(2)).subtract(JsclInteger.valueOf(8).multiply(b)),
                                    JsclInteger.valueOf(-1)
                            },
                            i
                    ).selfSimplify()
            ).selfSimplify();
        }
        switch (subscript) {
            case 0:
                return new Fraction(
                        a.add(y[0]).subtract(y[1]).subtract(y[2]),
                        JsclInteger.valueOf(4)
                ).selfSimplify().negate();
            case 1:
                return new Fraction(
                        a.subtract(y[0]).subtract(y[1]).add(y[2]),
                        JsclInteger.valueOf(4)
                ).selfSimplify().negate();
            case 2:
                return new Fraction(
                        a.add(y[0]).add(y[1]).add(y[2]),
                        JsclInteger.valueOf(4)
                ).selfSimplify().negate();
            default:
                return new Fraction(
                        a.subtract(y[0]).add(y[1]).subtract(y[2]),
                        JsclInteger.valueOf(4)
                ).selfSimplify().negate();
        }
    }

    public static Generic sigma(Generic parameter[], int n) {
        Sigma s = new Sigma(parameter, n);
        s.compute();
        return s.getValue();
    }

    @Override
    public int getMaxParameters() {
        return Integer.MAX_VALUE;
    }

    public Generic subscript() {
        return subscript;
    }

    public Root rootValue() {
        return this;
    }

    public Generic antiDerivative(@Nonnull Variable variable) throws NotIntegrableException {
        boolean polynomial = true;
        for (Generic parameter : parameters) {
            polynomial = parameter.isPolynomial(variable);
            if (!polynomial) {
                break;
            }
        }

        if (polynomial) {
            return AntiDerivative.compute(this, variable);
        } else {
            throw new NotIntegrableException(this);
        }
    }

    @Nonnull
    public Generic derivative(@Nonnull Variable variable) {
        if (compareTo(variable) == 0) {
            return JsclInteger.valueOf(1);
        } else {
            Variable t = new TechnicalVariable("t");
            Generic a[] = new Generic[parameters.length];
            for (int i = 0; i < parameters.length; i++) a[i] = parameters[i].derivative(variable);
            UnivariatePolynomial fact = (UnivariatePolynomial) Polynomial.factory(this);
            UnivariatePolynomial p = fact.valueof(parameters);
            UnivariatePolynomial q = (UnivariatePolynomial) p.derivative().multiply(t.expressionValue()).add(fact.valueof(a));
            UnivariatePolynomial r = (UnivariatePolynomial) Polynomial.factory(t).valueOf(p.resultant(q));
            return new Root(r.elements(), subscript).selfExpand();
        }
    }

    public Generic derivative(int n) {
        return null;
    }

    public Generic substitute(@Nonnull Variable variable, @Nonnull Generic generic) {
        Root v = (Root) newInstance();
        for (int i = 0; i < parameters.length; i++) {
            v.parameters[i] = parameters[i].substitute(variable, generic);
        }
        v.subscript = subscript.substitute(variable, generic);
        if (v.isIdentity(variable)) return generic;
        else return v.selfExpand();
    }

    public Generic expand() {
        Root v = (Root) newInstance();
        for (int i = 0; i < parameters.length; i++) {
            v.parameters[i] = parameters[i].expand();
        }
        v.subscript = subscript.expand();
        return v.selfExpand();
    }

    public Generic factorize() {
        Root v = (Root) newInstance();
        for (int i = 0; i < parameters.length; i++) {
            v.parameters[i] = parameters[i].factorize();
        }
        v.subscript = subscript;
        return v.expressionValue();
    }

    public Generic elementary() {
        Root v = (Root) newInstance();
        for (int i = 0; i < parameters.length; i++) {
            v.parameters[i] = parameters[i].elementary();
        }
        v.subscript = subscript.elementary();
        return v.selfElementary();
    }

    public Generic simplify() {
        Root v = (Root) newInstance();
        for (int i = 0; i < parameters.length; i++) {
            v.parameters[i] = parameters[i].simplify();
        }
        v.subscript = subscript.simplify();
        return v.selfSimplify();
    }

    public Generic numeric() {
        Root v = (Root) newInstance();
        for (int i = 0; i < parameters.length; i++) {
            v.parameters[i] = parameters[i].numeric();
        }
        v.subscript = subscript;
        return v.selfNumeric();
    }

    public Generic selfExpand() {
        if (isZero()) return JsclInteger.valueOf(0);
        try {
            int s = subscript.integerValue().intValue();
            switch (degree()) {
                case 1:
                    return new Fraction(parameters[0], parameters[1]).selfExpand().negate();
            }
        } catch (NotIntegerException e) {
        }
        return expressionValue();
    }

    public Generic selfElementary() {
        return selfExpand();
    }

    public Generic selfSimplify() {
        if (isZero()) return JsclInteger.valueOf(0);
        try {
            int s = subscript.integerValue().intValue();
            switch (degree()) {
                case 1:
                    return linear(parameters);
                case 2:
                    return quadratic(parameters, s);
                case 3:
                    return cubic(parameters, s);
                case 4:
                    return quartic(parameters, s);
                default:
                    if (isNth() && s == 0) return nth(parameters);
            }
        } catch (NotIntegerException e) {
        }
        return expressionValue();
    }

    boolean isZero() {
        boolean b = degree() > 0;
        for (int i = 0; i < degree(); i++) b = b && parameters[i].signum() == 0;
        b = b && parameters[degree()].signum() != 0;
        return b;
    }

    boolean isNth() {
        boolean b = degree() > 0;
        for (int i = 1; i < degree(); i++) b = b && parameters[i].signum() == 0;
        b = b && parameters[degree()].signum() != 0;
        return b;
    }

    public int degree() {
        return parameters.length - 1;
    }

    public Generic selfNumeric() {
        return NumericWrapper.root(subscript.integerValue().intValue(), parameters);
    }

    public int compareTo(Variable that) {
        if (this == that) return 0;
        int c = comparator.compare(this, that);
        if (c < 0) return -1;
        else if (c > 0) return 1;
        else {
            Root v = (Root) that;
            c = ArrayComparator.comparator.compare(parameters, v.parameters);
            if (c < 0) return -1;
            else if (c > 0) return 1;
            else return subscript.compareTo(v.subscript);
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(name);
        buffer.append("[").append(subscript).append("]");
        buffer.append("(");
        for (int i = 0; i < parameters.length; i++) {
            buffer.append(parameters[i]).append(i < parameters.length - 1 ? ", " : "");
        }
        buffer.append(")");
        return buffer.toString();
    }

    public String toJava() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Numeric.").append(name).append("(");
        buffer.append(subscript.integerValue().intValue());
        buffer.append(", new Numeric[] {");
        for (int i = 0; i < parameters.length; i++) {
            buffer.append(parameters[i].toJava()).append(i < parameters.length - 1 ? ", " : "");
        }
        buffer.append("})");
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        MathML e1;
        int exponent = data instanceof Integer ? ((Integer) data).intValue() : 1;
        if (exponent == 1) {
            e1 = element.element("msub");
            nameToMathML(e1);
            subscript.toMathML(e1, null);
            element.appendChild(e1);
        } else {
            e1 = element.element("msubsup");
            nameToMathML(e1);
            subscript.toMathML(e1, null);
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
        e1 = element.element("mfenced");
        for (int i = 0; i < parameters.length; i++) {
            parameters[i].toMathML(e1, null);
        }
        element.appendChild(e1);
    }

    void bodyToMathML(MathML element, boolean fenced) {
    }

    @Nonnull
    public Variable newInstance() {
        return new Root(new Generic[parameters.length], null);
    }
}

class Sigma {
    Generic root[];
    Generic generic;
    boolean place[];
    int n;

    Sigma(Generic parameter[], int n) {
        root = new Generic[parameter.length - 1];
        for (int i = 0; i < root.length; i++) root[i] = new Root(parameter, i).expressionValue();
        place = new boolean[root.length];
        this.n = n;
    }

    void compute() {
        generic = JsclInteger.valueOf(0);
        compute(0, n);
    }

    void compute(int p, int nn) {
        if (nn > 0) {
            for (int i = p; i < root.length; i++) {
                place[i] = true;
                compute(i + 1, nn - 1);
                place[i] = false;
            }
        } else {
            Generic s = JsclInteger.valueOf(1);
            for (int i = 0; i < root.length; i++) {
                if (place[i]) s = s.multiply(root[i]);
            }
            generic = generic.add(s);
        }
    }

    Generic getValue() {
        return generic;
    }
}
