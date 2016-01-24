package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;

import javax.annotation.Nonnull;

public class Division extends Operator {

    public static final String NAME = "div";

    public Division(Generic expression1, Generic expression2) {
        super(NAME, new Generic[]{expression1, expression2});
    }

    private Division(Generic parameters[]) {
        super(NAME, parameters);
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    public Generic selfExpand() {
        return parameters[0].divideAndRemainder(parameters[1])[0];
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Division(parameters);
    }

    @Nonnull
    public Variable newInstance() {
        return new Division(null, null);
    }
}
