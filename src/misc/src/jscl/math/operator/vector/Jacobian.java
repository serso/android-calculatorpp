package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

public class Jacobian extends VectorOperator {
    public Jacobian(Generic vector, Generic variable) {
        super("jacobian",new Generic[] {vector,variable});
    }

    public Generic compute() {
        Variable variable[]=variables(parameter[1]);
        if(parameter[0] instanceof JSCLVector) {
            JSCLVector vector=(JSCLVector)parameter[0];
            return vector.jacobian(variable);
        }
        return expressionValue();
    }

    protected void bodyToMathML(MathML element) {
        operator(element,"nabla");
        MathML e1=element.element("msup");
        parameter[0].toMathML(e1,null);
        MathML e2=element.element("mo");
        e2.appendChild(element.text("T"));
        e1.appendChild(e2);
        element.appendChild(e1);
    }

    protected void operator(MathML element, String name) {
        Variable variable[]=variables(GenericVariable.content(parameter[1]));
        MathML e1=element.element("msubsup");
        new Constant(name).toMathML(e1,null);
        MathML e2=element.element("mrow");
        for(int i=0;i<variable.length;i++) variable[i].expressionValue().toMathML(e2,null);
        e1.appendChild(e2);
        e2=element.element("mo");
        e2.appendChild(element.text("T"));
        e1.appendChild(e2);
        element.appendChild(e1);
    }

    protected Variable newinstance() {
        return new Jacobian(null,null);
    }
}
