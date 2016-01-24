package jscl.math;

import jscl.JsclMathEngine;
import jscl.math.function.Constant;
import jscl.math.function.Constants;
import jscl.math.function.IConstant;
import jscl.math.numeric.*;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public final class NumericWrapper extends Generic implements INumeric<NumericWrapper> {

    @Nonnull
    private final Numeric content;

    public NumericWrapper(@Nonnull JsclInteger integer) {
        content = Real.valueOf(integer.content().doubleValue());
    }

    public NumericWrapper(@Nonnull Rational rational) {
        content = Real.valueOf(rational.numerator().doubleValue() / rational.denominator().doubleValue());
    }

    public NumericWrapper(@Nonnull JsclVector vector) {
        final Numeric elements[] = new Numeric[vector.rows];

        for (int i = 0; i < vector.rows; i++) {
            elements[i] = ((NumericWrapper) vector.elements[i].numeric()).content();
        }

        content = new Vector(elements);
    }

    public NumericWrapper(@Nonnull Matrix matrix) {
        final Numeric elements[][] = new Numeric[matrix.rows][matrix.cols];

        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.cols; j++) {
                elements[i][j] = ((NumericWrapper) matrix.elements[i][j].numeric()).content();
            }
        }

        content = new jscl.math.numeric.Matrix(elements);
    }

    public NumericWrapper(@Nonnull Constant constant) {
        final IConstant constantFromRegistry = JsclMathEngine.getInstance().getConstantsRegistry().get(constant.getName());

        if (constantFromRegistry != null) {
            if (constantFromRegistry.getName().equals(Constants.I.getName())) {
                content = Complex.I;
            } else {
                if (constantFromRegistry.getValue() != null) {
                    final Double value = constantFromRegistry.getDoubleValue();
                    if (value == null) {
                        throw new ArithmeticException("Constant " + constant.getName() + " has invalid definition: " + constantFromRegistry.getValue());
                    } else {
                        content = Real.valueOf(value);
                    }
                } else {
                    throw new ArithmeticException("Could not create numeric wrapper: constant in registry doesn't have specified value: " + constant.getName());
                }
            }
        } else {
            throw new ArithmeticException("Could not create numeric wrapper: constant is not registered in constants registry: " + constant.getName());
        }
    }

    public NumericWrapper(@Nonnull Numeric numeric) {
        content = numeric;
    }

    public static Generic root(int subscript, Generic parameter[]) {
        Numeric param[] = new Numeric[parameter.length];
        for (int i = 0; i < param.length; i++) param[i] = ((NumericWrapper) parameter[i]).content;
        return new NumericWrapper(Numeric.root(subscript, param));
    }

    public Numeric content() {
        return content;
    }

    public NumericWrapper add(NumericWrapper wrapper) {
        return new NumericWrapper(content.add(wrapper.content));
    }

    @Nonnull
    public Generic add(@Nonnull Generic that) {
        if (that instanceof Expression) {
            return that.add(this);
        } else if (that instanceof NumericWrapper) {
            return add((NumericWrapper) that);
        } else {
            return add(valueOf(that));
        }
    }

    public NumericWrapper subtract(NumericWrapper wrapper) {
        return new NumericWrapper(content.subtract(wrapper.content));
    }

    @Nonnull
    public Generic subtract(@Nonnull Generic that) {
        if (that instanceof Expression) {
            return that.add(this);
        } else if (that instanceof NumericWrapper) {
            return subtract((NumericWrapper) that);
        } else {
            return subtract(valueOf(that));
        }
    }

    public NumericWrapper multiply(NumericWrapper wrapper) {
        return new NumericWrapper(content.multiply(wrapper.content));
    }

    @Nonnull
    public Generic multiply(@Nonnull Generic that) {
        if (that instanceof Expression) {
            return that.add(this);
        } else if (that instanceof NumericWrapper) {
            return multiply((NumericWrapper) that);
        } else {
            return multiply(valueOf(that));
        }
    }

    public NumericWrapper divide(NumericWrapper wrapper) throws ArithmeticException {
        return new NumericWrapper(content.divide(wrapper.content));
    }

    @Nonnull
    public Generic divide(@Nonnull Generic that) throws NotDivisibleException {
        if (that instanceof Expression) {
            return that.add(this);
        } else if (that instanceof NumericWrapper) {
            return divide((NumericWrapper) that);
        } else {
            return divide(valueOf(that));
        }
    }

    public Generic gcd(@Nonnull Generic generic) {
        return null;
    }

    @Nonnull
    public Generic gcd() {
        return null;
    }

    @Nonnull
    public NumericWrapper abs() {
        return new NumericWrapper(content.abs());
    }

    @Nonnull
    public NumericWrapper negate() {
        return new NumericWrapper(content.negate());
    }

    public int signum() {
        return content.signum();
    }

    public int degree() {
        return 0;
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
        return this;
    }

    public NumericWrapper valueOf(NumericWrapper wrapper) {
        return new NumericWrapper(content.valueOf(wrapper.content));
    }

    public Generic valueOf(@Nonnull Generic generic) {
        if (generic instanceof NumericWrapper) {
            return valueOf((NumericWrapper) generic);
        } else if (generic instanceof JsclInteger) {
            return new NumericWrapper((JsclInteger) generic);
        } else {
            throw new ArithmeticException("Could not create numeric wrapper for class: " + generic.getClass());
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
        throw new NotExpressionException();
    }

    public JsclInteger integerValue() throws NotIntegerException {
        if (content instanceof Real) {
            double doubleValue = ((Real) content).doubleValue();
            if (Math.floor(doubleValue) == doubleValue) {
                return JsclInteger.valueOf((int) doubleValue);
            } else {
                throw new NotIntegerException();
            }
        } else {
            throw new NotIntegerException();
        }
    }

    @Override
    public boolean isInteger() {
        if (content instanceof Real) {
            double value = ((Real) content).doubleValue();
            return Math.floor(value) == value;
        }
        return false;
    }

    public Variable variableValue() throws NotVariableException {
        throw new NotVariableException();
    }

    public Variable[] variables() {
        return new Variable[0];
    }

    public boolean isPolynomial(@Nonnull Variable variable) {
        return true;
    }

    public boolean isConstant(@Nonnull Variable variable) {
        return true;
    }

    @Nonnull
    public NumericWrapper sgn() {
        return new NumericWrapper(content.sgn());
    }

    @Nonnull
    public NumericWrapper ln() {
        return new NumericWrapper(content.ln());
    }

    @Nonnull
    public NumericWrapper lg() {
        return new NumericWrapper(content.lg());
    }

    @Nonnull
    public NumericWrapper exp() {
        return new NumericWrapper(content.exp());
    }

    @Nonnull
    public NumericWrapper inverse() {
        return new NumericWrapper(content.inverse());
    }

    @Nonnull
    public NumericWrapper pow(int exponent) {
        return new NumericWrapper(content.pow(exponent));
    }

    public Generic pow(Generic generic) {
        return new NumericWrapper(content.pow(((NumericWrapper) generic).content));
    }

    @Nonnull
    public NumericWrapper sqrt() {
        return new NumericWrapper(content.sqrt());
    }

    @Nonnull
    public NumericWrapper nThRoot(int n) {
        return new NumericWrapper(content.nThRoot(n));
    }

    public Generic conjugate() {
        return new NumericWrapper(content.conjugate());
    }

    @Nonnull
    public NumericWrapper acos() {
        return new NumericWrapper(content.acos());
    }

    @Nonnull
    public NumericWrapper asin() {
        return new NumericWrapper(content.asin());
    }

    @Nonnull
    public NumericWrapper atan() {
        return new NumericWrapper(content.atan());
    }

    @Nonnull
    public NumericWrapper acot() {
        return new NumericWrapper(content.acot());
    }

    @Nonnull
    public NumericWrapper cos() {
        return new NumericWrapper(content.cos());
    }

    @Nonnull
    public NumericWrapper sin() {
        return new NumericWrapper(content.sin());
    }

    @Nonnull
    public NumericWrapper tan() {
        return new NumericWrapper(content.tan());
    }

    @Nonnull
    public NumericWrapper cot() {
        return new NumericWrapper(content.cot());
    }

    @Nonnull
    public NumericWrapper acosh() {
        return new NumericWrapper(content.acosh());
    }

    @Nonnull
    public NumericWrapper asinh() {
        return new NumericWrapper(content.asinh());
    }

    @Nonnull
    public NumericWrapper atanh() {
        return new NumericWrapper(content.atanh());
    }

    @Nonnull
    public NumericWrapper acoth() {
        return new NumericWrapper(content.acoth());
    }

    @Nonnull
    public NumericWrapper cosh() {
        return new NumericWrapper(content.cosh());
    }

    @Nonnull
    public NumericWrapper sinh() {
        return new NumericWrapper(content.sinh());
    }

    @Nonnull
    public NumericWrapper tanh() {
        return new NumericWrapper(content.tanh());
    }

    @Nonnull
    public NumericWrapper coth() {
        return new NumericWrapper(content.coth());
    }

    public int compareTo(NumericWrapper wrapper) {
        return content.compareTo(wrapper.content);
    }

    public int compareTo(Generic generic) {
        if (generic instanceof NumericWrapper) {
            return compareTo((NumericWrapper) generic);
        } else {
            return compareTo(valueOf(generic));
        }
    }

    public String toString() {
        return content.toString();
    }

    public String toJava() {
        return "JsclDouble.valueOf(" + new Double(((Real) content).doubleValue()) + ")";
    }

    public void toMathML(MathML element, Object data) {
        int exponent = data instanceof Integer ? (Integer) data : 1;
        if (exponent == 1) bodyToMathML(element);
        else {
            MathML e1 = element.element("msup");
            bodyToMathML(e1);
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    @Nonnull
    @Override
    public Set<? extends Constant> getConstants() {
        return Collections.emptySet();
    }

    void bodyToMathML(MathML element) {
        MathML e1 = element.element("mn");
        e1.appendChild(element.text(String.valueOf(new Double(((Real) content).doubleValue()))));
        element.appendChild(e1);
    }
}
