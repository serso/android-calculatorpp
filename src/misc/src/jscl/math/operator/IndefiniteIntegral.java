package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.mathml.MathML;

public class IndefiniteIntegral extends Operator {
    public IndefiniteIntegral(Generic expression, Generic variable) {
        super("integral",new Generic[] {expression,variable});
    }

    public Generic compute() {
        Variable variable=parameter[1].variableValue();
        try {
            return parameter[0].antiderivative(variable);
        } catch (NotIntegrableException e) {}
        return expressionValue();
    }

    public void toMathML(MathML element, Object data) {
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) bodyToMathML(element);
        else {
            MathML e1=element.element("msup");
            MathML e2=element.element("mfenced");
            bodyToMathML(e2);
            e1.appendChild(e2);
            e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    void bodyToMathML(MathML element) {
        Variable v=parameter[1].variableValue();
        MathML e1=element.element("mrow");
        MathML e2=element.element("mo");
        e2.appendChild(element.text("\u222B"));
        e1.appendChild(e2);
        parameter[0].toMathML(e1,null);
        e2=element.element("mo");
        e2.appendChild(element.text(/*"\u2146"*/"d"));
        e1.appendChild(e2);
        v.toMathML(e1,null);
        element.appendChild(e1);
    }

    protected Variable newinstance() {
        return new IndefiniteIntegral(null,null);
    }
}
