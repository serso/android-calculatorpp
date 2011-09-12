package jscl.math.function.hyperbolic;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.math.function.Frac;
import jscl.math.function.Log;
import jscl.math.function.Trigonometric;

public class Coth extends Trigonometric {
    public Coth(Generic generic) {
        super("coth",new Generic[] {generic});
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        return new Log(
            JSCLInteger.valueOf(4).multiply(
                new Sinh(parameter[0]).evaluate()
            )
        ).evaluate();
    }

    public Generic derivative(int n) {
        return JSCLInteger.valueOf(1).subtract(
            new Coth(parameter[0]).evaluate().pow(2)
        );
    }

    public Generic evaluate() {
        if(parameter[0].signum()<0) {
            return new Coth(parameter[0].negate()).evaluate().negate();
        }
        return expressionValue();
    }

    public Generic evalelem() {
        return new Frac(
            new Cosh(parameter[0]).evalelem(),
            new Sinh(parameter[0]).evalelem()
        ).evalelem();
    }

    public Generic evalsimp() {
        if(parameter[0].signum()<0) {
            return new Coth(parameter[0].negate()).evaluate().negate();
        }
        try {
            Variable v=parameter[0].variableValue();
            if(v instanceof Acoth) {
                Generic g[]=((Acoth)v).parameters();
                return g[0];
            }
        } catch (NotVariableException e) {}
        return identity();
    }

    public Generic identity(Generic a, Generic b) {
        Generic ta=new Coth(a).evalsimp();
        Generic tb=new Coth(b).evalsimp();
        return new Frac(
            ta.multiply(tb).add(JSCLInteger.valueOf(1)),
                        ta.add(tb)
        ).evalsimp();
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).coth();
    }

    protected Variable newinstance() {
        return new Coth(null);
    }
}
