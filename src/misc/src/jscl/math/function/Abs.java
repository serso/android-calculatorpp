package jscl.math.function;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.mathml.MathML;

public class Abs extends Function {
    public Abs(Generic generic) {
        super("abs",new Generic[] {generic});
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        return Constant.half.multiply(parameter[0]).multiply(new Abs(parameter[0]).evaluate());
    }

    public Generic derivative(int n) {
        return new Sgn(parameter[0]).evaluate();
    }

    public Generic evaluate() {
        if(parameter[0].signum()<0) {
            return new Abs(parameter[0].negate()).evaluate();
        }
        try {
            return parameter[0].integerValue().abs();
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    public Generic evalelem() {
        return new Sqrt(
            parameter[0].pow(2)
        ).evalelem();
    }

    public Generic evalsimp() {
        if(parameter[0].signum()<0) {
            return new Abs(parameter[0].negate()).evalsimp();
        }
        try {
            return parameter[0].integerValue().abs();
        } catch (NotIntegerException e) {}
        try {
            Variable v=parameter[0].variableValue();
            if(v instanceof Abs) {
                Function f=(Function)v;
                return f.evalsimp();
            } else if(v instanceof Sgn) {
                return JSCLInteger.valueOf(1);
            }
        } catch (NotVariableException e) {}
        return expressionValue();
    }

    public Generic evalnum() {
        return ((NumericWrapper)parameter[0]).abs();
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(parameter[0].toJava());
        buffer.append(".abs()");
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) bodyToMathML(element);
        else {
            MathML e1=element.element("msup");
            bodyToMathML(e1);
            MathML e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    void bodyToMathML(MathML element) {
        MathML e1=element.element("mfenced");
        e1.setAttribute("open","|");
        e1.setAttribute("close","|");
        parameter[0].toMathML(e1,null);
        element.appendChild(e1);
    }

    protected Variable newinstance() {
        return new Abs(null);
    }
}
