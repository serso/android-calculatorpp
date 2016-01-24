package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.Variable;
import jscl.math.function.Root;
import jscl.math.polynomial.Polynomial;
import jscl.math.polynomial.UnivariatePolynomial;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class Solve extends Operator {

    public static final String NAME = "solve";

    public Solve(Generic expression, Generic variable, Generic subscript) {
        super(NAME, new Generic[]{expression, variable, subscript});
    }

    public Solve(Generic parameters[]) {
        super(NAME, createParameters(parameters));
    }

    private static Generic[] createParameters(Generic[] parameters) {
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

    @Override
    public int getMaxParameters() {
        return 3;
    }

    public Generic selfExpand() {
        final Variable variable = parameters[1].variableValue();

        int subscript = parameters[2].integerValue().intValue();
        if (parameters[0].isPolynomial(variable)) {
            return new Root((UnivariatePolynomial) Polynomial.factory(variable).valueOf(parameters[0]), subscript).selfExpand();
        }

        return expressionValue();
    }

    // todo serso: think
    /*public String toString() {
             StringBuilder result = new StringBuilder();
             int n=3;
             if(parameters[2].signum()==0) n=2;
             result.append(name);
             result.append("(");
             for(int i=0;i<n;i++) {
                 result.append(parameters[i]).append(i<n-1?", ":"");
             }
             result.append(")");
             return result.toString();
         }*/

    public void toMathML(MathML element, Object data) {
        MathML e1;
        int exponent = data instanceof Integer ? (Integer) data : 1;
        int n = 3;
        if (parameters[2].signum() == 0) n = 2;
        if (exponent == 1) nameToMathML(element);
        else {
            e1 = element.element("msup");
            nameToMathML(e1);
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
        e1 = element.element("mfenced");
        for (int i = 0; i < n; i++) {
            parameters[i].toMathML(e1, null);
        }
        element.appendChild(e1);
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Solve(parameters);
    }

    @Nonnull
    public Variable newInstance() {
        return new Solve(null, null, null);
    }
}
