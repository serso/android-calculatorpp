package jscl.math.numeric;

import javax.annotation.Nonnull;

public interface INumeric<T extends INumeric<T>> {

    @Nonnull
    T pow(int exponent);

    @Nonnull
    T abs();

    @Nonnull
    T negate();

    int signum();

    @Nonnull
    T sgn();

    @Nonnull
    T ln();

    @Nonnull
    T lg();

    @Nonnull
    T exp();

    @Nonnull
    T inverse();

    @Nonnull
    T sqrt();

    @Nonnull
    T nThRoot(int n);

    /*
     * ******************************************************************************************
     * <p/>
     * TRIGONOMETRIC FUNCTIONS
     * <p/>
     * *******************************************************************************************/

    @Nonnull
    T sin();

    @Nonnull
    T cos();

    @Nonnull
    T tan();

    @Nonnull
    T cot();

    /*
      * ******************************************************************************************
      * <p/>
      * INVERSE TRIGONOMETRIC FUNCTIONS
      * <p/>
      * *******************************************************************************************/

    @Nonnull
    T asin();

    @Nonnull
    T acos();

    @Nonnull
    T atan();

    @Nonnull
    T acot();

    /*
      * ******************************************************************************************
      * <p/>
      * HYPERBOLIC TRIGONOMETRIC FUNCTIONS
      * <p/>
      * *******************************************************************************************/

    @Nonnull
    T sinh();

    @Nonnull
    T cosh();

    @Nonnull
    T tanh();

    @Nonnull
    T coth();

    /*
      * ******************************************************************************************
      * <p/>
      * INVERSE HYPERBOLIC TRIGONOMETRIC FUNCTIONS
      * <p/>
      * *******************************************************************************************/

    @Nonnull
    T asinh();

    @Nonnull
    T acosh();

    @Nonnull
    T atanh();

    @Nonnull
    T acoth();
}
