package jscl.math.operator.matrix;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class Trace extends Operator {

    public static final String NAME = "trace";

    public Trace(Generic matrix) {
        super(NAME, new Generic[]{matrix});
    }

    private Trace(Generic parameters[]) {
        super(NAME, parameters);
    }

    @Override
    public int getMinParameters() {
        return 1;
    }

    public Generic selfExpand() {
        if (parameters[0] instanceof Matrix) {
            Matrix matrix = (Matrix) parameters[0];
            return matrix.trace();
        }
        return expressionValue();
    }

    public void toMathML(MathML element, Object data) {
        int exponent = data instanceof Integer ? (Integer) data : 1;
        if (exponent == 1) {
            MathML e1 = element.element("mo");
            e1.appendChild(element.text("tr"));
            element.appendChild(e1);
        } else {
            MathML e1 = element.element("msup");
            MathML e2 = element.element("mo");
            e2.appendChild(element.text("tr"));
            e1.appendChild(e2);
            e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
        parameters[0].toMathML(element, null);
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Trace(parameters);
    }

    @Nonnull
    public Variable newInstance() {
        return new Trace((Matrix) null);
    }
}
