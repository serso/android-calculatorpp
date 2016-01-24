package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.JsclVector;
import jscl.math.Variable;
import jscl.math.function.ImplicitFunction;
import jscl.math.operator.Operator;
import jscl.math.operator.VectorOperator;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class GeometricProduct extends VectorOperator {

    public static final String NAME = "geometric";

    public GeometricProduct(Generic vector1, Generic vector2, Generic algebra) {
        super(NAME, new Generic[]{vector1, vector2, algebra});
    }

    private GeometricProduct(@Nonnull Generic[] parameters) {
        super(NAME, createParameters(parameters));
    }

    private static Generic[] createParameters(@Nonnull Generic[] parameters) {
        final Generic[] result = new Generic[3];

        result[0] = parameters[0];
        result[1] = parameters[1];
        result[2] = parameters.length > 2 ? parameters[2] : JsclInteger.valueOf(0);

        return result;
    }

    public static int[] algebra(Generic generic) {
        if (generic.signum() == 0) return null;
        Variable v = generic.variableValue();
        if (v instanceof ImplicitFunction) {
            Generic g[] = ((ImplicitFunction) v).getParameters();
            int p = g[0].integerValue().intValue();
            int q = g[1].integerValue().intValue();
            if (v.compareTo(new ImplicitFunction("cl", new Generic[]{JsclInteger.valueOf(p), JsclInteger.valueOf(q)}, new int[]{0, 0}, new Generic[]{})) == 0)
                return new int[]{p, q};
        }
        throw new ArithmeticException();
    }

    @Override
    public int getMinParameters() {
        return 3;
    }

    public Generic selfExpand() {
        int algebra[] = algebra(parameters[2]);
        if (parameters[0] instanceof JsclVector && parameters[1] instanceof JsclVector) {
            JsclVector v1 = (JsclVector) parameters[0];
            JsclVector v2 = (JsclVector) parameters[1];
            return v1.geometricProduct(v2, algebra);
        }
        return expressionValue();
    }

    // todo serso: think
    /*public String toString() {
        StringBuffer buffer=new StringBuffer();
        int n=3;
        if(parameters[2].signum()==0) n=2;
        buffer.append(name);
        buffer.append("(");
        for(int i=0;i<n;i++) {
            buffer.append(parameters[i]).append(i<n-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }*/

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new GeometricProduct(parameters);
    }

    protected void bodyToMathML(MathML element) {
        parameters[0].toMathML(element, null);
        parameters[1].toMathML(element, null);
    }

    @Nonnull
    public Variable newInstance() {
        return new GeometricProduct(null, null, null);
    }
}
