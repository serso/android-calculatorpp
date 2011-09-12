package jscl.math.function.trigonometric;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.math.function.ArcTrigonometric;
import jscl.math.function.Constant;
import jscl.math.function.Inv;
import jscl.math.function.Log;
import jscl.math.function.Root;

public class Acot extends ArcTrigonometric {
    public Acot(Generic generic) {
        super("acot",new Generic[] {generic});
    }

    public Generic derivative(int n) {
        return new Inv(
            JSCLInteger.valueOf(1).add(parameter[0].pow(2))
        ).evaluate().negate();
    }

    public Generic evaluate() {
        if(parameter[0].signum()<0) {
            return Constant.pi.subtract(new Acot(parameter[0].negate()).evaluate());
        }
        return expressionValue();
    }

    public Generic evalelem() {
        return Constant.i.multiply(
            new Log(
                new Root(
                    new Generic[] {
                        Constant.i.add(parameter[0]),
                        JSCLInteger.valueOf(0),
                        Constant.i.subtract(parameter[0])
                    },
                    0
                ).evalelem()
            ).evalelem()
        );
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).acot();
    }

    protected Variable newinstance() {
        return new Acot(null);
    }
}
