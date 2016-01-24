package jscl.math.function.hyperbolic;

import jscl.math.*;
import jscl.math.function.Fraction;
import jscl.math.function.Ln;
import jscl.math.function.Trigonometric;

import javax.annotation.Nonnull;

public class Tanh extends Trigonometric {
    public Tanh(Generic generic) {
        super("tanh", new Generic[]{generic});
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        return new Ln(
                JsclInteger.valueOf(4).multiply(
                        new Cosh(parameters[0]).selfExpand()
                )
        ).selfExpand();
    }

    public Generic derivative(int n) {
        return JsclInteger.valueOf(1).subtract(
                new Tanh(parameters[0]).selfExpand().pow(2)
        );
    }

    public Generic selfExpand() {
        if (parameters[0].signum() < 0) {
            return new Tanh(parameters[0].negate()).selfExpand().negate();
        } else if (parameters[0].signum() == 0) {
            return JsclInteger.valueOf(0);
        }
        return expressionValue();
    }

    public Generic selfElementary() {
        return new Fraction(
                new Sinh(parameters[0]).selfElementary(),
                new Cosh(parameters[0]).selfElementary()
        ).selfElementary();
    }

    public Generic selfSimplify() {
        if (parameters[0].signum() < 0) {
            return new Tanh(parameters[0].negate()).selfExpand().negate();
        } else if (parameters[0].signum() == 0) {
            return JsclInteger.valueOf(0);
        }
        try {
            Variable v = parameters[0].variableValue();
            if (v instanceof Atanh) {
                Generic g[] = ((Atanh) v).getParameters();
                return g[0];
            }
        } catch (NotVariableException e) {
        }
        return identity();
    }

    public Generic identity(Generic a, Generic b) {
        Generic ta = new Tanh(a).selfSimplify();
        Generic tb = new Tanh(b).selfSimplify();
        return new Fraction(
                ta.add(tb),
                JsclInteger.valueOf(1).add(
                        ta.multiply(tb)
                )
        ).selfSimplify();
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).tanh();
    }

    @Nonnull
    public Variable newInstance() {
        return new Tanh(null);
    }
}
