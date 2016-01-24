package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JsclVector;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.math.operator.Operator;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class Jacobian extends VectorOperator {

    public static final String NAME = "jacobian";

    public Jacobian(Generic vector, Generic variable) {
        super(NAME, new Generic[]{vector, variable});
    }

    private Jacobian(Generic parameter[]) {
        super(NAME, parameter);
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    public Generic selfExpand() {
        Variable variable[] = toVariables(parameters[1]);
        if (parameters[0] instanceof JsclVector) {
            JsclVector vector = (JsclVector) parameters[0];
            return vector.jacobian(variable);
        }
        return expressionValue();
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Jacobian(parameters);
    }

    protected void bodyToMathML(MathML element) {
        operator(element, "nabla");
        MathML e1 = element.element("msup");
        parameters[0].toMathML(e1, null);
        MathML e2 = element.element("mo");
        e2.appendChild(element.text("T"));
        e1.appendChild(e2);
        element.appendChild(e1);
    }

    protected void operator(MathML element, String name) {
        Variable variable[] = toVariables(GenericVariable.content(parameters[1]));
        MathML e1 = element.element("msubsup");
        new Constant(name).toMathML(e1, null);
        MathML e2 = element.element("mrow");
        for (int i = 0; i < variable.length; i++) variable[i].expressionValue().toMathML(e2, null);
        e1.appendChild(e2);
        e2 = element.element("mo");
        e2.appendChild(element.text("T"));
        e1.appendChild(e2);
        element.appendChild(e1);
    }

    @Nonnull
    public Variable newInstance() {
        return new Jacobian(null, null);
    }
}
