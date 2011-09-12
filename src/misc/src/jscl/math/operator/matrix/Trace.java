package jscl.math.operator.matrix;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.mathml.MathML;

public class Trace extends Operator {
    public Trace(Generic matrix) {
        super("trace",new Generic[] {matrix});
    }

    public Generic compute() {
        if(parameter[0] instanceof Matrix) {
            Matrix matrix=(Matrix)parameter[0];
            return matrix.trace();
        }
        return expressionValue();
    }

    public void toMathML(MathML element, Object data) {
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) {
            MathML e1=element.element("mo");
            e1.appendChild(element.text("tr"));
            element.appendChild(e1);
        }
        else {
            MathML e1=element.element("msup");
            MathML e2=element.element("mo");
            e2.appendChild(element.text("tr"));
            e1.appendChild(e2);
            e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
        parameter[0].toMathML(element,null);
    }

    protected Variable newinstance() {
        return new Trace(null);
    }
}
