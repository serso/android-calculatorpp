package jscl.math.numeric;

import jscl.math.Arithmetic;

public abstract class Numeric implements Arithmetic, Comparable {
    public abstract Numeric add(Numeric numeric);

    public Numeric subtract(Numeric numeric) {
        return add(numeric.negate());
    }

    public abstract Numeric multiply(Numeric numeric);
    public abstract Numeric divide(Numeric numeric) throws ArithmeticException;

    public Arithmetic add(Arithmetic arithmetic) {
        return add((Numeric)arithmetic);
    }

    public Arithmetic subtract(Arithmetic arithmetic) {
        return subtract((Numeric)arithmetic);
    }

    public Arithmetic multiply(Arithmetic arithmetic) {
        return multiply((Numeric)arithmetic);
    }

    public Arithmetic divide(Arithmetic arithmetic) throws ArithmeticException {
        return divide((Numeric)arithmetic);
    }

    public Numeric pow(int exponent) {
        Numeric a=JSCLDouble.valueOf(1);
        for(int i=0;i<exponent;i++) a=a.multiply(this);
        return a;
    }

    public Numeric abs() {
        return signum()<0?negate():(signum()==0?JSCLDouble.valueOf(1):this);
    }

    public abstract Numeric negate();
    public abstract int signum();

    public Numeric sgn() {
        return divide(abs());
    }

    public abstract Numeric log();
    public abstract Numeric exp();

    public Numeric inverse() {
        return JSCLDouble.valueOf(1).divide(this);
    }

    public Numeric pow(Numeric numeric) {
        if(numeric.signum()==0) {
            return JSCLDouble.valueOf(1);
        } else if(numeric.compareTo(JSCLDouble.valueOf(1))==0) {
            return this;
        } else {
            return numeric.multiply(log()).exp();
        }
    }

    public Numeric sqrt() {
        return nthrt(2);
    }

    public Numeric nthrt(int n) {
        return pow(JSCLDouble.valueOf(1./n));
    }

    public static Numeric root(int subscript, Numeric parameter[]) {
        throw new ArithmeticException();
    }

    public abstract Numeric conjugate();

    public Numeric acos() {
        return add(JSCLDouble.valueOf(-1).add(pow(2)).sqrt()).log().multiply(Complex.valueOf(0, 1));
    }

    public Numeric asin() {
        return multiply(Complex.valueOf(0, 1)).negate().add(JSCLDouble.valueOf(1).subtract(pow(2)).sqrt()).log().multiply(Complex.valueOf(0, 1));
    }

    public Numeric atan() {
        return Complex.valueOf(0, 1).multiply(Complex.valueOf(0, 1).add(this).divide(Complex.valueOf(0, 1).subtract(this)).log()).divide(JSCLDouble.valueOf(2));
    }

    public Numeric acot() {
            return Complex.valueOf(0, 1).multiply(Complex.valueOf(0, 1).add(this).divide(Complex.valueOf(0, 1).subtract(this)).negate().log()).divide(JSCLDouble.valueOf(2));
        }

    public Numeric cos() {
        return JSCLDouble.valueOf(1).add(multiply(Complex.valueOf(0, 1)).exp().pow(2)).divide(JSCLDouble.valueOf(2).multiply(multiply(Complex.valueOf(0, 1)).exp()));
    }

    public Numeric sin() {
        return Complex.valueOf(0, 1).subtract(multiply(Complex.valueOf(0, 1)).exp().pow(2).multiply(Complex.valueOf(0, 1))).divide(JSCLDouble.valueOf(2).multiply(multiply(Complex.valueOf(0, 1)).exp()));
    }

    public Numeric tan() {
        return Complex.valueOf(0, 1).subtract(multiply(Complex.valueOf(0, 1)).exp().pow(2).multiply(Complex.valueOf(0, 1))).divide(JSCLDouble.valueOf(1).add(multiply(Complex.valueOf(0, 1)).exp().pow(2)));
    }

    public Numeric cot() {
        return Complex.valueOf(0, 1).add(Complex.valueOf(0, 1).multiply(Complex.valueOf(0, 1).multiply(this).exp().pow(2))).divide(JSCLDouble.valueOf(1).subtract(Complex.valueOf(0, 1).multiply(this).exp().pow(2))).negate();
    }

    public Numeric acosh() {
        return add(JSCLDouble.valueOf(-1).add(pow(2)).sqrt()).log();
    }

    public Numeric asinh() {
        return add(JSCLDouble.valueOf(1).add(pow(2)).sqrt()).log();
    }

    public Numeric atanh() {
        return JSCLDouble.valueOf(1).add(this).divide(JSCLDouble.valueOf(1).subtract(this)).log().divide(JSCLDouble.valueOf(2));
    }

    public Numeric acoth() {
        return JSCLDouble.valueOf(1).add(this).divide(JSCLDouble.valueOf(1).subtract(this)).negate().log().divide(JSCLDouble.valueOf(2));
    }

    public Numeric cosh() {
        return JSCLDouble.valueOf(1).add(exp().pow(2)).divide(JSCLDouble.valueOf(2).multiply(exp()));
    }

    public Numeric sinh() {
        return JSCLDouble.valueOf(1).subtract(exp().pow(2)).divide(JSCLDouble.valueOf(2).multiply(exp())).negate();
    }

    public Numeric tanh() {
        return JSCLDouble.valueOf(1).subtract(exp().pow(2)).divide(JSCLDouble.valueOf(1).add(exp().pow(2))).negate();
    }

    public Numeric coth() {
        return JSCLDouble.valueOf(1).add(exp().pow(2)).divide(JSCLDouble.valueOf(1).subtract(exp().pow(2))).negate();
    }

    public abstract Numeric valueof(Numeric numeric);

    public abstract int compareTo(Numeric numeric);

    public int compareTo(Object o) {
        return compareTo((Numeric)o);
    }

    public boolean equals(Object obj) {
        if(obj instanceof Numeric) {
            return compareTo((Numeric)obj)==0;
        } else return false;
    }
}
