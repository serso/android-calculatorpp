package jscl.math.operator.vector;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

public class Grad extends VectorOperator {
    public Grad(Generic expression, Generic variable) {
        super("grad",new Generic[] {expression,variable});
    }

    public Generic compute() {
        Variable variable[]=variables(parameter[1]);
        Expression expression=parameter[0].expressionValue();
        return expression.grad(variable);
    }

    protected void bodyToMathML(MathML element) {
        operator(element,"nabla");
        parameter[0].toMathML(element,null);
    }

    protected Variable newinstance() {
        return new Grad(null,null);
    }
}
