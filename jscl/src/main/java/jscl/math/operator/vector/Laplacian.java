package jscl.math.operator.vector;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class Laplacian extends VectorOperator {

    public static final String NAME = "laplacian";

    public Laplacian(Generic vector, Generic variable) {
        super(NAME, new Generic[]{vector, variable});
    }

    private Laplacian(Generic parameter[]) {
        super(NAME, parameter);
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    public Generic selfExpand() {
        Variable variable[] = toVariables(parameters[1]);
        Expression expression = parameters[0].expressionValue();
        return expression.laplacian(variable);
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Laplacian(parameters);
    }

    protected void bodyToMathML(MathML element) {
        operator(element, "Delta");
        parameters[0].toMathML(element, null);
    }

    @Nonnull
    public Variable newInstance() {
        return new Laplacian(null, null);
    }
}
