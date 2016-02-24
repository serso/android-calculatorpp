package org.solovyev.android.calculator.plot;

import org.solovyev.android.plotter.Function;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NumericWrapper;
import jscl.math.function.Constant;
import jscl.math.numeric.Complex;
import jscl.math.numeric.Numeric;
import jscl.math.numeric.Real;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExpressionFunction extends Function {
    private static final Complex NaN = Complex.valueOf(Double.NaN, 0d);

    @Nonnull
    public final jscl.math.function.Function function;
    public final Constant xVariable;
    public final Constant yVariable;
    public final boolean imaginary;
    public final int arity;

    public ExpressionFunction(@Nonnull jscl.math.function.Function function, @Nullable Constant x,
        @Nullable Constant y, boolean imaginary) {
        super(imaginary ? "Im(" + function.toString() + ")" : function.toString());
        this.function = function;
        this.xVariable = x;
        this.yVariable = y;
        this.imaginary = imaginary;
        this.arity = countArity(x, y);
    }

    private static int countArity(@Nullable Constant x, @Nullable Constant y) {
        if (x != null && y != null) {
            return 2;
        } else if (x == null && y == null) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getArity() {
        return arity;
    }

    @Override
    public float evaluate() {
        final Complex value = calculate(function);
        if (imaginary) {
            return (float) value.imaginaryPart();
        }
        return (float) value.realPart();
    }

    @Override
    public float evaluate(float x) {
        final Complex value = calculate(function, xVariable, x);
        if (imaginary) {
            return (float) value.imaginaryPart();
        }
        return (float) value.realPart();
    }

    @Override
    public float evaluate(float x, float y) {
        final Complex value = calculate(function, xVariable, x, yVariable, y);
        if (imaginary) {
            return (float) value.imaginaryPart();
        }
        return (float) value.realPart();
    }

    @Nonnull
    public static Complex calculate(jscl.math.function.Function function, Constant xVar,
        float x, Constant yVar, float y) {
        try {
            Generic tmp = function.substitute(xVar, Expression.valueOf((double) x));
            tmp = tmp.substitute(yVar, Expression.valueOf((double) y));
            return unwrap(tmp.numeric());
        } catch (RuntimeException e) {
            return NaN;
        }
    }

    @Nonnull
    public static Complex calculate(jscl.math.function.Function function, Constant xVar,
        float x) {
        try {
            return unwrap(function.substitute(xVar, Expression.valueOf((double) x)).numeric());
        } catch (RuntimeException e) {
            return NaN;
        }
    }

    @Nonnull
    public static Complex calculate(jscl.math.function.Function function) {
        try {
            return unwrap(function.numeric());
        } catch (RuntimeException e) {
            return NaN;
        }
    }

    @Nonnull
    public static Complex unwrap(Generic numeric) {
        if (numeric instanceof JsclInteger) {
            return Complex.valueOf(((JsclInteger) numeric).intValue(), 0d);
        } else if (numeric instanceof NumericWrapper) {
            return unwrap(((NumericWrapper) numeric).content());
        } else {
            return NaN;
        }
    }

    @Nonnull
    public static Complex unwrap(Numeric content) {
        if (content instanceof Real) {
            return Complex.valueOf(((Real) content).doubleValue(), 0d);
        } else if (content instanceof Complex) {
            return ((Complex) content);
        } else {
            throw new ArithmeticException();
        }
    }
}
