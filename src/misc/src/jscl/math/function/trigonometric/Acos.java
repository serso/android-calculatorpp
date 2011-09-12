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
import jscl.math.function.Sqrt;

public class Acos extends ArcTrigonometric {
    public Acos(Generic generic) {
        super("acos",new Generic[] {generic});
    }

    public Generic derivative(int n) {
        return new Inv(
            new Sqrt(
                JSCLInteger.valueOf(1).subtract(parameter[0].pow(2))
            ).evaluate()
        ).evaluate().negate();
    }

    public Generic evaluate() {
        if(parameter[0].signum()<0) {
            return Constant.pi.subtract(new Acos(parameter[0].negate()).evaluate());
        } else if(parameter[0].compareTo(JSCLInteger.valueOf(1))==0) {
            return JSCLInteger.valueOf(0);
        }
        return expressionValue();
    }

    public Generic evalelem() {
        return Constant.i.multiply(
            new Log(
                new Root(
                    new Generic[] {
                        JSCLInteger.valueOf(-1),
                        JSCLInteger.valueOf(2).multiply(parameter[0]),
                        JSCLInteger.valueOf(-1)
                    },
                    0
                ).evalelem()
            ).evalelem()
        );
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).acos();
    }

    protected Variable newinstance() {
        return new Acos(null);
    }
}
