package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.math.operator.product.GeometricProduct;
import jscl.mathml.MathML;

public class Del extends VectorOperator {
    public Del(Generic vector, Generic variable, Generic algebra) {
        super("del",new Generic[] {vector,variable,algebra});
    }

    public Generic compute() {
        Variable variable[]=variables(parameter[1]);
        int algebra[]=GeometricProduct.algebra(parameter[2]);
        if(parameter[0] instanceof JSCLVector) {
            JSCLVector vector=(JSCLVector)parameter[0];
            return vector.del(variable,algebra);
        }
        return expressionValue();
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        int n=3;
        if(parameter[2].signum()==0) n=2;
        buffer.append(name);
        buffer.append("(");
        for(int i=0;i<n;i++) {
            buffer.append(parameter[i]).append(i<n-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    protected void bodyToMathML(MathML element) {
        operator(element,"nabla");
        parameter[0].toMathML(element,null);
    }

    protected Variable newinstance() {
        return new Del(null,null,null);
    }
}
