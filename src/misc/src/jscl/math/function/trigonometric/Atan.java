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

public class Atan extends ArcTrigonometric {
    public Atan(Generic generic) {
        super("atan",new Generic[] {generic});
    }

    public Generic derivative(int n) {
        return new Inv(
            JSCLInteger.valueOf(1).add(parameter[0].pow(2))
        ).evaluate();
    }

    public Generic evaluate() {
        if(parameter[0].signum()<0) {
            return new Atan(parameter[0].negate()).evaluate().negate();
        } else if(parameter[0].signum()==0) {
            return JSCLInteger.valueOf(0);
        }
        return expressionValue();
    }

    public Generic evalelem() {
        return Constant.i.multiply(
            new Log(
                new Root(
                    new Generic[] {
                        JSCLInteger.valueOf(-1).add(Constant.i.multiply(parameter[0])),
                        JSCLInteger.valueOf(0),
                        JSCLInteger.valueOf(1).add(Constant.i.multiply(parameter[0]))
                    },
                    0
                ).evalelem()
            ).evalelem()
        );
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).atan();
    }

    protected Variable newinstance() {
        return new Atan(null);
    }
}
