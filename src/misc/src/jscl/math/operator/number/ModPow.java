package jscl.math.operator.number;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;
import jscl.math.operator.Operator;

public class ModPow extends Operator {
    public ModPow(Generic integer, Generic exponent, Generic modulo) {
        super("modpow",new Generic[] {integer,exponent,modulo});
    }

    public Generic compute() {
        try {
            JSCLInteger en=parameter[0].integerValue();
            JSCLInteger exponent=parameter[1].integerValue();
            JSCLInteger modulo=parameter[2].integerValue();
            return en.modPow(exponent,modulo);
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    protected Variable newinstance() {
        return new ModPow(null,null,null);
    }
}
