package jscl.text;

import jscl.math.Generic;
import jscl.math.MatrixVariable;
import jscl.math.Variable;

import javax.annotation.Nonnull;

class MatrixVariableParser implements Parser<Variable> {
    public static final Parser<Variable> parser = new MatrixVariableParser();

    private MatrixVariableParser() {
    }

    public Variable parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        return new MatrixVariable(MatrixParser.parser.parse(p, previousSumElement));
    }
}
