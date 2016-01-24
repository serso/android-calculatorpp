package jscl.math.operator.number;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;
import jscl.math.operator.Operator;

import javax.annotation.Nonnull;

public class ModPow extends Operator {

    public static final String NAME = "modpow";

    public ModPow(Generic integer, Generic exponent, Generic modulo) {
        super(NAME, new Generic[]{integer, exponent, modulo});
    }

    private ModPow(Generic parameters[]) {
        super(NAME, parameters);
    }

    @Override
    public int getMinParameters() {
        return 3;
    }

    public Generic selfExpand() {
        try {
            JsclInteger en = parameters[0].integerValue();
            JsclInteger exponent = parameters[1].integerValue();
            JsclInteger modulo = parameters[2].integerValue();
            return en.modPow(exponent, modulo);
        } catch (NotIntegerException e) {
        }
        return expressionValue();
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new ModPow(parameters);
    }

    @Nonnull
    public Variable newInstance() {
        return new ModPow(null, null, null);
    }
}
