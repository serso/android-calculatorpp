package jscl.text;

import jscl.math.Generic;
import jscl.math.JsclVector;
import jscl.math.Variable;
import jscl.math.VectorVariable;

import javax.annotation.Nonnull;

public class VectorVariableParser implements Parser<Variable> {
    public static final Parser<Variable> parser = new VectorVariableParser();

    private VectorVariableParser() {
    }

    public Variable parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        JsclVector result;
        try {
            result = VectorParser.parser.parse(p, previousSumElement);
        } catch (ParseException e) {
            throw e;
        }
        return new VectorVariable(result);
    }
}
