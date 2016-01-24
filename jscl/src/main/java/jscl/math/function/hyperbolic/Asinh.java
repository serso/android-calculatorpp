package jscl.math.function.hyperbolic;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.math.function.*;

import javax.annotation.Nonnull;

public class Asinh extends ArcTrigonometric {
    public Asinh(Generic generic) {
        super("asinh", new Generic[]{generic});
    }

    public Generic derivative(int n) {
        return new Inverse(
                new Sqrt(
                        JsclInteger.valueOf(1).add(
                                parameters[0].pow(2)
                        )
                ).selfExpand()
        ).selfExpand();
    }

    public Generic selfExpand() {
        if (parameters[0].signum() < 0) {
            return new Asinh(parameters[0].negate()).selfExpand().negate();
        } else if (parameters[0].signum() == 0) {
            return JsclInteger.valueOf(0);
        }
        return expressionValue();
    }

    public Generic selfElementary() {
        return new Ln(
                new Root(
                        new Generic[]{
                                JsclInteger.valueOf(1),
                                JsclInteger.valueOf(2).multiply(parameters[0]),
                                JsclInteger.valueOf(-1)
                        },
                        0
                ).selfElementary()
        ).selfElementary();
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).asinh();
    }

    @Nonnull
    public Variable newInstance() {
        return new Asinh(null);
    }
}
