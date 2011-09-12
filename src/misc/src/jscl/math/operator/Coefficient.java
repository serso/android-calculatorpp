package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.polynomial.Polynomial;

public class Coefficient extends Operator {
    public Coefficient(Generic expression, Generic variable) {
        super("coef",new Generic[] {expression,variable});
    }

    public Generic compute() {
        Variable variable=parameter[1].variableValue();
        if(parameter[0].isPolynomial(variable)) {
            return new JSCLVector(Polynomial.factory(variable).valueof(parameter[0]).elements());
        }
        return expressionValue();
    }

    protected Variable newinstance() {
        return new Coefficient(null,null);
    }
}
