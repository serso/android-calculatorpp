package jscl.math.function;

import org.solovyev.common.math.AbstractMathRegistry;

import javax.annotation.Nonnull;

import jscl.math.Variable;
import jscl.math.function.hyperbolic.Acosh;
import jscl.math.function.hyperbolic.Acoth;
import jscl.math.function.hyperbolic.Asinh;
import jscl.math.function.hyperbolic.Atanh;
import jscl.math.function.hyperbolic.Cosh;
import jscl.math.function.hyperbolic.Coth;
import jscl.math.function.hyperbolic.Sinh;
import jscl.math.function.hyperbolic.Tanh;
import jscl.math.function.trigonometric.Acos;
import jscl.math.function.trigonometric.Acot;
import jscl.math.function.trigonometric.Asin;
import jscl.math.function.trigonometric.Atan;
import jscl.math.function.trigonometric.Cos;
import jscl.math.function.trigonometric.Cot;
import jscl.math.function.trigonometric.Sin;
import jscl.math.function.trigonometric.Tan;

/**
 * User: serso
 * Date: 10/29/11
 * Time: 12:54 PM
 */
public class FunctionsRegistry extends AbstractMathRegistry<Function> {

    private final static FunctionsRegistry instance = new FunctionsRegistry();

    @Nonnull
    public static FunctionsRegistry getInstance() {
        instance.init();
        return instance;
    }

    @Nonnull
    public static FunctionsRegistry lazyInstance() {
        return instance;
    }

    @Nonnull
    public static <T extends Variable> T copy(@Nonnull T variable) {
        final T result = (T) variable.newInstance();
        if (variable.isIdDefined()) {
            result.setId(variable.getId());
        }
        result.setSystem(variable.isSystem());
        return result;
    }

    @Override
    public Function get(@Nonnull String name) {
        final Function function = super.get(name);
        return function == null ? null : copy(function);
    }

    @Override
    public void onInit() {
        add(new Deg(null));
        add(new Rad(null, null, null));
        add(new Dms(null, null, null));

        add(new Sin(null));
        add(new Cos(null));
        add(new Tan(null));
        add(new Cot(null));

        add(new Asin(null));
        add(new Acos(null));
        add(new Atan(null));
        add(new Acot(null));

        add(new Ln(null));
        add(new Lg(null));
        add(new Exp(null));
        add(new Sqrt(null));
        add(new Cubic(null));

        add(new Sinh(null));
        add(new Cosh(null));
        add(new Tanh(null));
        add(new Coth(null));

        add(new Asinh(null));
        add(new Acosh(null));
        add(new Atanh(null));
        add(new Acoth(null));

        add(new Abs(null));
        add(new Sgn(null));

        add(new Conjugate(null));

        for (String name : Comparison.names) {
            add(new Comparison(name, null, null));
        }
    }
}
