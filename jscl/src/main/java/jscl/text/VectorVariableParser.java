package jscl.text;

import jscl.math.Generic;
import jscl.math.Variable;
import jscl.math.VectorVariable;

import javax.annotation.Nonnull;

public class VectorVariableParser implements Parser<Variable> {
    public static final Parser<Variable> parser = new VectorVariableParser();

    private VectorVariableParser() {
    }

    public Variable parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        return new VectorVariable(VectorParser.parser.parse(p, previousSumElement));
    }
}
