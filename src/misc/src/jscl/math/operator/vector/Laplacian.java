package jscl.math.operator.vector;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

public class Laplacian extends VectorOperator {
    public Laplacian(Generic vector, Generic variable) {
        super("laplacian",new Generic[] {vector,variable});
    }

    public Generic compute() {
        Variable variable[]=variables(parameter[1]);
        Expression expression=parameter[0].expressionValue();
        return expression.laplacian(variable);
    }

    protected void bodyToMathML(MathML element) {
        operator(element,"Delta");
        parameter[0].toMathML(element,null);
    }

    protected Variable newinstance() {
        return new Laplacian(null,null);
    }
}
