package org.solovyev.android.calculator.plot;

import android.text.TextUtils;
import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NumericWrapper;
import jscl.math.function.CustomFunction;
import jscl.math.numeric.Complex;
import jscl.math.numeric.Numeric;
import jscl.math.numeric.Real;
import org.solovyev.android.plotter.Function;

import javax.annotation.Nonnull;

public class ExpressionFunction extends Function {
    @Nonnull
    public final jscl.math.function.Function function;
    public final int arity;
    private final Generic[] parameters;

    public ExpressionFunction(@Nonnull jscl.math.function.Function function) {
        super(makeFunctionName(function));
        this.function = function;
        this.arity = function.getMaxParameters();
        this.parameters = new Generic[this.arity];
    }

    @Nonnull
    private static String makeFunctionName(@Nonnull jscl.math.function.Function function) {
        String name = function.getName();
        if (TextUtils.isEmpty(name)) {
            if (function instanceof CustomFunction) {
                name = ((CustomFunction) function).getContent();
            } else {
                name = function.toString();
            }
            if (name.length() > 10) {
                name = name.substring(0, 10) + "…";
            }
        }
        return name;
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
            parameters[0] = Expression.valueOf((double) x);
            function.setParameters(parameters);
            return unwrap(function.numeric());
        } catch (RuntimeException e) {
            return Float.NaN;
        }
    }

    @Override
    public float evaluate(float x, float y) {
        try {
            parameters[0] = Expression.valueOf((double) x);
            parameters[1] = Expression.valueOf((double) y);
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
            return (float) content.doubleValue();
        }
        if (content instanceof Complex) {
            final Complex complex = (Complex) content;
            final double imag = complex.imaginaryPart();
            final double real = complex.realPart();
            if (real == 0f && imag != 0f) {
                return Float.NaN;
            }
            return (float) real;
        }
        return Float.NaN;
    }
}
