package jscl.math.function.hyperbolic;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.math.function.ArcTrigonometric;
import jscl.math.function.Inv;
import jscl.math.function.Log;
import jscl.math.function.Root;
import jscl.math.function.Sqrt;

public class Asinh extends ArcTrigonometric {
    public Asinh(Generic generic) {
        super("asinh",new Generic[] {generic});
    }

    public Generic derivative(int n) {
        return new Inv(
            new Sqrt(
                JSCLInteger.valueOf(1).add(
                    parameter[0].pow(2)
                )
            ).evaluate()
        ).evaluate();
    }

    public Generic evaluate() {
        if(parameter[0].signum()<0) {
            return new Asinh(parameter[0].negate()).evaluate().negate();
        } else if(parameter[0].signum()==0) {
            return JSCLInteger.valueOf(0);
        }
        return expressionValue();
    }

    public Generic evalelem() {
        return new Log(
            new Root(
                new Generic[] {
                    JSCLInteger.valueOf(1),
                    JSCLInteger.valueOf(2).multiply(parameter[0]),
                    JSCLInteger.valueOf(-1)
                },
                0
            ).evalelem()
        ).evalelem();
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).asinh();
    }

    protected Variable newinstance() {
        return new Asinh(null);
    }
}
