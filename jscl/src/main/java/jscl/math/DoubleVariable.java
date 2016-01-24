package jscl.math;

import javax.annotation.Nonnull;

public class DoubleVariable extends GenericVariable {

    public DoubleVariable(Generic generic) {
        super(generic);
    }

    public JsclInteger symbolic() {
        return content.integerValue();
    }

    public Generic antiDerivative(Variable variable) throws NotIntegrableException {
        return expressionValue().multiply(variable.expressionValue());
    }

    @Nonnull
    public Generic derivative(Variable variable) {
        return JsclInteger.valueOf(0);
    }

    public Generic substitute(Variable variable, Generic generic) {
        if (isIdentity(variable)) return generic;
        else return expressionValue();
    }

    public Generic expand() {
        return expressionValue();
    }

    public Generic factorize() {
        return expressionValue();
    }

    public Generic elementary() {
        return expressionValue();
    }

    public Generic simplify() {
        return expressionValue();
    }

    @Nonnull
    public Variable newInstance() {
        return new DoubleVariable(null);
    }
}
