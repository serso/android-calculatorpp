package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;

public class Division extends Operator {
    public Division(Generic expression1, Generic expression2) {
        super("div",new Generic[] {expression1,expression2});
    }

    public Generic compute() {
        return parameter[0].divideAndRemainder(parameter[1])[0];
    }

    protected Variable newinstance() {
        return new Division(null,null);
    }
}
