package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class MatrixProduct extends VectorOperator {

    public static final String NAME = "matrix";

    public MatrixProduct(Generic matrix1, Generic matrix2) {
        super(NAME, new Generic[]{matrix1, matrix2});
    }

    private MatrixProduct(Generic parameter[]) {
        super(NAME, parameter);
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    public Generic selfExpand() {
        if (Matrix.isMatrixProduct(parameters[0], parameters[1])) {
            return parameters[0].multiply(parameters[1]);
        }
        return expressionValue();
    }

    public String toJava() {
        final StringBuilder result = new StringBuilder();
        result.append(parameters[0].toJava());
        result.append(".multiply(");
        result.append(parameters[1].toJava());
        result.append(")");
        return result.toString();
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new MatrixProduct(parameters);
    }

    protected void bodyToMathML(MathML element) {
        parameters[0].toMathML(element, null);
        parameters[1].toMathML(element, null);
    }

    @Nonnull
    public Variable newInstance() {
        return new MatrixProduct(null, null);
    }
}
