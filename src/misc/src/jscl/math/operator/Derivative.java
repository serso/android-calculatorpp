package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;
import jscl.mathml.MathML;

public class Derivative extends Operator {
    public Derivative(Generic expression, Generic variable, Generic value, Generic order) {
        super("d",new Generic[] {expression,variable,value,order});
    }

    public Generic compute() {
        Variable variable=parameter[1].variableValue();
        try {
            int n=parameter[3].integerValue().intValue();
            Generic a=parameter[0];
            for(int i=0;i<n;i++) {
                a=a.derivative(variable);
            }
            return a.substitute(variable,parameter[2]);
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        int n=4;
        if(parameter[3].compareTo(JSCLInteger.valueOf(1))==0) {
            n=3;
            if(parameter[2].compareTo(parameter[1])==0) n=2;
        }
        buffer.append(name);
        buffer.append("(");
        for(int i=0;i<n;i++) {
            buffer.append(parameter[i]).append(i<n-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) derivationToMathML(element,false);
        else {
            MathML e1=element.element("msup");
            derivationToMathML(e1,true);
            MathML e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
        MathML e1=element.element("mfenced");
        parameter[0].toMathML(e1,null);
        if(parameter[2].compareTo(parameter[1])!=0) parameter[2].toMathML(e1,null);
        element.appendChild(e1);
    }

    void derivationToMathML(MathML element, boolean fenced) {
        if(fenced) {
            MathML e1=element.element("mfenced");
            derivationToMathML(e1);
            element.appendChild(e1);
        } else {
            derivationToMathML(element);
        }
    }

    void derivationToMathML(MathML element) {
        Variable v=parameter[1].variableValue();
        int n=0;
        try {
            n=parameter[3].integerValue().intValue();
        } catch (NotIntegerException e) {}
        if(n==1) {
            MathML e1=element.element("mfrac");
            MathML e2=element.element("mo");
            e2.appendChild(element.text(/*"\u2146"*/"d"));
            e1.appendChild(e2);
            e2=element.element("mrow");
            MathML e3=element.element("mo");
            e3.appendChild(element.text(/*"\u2146"*/"d"));
            e2.appendChild(e3);
            v.toMathML(e2,null);
            e1.appendChild(e2);
            element.appendChild(e1);
        } else {
            MathML e1=element.element("mfrac");
            MathML e2=element.element("msup");
            MathML e3=element.element("mo");
            e3.appendChild(element.text(/*"\u2146"*/"d"));
            e2.appendChild(e3);
            parameter[3].toMathML(e2,null);
            e1.appendChild(e2);
            e2=element.element("mrow");
            e3=element.element("mo");
            e3.appendChild(element.text(/*"\u2146"*/"d"));
            e2.appendChild(e3);
            e3=element.element("msup");
            parameter[1].toMathML(e3,null);
            parameter[3].toMathML(e3,null);
            e2.appendChild(e3);
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    protected Variable newinstance() {
        return new Derivative(null,null,null,null);
    }
}
