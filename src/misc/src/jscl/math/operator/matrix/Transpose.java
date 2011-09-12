package jscl.math.operator.matrix;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.mathml.MathML;

public class Transpose extends Operator {
    public Transpose(Generic matrix) {
        super("tran",new Generic[] {matrix});
    }

    public Generic compute() {
        if(parameter[0] instanceof Matrix) {
            Matrix matrix=(Matrix)parameter[0];
            return matrix.transpose();
        }
        return expressionValue();
    }

    public void toMathML(MathML element, Object data) {
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) bodyToMathML(element);
        else {
            MathML e1=element.element("msup");
            bodyToMathML(e1);
            MathML e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    void bodyToMathML(MathML element) {
        MathML e1=element.element("msup");
        parameter[0].toMathML(e1,null);
        MathML e2=element.element("mo");
        e2.appendChild(element.text("T"));
        e1.appendChild(e2);
        element.appendChild(e1);
    }

    protected Variable newinstance() {
        return new Transpose(null);
    }
}
