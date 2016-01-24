package jscl.math.operator.matrix;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class Determinant extends Operator {

    public static final String NAME = "det";

    public Determinant(Generic matrix) {
        super(NAME, new Generic[]{matrix});
    }

    private Determinant(Generic parameters[]) {
        super(NAME, parameters);
    }

    @Override
    public int getMinParameters() {
        return 1;
    }

    public Generic selfExpand() {
        if (parameters[0] instanceof Matrix) {
            Matrix matrix = (Matrix) parameters[0];
            return matrix.determinant();
        }
        return expressionValue();
    }

    public void toMathML(MathML element, Object data) {
        int exponent = data instanceof Integer ? (Integer) data : 1;
        if (exponent == 1) bodyToMathML(element);
        else {
            MathML e1 = element.element("msup");
            bodyToMathML(e1);
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Determinant(parameters);
    }

    void bodyToMathML(MathML e0) {
        Generic m = GenericVariable.content(parameters[0]);
        MathML e1 = e0.element("mfenced");
        e1.setAttribute("open", "|");
        e1.setAttribute("close", "|");
        if (m instanceof Matrix) {
            Generic element[][] = ((Matrix) m).elements();
            MathML e2 = e0.element("mtable");
            for (int i = 0; i < element.length; i++) {
                MathML e3 = e0.element("mtr");
                for (int j = 0; j < element.length; j++) {
                    MathML e4 = e0.element("mtd");
                    element[i][j].toMathML(e4, null);
                    e3.appendChild(e4);
                }
                e2.appendChild(e3);
            }
            e1.appendChild(e2);
        } else m.toMathML(e1, null);
        e0.appendChild(e1);
    }

    @Nonnull
    public Variable newInstance() {
        return new Determinant((Matrix) null);
    }
}
