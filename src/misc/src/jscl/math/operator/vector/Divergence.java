package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

public class Divergence extends VectorOperator {
    public Divergence(Generic vector, Generic variable) {
        super("diverg",new Generic[] {vector,variable});
    }

    public Generic compute() {
        Variable variable[]=variables(parameter[1]);
        if(parameter[0] instanceof JSCLVector) {
            JSCLVector vector=(JSCLVector)parameter[0];
            return vector.divergence(variable);
        }
        return expressionValue();
    }

    protected void bodyToMathML(MathML element) {
        operator(element,"nabla");
        parameter[0].toMathML(element,null);
    }

    protected Variable newinstance() {
        return new Divergence(null,null);
    }
}
