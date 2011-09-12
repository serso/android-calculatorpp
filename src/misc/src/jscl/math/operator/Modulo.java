package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;

public class Modulo extends Operator {
    public Modulo(Generic expression1, Generic expression2) {
        super("mod",new Generic[] {expression1,expression2});
    }

    public Generic compute() {
        try {
            JSCLInteger en=parameter[0].integerValue();
            JSCLInteger en2=parameter[1].integerValue();
            return en.mod(en2);
        } catch (NotIntegerException e) {}
        return parameter[0].remainder(parameter[1]);
    }

    protected Variable newinstance() {
        return new Modulo(null,null);
    }
}
