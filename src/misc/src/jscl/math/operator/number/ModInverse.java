package jscl.math.operator.number;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;
import jscl.math.operator.Operator;

public class ModInverse extends Operator {
    public ModInverse(Generic integer, Generic modulo) {
        super("modinv",new Generic[] {integer,modulo});
    }

    public Generic compute() {
        try {
            JSCLInteger en=parameter[0].integerValue();
            JSCLInteger modulo=parameter[1].integerValue();
            return en.modInverse(modulo);
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    protected Variable newinstance() {
        return new ModInverse(null,null);
    }
}
