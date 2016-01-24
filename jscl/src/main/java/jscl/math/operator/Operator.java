package jscl.math.operator;

import jscl.math.*;

import javax.annotation.Nonnull;

public abstract class Operator extends AbstractFunction {

    protected Operator(String name, Generic parameters[]) {
        super(name, parameters);
    }

    @Nonnull
    protected static Variable[] toVariables(@Nonnull Generic vector) throws NotVariableException {
        return toVariables((JsclVector) vector);
    }

    @Nonnull
    protected static Variable[] toVariables(@Nonnull JsclVector vector) throws NotVariableException {
        final Generic element[] = vector.elements();
        final Variable variable[] = new Variable[element.length];

        for (int i = 0; i < element.length; i++) {
            variable[i] = element[i].variableValue();
        }

        return variable;
    }

    public Generic antiDerivative(Variable variable) throws NotIntegrableException {
        throw new NotIntegrableException(this);
    }

    @Nonnull
    public Generic derivative(Variable variable) {
        if (isIdentity(variable)) {
            return JsclInteger.valueOf(1);
        } else {
            return JsclInteger.valueOf(0);
        }
    }

    @Override
    public Generic selfElementary() {
        return expressionValue();
    }

    @Override
    public Generic selfSimplify() {
        return expressionValue();
    }

    @Override
    public Generic selfNumeric() {
        return numeric();
    }

    public Generic numeric() {
        throw new ArithmeticException();
    }

    public boolean isConstant(Variable variable) {
        return !isIdentity(variable);
    }

    @Nonnull
    public abstract Operator newInstance(@Nonnull Generic[] parameters);
}
