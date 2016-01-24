package jscl.math.operator.stat;

import jscl.math.Generic;
import jscl.math.operator.Operator;

/**
 * User: serso
 * Date: 1/15/12
 * Time: 4:59 PM
 */
public abstract class AbstractStatFunction extends Operator {

    protected AbstractStatFunction(String name, Generic[] parameters) {
        super(name, parameters);
    }

    @Override
    public final Generic numeric() {
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = parameters[i].expand();
        }

        return selfNumeric();
    }
}
