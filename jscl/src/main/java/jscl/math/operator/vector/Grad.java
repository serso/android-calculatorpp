package jscl.math.operator.vector;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JsclVector;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class Grad extends VectorOperator {

    public static final String NAME = "grad";

    public Grad(Generic expression, Generic variable) {
        super(NAME, new Generic[]{expression, variable});
    }

    private Grad(Generic parameter[]) {
        super(NAME, parameter);
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    public Generic selfExpand() {
        Variable variable[] = toVariables((JsclVector) parameters[1]);
        Expression expression = parameters[0].expressionValue();
        return expression.grad(variable);
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Grad(parameters);
    }

    protected void bodyToMathML(MathML element) {
        operator(element, "nabla");
        parameters[0].toMathML(element, null);
    }

    @Nonnull
    public Variable newInstance() {
        return new Grad(null, null);
    }
}
