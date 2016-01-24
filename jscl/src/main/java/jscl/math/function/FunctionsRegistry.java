package jscl.math.function;

import jscl.math.Variable;
import jscl.math.function.hyperbolic.*;
import jscl.math.function.trigonometric.*;
import org.solovyev.common.math.AbstractMathRegistry;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 10/29/11
 * Time: 12:54 PM
 */
public class FunctionsRegistry extends AbstractMathRegistry<Function> {

    private final static FunctionsRegistry instance = new FunctionsRegistry();

    static {
        instance.add(new Deg(null));
        instance.add(new Rad(null, null, null));
        instance.add(new Dms(null, null, null));

        instance.add(new Sin(null));
        instance.add(new Cos(null));
        instance.add(new Tan(null));
        instance.add(new Cot(null));

        instance.add(new Asin(null));
        instance.add(new Acos(null));
        instance.add(new Atan(null));
        instance.add(new Acot(null));

        instance.add(new Ln(null));
        instance.add(new Lg(null));
        instance.add(new Exp(null));
        instance.add(new Sqrt(null));
        instance.add(new Cubic(null));

        instance.add(new Sinh(null));
        instance.add(new Cosh(null));
        instance.add(new Tanh(null));
        instance.add(new Coth(null));

        instance.add(new Asinh(null));
        instance.add(new Acosh(null));
        instance.add(new Atanh(null));
        instance.add(new Acoth(null));

        instance.add(new Abs(null));
        instance.add(new Sgn(null));

        instance.add(new Conjugate(null));

        for (String name : Comparison.names) {
            instance.add(new Comparison(name, null, null));
        }
    }


    @Nonnull
    public static FunctionsRegistry getInstance() {
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
}
