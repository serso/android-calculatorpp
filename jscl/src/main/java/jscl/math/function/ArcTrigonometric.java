package jscl.math.function;

import jscl.math.Generic;
import jscl.math.NotIntegrableException;

public abstract class ArcTrigonometric extends Function {

    public ArcTrigonometric(String name, Generic parameter[]) {
        super(name, parameter);
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        throw new NotIntegrableException(this);
    }

    public Generic selfSimplify() {
        return selfExpand();
    }
}
