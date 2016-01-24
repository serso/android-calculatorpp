package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.JsclVector;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.math.operator.VectorOperator;
import jscl.math.operator.product.GeometricProduct;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class Del extends VectorOperator {

    public static final String NAME = "del";

    public Del(Generic vector, Generic variable, Generic algebra) {
        super(NAME, new Generic[]{vector, variable, algebra});
    }

    private Del(@Nonnull Generic parameters[]) {
        super(NAME, createParameters(parameters));
    }

    private static Generic[] createParameters(@Nonnull Generic[] parameters) {
        final Generic[] result = new Generic[3];

        result[0] = parameters[0];
        result[1] = parameters[1];
        result[2] = parameters.length > 2 ? parameters[2] : JsclInteger.valueOf(0);

        return result;
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    public Generic selfExpand() {
        Variable variable[] = toVariables((JsclVector) parameters[1]);
        int algebra[] = GeometricProduct.algebra(parameters[2]);
        if (parameters[0] instanceof JsclVector) {
            JsclVector vector = (JsclVector) parameters[0];
            return vector.del(variable, algebra);
        }
        return expressionValue();
    }

    // todo serso: think
    /*public String toString() {
         final StringBuilder result = new StringBuilder();
         int n = 3;
         if (parameters[2].signum() == 0) n = 2;
         result.append(name);
         result.append("(");
         for (int i = 0; i < n; i++) {
             result.append(parameters[i]).append(i < n - 1 ? ", " : "");
         }
         result.append(")");
         return result.toString();
     }*/

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Del(parameters);
    }

    protected void bodyToMathML(MathML element) {
        operator(element, "nabla");
        parameters[0].toMathML(element, null);
    }

    @Nonnull
    public Variable newInstance() {
        return new Del(null, null, null);
    }
}
