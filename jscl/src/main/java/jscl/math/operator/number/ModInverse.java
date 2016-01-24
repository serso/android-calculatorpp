package jscl.math.operator.number;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;
import jscl.math.operator.Operator;

import javax.annotation.Nonnull;

public class ModInverse extends Operator {

    public static final String NAME = "modinv";

    public ModInverse(Generic integer, Generic modulo) {
        super(NAME, new Generic[]{integer, modulo});
    }

    private ModInverse(Generic parameters[]) {
        super(NAME, parameters);
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    public Generic selfExpand() {
        try {
            JsclInteger en = parameters[0].integerValue();
            JsclInteger modulo = parameters[1].integerValue();
            return en.modInverse(modulo);
        } catch (NotIntegerException e) {
        }
        return expressionValue();
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new ModInverse(parameters);
    }

    @Nonnull
    public Variable newInstance() {
        return new ModInverse(null, null);
    }


}
