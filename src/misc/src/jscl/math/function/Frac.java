package jscl.math.function;

import jscl.math.Antiderivative;
import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JSCLInteger;
import jscl.math.NotDivisibleException;
import jscl.math.NotExpressionException;
import jscl.math.NotIntegerException;
import jscl.math.NotIntegrableException;
import jscl.math.NotPowerException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.mathml.MathML;

public class Frac extends Algebraic {
    public Frac(Generic numerator, Generic denominator) {
        super("frac",new Generic[] {numerator,denominator});
    }

    public Root rootValue() {
        return new Root(
            new Generic[] {
                parameter[0].negate(),
                parameter[1]
            },
            0
        );
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        if(parameter[0].isPolynomial(variable) && parameter[1].isPolynomial(variable)) {
            return Antiderivative.compute(this,variable);
        } else throw new NotIntegrableException();
    }

    public Generic derivative(int n) {
        if(n==0) {
            return new Inv(parameter[1]).evaluate();
        } else {
            return parameter[0].multiply(new Inv(parameter[1]).evaluate().pow(2).negate());
        }
    }

    public boolean integer() {
        try {
            parameter[0].integerValue().intValue();
            parameter[1].integerValue().intValue();
            return true;
        } catch (NotIntegerException e) {}
        return false;
    }

    public Generic evaluate() {
        if(parameter[0].compareTo(JSCLInteger.valueOf(1))==0) {
            return new Inv(parameter[1]).evaluate();
        }
        try {
            return parameter[0].divide(parameter[1]);
        } catch (NotDivisibleException e) {}
        return expressionValue();
    }

    public Generic evalelem() {
        return evaluate();
    }

    public Generic evalsimp() {
        if(parameter[0].signum()<0) {
            return new Frac(parameter[0].negate(),parameter[1]).evalsimp().negate();
        }
        if(parameter[1].signum()<0) {
            return new Frac(parameter[0].negate(),parameter[1].negate()).evalsimp();
        }
        return evaluate();
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).divide((NumericWrapper)parameter[1]);
    }

    static Generic[] separateCoefficient(Generic generic) {
        if(generic.signum()<0) {
            Generic n[]=separateCoefficient(generic.negate());
            return new Generic[] {n[0],n[1],n[2].negate()};
        }
        try {
            Variable v=generic.variableValue();
            if(v instanceof Frac) {
                Generic g[]=((Frac)v).parameters();
                Generic a=g[0].expressionValue();
                Generic d=g[1].expressionValue();
                Generic na[]=a.gcdAndNormalize();
                Generic nd[]=d.gcdAndNormalize();
                return new Generic[] {na[0],nd[0],new Frac(na[1],nd[1]).evaluate()};
            }
        } catch (NotVariableException e) {
            try {
                Generic a=generic.expressionValue();
                Generic n[]=a.gcdAndNormalize();
                return new Generic[] {n[0],JSCLInteger.valueOf(1),n[1]};
            } catch (NotExpressionException e2) {}
        }
        return new Generic[] {JSCLInteger.valueOf(1),JSCLInteger.valueOf(1),generic};
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        try {
            parameter[0].powerValue();
            buffer.append(parameter[0]);
        } catch (NotPowerException e) {
            buffer.append(GenericVariable.valueOf(parameter[0]));
        }
        buffer.append("/");
        try {
            Variable v=parameter[1].variableValue();
            if(v instanceof Frac) {
                buffer.append(GenericVariable.valueOf(parameter[1]));
            } else buffer.append(v);
        } catch (NotVariableException e) {
            try {
                parameter[1].abs().powerValue();
                buffer.append(parameter[1]);
            } catch (NotPowerException e2) {
                buffer.append(GenericVariable.valueOf(parameter[1]));
            }
        }
        return buffer.toString();
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(parameter[0].toJava());
        buffer.append(".divide(");
        buffer.append(parameter[1].toJava());
        buffer.append(")");
        return buffer.toString();
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
        MathML e1=element.element("mfrac");
        parameter[0].toMathML(e1,null);
        parameter[1].toMathML(e1,null);
        element.appendChild(e1);
    }

    protected Variable newinstance() {
        return new Frac(null,null);
    }
}
