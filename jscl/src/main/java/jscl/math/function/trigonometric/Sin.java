package jscl.math.function.trigonometric;

import jscl.math.*;
import jscl.math.function.Constants;
import jscl.math.function.Exp;
import jscl.math.function.Trigonometric;

import javax.annotation.Nonnull;

import static jscl.math.function.Constants.Generic.HALF;
import static jscl.math.function.Constants.Generic.I;

public class Sin extends Trigonometric {

    public Sin(Generic generic) {
        super("sin", new Generic[]{generic});
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        return new Cos(parameters[0]).selfExpand().negate();
    }

    public Generic derivative(int n) {
        return new Cos(parameters[0]).selfExpand();
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
        final Generic power = I.multiply(parameters[0]);
        final Generic e = new Exp(power).selfElementary().subtract(new Exp(I.multiply(parameters[0].negate())).selfElementary()).multiply(I.negate().multiply(HALF));
        return e;
    }

    public Generic selfSimplify() {
        final Generic result = trySimplify();

        if (result != null) {
            return result;
        } else {

            try {
                Variable v = parameters[0].variableValue();
                if (v instanceof Asin) {
                    Generic g[] = ((Asin) v).getParameters();
                    return g[0];
                }
            } catch (NotVariableException e) {
            }
            return identity();
        }
    }

    private Generic trySimplify() {
        Generic result = null;

        if (parameters[0].signum() < 0) {
            result = new Sin(parameters[0].negate()).selfExpand().negate();
        } else if (parameters[0].signum() == 0) {
            result = JsclInteger.valueOf(0);
        } else if (parameters[0].compareTo(Constants.Generic.PI) == 0) {
            result = JsclInteger.valueOf(0);
        }

        return result;
    }

    public Generic identity(Generic a, Generic b) {
        return new Cos(b).selfSimplify().multiply(
                new Sin(a).selfSimplify()
        ).add(
                new Cos(a).selfSimplify().multiply(
                        new Sin(b).selfSimplify()
                )
        );
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).sin();
    }

    @Nonnull
    public Variable newInstance() {
        return new Sin(null);
    }
}
