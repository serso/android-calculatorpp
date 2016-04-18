package jscl.math.numeric;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.math.Arithmetic;

import javax.annotation.Nonnull;
import java.math.BigInteger;

import static jscl.math.numeric.Complex.I;
import static jscl.math.numeric.Real.ONE;
import static jscl.math.numeric.Real.TWO;

public abstract class Numeric implements Arithmetic<Numeric>, INumeric<Numeric>, Comparable {

    /*@Nonnull
     public Numeric subtract(@Nonnull Numeric numeric) {
         return add(numeric.negate());
     }*/

    public static Numeric root(int subscript, Numeric parameter[]) {
        throw new ArithmeticException();
    }

    protected static double defaultToRad(double value) {
        return JsclMathEngine.getInstance().getAngleUnits().transform(AngleUnit.rad, value);
    }

    protected static double radToDefault(double value) {
        return AngleUnit.rad.transform(JsclMathEngine.getInstance().getAngleUnits(), value);
    }

    @Nonnull
    protected static Numeric defaultToRad(@Nonnull Numeric value) {
        return JsclMathEngine.getInstance().getAngleUnits().transform(AngleUnit.rad, value);
    }

    @Nonnull
    protected static Numeric radToDefault(@Nonnull Numeric value) {
        return AngleUnit.rad.transform(JsclMathEngine.getInstance().getAngleUnits(), value);
    }

    @Override
    @Nonnull
    public Numeric abs() {
        return signum() < 0 ? negate() : this;
    }

    @Nonnull
    @Override
    public Numeric sgn() {
        return divide(abs());
    }

    @Nonnull
    @Override
    public Numeric inverse() {
        return ONE.divide(this);
    }

    @Override
    @Nonnull
    public Numeric pow(int exponent) {
        Numeric result = ONE;

        for (int i = 0; i < exponent; i++) {
            result = result.multiply(this);
        }

        return result;
    }

    /*
      * ******************************************************************************************
      * <p/>
      * CONVERSION FUNCTIONS (rad to default angle units and vice versa)
      * <p/>
      * *******************************************************************************************
      */

    public Numeric pow(@Nonnull Numeric numeric) {
        if (numeric.signum() == 0) {
            return ONE;
        } else if (numeric.compareTo(ONE) == 0) {
            return this;
        } else {
            return numeric.multiply(this.ln()).exp();
        }
    }

    @Nonnull
    @Override
    public Numeric sqrt() {
        return nThRoot(2);
    }

    @Nonnull
    @Override
    public Numeric nThRoot(int n) {
        return pow(Real.valueOf(1. / n));
    }

    public abstract Numeric conjugate();

    /*
      * ******************************************************************************************
      * <p/>
      * TRIGONOMETRIC FUNCTIONS
      * <p/>
      * *******************************************************************************************
      */

    @Nonnull
    @Override
    public Numeric sin() {
        // e = exp(i)
        final Numeric e = defaultToRad(this).multiply(I).exp();
        // e1 = exp(2ix)
        final Numeric e1 = e.pow(2);

        // result = [i - i * exp(2i)] / [2exp(i)]
        return I.subtract(e1.multiply(I)).divide(TWO.multiply(e));
    }

    @Nonnull
    @Override
    public Numeric cos() {
        // e = exp(ix)
        final Numeric e = defaultToRad(this).multiply(I).exp();
        // e1 = exp(2ix)
        final Numeric e1 = e.pow(2);

        // result = [ 1 + exp(2ix) ] / (2 *exp(ix))
        return ONE.add(e1).divide(TWO.multiply(e));
    }

    @Nonnull
    @Override
    public Numeric tan() {
        // e = exp(2xi)
        final Numeric e = defaultToRad(this).multiply(I).exp().pow(2);

        // e1 = i * exp(2xi)
        final Numeric e1 = e.multiply(I);

        // result = (i - i * exp(2xi)) / ( 1 + exp(2xi) )
        return I.subtract(e1).divide(ONE.add(e));
    }

    @Nonnull
    @Override
    public Numeric cot() {
        // e = exp(2xi)
        final Numeric e = I.multiply(defaultToRad(this)).exp().pow(2);

        // result = - (i + i * exp(2ix)) / ( 1 - exp(2xi))
        return I.add(I.multiply(e)).divide(ONE.subtract(e)).negate();
    }

    /**
     * ******************************************************************************************
     * <p/>
     * INVERSE TRIGONOMETRIC FUNCTIONS
     * <p/>
     * *******************************************************************************************
     */

    @Nonnull
    @Override
    public Numeric asin() {
        // e = √(1 - x^2)
        final Numeric e = ONE.subtract(this.pow(2)).sqrt();
        // result = -iln[xi + √(1 - x^2)]
        return radToDefault(this.multiply(I).add(e).ln().multiply(I.negate()));
    }

    @Nonnull
    @Override
    public Numeric acos() {
        // e = √(-1 + x^2) = i √(1 - x^2)
        final Numeric e = I.multiply(Real.ONE.subtract(this.pow(2)).sqrt());

        // result = -i * ln[ x + √(-1 + x^2) ]
        return radToDefault(this.add(e).ln().multiply(I.negate()));
    }

    @Nonnull
    @Override
    public Numeric atan() {
        // e = ln[(i + x)/(i-x)]
        final Numeric e = I.add(this).divide(I.subtract(this)).ln();
        // result = iln[(i + x)/(i-x)]/2
        return radToDefault(I.multiply(e).divide(TWO));
    }

    @Nonnull
    @Override
    public Numeric acot() {
        // e = ln[-(i + x)/(i-x)]
        final Numeric e = I.add(this).divide(I.subtract(this)).negate().ln();
        // result = iln[-(i + x)/(i-x)]/2
        return radToDefault(I.multiply(e).divide(TWO));
    }

    /**
     * ******************************************************************************************
     * <p/>
     * HYPERBOLIC TRIGONOMETRIC FUNCTIONS
     * <p/>
     * *******************************************************************************************
     */

    @Nonnull
    @Override
    public Numeric sinh() {
        final Numeric thisRad = defaultToRad(this);

        // e = exp(2x)
        final Numeric e = thisRad.exp().pow(2);

        // e1 = 2exp(x)
        final Numeric e1 = TWO.multiply(thisRad.exp());

        // result = -[1 - exp(2x)]/[2exp(x)]
        return ONE.subtract(e).divide(e1).negate();
    }

    @Nonnull
    @Override
    public Numeric cosh() {
        final Numeric thisExpRad = defaultToRad(this).exp();

        // e = exp(2x)
        final Numeric e = thisExpRad.pow(2);

        // e1 = 2exp(x)
        final Numeric e1 = TWO.multiply(thisExpRad);

        // result = [ 1 + exp(2x )] / 2exp(x)
        return ONE.add(e).divide(e1);
    }


    @Nonnull
    @Override
    public Numeric tanh() {
        // e = exp(2x)
        final Numeric e = defaultToRad(this).exp().pow(2);

        // result = - (1 - exp(2x)) / (1 + exp(2x))
        return ONE.subtract(e).divide(ONE.add(e)).negate();
    }

    @Nonnull
    @Override
    public Numeric coth() {
        // e = exp(2x)
        final Numeric e = defaultToRad(this).exp().pow(2);

        // result = - (1 + exp(2x)) / (1 - exp(2x))
        return ONE.add(e).divide(ONE.subtract(e)).negate();
    }

    /**
     * ******************************************************************************************
     * <p/>
     * INVERSE HYPERBOLIC TRIGONOMETRIC FUNCTIONS
     * <p/>
     * *******************************************************************************************
     */

    @Nonnull
    @Override
    public Numeric asinh() {
        // e = √( 1 + x ^ 2 )
        final Numeric e = ONE.add(this.pow(2)).sqrt();

        // result = ln [ x + √( 1 + x ^ 2 ) ]
        return radToDefault(this.add(e).ln());
    }

    @Nonnull
    @Override
    public Numeric acosh() {
        // e = √(x ^ 2 - 1)
        final Numeric e = Real.valueOf(-1).add(this.pow(2)).sqrt();

        // result = ln( x + √(x ^ 2 - 1) )
        return radToDefault(this.add(e).ln());
    }

    @Nonnull
    @Override
    public Numeric atanh() {
        // e = 1 - x
        final Numeric e = ONE.subtract(this);

        // result = ln [ ( 1 + x ) / ( 1 - x ) ] / 2
        return radToDefault(ONE.add(this).divide(e).ln().divide(TWO));
    }

    @Nonnull
    @Override
    public Numeric acoth() {
        // e = 1 - x
        final Numeric e = ONE.subtract(this);

        // result = ln [ - (1 + x) / (1 - x) ] / 2
        return radToDefault(ONE.add(this).divide(e).negate().ln().divide(TWO));
    }

    @Nonnull
    public abstract Numeric valueOf(@Nonnull Numeric numeric);

    public abstract int compareTo(Numeric numeric);

    public int compareTo(Object o) {
        return compareTo((Numeric) o);
    }

    public boolean equals(Object obj) {
        return obj instanceof Numeric && compareTo((Numeric) obj) == 0;
    }

    @Nonnull
    protected String toString(final double value) {
        return JsclMathEngine.getInstance().format(value);
    }

    public BigInteger toBigInteger() {
        return null;
    }

    public abstract double doubleValue();
}
