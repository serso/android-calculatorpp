package jscl.math.function;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;

public class Sgn extends Function {
    public Sgn(Generic generic) {
        super("sgn",new Generic[] {generic});
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        return new Abs(parameter[0]).evaluate();
    }

    public Generic derivative(int n) {
        return JSCLInteger.valueOf(0);
    }

    public Generic evaluate() {
        if(parameter[0].signum()<0) {
            return new Sgn(parameter[0].negate()).evaluate().negate();
        } else if(parameter[0].signum()==0) {
            return JSCLInteger.valueOf(1);
        }
        try {
            return JSCLInteger.valueOf(parameter[0].integerValue().signum());
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    public Generic evalelem() {
        return new Frac(
            parameter[0],
            new Abs(parameter[0]).evalelem()
        ).evalelem();
    }

    public Generic evalsimp() {
        if(parameter[0].signum()<0) {
            return new Sgn(parameter[0].negate()).evaluate().negate();
        } else if(parameter[0].signum()==0) {
            return JSCLInteger.valueOf(1);
        }
        try {
            return JSCLInteger.valueOf(parameter[0].integerValue().signum());
        } catch (NotIntegerException e) {}
        try {
            Variable v=parameter[0].variableValue();
            if(v instanceof Abs) {
                return JSCLInteger.valueOf(1);
            } else if(v instanceof Sgn) {
                Function f=(Function)v;
                return f.evalsimp();
            }
        } catch (NotVariableException e) {}
        return expressionValue();
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).sgn();
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(parameter[0].toJava());
        buffer.append(".sgn()");
        return buffer.toString();
    }

    protected Variable newinstance() {
        return new Sgn(null);
    }
}
