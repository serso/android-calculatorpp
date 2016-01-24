package jscl.math.function.hyperbolic;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.math.function.ArcTrigonometric;
import jscl.math.function.Inverse;
import jscl.math.function.Ln;
import jscl.math.function.Root;

import javax.annotation.Nonnull;

public class Acoth extends ArcTrigonometric {
    public Acoth(Generic generic) {
        super("acoth", new Generic[]{generic});
    }

    public Generic derivative(int n) {
        return new Inverse(
                JsclInteger.valueOf(1).subtract(
                        parameters[0].pow(2)
                )
        ).selfExpand();
    }

    public Generic selfExpand() {
        if (parameters[0].signum() < 0) {
            return new Acoth(parameters[0].negate()).selfExpand().negate();
        }
        return expressionValue();
    }

    public Generic selfElementary() {
        return new Ln(
                new Root(
                        new Generic[]{
                                JsclInteger.valueOf(1).add(parameters[0]),
                                JsclInteger.valueOf(0),
                                JsclInteger.valueOf(1).subtract(parameters[0])
                        },
                        0
                ).selfElementary()
        ).selfElementary();
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).acoth();
    }

    @Nonnull
    public Variable newInstance() {
        return new Acoth(null);
    }
}
