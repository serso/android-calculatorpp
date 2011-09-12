package jscl.math.operator.matrix;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.mathml.MathML;

public class Determinant extends Operator {
    public Determinant(Generic matrix) {
        super("det",new Generic[] {matrix});
    }

    public Generic compute() {
        if(parameter[0] instanceof Matrix) {
            Matrix matrix=(Matrix)parameter[0];
            return matrix.determinant();
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

    void bodyToMathML(MathML e0) {
        Generic m=GenericVariable.content(parameter[0]);
        MathML e1=e0.element("mfenced");
        e1.setAttribute("open","|");
        e1.setAttribute("close","|");
        if(m instanceof Matrix) {
            Generic element[][]=((Matrix)m).elements();
            MathML e2=e0.element("mtable");
            for(int i=0;i<element.length;i++) {
                MathML e3=e0.element("mtr");
                for(int j=0;j<element.length;j++) {
                    MathML e4=e0.element("mtd");
                    element[i][j].toMathML(e4,null);
                    e3.appendChild(e4);
                }
                e2.appendChild(e3);
            }
            e1.appendChild(e2);
        } else m.toMathML(e1,null);
        e0.appendChild(e1);
    }

    protected Variable newinstance() {
        return new Determinant(null);
    }
}
