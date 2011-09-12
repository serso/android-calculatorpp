package jscl.math.function;

import jscl.math.Generic;
import jscl.math.NotIntegrableException;
import jscl.mathml.MathML;

public abstract class Algebraic extends Function {
    public Algebraic(String name, Generic parameter[]) {
        super(name,parameter);
    }

    public abstract Root rootValue() throws NotRootException;

    public Generic antiderivative(int n) throws NotIntegrableException {
        return null;
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

    abstract void bodyToMathML(MathML element, boolean fenced);
}
