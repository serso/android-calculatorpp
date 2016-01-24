package jscl.math.operator.stat;

import jscl.math.*;
import jscl.math.operator.Operator;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 12/26/11
 * Time: 11:09 AM
 */
public class Max extends AbstractStatFunction {

    public static final String NAME = "max";

    public Max(JsclVector vector) {
        this(new Generic[]{vector});
    }

    private Max(@Nonnull Generic[] parameters) {
        super(NAME, parameters);
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Max(parameters);
    }

    @Override
    public int getMinParameters() {
        return 1;
    }

    @Override
    public Generic selfExpand() {
        return expressionValue();
    }

    @Override
    public Generic selfNumeric() {
        if (parameters[0] instanceof JsclVector) {
            final JsclVector vector = (JsclVector) parameters[0];
            final Generic[] elements = vector.elements();

            if (elements.length == 0) {
                return new NumericWrapper(JsclInteger.ZERO);
            } else if (elements.length == 1) {
                return elements[0];
            } else {
                Generic result = elements[0].numeric();

                for (int i = 1; i < elements.length; i++) {
                    final Generic candidate = elements[i].numeric();
                    if (result.subtract(candidate).signum() > 0) {
                        result = candidate;
                    }
                }

                return result;
            }
        } else {
            return expressionValue();
        }
    }

    @Nonnull
    @Override
    public Variable newInstance() {
        return new Max((JsclVector) null);
    }
}
