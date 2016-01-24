package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.Variable;
import jscl.math.function.Constants;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class Limit extends Operator {

    public static final String NAME = "lim";

    public Limit(Generic expression, Generic variable, Generic limit, Generic direction) {
        super(NAME, new Generic[]{expression, variable, limit, direction});
    }

    private Limit(Generic parameters[]) {
        super(NAME, createParameters(parameters));
    }

    private static Generic[] createParameters(Generic[] parameters) {
        final Generic[] result = new Generic[4];

        result[0] = parameters[0];
        result[1] = parameters[1];
        result[2] = parameters[2];
        result[3] = parameters.length > 3 && (parameters[2].compareTo(Constants.Generic.INF) != 0 && parameters[2].compareTo(Constants.Generic.INF.negate()) != 0) ? JsclInteger.valueOf(parameters[3].signum()) : JsclInteger.valueOf(0);

        return result;
    }

    @Override
    public int getMinParameters() {
        return 3;
    }

    @Override
    public int getMaxParameters() {
        return 4;
    }

    public Generic selfExpand() {
        return expressionValue();
    }

    // todo serso: think
    /*public String toString() {
		StringBuilder result = new StringBuilder();
        int n=4;
        if(parameters[3].signum()==0) n=3;
        result.append(name);
        result.append("(");
        for(int i=0;i<n;i++) {
            result.append(parameters[i]).append(i<n-1?", ":"");
        }
        result.append(")");
        return result.toString();
    }*/

    public void toMathML(MathML element, Object data) {
        int exponent = data instanceof Integer ? (Integer) data : 1;
        if (exponent == 1) bodyToMathML(element);
        else {
            MathML e1 = element.element("msup");
            MathML e2 = element.element("mfenced");
            bodyToMathML(e2);
            e1.appendChild(e2);
            e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Limit(parameters);
    }

    void bodyToMathML(MathML element) {
        int c = parameters[3].signum();
        MathML e1 = element.element("mrow");
        MathML e2 = element.element("munder");
        MathML e3 = element.element("mo");
        e3.appendChild(element.text("lim"));
        e2.appendChild(e3);
        e3 = element.element("mrow");
        parameters[1].toMathML(e3, null);
        MathML e4 = element.element("mo");
        e4.appendChild(element.text("\u2192"));
        e3.appendChild(e4);
        if (c == 0) parameters[2].toMathML(e3, null);
        else {
            e4 = element.element("msup");
            parameters[2].toMathML(e4, null);
            MathML e5 = element.element("mo");
            if (c < 0) e5.appendChild(element.text("-"));
            else if (c > 0) e5.appendChild(element.text("+"));
            e4.appendChild(e5);
            e3.appendChild(e4);
        }
        e2.appendChild(e3);
        e1.appendChild(e2);
        parameters[0].toMathML(e1, null);
        element.appendChild(e1);
    }

    @Nonnull
    public Variable newInstance() {
        return new Limit(null, null, null, null);
    }
}
