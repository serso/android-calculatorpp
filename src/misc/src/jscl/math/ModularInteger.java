package jscl.math;

import java.math.BigInteger;
import jscl.mathml.MathML;

public class ModularInteger extends Generic implements Field {
    public static final ModularInteger booleanFactory=new ModularInteger(0,2);
    final int modulo;
    final int content;

    public ModularInteger(long content, int modulo) {
        this.modulo=modulo;
        this.content=(int)(content%modulo);
    }

    public int content() {
        return content;
    }

    public int modulo() {
        return modulo;
    }

    public ModularInteger add(ModularInteger integer) {
        return newinstance((long)content+integer.content);
    }

    public Generic add(Generic generic) {
        return add((ModularInteger)generic);
    }

    public ModularInteger subtract(ModularInteger integer) {
        return newinstance((long)content+(modulo-integer.content));
    }

    public Generic subtract(Generic generic) {
        return subtract((ModularInteger)generic);
    }

    public ModularInteger multiply(ModularInteger integer) {
        return newinstance((long)content*integer.content);
    }

    public Generic multiply(Generic generic) {
        return multiply((ModularInteger)generic);
    }

    public Generic divide(Generic generic) throws ArithmeticException {
        return multiply(generic.inverse());
    }

    public Generic inverse() {
        return newinstance(BigInteger.valueOf(content).modInverse(BigInteger.valueOf(modulo)).intValue());
    }

    public Generic gcd(Generic generic) {
        throw new UnsupportedOperationException();
    }

    public Generic gcd() {
        throw new UnsupportedOperationException();
    }

    public Generic pow(int exponent) {
        throw new UnsupportedOperationException();
    }

    public Generic negate() {
        return newinstance(modulo-content);
    }

    public int signum() {
        return content>0?1:0;
    }

    public int degree() {
        return 0;
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        throw new UnsupportedOperationException();
    }

    public Generic derivative(Variable variable) {
        throw new UnsupportedOperationException();
    }

    public Generic substitute(Variable variable, Generic generic) {
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

    public Generic valueof(Generic generic) {
        if(generic instanceof ModularInteger) {
            return newinstance(((ModularInteger)generic).content);
        } else {
            return newinstance(((JSCLInteger)generic).content().mod(BigInteger.valueOf(modulo)).intValue());
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

    public JSCLInteger integerValue() throws NotIntegerException {
        return JSCLInteger.valueOf(content);
    }

    public Variable variableValue() throws NotVariableException {
        throw new UnsupportedOperationException();
    }

    public Variable[] variables() {
        throw new UnsupportedOperationException();
    }

    public boolean isPolynomial(Variable variable) {
        throw new UnsupportedOperationException();
    }

    public boolean isConstant(Variable variable) {
        throw new UnsupportedOperationException();
    }

    public int compareTo(ModularInteger integer) {
        return content<integer.content?-1:content>integer.content?1:0;
    }

    public int compareTo(Generic generic) {
        if(generic instanceof ModularInteger) {
            return compareTo((ModularInteger)generic);
        } else if(generic instanceof JSCLInteger) {
            return compareTo(valueof(generic));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static ModularInteger factory(int modulo) {
        return new ModularInteger(0,modulo);
    }

    public String toString() {
        return ""+content;
    }

    public String toJava() {
        throw new UnsupportedOperationException();
    }

    public void toMathML(MathML element, Object data) {
        throw new UnsupportedOperationException();
    }

    protected ModularInteger newinstance(long content) {
        return new ModularInteger(content,modulo);
    }
}
