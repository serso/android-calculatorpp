package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JsclVector;
import jscl.math.Variable;
import jscl.math.polynomial.Polynomial;

import javax.annotation.Nonnull;

public class Coefficient extends Operator {

    public static final String NAME = "coef";

    public Coefficient(Generic expression, Generic variable) {
        super(NAME, new Generic[]{expression, variable});
    }

    private Coefficient(Generic parameters[]) {
        super(NAME, parameters);
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    public Generic selfExpand() {
        Variable variable = parameters[1].variableValue();
        if (parameters[0].isPolynomial(variable)) {
            return new JsclVector(Polynomial.factory(variable).valueOf(parameters[0]).elements());
        }
        return expressionValue();
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Coefficient(parameters);
    }

    @Nonnull
    public Variable newInstance() {
        return new Coefficient(null, null);
    }
}
