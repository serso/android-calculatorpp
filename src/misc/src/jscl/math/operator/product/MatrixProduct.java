package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

public class MatrixProduct extends VectorOperator {
    public MatrixProduct(Generic matrix1, Generic matrix2) {
        super("matrix",new Generic[] {matrix1,matrix2});
    }

    public Generic compute() {
        if(Matrix.product(parameter[0],parameter[1])) {
            return parameter[0].multiply(parameter[1]);
        }
        return expressionValue();
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(parameter[0].toJava());
        buffer.append(".multiply(");
        buffer.append(parameter[1].toJava());
        buffer.append(")");
        return buffer.toString();
    }

    protected void bodyToMathML(MathML element) {
        parameter[0].toMathML(element,null);
        parameter[1].toMathML(element,null);
    }

    protected Variable newinstance() {
        return new MatrixProduct(null,null);
    }
}
