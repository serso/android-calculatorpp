package jscl.text;

import jscl.math.Generic;
import jscl.math.Variable;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 3:21 PM
 */
class VariableConverter<T extends Variable> extends AbstractConverter<T, Generic> {

    VariableConverter(@Nonnull Parser<T> variableParser) {
        super(variableParser);
    }

    @Override
    public Generic parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        return this.parser.parse(p, previousSumElement).expressionValue();
    }
}
