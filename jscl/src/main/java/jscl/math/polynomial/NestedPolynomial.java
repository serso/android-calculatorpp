package jscl.math.polynomial;

import jscl.math.*;
import jscl.math.function.Constant;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;
import java.util.Set;

class NestedPolynomial extends UnivariatePolynomial {
    NestedPolynomial(Variable variable[]) {
        this(variable[0], PolynomialWrapper.factory(variable));
    }

    NestedPolynomial(Variable variable, Generic coefFactory) {
        super(variable, coefFactory);
    }

    protected UnivariatePolynomial newinstance() {
        return new NestedPolynomial(variable, coefFactory);
    }
}

final class PolynomialWrapper extends Generic {
    final Polynomial content;

    PolynomialWrapper(Polynomial polynomial) {
        content = polynomial;
    }

    public static Generic factory(Variable variable[]) {
        if (variable.length > 1) {
            Variable var[] = new Variable[variable.length - 1];
            for (int i = 0; i < var.length; i++) var[i] = variable[i + 1];
            return new PolynomialWrapper(NestedPolynomial.factory(var));
        } else return null;
    }

    Polynomial content() {
        return content;
    }

    public PolynomialWrapper add(PolynomialWrapper wrapper) {
        return new PolynomialWrapper(content.add(wrapper.content));
    }

    @Nonnull
    public Generic add(@Nonnull Generic that) {
        if (that instanceof PolynomialWrapper) {
            return add((PolynomialWrapper) that);
        } else {
            return add(valueOf(that));
        }
    }

    public PolynomialWrapper subtract(PolynomialWrapper wrapper) {
        return new PolynomialWrapper(content.subtract(wrapper.content));
    }

    @Nonnull
    public Generic subtract(@Nonnull Generic that) {
        if (that instanceof PolynomialWrapper) {
            return subtract((PolynomialWrapper) that);
        } else {
            return subtract(valueOf(that));
        }
    }

    public PolynomialWrapper multiply(PolynomialWrapper wrapper) {
        return new PolynomialWrapper(content.multiply(wrapper.content));
    }

    @Nonnull
    public Generic multiply(@Nonnull Generic that) {
        if (that instanceof PolynomialWrapper) {
            return multiply((PolynomialWrapper) that);
        } else {
            return multiply(valueOf(that));
        }
    }

    public PolynomialWrapper divide(PolynomialWrapper wrapper) throws ArithmeticException {
        return new PolynomialWrapper(content.divide(wrapper.content));
    }

    @Nonnull
    public Generic divide(@Nonnull Generic that) throws NotDivisibleException {
        if (that instanceof PolynomialWrapper) {
            return divide((PolynomialWrapper) that);
        } else {
            return divide(valueOf(that));
        }
    }

    public PolynomialWrapper gcd(PolynomialWrapper wrapper) {
        return new PolynomialWrapper(content.gcd(wrapper.content));
    }

    public Generic gcd(@Nonnull Generic generic) {
        if (generic instanceof PolynomialWrapper) {
            return gcd((PolynomialWrapper) generic);
        } else {
            return gcd(valueOf(generic));
        }
    }

    @Nonnull
    public Generic gcd() {
        return content.gcd();
    }

    public Generic negate() {
        return new PolynomialWrapper(content.negate());
    }

    public int signum() {
        return content.signum();
    }

    public int degree() {
        return content.degree();
    }

    public Generic antiDerivative(@Nonnull Variable variable) throws NotIntegrableException {
        return null;
    }

    public Generic derivative(@Nonnull Variable variable) {
        return null;
    }

    public Generic substitute(@Nonnull Variable variable, Generic generic) {
        return null;
    }

    public Generic expand() {
        return null;
    }

    public Generic factorize() {
        return null;
    }

    public Generic elementary() {
        return null;
    }

    public Generic simplify() {
        return null;
    }

    public Generic numeric() {
        return null;
    }

    public Generic valueOf(Generic generic) {
        if (generic instanceof PolynomialWrapper) {
            return new PolynomialWrapper(content.valueOf(((PolynomialWrapper) generic).content));
        } else {
            return new PolynomialWrapper(content.valueOf(generic));
        }
    }

    public Generic[] sumValue() {
        return null;
    }

    public Generic[] productValue() throws NotProductException {
        return null;
    }

    public Power powerValue() throws NotPowerException {
        return null;
    }

    public Expression expressionValue() throws NotExpressionException {
        return content.genericValue().expressionValue();
    }

    public JsclInteger integerValue() throws NotIntegerException {
        throw new NotIntegerException();
    }

    @Override
    public boolean isInteger() {
        return false;
    }

    public Variable variableValue() throws NotVariableException {
        throw new NotVariableException();
    }

    public Variable[] variables() {
        return new Variable[0];
    }

    public boolean isPolynomial(@Nonnull Variable variable) {
        return false;
    }

    public boolean isConstant(@Nonnull Variable variable) {
        return false;
    }

    public int compareTo(PolynomialWrapper wrapper) {
        return content.compareTo(wrapper.content);
    }

    public int compareTo(Generic generic) {
        if (generic instanceof PolynomialWrapper) {
            return compareTo((PolynomialWrapper) generic);
        } else {
            return compareTo(valueOf(generic));
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if (signum() < 0) buffer.append("-").append(negate());
        else buffer.append("(").append(content).append(")");
        return buffer.toString();
    }

    public String toJava() {
        return null;
    }

    public void toMathML(MathML element, Object data) {
    }

    @Nonnull
    @Override
    public Set<? extends Constant> getConstants() {
        return content.getConstants();
    }
}
