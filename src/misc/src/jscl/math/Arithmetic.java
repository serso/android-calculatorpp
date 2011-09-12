package jscl.math;

public interface Arithmetic {
        abstract Arithmetic add(Arithmetic arithmetic);
        abstract Arithmetic subtract(Arithmetic arithmetic);
        abstract Arithmetic multiply(Arithmetic arithmetic);
        abstract Arithmetic divide(Arithmetic arithmetic) throws ArithmeticException;
}
