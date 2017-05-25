package jscl.math.operator.matrix;

import org.solovyev.common.math.AbstractMathRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.math.Generic;
import jscl.math.function.FunctionsRegistry;
import jscl.math.operator.Derivative;
import jscl.math.operator.IndefiniteIntegral;
import jscl.math.operator.Integral;
import jscl.math.operator.Modulo;
import jscl.math.operator.Operator;
import jscl.math.operator.Product;
import jscl.math.operator.Sum;

/**
 * User: serso
 * Date: 11/17/11
 * Time: 10:22 AM
 */
public class OperatorsRegistry extends AbstractMathRegistry<Operator> {

    private final static OperatorsRegistry instance = new OperatorsRegistry();

    @Nonnull
    public static OperatorsRegistry getInstance() {
        instance.init();
        return instance;
    }

    @Nonnull
    public static OperatorsRegistry lazyInstance() {
        return instance;
    }

    @Nullable
    public Operator get(@Nonnull String name, @Nonnull Generic[] parameters) {
        final Operator operator = super.get(name);
        if (operator == null) {
            return null;
        } else {
            if (operator.getMinParameters() <= parameters.length && operator.getMaxParameters() >= parameters.length) {
                return operator.newInstance(parameters);
            } else {
                return null;
            }
        }
    }

    @Override
    public Operator get(@Nonnull String name) {
        final Operator operator = super.get(name);
        return operator == null ? null : FunctionsRegistry.copy(operator);
    }

    @Override
    public void onInit() {
        add(new Derivative(null, null, null, null));
        add(new Sum(null, null, null, null));
        add(new Product(null, null, null, null));
        add(new Modulo(null, null));
        add(new Integral(null, null, null, null));
        add(new IndefiniteIntegral(null, null));
    }
}
