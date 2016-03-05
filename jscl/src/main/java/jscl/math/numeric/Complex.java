package jscl.math.numeric;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.math.NotDivisibleException;
import jscl.math.NotDoubleException;
import jscl.text.msg.JsclMessage;
import jscl.text.msg.Messages;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;

public final class Complex extends Numeric {

    @Nonnull
    public static final Complex I = new Complex(0, 1);
    private final double real, imaginary;

    private Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    @Nonnull
    public static Complex valueOf(double real, double imaginary) {
        if (JsclMathEngine.getInstance().getAngleUnits() != AngleUnit.rad) {
            JsclMathEngine.getInstance().getMessageRegistry().addMessage(new JsclMessage(Messages.msg_23, MessageType.warning));
        }

        if (real == 0d && imaginary == 1d) {
            return I;
        } else {
            return new Complex(real, imaginary);
        }
    }

    public Complex add(Complex complex) {
        return valueOf(real + complex.real, imaginary + complex.imaginary);
    }

    @Nonnull
    public Numeric add(@Nonnull Numeric that) {
        if (that instanceof Complex) {
            return add((Complex) that);
        } else if (that instanceof Real) {
            return add(valueOf(that));
        } else {
            return that.valueOf(this).add(that);
        }
    }

    public Complex subtract(Complex complex) {
        return valueOf(real - complex.real, imaginary - complex.imaginary);
    }

    @Nonnull
    public Numeric subtract(@Nonnull Numeric that) {
        if (that instanceof Complex) {
            return subtract((Complex) that);
        } else if (that instanceof Real) {
            return subtract(valueOf(that));
        } else {
            return that.valueOf(this).subtract(that);
        }
    }

    public Complex multiply(Complex complex) {
        return valueOf(real * complex.real - imaginary * complex.imaginary, real * complex.imaginary + imaginary * complex.real);
    }

    @Nonnull
    public Numeric multiply(@Nonnull Numeric that) {
        if (that instanceof Complex) {
            return multiply((Complex) that);
        } else if (that instanceof Real) {
            return multiply(valueOf(that));
        } else {
            return that.multiply(this);
        }
    }

    public Complex divide(Complex complex) throws ArithmeticException {
        return multiply((Complex) complex.inverse());
    }

    @Nonnull
    public Numeric divide(@Nonnull Numeric that) throws NotDivisibleException {
        if (that instanceof Complex) {
            return divide((Complex) that);
        } else if (that instanceof Real) {
            return divide(valueOf(that));
        } else {
            return that.valueOf(this).divide(that);
        }
    }

    @Nonnull
    public Numeric negate() {
        return valueOf(-real, -imaginary);
    }

    @Nonnull
    @Override
    public Numeric abs() {
        final Numeric realSquare = new Real(real).pow(2);
        final Numeric imaginarySquare = new Real(imaginary).pow(2);
        final Numeric sum = realSquare.add(imaginarySquare);
        return sum.sqrt();
    }

    public int signum() {
        int result;

        if (real > .0) {
            result = 1;
        } else if (real < .0) {
            result = -1;
        } else {
            result = Real.signum(imaginary);
        }

        return result;
    }

    public double magnitude() {
        return Math.sqrt(real * real + imaginary * imaginary);
    }

    public double magnitude2() {
        return real * real + imaginary * imaginary;
    }

    public double angle() {
        return Math.atan2(imaginary, real);
    }

    @Nonnull
    public Numeric ln() {
        if (signum() == 0) {
            return Real.ZERO.ln();
        } else {
            return valueOf(Math.log(magnitude()), angle());
        }
    }

    @Nonnull
    public Numeric lg() {
        if (signum() == 0) {
            return Real.ZERO.lg();
        } else {
            return valueOf(Math.log10(magnitude()), angle());
        }
    }

    @Nonnull
    public Numeric exp() {
        return valueOf(Math.cos(defaultToRad(imaginary)), Math.sin(defaultToRad(imaginary))).multiply(Math.exp(real));
    }

    @Nonnull
    public Numeric inverse() {
        return ((Complex) conjugate()).divide(magnitude2());
    }

    Complex multiply(double d) {
        return valueOf(real * d, imaginary * d);
    }

    Complex divide(double d) {
        return valueOf(real / d, imaginary / d);
    }

    public Numeric conjugate() {
        return valueOf(real, -imaginary);
    }

    public double realPart() {
        return real;
    }

    public double imaginaryPart() {
        return imaginary;
    }

    public int compareTo(Complex that) {
        if (imaginary < that.imaginary) {
            return -1;
        } else if (imaginary > that.imaginary) {
            return 1;
        } else if (imaginary == that.imaginary) {
            if (real < that.real) {
                return -1;
            } else if (real > that.real) {
                return 1;
            } else if (real == that.real) {
                return 0;
            } else throw new ArithmeticException();
        } else throw new ArithmeticException();
    }

    public int compareTo(Numeric that) {
        if (that instanceof Complex) {
            return compareTo((Complex) that);
        } else if (that instanceof Real) {
            return compareTo(valueOf(that));
        } else {
            return that.valueOf(this).compareTo(that);
        }
    }

    @Override
    public double doubleValue() {
        throw NotDoubleException.get();
    }

    public Complex copyOf(@Nonnull Complex complex) {
        return valueOf(complex.real, complex.imaginary);
    }

    @Nonnull
    public Numeric valueOf(@Nonnull Numeric numeric) {
        if (numeric instanceof Complex) {
            return copyOf((Complex) numeric);
        } else if (numeric instanceof Real) {
            Real d = (Real) numeric;
            return d.toComplex();
        } else throw new ArithmeticException();
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();

        if (imaginary == 0.) {
            result.append(toString(real));
        } else {
            if (real != 0.) {
                result.append(toString(real));
                if (imaginary > 0.) {
                    result.append("+");
                }
            }

            if (imaginary != 1.) {
                if (imaginary == -1.) {
                    result.append("-");
                } else {
                    if (imaginary < 0.) {
                        final String imagStr = toString(imaginary);
                        // due to rounding we can forget sign (-0.00000000001 can be round to 0 => plus sign would not be added above and no sign will be before i)
                        if (imagStr.startsWith("-")) {
                            result.append(imagStr);
                        } else {
                            result.append("-").append(imagStr);
                        }
                    } else {
                        result.append(toString(imaginary));
                    }
                    result.append("*");
                }
            }
            result.append("i");
        }

        return result.toString();
    }
}
