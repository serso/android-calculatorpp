package jscl.math.function;

import jscl.math.*;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Sqrt extends Algebraic {

    public Sqrt(Generic parameter) {
        super("√", new Generic[]{parameter});
    }

    public Root rootValue() {
        return new Root(new Generic[]{parameters[0].negate(), JsclInteger.valueOf(0), JsclInteger.valueOf(1)}, 0);
    }

    public Generic antiDerivative(@Nonnull Variable variable) throws NotIntegrableException {
        Root r = rootValue();
        Generic g[] = r.getParameters();
        if (g[0].isPolynomial(variable)) {
            return AntiDerivative.compute(r, variable);
        } else {
            throw new NotIntegrableException(this);
        }
    }

    public Generic derivative(int n) {
        return Constants.Generic.HALF.multiply(new Inverse(selfExpand()).selfExpand());
    }

    public boolean imaginary() {
        return parameters[0] != null && parameters[0].compareTo(JsclInteger.valueOf(-1)) == 0;
    }

    public Generic selfExpand() {
        Generic result;

        try {
            final JsclInteger p = parameters[0].integerValue();
            if (p.signum() < 0) {
                // result will be complex => evaluate
                result = expressionValue();
            } else {
                final Generic sqrt = p.sqrt();
                if (sqrt.pow(2).compareTo(p) == 0) {
                    result = sqrt;
                } else {
                    result = expressionValue();
                }
            }
        } catch (NotIntegerException e) {
            result = expressionValue();
        }

        return result;
    }

    public Generic selfElementary() {
        return selfExpand();
    }

    public Generic selfSimplify() {
        Generic result = null;
        try {
            final JsclInteger p = parameters[0].integerValue();
            if (p.signum() < 0) {
                return Constants.Generic.I.multiply(new Sqrt(p.negate()).selfSimplify());
            } else {
                final Generic sqrt = p.sqrt();
                if (sqrt.pow(2).compareTo(p) == 0) {
                    return sqrt;
                }
            }
            result = simplify0(p);
        } catch (NotIntegerException e) {
            result = simplify0(parameters[0]);
        }

        if (result == null) {
            return expressionValue();
        } else {
            return result;
        }
    }

    @Nullable
    private Generic simplifyFractions() {
        final Generic n[] = Fraction.separateCoefficient(parameters[0]);

        if (n[0].compareTo(JsclInteger.valueOf(1)) != 0 || n[1].compareTo(JsclInteger.valueOf(1)) != 0) {
            // n
            final Generic numerator = new Sqrt(n[0]).selfSimplify();
            // d
            final Generic denominator = new Sqrt(n[1]).selfSimplify();
            // fraction = n / d
            final Generic fraction = new Fraction(numerator, denominator).selfSimplify();
            return new Sqrt(n[2]).selfSimplify().multiply(fraction);
        }

        return null;
    }

    @Nullable
    private Generic simplify0(@Nonnull Generic generic) {
        Generic result;

        try {
            // let's try to present sqrt expression as products
            final Generic products[] = generic.factorize().productValue();

            result = JsclInteger.valueOf(1);
            for (Generic product : products) {
                // and try sqrt for each product
                final Power power = product.powerValue();
                Generic q = power.value(true);
                int c = power.exponent();
                result = result.multiply(q.pow(c / 2).multiply(new Sqrt(q).expressionValue().pow(c % 2)));
            }
        } catch (NotPowerException e) {
            result = simplifyFractions();
        } catch (NotProductException e) {
            result = simplifyFractions();
        }

        return result;
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).sqrt();
    }

    public String toJava() {
        if (parameters[0].compareTo(JsclInteger.valueOf(-1)) == 0) {
            return "Complex.valueOf(0, 1)";
        } else {
            final StringBuilder result = new StringBuilder();
            result.append(parameters[0].toJava());
            result.append(".").append(name).append("()");
            return result.toString();
        }
    }

    @Override
    public String toString() {
        final Generic parameter = parameters[0];
        if (parameter != null) {
            try {
                if (JsclInteger.ONE.negate().equals(parameter.integerValue())) {
                    return Constants.I.getName();
                } else {
                    return super.toString();
                }
            } catch (NotIntegerException e) {
                return super.toString();
            }
        } else {
            return super.toString();
        }
    }

    void bodyToMathML(MathML element, boolean fenced) {
        if (parameters[0].compareTo(JsclInteger.valueOf(-1)) == 0) {
            MathML e1 = element.element("mi");
            e1.appendChild(element.text(/*"\u2148"*/"i"));
            element.appendChild(e1);
        } else {
            MathML e1 = element.element("msqrt");
            parameters[0].toMathML(e1, null);
            element.appendChild(e1);
        }
    }

    @Nonnull
    public Variable newInstance() {
        return new Sqrt(null);
    }
}
