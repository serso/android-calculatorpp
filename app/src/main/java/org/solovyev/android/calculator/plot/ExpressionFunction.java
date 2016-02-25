package org.solovyev.android.calculator.plot;

import android.text.TextUtils;

import org.solovyev.android.plotter.Function;

import javax.annotation.Nonnull;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NumericWrapper;
import jscl.math.function.CustomFunction;
import jscl.math.numeric.Complex;
import jscl.math.numeric.Numeric;
import jscl.math.numeric.Real;

public class ExpressionFunction extends Function {
    @Nonnull
    public final jscl.math.function.Function function;
    public final boolean imaginary;
    public final int arity;
    private final Generic[] parameters;

    public ExpressionFunction(@Nonnull jscl.math.function.Function function,
                              boolean imaginary) {
        super(makeFunctionName(function, imaginary));
        this.function = function;
        this.imaginary = imaginary;
        this.arity = function.getMaxParameters();
        this.parameters = new Generic[this.arity];
    }

    @Nonnull
    private static String makeFunctionName(@Nonnull jscl.math.function.Function function, boolean imaginary) {
        String name = function.getName();
        if (TextUtils.isEmpty(name)) {
            if (function instanceof CustomFunction) {
                name = ((CustomFunction) function).getContent();
            } else {
                name = function.toString();
            }
        }
        return imaginary ? "Im(" + name + ")" : name;
    }

    @Override
    public int getArity() {
        return arity;
    }

    @Override
    public float evaluate() {
        try {
            return unwrap(function.numeric());
        } catch (RuntimeException e) {
            return Float.NaN;
        }
    }

    @Override
    public float evaluate(float x) {
        try {
            parameters[0] = new NumericWrapper(Real.valueOf(x));
            function.setParameters(parameters);
            return unwrap(function.numeric());
        } catch (RuntimeException e) {
            return Float.NaN;
        }
    }

    @Override
    public float evaluate(float x, float y) {
        try {
            parameters[0] = new NumericWrapper(Real.valueOf(x));
            parameters[1] = new NumericWrapper(Real.valueOf(y));
            function.setParameters(parameters);
            return unwrap(function.numeric());
        } catch (RuntimeException e) {
            return Float.NaN;
        }
    }

    public float unwrap(Generic numeric) {
        if (numeric instanceof JsclInteger) {
            return ((JsclInteger) numeric).intValue();
        }
        if (numeric instanceof NumericWrapper) {
            return unwrap(((NumericWrapper) numeric).content());
        }
        return Float.NaN;
    }

    public float unwrap(Numeric content) {
        if (content instanceof Real) {
            return (float) ((Real) content).doubleValue();
        }
        if (content instanceof Complex) {
            return (float) (imaginary ? ((Complex) content).imaginaryPart() : ((Complex) content).realPart());
        }
        return Float.NaN;
    }
}
