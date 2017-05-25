package jscl.math.function;

import org.solovyev.common.math.AbstractMathRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.math.Generic;
import jscl.math.operator.Degree;
import jscl.math.operator.DoubleFactorial;
import jscl.math.operator.Factorial;
import jscl.math.operator.Operator;
import jscl.math.operator.Percent;

/**
 * User: serso
 * Date: 10/31/11
 * Time: 10:56 PM
 */
public class PostfixFunctionsRegistry extends AbstractMathRegistry<Operator> {

    private final static PostfixFunctionsRegistry instance = new PostfixFunctionsRegistry();

    @Nonnull
    public static PostfixFunctionsRegistry getInstance() {
        instance.init();
        return instance;
    }

    @Nonnull
    public static PostfixFunctionsRegistry lazyInstance() {
        return instance;
    }

    @Nullable
    public Operator get(@Nonnull String name, @Nonnull Generic[] parameters) {
        final Operator operator = super.get(name);
        return operator == null ? null : operator.newInstance(parameters);
    }

    @Override
    public Operator get(@Nonnull String name) {
        final Operator operator = super.get(name);
        return operator == null ? null : FunctionsRegistry.copy(operator);
    }

    @Override
    public void onInit() {
        add(new DoubleFactorial(null));
        add(new Factorial(null));
        add(new Degree(null));
        add(new Percent(null, null));
    }
}
