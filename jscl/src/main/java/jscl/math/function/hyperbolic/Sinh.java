package jscl.math.function.hyperbolic;

import jscl.math.*;
import jscl.math.function.Constants;
import jscl.math.function.Exp;
import jscl.math.function.Trigonometric;

import javax.annotation.Nonnull;

public class Sinh extends Trigonometric {
    public Sinh(Generic generic) {
        super("sinh", new Generic[]{generic});
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        return new Cosh(parameters[0]).selfExpand();
    }

    public Generic derivative(int n) {
        return new Cosh(parameters[0]).selfExpand();
    }

    public Generic selfExpand() {
        if (parameters[0].signum() < 0) {
            return new Sinh(parameters[0].negate()).selfExpand().negate();
        } else if (parameters[0].signum() == 0) {
            return JsclInteger.valueOf(0);
        }
        return expressionValue();
    }

    public Generic selfElementary() {
        return new Exp(
                parameters[0]
        ).selfElementary().subtract(
                new Exp(
                        parameters[0].negate()
                ).selfElementary()
        ).multiply(Constants.Generic.HALF);
    }

    public Generic selfSimplify() {
        if (parameters[0].signum() < 0) {
            return new Sinh(parameters[0].negate()).selfExpand().negate();
        } else if (parameters[0].signum() == 0) {
            return JsclInteger.valueOf(0);
        }
        try {
            Variable v = parameters[0].variableValue();
            if (v instanceof Asinh) {
                Generic g[] = ((Asinh) v).getParameters();
                return g[0];
            }
        } catch (NotVariableException e) {
        }
        return identity();
    }

    public Generic identity(Generic a, Generic b) {
        return new Cosh(b).selfSimplify().multiply(
                new Sinh(a).selfSimplify()
        ).add(
                new Cosh(a).selfSimplify().multiply(
                        new Sinh(b).selfSimplify()
                )
        );
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).sinh();
    }

    @Nonnull
    public Variable newInstance() {
        return new Sinh(null);
    }
}
