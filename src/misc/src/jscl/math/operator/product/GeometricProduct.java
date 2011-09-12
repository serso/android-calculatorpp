package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.function.ImplicitFunction;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

public class GeometricProduct extends VectorOperator {
    public GeometricProduct(Generic vector1, Generic vector2, Generic algebra) {
        super("geometric",new Generic[] {vector1,vector2,algebra});
    }

    public Generic compute() {
        int algebra[]=algebra(parameter[2]);
        if(parameter[0] instanceof JSCLVector && parameter[1] instanceof JSCLVector) {
            JSCLVector v1=(JSCLVector)parameter[0];
            JSCLVector v2=(JSCLVector)parameter[1];
            return v1.geometricProduct(v2,algebra);
        }
        return expressionValue();
    }

    public static int[] algebra(Generic generic) {
        if(generic.signum()==0) return null;
        Variable v=generic.variableValue();
        if(v instanceof ImplicitFunction) {
            Generic g[]=((ImplicitFunction)v).parameters();
            int p=g[0].integerValue().intValue();
            int q=g[1].integerValue().intValue();
            if(v.compareTo(new ImplicitFunction("cl",new Generic[] {JSCLInteger.valueOf(p),JSCLInteger.valueOf(q)},new int[] {0,0},new Generic[] {}))==0) return new int[] {p,q};
        }
        throw new ArithmeticException();
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
        parameter[0].toMathML(element,null);
        parameter[1].toMathML(element,null);
    }

    protected Variable newinstance() {
        return new GeometricProduct(null,null,null);
    }
}
