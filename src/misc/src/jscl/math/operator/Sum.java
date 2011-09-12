package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;
import jscl.mathml.MathML;

public class Sum extends Operator {
    public Sum(Generic expression, Generic variable, Generic n1, Generic n2) {
        super("sum",new Generic[] {expression,variable,n1,n2});
    }

    public Generic compute() {
        Variable variable=parameter[1].variableValue();
        try {
            int n1=parameter[2].integerValue().intValue();
            int n2=parameter[3].integerValue().intValue();
            Generic a=JSCLInteger.valueOf(0);
            for(int i=n1;i<=n2;i++) {
                a=a.add(parameter[0].substitute(variable,JSCLInteger.valueOf(i)));
            }
            return a;
        } catch (NotIntegerException e) {}
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
        MathML e1=element.element("mrow");
        MathML e2=element.element("munderover");
        MathML e3=element.element("mo");
        e3.appendChild(element.text("\u2211"));
        e2.appendChild(e3);
        e3=element.element("mrow");
        parameter[1].toMathML(e3,null);
        MathML e4=element.element("mo");
        e4.appendChild(element.text("="));
        e3.appendChild(e4);
        parameter[2].toMathML(e3,null);
        e2.appendChild(e3);
        parameter[3].toMathML(e2,null);
        e1.appendChild(e2);
        parameter[0].toMathML(e1,null);
        element.appendChild(e1);
    }

    protected Variable newinstance() {
        return new Sum(null,null,null,null);
    }
}
