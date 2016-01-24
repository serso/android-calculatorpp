package jscl.math.function;

import jscl.math.*;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

import static jscl.math.JsclInteger.ONE;
import static jscl.math.JsclInteger.ZERO;

public class Pow extends Algebraic {

    private static final int MAX_ARRAY_SIZE = 10000;

    public Pow(Generic generic, Generic exponent) {
        super("pow", new Generic[]{generic, exponent});
    }

    static Generic root_minus_1(int d) {
        switch (d) {
            case 1:
                return JsclInteger.valueOf(-1);
            case 2:
                return Constants.Generic.I;
            case 3:
                return Constants.Generic.J_BAR.negate();
            case 4:
                return new Sqrt(Constants.Generic.HALF).expressionValue().multiply(JsclInteger.valueOf(1).add(Constants.Generic.I));
            case 6:
                return Constants.Generic.HALF.multiply(new Sqrt(JsclInteger.valueOf(3)).expressionValue().add(Constants.Generic.I));
            default:
                return null;
        }
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    public Root rootValue() throws NotRootException {
        try {
            Variable v = parameters[1].variableValue();
            if (v instanceof Inverse) {
                Generic g = ((Inverse) v).parameter();
                try {
                    int d = g.integerValue().intValue();
                    if (d > 0 && d < MAX_ARRAY_SIZE) {
                        Generic a[] = new Generic[d + 1];
                        a[0] = parameters[0].negate();
                        for (int i = 1; i < d; i++) {
                            a[i] = ZERO;
                        }
                        a[d] = ONE;
                        return new Root(a, 0);
                    }
                } catch (NotIntegerException e) {
                }
            }
        } catch (NotVariableException e) {
        }
        throw new NotRootException();
    }

    public Generic antiDerivative(@Nonnull Variable variable) throws NotIntegrableException {
        try {
            Root r = rootValue();
            Generic g[] = r.getParameters();
            if (g[0].isPolynomial(variable)) {
                return AntiDerivative.compute(r, variable);
            } else throw new NotIntegrableException(this);
        } catch (NotRootException e) {
        }
        return super.antiDerivative(variable);
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        if (n == 0) {
            return new Pow(parameters[0], parameters[1].add(JsclInteger.valueOf(1))).selfExpand().multiply(new Inverse(parameters[1].add(JsclInteger.valueOf(1))).selfExpand());
        } else {
            return new Pow(parameters[0], parameters[1]).selfExpand().multiply(new Inverse(new Ln(parameters[0]).selfExpand()).selfExpand());
        }
    }

    public Generic derivative(int n) {
        if (n == 0) {
            return new Pow(parameters[0], parameters[1].subtract(JsclInteger.valueOf(1))).selfExpand().multiply(parameters[1]);
        } else {
            return new Pow(parameters[0], parameters[1]).selfExpand().multiply(new Ln(parameters[0]).selfExpand());
        }
    }

    public Generic selfExpand() {
        if (parameters[0].compareTo(JsclInteger.valueOf(1)) == 0) {
            return JsclInteger.valueOf(1);
        }
        if (parameters[1].signum() < 0) {
            return new Pow(new Inverse(parameters[0]).selfExpand(), parameters[1].negate()).selfExpand();
        }
        try {
            int c = parameters[1].integerValue().intValue();
            return parameters[0].pow(c);
        } catch (NotIntegerException e) {
        }
        try {
            Root r = rootValue();
            int d = r.degree();
            Generic g[] = r.getParameters();
            Generic a = g[0].negate();
            try {
                JsclInteger en = a.integerValue();
                if (en.signum() < 0) ;
                else {
                    Generic rt = en.nthrt(d);
                    if (rt.pow(d).compareTo(en) == 0) return rt;
                }
            } catch (NotIntegerException e) {
            }
        } catch (NotRootException e) {
        }
        return expressionValue();
    }

    public Generic selfElementary() {
        return new Exp(
                new Ln(
                        parameters[0]
                ).selfElementary().multiply(
                        parameters[1]
                )
        ).selfElementary();
    }

    public Generic selfSimplify() {
        // a ^ b

        // a = 1 => for any b: 1 ^ b = 1
        if (parameters[0].compareTo(ONE) == 0) {
            return JsclInteger.valueOf(1);
        }

        // b < 0 => a ^ b = (1 / a) ^ (-b)
        if (parameters[1].signum() < 0) {
            return new Pow(new Inverse(parameters[0]).selfSimplify(), parameters[1].negate()).selfSimplify();
        }

        try {
            // if b is integer => just calculate the result
            int intPower = parameters[1].integerValue().intValue();
            return parameters[0].pow(intPower);
        } catch (NotIntegerException e) {
        }

        try {
            Root r = rootValue();
            int d = r.degree();
            Generic g[] = r.getParameters();
            Generic a = g[0].negate();
            try {
                JsclInteger en = a.integerValue();
                if (en.signum() < 0) ;
                else {
                    Generic rt = en.nthrt(d);
                    if (rt.pow(d).compareTo(en) == 0) return rt;
                }
            } catch (NotIntegerException e) {
            }
            switch (d) {
                case 2:
                    return new Sqrt(a).selfSimplify();
                case 3:
                case 4:
                case 6:
                    if (a.compareTo(JsclInteger.valueOf(-1)) == 0) return root_minus_1(d);
            }
        } catch (NotRootException e) {
            Generic n[] = Fraction.separateCoefficient(parameters[1]);
            if (n[0].compareTo(ONE) == 0 && n[1].compareTo(ONE) == 0) {
                // do nothing
            } else {
                return new Pow(
                        new Pow(
                                new Pow(
                                        parameters[0],
                                        n[2]
                                ).selfSimplify(),
                                new Inverse(
                                        n[1]
                                ).selfSimplify()
                        ).selfSimplify(),
                        n[0]
                ).selfSimplify();
            }
        }
        return expressionValue();
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).pow((NumericWrapper) parameters[1]);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        try {
            JsclInteger en = parameters[0].integerValue();
            if (en.signum() < 0) buffer.append(GenericVariable.valueOf(en, true));
            else buffer.append(en);
        } catch (NotIntegerException e) {
            try {
                Variable v = parameters[0].variableValue();
                if (v instanceof Fraction || v instanceof Pow) {
                    buffer.append(GenericVariable.valueOf(parameters[0]));
                } else buffer.append(v);
            } catch (NotVariableException e2) {
                try {
                    Power o = parameters[0].powerValue();
                    if (o.exponent() == 1) buffer.append(o.value(true));
                    else buffer.append(GenericVariable.valueOf(parameters[0]));
                } catch (NotPowerException e3) {
                    buffer.append(GenericVariable.valueOf(parameters[0]));
                }
            }
        }
        buffer.append("^");
        try {
            JsclInteger en = parameters[1].integerValue();
            buffer.append(en);
        } catch (NotIntegerException e) {
            try {
                Variable v = parameters[1].variableValue();
                if (v instanceof Fraction) {
                    buffer.append(GenericVariable.valueOf(parameters[1]));
                } else buffer.append(v);
            } catch (NotVariableException e2) {
                try {
                    parameters[1].powerValue();
                    buffer.append(parameters[1]);
                } catch (NotPowerException e3) {
                    buffer.append(GenericVariable.valueOf(parameters[1]));
                }
            }
        }
        return buffer.toString();
    }

    public String toJava() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(parameters[0].toJava());
        buffer.append(".pow(");
        buffer.append(parameters[1].toJava());
        buffer.append(")");
        return buffer.toString();
    }

    void bodyToMathML(MathML element, boolean fenced) {
        if (fenced) {
            MathML e1 = element.element("mfenced");
            bodyToMathML(e1);
            element.appendChild(e1);
        } else {
            bodyToMathML(element);
        }
    }

    void bodyToMathML(MathML element) {
        MathML e1 = element.element("msup");
        try {
            Variable v = parameters[0].variableValue();
            if (v instanceof Fraction || v instanceof Pow || v instanceof Exp) {
                GenericVariable.valueOf(parameters[0]).toMathML(e1, null);
            } else parameters[0].toMathML(e1, null);
        } catch (NotVariableException e2) {
            try {
                Power o = parameters[0].powerValue();
                if (o.exponent() == 1) o.value(true).toMathML(e1, null);
                else GenericVariable.valueOf(parameters[0]).toMathML(e1, null);
            } catch (NotPowerException e3) {
                GenericVariable.valueOf(parameters[0]).toMathML(e1, null);
            }
        }
        parameters[1].toMathML(e1, null);
        element.appendChild(e1);
    }

    @Nonnull
    public Variable newInstance() {
        return new Pow(null, null);
    }
}
