package jscl.math.function;

import jscl.math.Antiderivative;
import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.NotIntegrableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.mathml.MathML;

public class Cubic extends Algebraic {
    public Cubic(Generic generic) {
        super("cubic",new Generic[] {generic});
    }

    public Root rootValue() {
        return new Root(
            new Generic[] {
                parameter[0].negate(),
                JSCLInteger.valueOf(0),
                JSCLInteger.valueOf(0),
                JSCLInteger.valueOf(1)
            },
            0
        );
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        Root r=rootValue();
        Generic g[]=r.parameters();
        if(g[0].isPolynomial(variable)) {
            return Antiderivative.compute(r,variable);
        } else throw new NotIntegrableException();
    }

    public Generic derivative(int n) {
        return Constant.third.multiply(
            new Inv(
                evaluate().pow(2)
            ).evaluate()
        );
    }

    public Generic evaluate() {
        try {
            JSCLInteger en=parameter[0].integerValue();
            if(en.signum()<0);
            else {
                Generic rt=en.nthrt(3);
                if(rt.pow(3).compareTo(en)==0) return rt;
            }
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    public Generic evalelem() {
        return evaluate();
    }

    public Generic evalsimp() {
        try {
            JSCLInteger en=parameter[0].integerValue();
            if(en.signum()<0) return new Cubic(en.negate()).evalsimp().negate();
            else {
                Generic rt=en.nthrt(3);
                if(rt.pow(3).compareTo(en)==0) return rt;
            }
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).nthrt(3);
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(parameter[0].toJava());
        buffer.append(".pow(");
        buffer.append(Constant.third.toJava());
        buffer.append(")");
        return buffer.toString();
    }

    void bodyToMathML(MathML element, boolean fenced) {
        MathML e1=element.element("mroot");
        parameter[0].toMathML(e1,null);
        JSCLInteger.valueOf(3).toMathML(e1,null);
        element.appendChild(e1);
    }

    protected Variable newinstance() {
        return new Cubic(null);
    }
}
