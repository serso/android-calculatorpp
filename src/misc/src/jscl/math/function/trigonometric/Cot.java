package jscl.math.function.trigonometric;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.math.function.Frac;
import jscl.math.function.Log;
import jscl.math.function.Trigonometric;

public class Cot extends Trigonometric {
    public Cot(Generic generic) {
        super("cot",new Generic[] {generic});
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        return new Log(
            JSCLInteger.valueOf(4).multiply(
                new Sin(parameter[0]).evaluate()
            )
        ).evaluate();
    }

    public Generic derivative(int n) {
        return JSCLInteger.valueOf(1).add(
            new Cot(parameter[0]).evaluate().pow(2)
        ).negate();
    }

    public Generic evaluate() {
        if(parameter[0].signum()<0) {
            return new Cot(parameter[0].negate()).evaluate().negate();
        }
        return expressionValue();
    }

    public Generic evalelem() {
        return new Frac(
            new Cos(parameter[0]).evalelem(),
            new Sin(parameter[0]).evalelem()
        ).evalelem();
    }

    public Generic evalsimp() {
        if(parameter[0].signum()<0) {
            return new Cot(parameter[0].negate()).evaluate().negate();
        }
        try {
            Variable v=parameter[0].variableValue();
            if(v instanceof Acot) {
                Generic g[]=((Acot)v).parameters();
                return g[0];
            }
        } catch (NotVariableException e) {}
        return identity();
    }

    public Generic identity(Generic a, Generic b) {
        Generic ta=new Cot(a).evalsimp();
        Generic tb=new Cot(b).evalsimp();
        return new Frac(
            ta.multiply(tb).subtract(JSCLInteger.valueOf(1)),
                        ta.add(tb)
        ).evalsimp();
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).cot();
    }

    protected Variable newinstance() {
        return new Cot(null);
    }
}
