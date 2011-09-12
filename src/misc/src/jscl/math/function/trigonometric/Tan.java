package jscl.math.function.trigonometric;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.math.function.Frac;
import jscl.math.function.Log;
import jscl.math.function.Trigonometric;

public class Tan extends Trigonometric {
    public Tan(Generic generic) {
        super("tan",new Generic[] {generic});
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        return new Log(
            JSCLInteger.valueOf(4).multiply(
                new Cos(parameter[0]).evaluate()
            )
        ).evaluate().negate();
    }

    public Generic derivative(int n) {
        return JSCLInteger.valueOf(1).add(
            new Tan(parameter[0]).evaluate().pow(2)
        );
    }

    public Generic evaluate() {
        if(parameter[0].signum()<0) {
            return new Tan(parameter[0].negate()).evaluate().negate();
        } else if(parameter[0].signum()==0) {
            return JSCLInteger.valueOf(0);
        } else if(parameter[0].compareTo(Constant.pi)==0) {
            return JSCLInteger.valueOf(0);
        }
        return expressionValue();
    }

    public Generic evalelem() {
        return new Frac(
            new Sin(parameter[0]).evalelem(),
            new Cos(parameter[0]).evalelem()
        ).evalelem();
    }

    public Generic evalsimp() {
        if(parameter[0].signum()<0) {
            return new Tan(parameter[0].negate()).evaluate().negate();
        } else if(parameter[0].signum()==0) {
            return JSCLInteger.valueOf(0);
        } else if(parameter[0].compareTo(Constant.pi)==0) {
            return JSCLInteger.valueOf(0);
        }
        try {
            Variable v=parameter[0].variableValue();
            if(v instanceof Atan) {
                Generic g[]=((Atan)v).parameters();
                return g[0];
            }
        } catch (NotVariableException e) {}
        return identity();
    }

    public Generic identity(Generic a, Generic b) {
        Generic ta=new Tan(a).evalsimp();
        Generic tb=new Tan(b).evalsimp();
        return new Frac(
            ta.add(tb),
            JSCLInteger.valueOf(1).subtract(
                ta.multiply(tb)
            )
        ).evalsimp();
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).tan();
    }

    protected Variable newinstance() {
        return new Tan(null);
    }
}
