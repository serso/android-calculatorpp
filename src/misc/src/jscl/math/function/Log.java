package jscl.math.function;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Power;
import jscl.math.Variable;

public class Log extends Function {
    public Log(Generic generic) {
        super("log",new Generic[] {generic});
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        throw new NotIntegrableException();
    }

    public Generic derivative(int n) {
        return new Inv(parameter[0]).evaluate();
    }

    public Generic evaluate() {
        if(parameter[0].compareTo(JSCLInteger.valueOf(1))==0) {
            return JSCLInteger.valueOf(0);
        }
        return expressionValue();
    }

    public Generic evalelem() {
        return evaluate();
    }

    public Generic evalsimp() {
        try {
            JSCLInteger en=parameter[0].integerValue();
            if(en.signum()<0) return Constant.i.multiply(Constant.pi).add(new Log(en.negate()).evalsimp());
            else {
                Generic a=en.factorize();
                Generic p[]=a.productValue();
                Generic s=JSCLInteger.valueOf(0);
                for(int i=0;i<p.length;i++) {
                    Power o=p[i].powerValue();
                    s=s.add(JSCLInteger.valueOf(o.exponent()).multiply(new Log(o.value(true)).expressionValue()));
                }
                return s;
            }
        } catch (NotIntegerException e) {}
        try {
            Variable v=parameter[0].variableValue();
            if(v instanceof Sqrt) {
                Generic g[]=((Sqrt)v).parameters();
                return Constant.half.multiply(new Log(g[0]).evalsimp());
            }
        } catch (NotVariableException e) {}
        Generic n[]=Frac.separateCoefficient(parameter[0]);
        if(n[0].compareTo(JSCLInteger.valueOf(1))==0 && n[1].compareTo(JSCLInteger.valueOf(1))==0);
        else return new Log(n[2]).evalsimp().add(
            new Log(n[0]).evalsimp()
        ).subtract(
            new Log(n[1]).evalsimp()
        );
        return expressionValue();
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).log();
    }

    protected Variable newinstance() {
        return new Log(null);
    }
}
