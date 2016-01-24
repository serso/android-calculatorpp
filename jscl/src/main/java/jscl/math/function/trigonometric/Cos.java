package jscl.math.function.trigonometric;

import jscl.math.*;
import jscl.math.function.Constants;
import jscl.math.function.Exp;
import jscl.math.function.Trigonometric;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Cos extends Trigonometric {
    public Cos(Generic generic) {
        super("cos", new Generic[]{generic});
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        return new Sin(parameters[0]).selfExpand();
    }

    public Generic derivative(int n) {
        return new Sin(parameters[0]).selfExpand().negate();
    }

    public Generic selfExpand() {
        final Generic result = trySimplify();

        if (result != null) {
            return result;
        } else {
            return expressionValue();
        }
    }

    @Nullable
    private Generic trySimplify() {
        Generic result = null;

        if (parameters[0].signum() < 0) {
            result = new Cos(parameters[0].negate()).selfExpand();
        } else if (parameters[0].signum() == 0) {
            result = JsclInteger.valueOf(1);
        } else if (parameters[0].compareTo(Constants.Generic.PI) == 0) {
            result = JsclInteger.valueOf(-1);
        }

        return result;
    }

    public Generic selfElementary() {
        return new Exp(
                Constants.Generic.I.multiply(parameters[0])
        ).selfElementary().add(
                new Exp(
                        Constants.Generic.I.multiply(parameters[0].negate())
                ).selfElementary()
        ).multiply(Constants.Generic.HALF);
    }

    public Generic selfSimplify() {
        final Generic result = trySimplify();

        if (result != null) {
            return result;
        } else {

            try {
                Variable v = parameters[0].variableValue();
                if (v instanceof Acos) {
                    Generic g[] = ((Acos) v).getParameters();
                    return g[0];
                }
            } catch (NotVariableException e) {
            }
            return identity();
        }
    }

    public Generic identity(Generic a, Generic b) {
        return new Cos(a).selfSimplify().multiply(
                new Cos(b).selfSimplify()
        ).subtract(
                new Sin(a).selfSimplify().multiply(
                        new Sin(b).selfSimplify()
                )
        );
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).cos();
    }

    @Nonnull
    public Variable newInstance() {
        return new Cos(null);
    }
}
