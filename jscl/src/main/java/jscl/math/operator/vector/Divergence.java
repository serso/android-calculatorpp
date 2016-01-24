package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.JsclVector;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class Divergence extends VectorOperator {

    public static final String NAME = "diverg";

    public Divergence(Generic vector, Generic variable) {
        super(NAME, new Generic[]{vector, variable});
    }

    private Divergence(@Nonnull Generic parameter[]) {
        super(NAME, parameter);
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    public Generic selfExpand() {
        Variable variable[] = toVariables((JsclVector) parameters[1]);
        if (parameters[0] instanceof JsclVector) {
            JsclVector vector = (JsclVector) parameters[0];
            return vector.divergence(variable);
        }
        return expressionValue();
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Divergence(parameters);
    }

    protected void bodyToMathML(MathML element) {
        operator(element, "nabla");
        parameters[0].toMathML(element, null);
    }

    @Nonnull
    public Variable newInstance() {
        return new Divergence(null, null);
    }
}
