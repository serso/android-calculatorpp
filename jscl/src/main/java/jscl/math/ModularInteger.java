package jscl.math;

import jscl.math.function.Constant;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;

public class ModularInteger extends Generic implements Field {
    public static final ModularInteger booleanFactory = new ModularInteger(0, 2);
    final int modulo;
    final int content;

    public ModularInteger(long content, int modulo) {
        this.modulo = modulo;
        this.content = (int) (content % modulo);
    }

    public static ModularInteger factory(int modulo) {
        return new ModularInteger(0, modulo);
    }

    public int content() {
        return content;
    }

    public int modulo() {
        return modulo;
    }

    public ModularInteger add(ModularInteger integer) {
        return newinstance((long) content + integer.content);
    }

    @Nonnull
    public Generic add(@Nonnull Generic that) {
        return add((ModularInteger) that);
    }

    public ModularInteger subtract(ModularInteger integer) {
        return newinstance((long) content + (modulo - integer.content));
    }

    @Nonnull
    public Generic subtract(@Nonnull Generic that) {
        return subtract((ModularInteger) that);
    }

    public ModularInteger multiply(ModularInteger integer) {
        return newinstance((long) content * integer.content);
    }

    @Nonnull
    public Generic multiply(@Nonnull Generic that) {
        return multiply((ModularInteger) that);
    }

    @Nonnull
    public Generic divide(@Nonnull Generic that) throws NotDivisibleException {
        return multiply(that.inverse());
    }

    public Generic inverse() {
        return newinstance(BigInteger.valueOf(content).modInverse(BigInteger.valueOf(modulo)).intValue());
    }

    public Generic gcd(@Nonnull Generic generic) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public Generic gcd() {
        throw new UnsupportedOperationException();
    }

    public Generic pow(int exponent) {
        throw new UnsupportedOperationException();
    }

    public Generic negate() {
        return newinstance(modulo - content);
    }

    public int signum() {
        return content > 0 ? 1 : 0;
    }

    public int degree() {
        return 0;
    }

    public Generic antiDerivative(@Nonnull Variable variable) throws NotIntegrableException {
        throw new UnsupportedOperationException();
    }

    public Generic derivative(@Nonnull Variable variable) {
        throw new UnsupportedOperationException();
    }

    public Generic substitute(@Nonnull Variable variable, Generic generic) {
        throw new UnsupportedOperationException();
    }

    public Generic expand() {
        throw new UnsupportedOperationException();
    }

    public Generic factorize() {
        throw new UnsupportedOperationException();
    }

    public Generic elementary() {
        throw new UnsupportedOperationException();
    }

    public Generic simplify() {
        throw new UnsupportedOperationException();
    }

    public Generic numeric() {
        throw new UnsupportedOperationException();
    }

    public Generic valueOf(Generic generic) {
        if (generic instanceof ModularInteger) {
            return newinstance(((ModularInteger) generic).content);
        } else {
            return newinstance(((JsclInteger) generic).content().mod(BigInteger.valueOf(modulo)).intValue());
        }
    }

    public Generic[] sumValue() {
        throw new UnsupportedOperationException();
    }

    public Generic[] productValue() throws NotProductException {
        throw new UnsupportedOperationException();
    }

    public Power powerValue() throws NotPowerException {
        throw new UnsupportedOperationException();
    }

    public Expression expressionValue() throws NotExpressionException {
        return Expression.valueOf(integerValue());
    }

    public JsclInteger integerValue() throws NotIntegerException {
        return JsclInteger.valueOf(content);
    }

    @Override
    public double doubleValue() throws NotDoubleException {
        return content;
    }

    @Override
    public boolean isInteger() {
        return true;
    }

    public Variable variableValue() throws NotVariableException {
        throw new UnsupportedOperationException();
    }

    public Variable[] variables() {
        throw new UnsupportedOperationException();
    }

    public boolean isPolynomial(@Nonnull Variable variable) {
        throw new UnsupportedOperationException();
    }

    public boolean isConstant(@Nonnull Variable variable) {
        throw new UnsupportedOperationException();
    }

    public int compareTo(ModularInteger integer) {
        return content < integer.content ? -1 : content > integer.content ? 1 : 0;
    }

    public int compareTo(Generic generic) {
        if (generic instanceof ModularInteger) {
            return compareTo((ModularInteger) generic);
        } else if (generic instanceof JsclInteger) {
            return compareTo(valueOf(generic));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public String toString() {
        return "" + content;
    }

    public String toJava() {
        throw new UnsupportedOperationException();
    }

    public void toMathML(MathML element, Object data) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public Set<? extends Constant> getConstants() {
        return Collections.emptySet();
    }

    protected ModularInteger newinstance(long content) {
        return new ModularInteger(content, modulo);
    }
}
