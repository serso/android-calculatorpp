package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.mathml.MathML;

public abstract class VectorOperator extends Operator {
    public VectorOperator(String name, Generic parameter[]) {
        super(name,parameter);
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

    protected abstract void bodyToMathML(MathML element);

    protected void operator(MathML element, String name) {
        Variable variable[]=variables(GenericVariable.content(parameter[1]));
        MathML e1=element.element("msub");
        new Constant(name).toMathML(e1,null);
        MathML e2=element.element("mrow");
        for(int i=0;i<variable.length;i++) variable[i].expressionValue().toMathML(e2,null);
        e1.appendChild(e2);
        element.appendChild(e1);
    }
}
