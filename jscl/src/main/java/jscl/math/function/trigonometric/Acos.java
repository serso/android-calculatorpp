package jscl.math.function.trigonometric;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.math.function.*;

import javax.annotation.Nonnull;

public class Acos extends ArcTrigonometric {

    public Acos(Generic generic) {
        super("acos", new Generic[]{generic});
    }

    public Generic derivative(int n) {
        return new Inverse(new Sqrt(JsclInteger.valueOf(1).subtract(parameters[0].pow(2))).selfExpand()).selfExpand().negate();
    }

    public Generic selfExpand() {
        if (parameters[0].signum() < 0) {
            return Constants.Generic.PI.subtract(new Acos(parameters[0].negate()).selfExpand());
        } else if (parameters[0].compareTo(JsclInteger.valueOf(1)) == 0) {
            return JsclInteger.valueOf(0);
        }

        return expressionValue();
    }

    public Generic selfElementary() {
        return Constants.Generic.I.multiply(
                new Ln(
                        new Root(
                                new Generic[]{
                                        JsclInteger.valueOf(-1),
                                        JsclInteger.valueOf(2).multiply(parameters[0]),
                                        JsclInteger.valueOf(-1)
                                },
                                0
                        ).selfElementary()
                ).selfElementary()
        );
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).acos();
    }

    @Nonnull
    public Variable newInstance() {
        return new Acos(null);
    }
}
