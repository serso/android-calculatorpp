package jscl.math.function;

import jscl.math.Antiderivative;
import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.NotIntegrableException;
import jscl.math.NumericWrapper;
import jscl.math.Power;
import jscl.math.Variable;
import jscl.mathml.MathML;

public class Sqrt extends Algebraic {
    public Sqrt(Generic generic) {
        super("sqrt",new Generic[] {generic});
    }

    public Root rootValue() {
        return new Root(
            new Generic[] {
                parameter[0].negate(),
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
        return Constant.half.multiply(
            new Inv(
                evaluate()
            ).evaluate()
        );
    }

    public boolean imaginary() {
        return parameter[0].compareTo(JSCLInteger.valueOf(-1))==0;
    }

    public Generic evaluate() {
        try {
            JSCLInteger en=parameter[0].integerValue();
            if(en.signum()<0);
            else {
                Generic rt=en.sqrt();
                if(rt.pow(2).compareTo(en)==0) return rt;
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
            if(en.signum()<0) return Constant.i.multiply(new Sqrt(en.negate()).evalsimp());
            else {
                Generic rt=en.sqrt();
                if(rt.pow(2).compareTo(en)==0) return rt;
            }
            Generic a=en.factorize();
            Generic p[]=a.productValue();
            Generic s=JSCLInteger.valueOf(1);
            for(int i=0;i<p.length;i++) {
                Power o=p[i].powerValue();
                Generic q=o.value(true);
                int c=o.exponent();
                s=s.multiply(q.pow(c/2).multiply(new Sqrt(q).expressionValue().pow(c%2)));
            }
            return s;
        } catch (NotIntegerException e) {
            Generic n[]=Frac.separateCoefficient(parameter[0]);
            if(n[0].compareTo(JSCLInteger.valueOf(1))==0 && n[1].compareTo(JSCLInteger.valueOf(1))==0);
            else return new Sqrt(n[2]).evalsimp().multiply(
                new Frac(
                    new Sqrt(n[0]).evalsimp(),
                    new Sqrt(n[1]).evalsimp()
                ).evalsimp()
            );
        }
        return expressionValue();
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).sqrt();
    }

    public String toJava() {
        if(parameter[0].compareTo(JSCLInteger.valueOf(-1))==0) return "Complex.valueOf(0, 1)";
        StringBuffer buffer=new StringBuffer();
        buffer.append(parameter[0].toJava());
        buffer.append(".").append(name).append("()");
        return buffer.toString();
    }

    void bodyToMathML(MathML element, boolean fenced) {
        if(parameter[0].compareTo(JSCLInteger.valueOf(-1))==0) {
            MathML e1=element.element("mi");
            e1.appendChild(element.text(/*"\u2148"*/"i"));
            element.appendChild(e1);
        } else {
            MathML e1=element.element("msqrt");
            parameter[0].toMathML(e1,null);
            element.appendChild(e1);
        }
    }

    protected Variable newinstance() {
        return new Sqrt(null);
    }
}
