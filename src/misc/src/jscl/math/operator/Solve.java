package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;
import jscl.math.function.Root;
import jscl.math.polynomial.Polynomial;
import jscl.math.polynomial.UnivariatePolynomial;
import jscl.mathml.MathML;

public class Solve extends Operator {
    public Solve(Generic expression, Generic variable, Generic subscript) {
        super("solve",new Generic[] {expression,variable,subscript});
    }

    public Generic compute() {
        Variable variable=parameter[1].variableValue();
        int subscript=parameter[2].integerValue().intValue();
        if(parameter[0].isPolynomial(variable)) {
            return new Root((UnivariatePolynomial)Polynomial.factory(variable).valueof(parameter[0]),subscript).evaluate();
        }
        return expressionValue();
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        int n=3;
        if(parameter[2].signum()==0) n=2;
        buffer.append(name);
        buffer.append("(");
        for(int i=0;i<n;i++) {
            buffer.append(parameter[i]).append(i<n-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        MathML e1;
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        int n=3;
        if(parameter[2].signum()==0) n=2;
        if(exponent==1) nameToMathML(element);
        else {
            e1=element.element("msup");
            nameToMathML(e1);
            MathML e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
        e1=element.element("mfenced");
        for(int i=0;i<n;i++) {
            parameter[i].toMathML(e1,null);
        }
        element.appendChild(e1);
    }

    protected Variable newinstance() {
        return new Solve(null,null,null);
    }
}
