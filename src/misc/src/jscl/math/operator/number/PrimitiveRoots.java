package jscl.math.operator.number;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.JSCLVector;
import jscl.math.NotIntegerException;
import jscl.math.Variable;
import jscl.math.operator.Operator;

public class PrimitiveRoots extends Operator {
    public PrimitiveRoots(Generic integer) {
        super("primitiveroots",new Generic[] {integer});
    }

    public Generic compute() {
        try {
            JSCLInteger en=parameter[0].integerValue();
            Generic a[]=en.primitiveRoots();
            return new JSCLVector(a.length>0?a:new Generic[] {JSCLInteger.valueOf(0)});
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    protected Variable newinstance() {
        return new PrimitiveRoots(null);
    }
}
