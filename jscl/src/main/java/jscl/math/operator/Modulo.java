package jscl.math.operator;

import jscl.math.*;
import jscl.math.numeric.Real;

import javax.annotation.Nonnull;

public class Modulo extends Operator {

    public static final String NAME = "mod";

    public Modulo(Generic first, Generic second) {
        super(NAME, new Generic[]{first, second});
    }

    private Modulo(Generic parameters[]) {
        super(NAME, parameters);
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    public Generic selfExpand() {
        try {
            return tryIntegerMod();
        } catch (NotIntegerException e) {
        }
        return tryRealMod();
    }

    private Generic tryRealMod() {
        final double numerator = parameters[0].doubleValue();
        final double denominator = parameters[1].doubleValue();
        return new NumericWrapper(Real.valueOf(numerator % denominator));
    }

    @Nonnull
    private Generic tryIntegerMod() throws NotIntegerException{
        final JsclInteger numerator = parameters[0].integerValue();
        final JsclInteger denominator = parameters[1].integerValue();
        return numerator.mod(denominator);
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Modulo(parameters);
    }

    @Override
    public Generic numeric() {
        return newNumericFunction().selfNumeric();
    }

    @Override
    public Generic selfNumeric() {
        return selfExpand();
    }

    @Nonnull
    public Variable newInstance() {
        return new Modulo(null, null);
    }
}
