package jscl.math.operator.stat;

import jscl.math.*;
import jscl.math.function.Sqrt;
import jscl.math.operator.Operator;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 12/26/11
 * Time: 11:09 AM
 */
public class StandardDeviation extends AbstractStatFunction {

    public static final String NAME = "st_dev";

    public StandardDeviation(JsclVector vector) {
        this(new Generic[]{vector});
    }

    private StandardDeviation(@Nonnull Generic[] parameters) {
        super(NAME, parameters);
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new StandardDeviation(parameters);
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
                return new NumericWrapper(JsclInteger.ZERO);
            } else {
                final Generic mean = new Mean(vector).numeric();

                Generic result = new NumericWrapper(JsclInteger.ZERO);
                for (int i = 0; i < elements.length; i++) {
                    result = result.add(elements[i].numeric().subtract(mean).pow(2));
                }
                return new Sqrt(result.divide(JsclInteger.valueOf(elements.length).numeric().subtract(JsclInteger.ONE.negate().numeric()))).numeric();
            }
        } else {
            return expressionValue();
        }
    }

    @Nonnull
    @Override
    public Variable newInstance() {
        return new StandardDeviation((JsclVector) null);
    }
}
