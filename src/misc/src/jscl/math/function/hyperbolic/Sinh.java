package jscl.math.function.hyperbolic;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.math.function.Exp;
import jscl.math.function.Trigonometric;

public class Sinh extends Trigonometric {
    public Sinh(Generic generic) {
        super("sinh",new Generic[] {generic});
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        return new Cosh(parameter[0]).evaluate();
    }

    public Generic derivative(int n) {
        return new Cosh(parameter[0]).evaluate();
    }

    public Generic evaluate() {
        if(parameter[0].signum()<0) {
            return new Sinh(parameter[0].negate()).evaluate().negate();
        } else if(parameter[0].signum()==0) {
            return JSCLInteger.valueOf(0);
        }
        return expressionValue();
    }

    public Generic evalelem() {
        return new Exp(
            parameter[0]
        ).evalelem().subtract(
            new Exp(
                parameter[0].negate()
            ).evalelem()
        ).multiply(Constant.half);
    }

    public Generic evalsimp() {
        if(parameter[0].signum()<0) {
            return new Sinh(parameter[0].negate()).evaluate().negate();
        } else if(parameter[0].signum()==0) {
            return JSCLInteger.valueOf(0);
        }
        try {
            Variable v=parameter[0].variableValue();
            if(v instanceof Asinh) {
                Generic g[]=((Asinh)v).parameters();
                return g[0];
            }
        } catch (NotVariableException e) {}
        return identity();
    }

    public Generic identity(Generic a, Generic b) {
        return new Cosh(b).evalsimp().multiply(
            new Sinh(a).evalsimp()
        ).add(
            new Cosh(a).evalsimp().multiply(
                new Sinh(b).evalsimp()
            )
        );
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).sinh();
    }

    protected Variable newinstance() {
        return new Sinh(null);
    }
}
