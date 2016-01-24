package jscl.math.function.hyperbolic;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.math.function.*;

import javax.annotation.Nonnull;

public class Acosh extends ArcTrigonometric {
    public Acosh(Generic generic) {
        super("acosh", new Generic[]{generic});
    }

    public Generic derivative(int n) {
        return new Inverse(
                new Sqrt(
                        parameters[0].pow(2).subtract(
                                JsclInteger.valueOf(1)
                        )
                ).selfExpand()
        ).selfExpand();
    }

    public Generic selfExpand() {
        if (parameters[0].signum() == 0) {
            return JsclInteger.valueOf(0);
        }
        return expressionValue();
    }

    public Generic selfElementary() {
        return new Ln(
                new Root(
                        new Generic[]{
                                JsclInteger.valueOf(-1),
                                JsclInteger.valueOf(2).multiply(parameters[0]),
                                JsclInteger.valueOf(-1)
                        },
                        0
                ).selfElementary()
        ).selfElementary();
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).acosh();
    }

    @Nonnull
    public Variable newInstance() {
        return new Acosh(null);
    }
}
