package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

public class Curl extends VectorOperator {
    public Curl(Generic vector, Generic variable) {
        super("curl",new Generic[] {vector,variable});
    }

    public Generic compute() {
        Variable variable[]=variables(parameter[1]);
        if(parameter[0] instanceof JSCLVector) {
            JSCLVector vector=(JSCLVector)parameter[0];
            return vector.curl(variable);
        }
        return expressionValue();
    }

    protected void bodyToMathML(MathML element) {
        operator(element,"nabla");
        MathML e1=element.element("mo");
        e1.appendChild(element.text("\u2227"));
        element.appendChild(e1);
        parameter[0].toMathML(element,null);
    }

    protected Variable newinstance() {
        return new Curl(null,null);
    }
}
