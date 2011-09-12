package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

public class ComplexProduct extends VectorOperator {
    public ComplexProduct(Generic vector1, Generic vector2) {
        super("complex",new Generic[] {vector1,vector2});
    }

    public Generic compute() {
        if(parameter[0] instanceof JSCLVector && parameter[1] instanceof JSCLVector) {
            JSCLVector v1=(JSCLVector)parameter[0];
            JSCLVector v2=(JSCLVector)parameter[1];
            return v1.complexProduct(v2);
        }
        return expressionValue();
    }

    protected void bodyToMathML(MathML element) {
        parameter[0].toMathML(element,null);
        parameter[1].toMathML(element,null);
    }

    protected Variable newinstance() {
        return new ComplexProduct(null,null);
    }
}
