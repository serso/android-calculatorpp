package jscl.text;

import jscl.math.DoubleVariable;
import jscl.math.Generic;
import jscl.math.Variable;

import javax.annotation.Nonnull;

public class DoubleVariableParser implements Parser<Variable> {

    public static final Parser<Variable> parser = new DoubleVariableParser();

    private DoubleVariableParser() {
    }

    @Nonnull
    public Variable parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        return new DoubleVariable(DoubleParser.parser.parse(p, previousSumElement));
    }
}
