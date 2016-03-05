package jscl.math.numeric;

import jscl.math.NotDivisibleException;

import javax.annotation.Nonnull;
import java.math.BigInteger;

public final class Real extends Numeric {

    public static final Real ZERO = new Real(0d);
    public static final Real ONE = new Real(1d);
    public static final Real TWO = new Real(2d);
    private final static Real PI_DIV_BY_2_RAD = Real.valueOf(Math.PI).divide(TWO);
    private final static Double PI_DIV_BY_2_RAD_DOUBLE = Math.PI / 2;
    private final double content;

    Real(double val) {
        content = val;
    }

    public static int signum(double value) {
        return value == 0. ? 0 : (value < 0. ? -1 : 1);
    }

    public static Real valueOf(double value) {
        if (value == 0d) {
            return ZERO;
        } else if (value == 1d) {
            return ONE;
        } else if (value == 2d) {
            return TWO;
        } else {
            return new Real(value);
        }
    }

    public Real add(@Nonnull Real that) {
        return new Real(content + that.content);
    }

    @Nonnull
    public Numeric add(@Nonnull Numeric that) {
        if (that instanceof Real) {
            return add((Real) that);
        } else {
            return that.valueOf(this).add(that);
        }
    }

    public Real subtract(Real that) {
        return new Real(content - that.content);
    }

    @Nonnull
    public Numeric subtract(@Nonnull Numeric that) {
        if (that instanceof Real) {
            return subtract((Real) that);
        } else {
            return that.valueOf(this).subtract(that);
        }
    }

    public Real multiply(Real that) {
        return new Real(content * that.content);
    }

    @Nonnull
    public Numeric multiply(@Nonnull Numeric that) {
        if (that instanceof Real) {
            return multiply((Real) that);
        } else {
            return that.multiply(this);
        }
    }

    public Real divide(Real that) throws ArithmeticException {
        return new Real(content / that.content);
    }

    @Nonnull
    public Numeric divide(@Nonnull Numeric that) throws NotDivisibleException {
        if (that instanceof Real) {
            return divide((Real) that);
        } else {
            return that.valueOf(this).divide(that);
        }
    }

    @Nonnull
    public Numeric negate() {
        return new Real(-content);
    }

    public int signum() {
        return signum(content);
    }

    @Nonnull
    public Numeric ln() {
        if (signum() >= 0) {
            return new Real(Math.log(content));
        } else {
            return Complex.valueOf(Math.log(-content), Math.PI);
        }
    }

    @Nonnull
    public Numeric lg() {
        if (signum() >= 0) {
            return new Real(Math.log10(content));
        } else {
            return Complex.valueOf(Math.log10(-content), Math.PI);
        }
    }

    @Nonnull
    public Numeric exp() {
        return new Real(Math.exp(content));
    }

    @Nonnull
    public Numeric inverse() {
        return new Real(1. / content);
    }

    public Numeric pow(Real that) {
        if (signum() < 0) {
            return Complex.valueOf(content, 0).pow(that);
        } else {
            return new Real(Math.pow(content, that.content));
        }
    }

    public Numeric pow(@Nonnull Numeric numeric) {
        if (numeric instanceof Real) {
            return pow((Real) numeric);
        } else {
            return numeric.valueOf(this).pow(numeric);
        }
    }

    @Nonnull
    public Numeric sqrt() {
        if (signum() < 0) {
            return Complex.I.multiply(negate().sqrt());
        } else {
            return new Real(Math.sqrt(content));
        }
    }

    @Nonnull
    public Numeric nThRoot(int n) {
        if (signum() < 0) {
            return n % 2 == 0 ? sqrt().nThRoot(n / 2) : negate().nThRoot(n).negate();
        } else {
            return super.nThRoot(n);
        }
    }

    public Numeric conjugate() {
        return this;
    }

    @Nonnull
    public Numeric acos() {
        final Real result = new Real(radToDefault(Math.acos(content)));
        if (Double.isNaN(result.content)) {
            return super.acos();
        }
        return result;
    }

    @Nonnull
    public Numeric asin() {
        final Real result = new Real(radToDefault(Math.asin(content)));
        if (Double.isNaN(result.content)) {
            return super.asin();
        }
        return result;
    }

    @Nonnull
    public Numeric atan() {
        final Real result = new Real(radToDefault(atanRad()));
        if (Double.isNaN(result.content)) {
            return super.atan();
        }
        return result;
    }

    @Nonnull
    private Double atanRad() {
        return Math.atan(content);
    }

    @Nonnull
    @Override
    public Numeric acot() {
        final Real result = new Real(radToDefault(PI_DIV_BY_2_RAD_DOUBLE - atanRad()));
        if (Double.isNaN(result.content)) {
            return super.acot();
        }
        return result;
    }

    @Nonnull
    public Numeric cos() {
        return new Real(Math.cos(defaultToRad(content)));
    }

    @Nonnull
    public Numeric sin() {
        return new Real(Math.sin(defaultToRad(content)));
    }

    @Nonnull
    public Numeric tan() {
        return new Real(tan(defaultToRad(content)));
    }

    private double tan(double value) {
        if (value > Math.PI || value < Math.PI) {
            value = value % Math.PI;
        }
        if (value == Math.PI / 2) {
            return Double.POSITIVE_INFINITY;
        }
        if (value == Math.PI) {
            return 0;
        }
        if (value == -Math.PI / 2) {
            return Double.NEGATIVE_INFINITY;
        }
        if (value == -Math.PI) {
            return 0;
        }
        return Math.tan(value);
    }

    @Nonnull
    @Override
    public Numeric cot() {
        return Real.ONE.divide(tan());
    }

    public Real valueOf(Real value) {
        return new Real(value.content);
    }

    @Nonnull
    public Numeric valueOf(@Nonnull Numeric numeric) {
        if (numeric instanceof Real) {
            return valueOf((Real) numeric);
        } else throw new ArithmeticException();
    }

    public int compareTo(@Nonnull Real that) {
        return Double.compare(this.content, that.content);
    }

    public int compareTo(Numeric numeric) {
        if (numeric instanceof Real) {
            return compareTo((Real) numeric);
        } else {
            return numeric.valueOf(this).compareTo(numeric);
        }
    }

    public String toString() {
        return toString(content);
    }

    @Nonnull
    public Complex toComplex() {
        return Complex.valueOf(this.content, 0.);
    }

    @Override
    public BigInteger toBigInteger() {
        if (content == Math.floor(content)) {
            return BigInteger.valueOf((long) content);
        }
        return null;
    }

    @Override
    public double doubleValue() {
        return content;
    }
}
