package jscl.math.function;

import jscl.math.*;

import javax.annotation.Nonnull;

public class Ln extends Function {

    public Ln(Generic generic) {
        super("ln", new Generic[]{generic});
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        return parameters[0].multiply(new Ln(parameters[0]).selfExpand().subtract(JsclInteger.ONE));
    }

    public Generic derivative(int n) {
        return new Inverse(parameters[0]).selfExpand();
    }

    public Generic selfExpand() {
        if (parameters[0].compareTo(JsclInteger.valueOf(1)) == 0) {
            return JsclInteger.valueOf(0);
        }
        return expressionValue();
    }

    public Generic selfElementary() {
        return selfExpand();
    }

    public Generic selfSimplify() {
        try {
            JsclInteger en = parameters[0].integerValue();
            if (en.signum() < 0) {
                return Constants.Generic.I_BY_PI.add(new Ln(en.negate()).selfSimplify());
            } else {
                Generic a = en.factorize();
                Generic p[] = a.productValue();
                Generic s = JsclInteger.valueOf(0);
                for (int i = 0; i < p.length; i++) {
                    Power o = p[i].powerValue();
                    s = s.add(JsclInteger.valueOf(o.exponent()).multiply(new Ln(o.value(true)).expressionValue()));
                }
                return s;
            }
        } catch (NotIntegerException e) {
        }
        try {
            Variable v = parameters[0].variableValue();
            if (v instanceof Sqrt) {
                Generic g[] = ((Sqrt) v).getParameters();
                return Constants.Generic.HALF.multiply(new Ln(g[0]).selfSimplify());
            }
        } catch (NotVariableException e) {
        }
        Generic n[] = Fraction.separateCoefficient(parameters[0]);
        if (n[0].compareTo(JsclInteger.valueOf(1)) == 0 && n[1].compareTo(JsclInteger.valueOf(1)) == 0) ;
        else return new Ln(n[2]).selfSimplify().add(
                new Ln(n[0]).selfSimplify()
        ).subtract(
                new Ln(n[1]).selfSimplify()
        );
        return expressionValue();
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).ln();
    }

    @Nonnull
    public Variable newInstance() {
        return new Ln(null);
    }
}
