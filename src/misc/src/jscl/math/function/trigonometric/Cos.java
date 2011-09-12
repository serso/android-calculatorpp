package jscl.math.function.trigonometric;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.math.function.Exp;
import jscl.math.function.Trigonometric;

public class Cos extends Trigonometric {
    public Cos(Generic generic) {
        super("cos",new Generic[] {generic});
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        return new Sin(parameter[0]).evaluate();
    }

    public Generic derivative(int n) {
        return new Sin(parameter[0]).evaluate().negate();
    }

    public Generic evaluate() {
        if(parameter[0].signum()<0) {
            return new Cos(parameter[0].negate()).evaluate();
        } else if(parameter[0].signum()==0) {
            return JSCLInteger.valueOf(1);
        } else if(parameter[0].compareTo(Constant.pi)==0) {
            return JSCLInteger.valueOf(-1);
        }
        return expressionValue();
    }

    public Generic evalelem() {
        return new Exp(
            Constant.i.multiply(parameter[0])
        ).evalelem().add(
            new Exp(
                Constant.i.multiply(parameter[0].negate())
            ).evalelem()
        ).multiply(Constant.half);
    }

    public Generic evalsimp() {
        if(parameter[0].signum()<0) {
            return new Cos(parameter[0].negate()).evaluate();
        } else if(parameter[0].signum()==0) {
            return JSCLInteger.valueOf(1);
        } else if(parameter[0].compareTo(Constant.pi)==0) {
            return JSCLInteger.valueOf(-1);
        }
        try {
            Variable v=parameter[0].variableValue();
            if(v instanceof Acos) {
                Generic g[]=((Acos)v).parameters();
                return g[0];
            }
        } catch (NotVariableException e) {}
        return identity();
    }

    public Generic identity(Generic a, Generic b) {
        return new Cos(a).evalsimp().multiply(
            new Cos(b).evalsimp()
        ).subtract(
            new Sin(a).evalsimp().multiply(
                new Sin(b).evalsimp()
            )
        );
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).cos();
    }

    protected Variable newinstance() {
        return new Cos(null);
    }
}
