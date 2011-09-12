package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

public class TensorProduct extends VectorOperator {
    public TensorProduct(Generic matrix1, Generic matrix2) {
        super("tensor",new Generic[] {matrix1,matrix2});
    }

    public Generic compute() {
        if(parameter[0] instanceof Matrix && parameter[1] instanceof Matrix) {
            Matrix m1=(Matrix)parameter[0];
            Matrix m2=(Matrix)parameter[1];
            return m1.tensorProduct(m2);
        }
        return expressionValue();
    }

    protected void bodyToMathML(MathML element) {
        parameter[0].toMathML(element,null);
        MathML e1=element.element("mo");
        e1.appendChild(element.text(/*"\u2A2F"*/"*"));
        element.appendChild(e1);
        parameter[1].toMathML(element,null);
    }

    protected Variable newinstance() {
        return new TensorProduct(null,null);
    }
}
