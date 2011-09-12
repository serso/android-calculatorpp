package jscl.math.function;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.math.polynomial.Polynomial;
import jscl.mathml.MathML;

public class Exp extends Function {
    public Exp(Generic generic) {
        super("exp",new Generic[] {generic});
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        Generic s=parameter[0];
        if(s.isPolynomial(variable)) {
            Polynomial p=Polynomial.factory(variable).valueof(s);
            if(p.degree()==1) {
                Generic a[]=p.elements();
                return new Inv(a[1]).evaluate().multiply(antiderivative(0));
            } else throw new NotIntegrableException();
        } else throw new NotIntegrableException();
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        return evaluate();
    }

    public Generic derivative(int n) {
        return evaluate();
    }

    public Generic evaluate() {
        if(parameter[0].signum()<0) {
            return new Inv(new Exp(parameter[0].negate()).evaluate()).evaluate();
        } else if(parameter[0].signum()==0) {
            return JSCLInteger.valueOf(1);
        }
        return expressionValue();
    }

    public Generic evalelem() {
        return evaluate();
    }

    public Generic evalsimp() {
        if(parameter[0].signum()<0) {
            return new Inv(new Exp(parameter[0].negate()).evalsimp()).evalsimp();
        } else if(parameter[0].signum()==0) {
            return JSCLInteger.valueOf(1);
        } else if(parameter[0].compareTo(Constant.i.multiply(Constant.pi))==0) {
            return JSCLInteger.valueOf(-1);
        }
        try {
            Variable v=parameter[0].variableValue();
            if(v instanceof Log) {
                Generic g[]=((Log)v).parameters();
                return g[0];
            }
        } catch (NotVariableException e) {
            Generic a[]=parameter[0].sumValue();
            if(a.length>1) {
                Generic s=JSCLInteger.valueOf(1);
                for(int i=0;i<a.length;i++) {
                    s=s.multiply(new Exp(a[i]).evalsimp());
                }
                return s;
            }
        }
        Generic n[]=Frac.separateCoefficient(parameter[0]);
        if(n[0].compareTo(JSCLInteger.valueOf(1))==0 && n[1].compareTo(JSCLInteger.valueOf(1))==0);
        else return new Pow(
            new Exp(n[2]).evalsimp(),
            new Frac(n[0],n[1]).evalsimp()
        ).evalsimp();
        return expressionValue();
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).exp();
    }

    public void toMathML(MathML element, Object data) {
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) bodyToMathML(element,false);
        else {
            MathML e1=element.element("msup");
            bodyToMathML(e1,true);
            MathML e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    void bodyToMathML(MathML element, boolean fenced) {
        if(fenced) {
            MathML e1=element.element("mfenced");
            bodyToMathML(e1);
            element.appendChild(e1);
        } else {
            bodyToMathML(element);
        }
    }

    void bodyToMathML(MathML element) {
        MathML e1=element.element("msup");
        MathML e2=element.element("mi");
        e2.appendChild(element.text(/*"\u2147"*/"e"));
        e1.appendChild(e2);
        parameter[0].toMathML(e1,null);
        element.appendChild(e1);
    }
    
    protected Variable newinstance() {
        return new Exp(null);
    }
}
