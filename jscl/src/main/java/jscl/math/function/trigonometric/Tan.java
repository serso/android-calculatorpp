package jscl.math.function.trigonometric;

import jscl.math.*;
import jscl.math.function.Constants;
import jscl.math.function.Fraction;
import jscl.math.function.Ln;
import jscl.math.function.Trigonometric;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Tan extends Trigonometric {
    public Tan(Generic generic) {
        super("tan", new Generic[]{generic});
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        return new Ln(
                JsclInteger.valueOf(4).multiply(
                        new Cos(parameters[0]).selfExpand()
                )
        ).selfExpand().negate();
    }

    public Generic derivative(int n) {
        return JsclInteger.valueOf(1).add(
                new Tan(parameters[0]).selfExpand().pow(2)
        );
    }

    public Generic selfExpand() {
        final Generic result = trySimplify();

        if (result != null) {
            return result;
        } else {
            return expressionValue();
        }
    }

    public Generic selfElementary() {
        return new Fraction(
                new Sin(parameters[0]).selfElementary(),
                new Cos(parameters[0]).selfElementary()
        ).selfElementary();
    }

    public Generic selfSimplify() {
        final Generic result = trySimplify();

        if (result != null) {
            return result;
        } else {

            try {
                Variable v = parameters[0].variableValue();
                if (v instanceof Atan) {
                    Generic g[] = ((Atan) v).getParameters();
                    return g[0];
                }
            } catch (NotVariableException e) {
                // ok
            }

            return identity();
        }
    }

    @Nullable
    private Generic trySimplify() {
        Generic result = null;

        if (parameters[0].signum() < 0) {
            result = new Tan(parameters[0].negate()).selfExpand().negate();
        } else if (parameters[0].signum() == 0) {
            result = JsclInteger.valueOf(0);
        } else if (parameters[0].compareTo(Constants.Generic.PI) == 0) {
            result = JsclInteger.valueOf(0);
        }

        return result;
    }

    public Generic identity(Generic a, Generic b) {
        Generic ta = new Tan(a).selfSimplify();
        Generic tb = new Tan(b).selfSimplify();
        return new Fraction(
                ta.add(tb),
                JsclInteger.valueOf(1).subtract(
                        ta.multiply(tb)
                )
        ).selfSimplify();
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).tan();
    }

    @Nonnull
    public Variable newInstance() {
        return new Tan(null);
    }
}
