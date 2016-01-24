package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class TensorProduct extends VectorOperator {

    public static final String NAME = "tensor";

    public TensorProduct(Generic matrix1, Generic matrix2) {
        super(NAME, new Generic[]{matrix1, matrix2});
    }

    private TensorProduct(Generic parameter[]) {
        super(NAME, parameter);
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    public Generic selfExpand() {
        if (parameters[0] instanceof Matrix && parameters[1] instanceof Matrix) {
            Matrix m1 = (Matrix) parameters[0];
            Matrix m2 = (Matrix) parameters[1];
            return m1.tensorProduct(m2);
        }
        return expressionValue();
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new TensorProduct(parameters);
    }

    protected void bodyToMathML(MathML element) {
        parameters[0].toMathML(element, null);
        MathML e1 = element.element("mo");
        e1.appendChild(element.text(/*"\u2A2F"*/"*"));
        element.appendChild(e1);
        parameters[1].toMathML(element, null);
    }

    @Nonnull
    public Variable newInstance() {
        return new TensorProduct(null, null);
    }
}
