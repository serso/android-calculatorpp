package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.Variable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 11/14/11
 * Time: 2:05 PM
 */
public class Percent extends PostfixFunction {

    public static final String NAME = "%";

    public Percent(Generic content, Generic previousSumElement) {
        super(NAME, new Generic[]{content, previousSumElement});
    }

    private Percent(Generic[] parameters) {
        super(NAME, createParameters(getParameter(parameters, 0), getParameter(parameters, 1)));
    }

    private static Generic[] createParameters(@Nullable Generic content, @Nullable Generic previousSumElement) {
        final Generic[] result;

        if (previousSumElement == null) {
            result = new Generic[]{content, UNDEFINED_PARAMETER};
        } else {
            result = new Generic[]{content, previousSumElement};
        }

        return result;
    }

    @Override
    public int getMinParameters() {
        return 1;
    }

    @Override
    public int getMaxParameters() {
        return 2;
    }

    public Generic selfExpand() {
        return expressionValue();
    }

    @Override
    public Generic simplify() {
        return expressionValue();
    }

    @Override
    public Generic selfNumeric() {
        Generic percentValue = parameters[0];

        final Generic normalizedPercentage = percentValue.divide(JsclInteger.valueOf(100));
        if (UNDEFINED_PARAMETER != parameters[1]) {
            Generic previousSumElement = parameters[1];

            return previousSumElement.multiply(normalizedPercentage);
        } else {
            return normalizedPercentage;
        }
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Percent(parameters);
    }

    @Nonnull
    @Override
    public Variable newInstance() {
        return new Percent(null, null);
    }
}
