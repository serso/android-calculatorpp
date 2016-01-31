package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;

/**
 * User: serso
 * Date: 11/2/11
 * Time: 11:07 AM
 */
abstract class PostfixFunction extends Operator {

    PostfixFunction(String name, Generic[] parameter) {
        super(name, parameter);
    }

    public String toString() {
        return formatParameter(0) + getName();
    }

    public final Generic numeric() {
        final AbstractFunction result = (AbstractFunction) newInstance();

        for (int i = 0; i < parameters.length; i++) {
            result.parameters[i] = parameters[i].numeric();
        }

        return result.selfNumeric();
    }

    public abstract Generic selfNumeric();

    public boolean isConstant(Variable variable) {
        boolean result = !isIdentity(variable);

        if (result) {
            for (Generic parameter : parameters) {
                if (!parameter.isConstant(variable)) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }
}
