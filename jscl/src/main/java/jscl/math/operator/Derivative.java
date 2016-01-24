package jscl.math.operator;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;
import jscl.mathml.MathML;
import jscl.text.msg.JsclMessage;
import jscl.text.msg.Messages;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;

public class Derivative extends Operator {

    public static final String NAME = "∂";

    public Derivative(Generic expression, Generic variable, Generic value, Generic order) {
        super(NAME, new Generic[]{expression, variable, value, order});
    }

    private Derivative(Generic parameters[]) {
        super(NAME, createParameters(parameters));
    }

    private static Generic[] createParameters(@Nonnull Generic[] parameters) {
        final Generic[] result = new Generic[4];

        result[0] = parameters[0];
        result[1] = parameters[1];
        result[2] = parameters.length > 2 ? parameters[2] : parameters[1];
        result[3] = parameters.length > 3 ? parameters[3] : JsclInteger.valueOf(1);

        return result;
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    @Override
    public int getMaxParameters() {
        return 4;
    }

    @Nonnull
    @Override
    protected String formatUndefinedParameter(int i) {
        switch (i) {
            case 0:
                return "f(x)";
            case 1:
                return "x";
            case 2:
                return "x_point";
            case 3:
                return "order";
            default:
                return super.formatUndefinedParameter(i);
        }
    }

    public Generic selfExpand() {
        if (JsclMathEngine.getInstance().getAngleUnits() != AngleUnit.rad) {
            JsclMathEngine.getInstance().getMessageRegistry().addMessage(new JsclMessage(Messages.msg_25, MessageType.warning));
        }

        Variable variable = parameters[1].variableValue();
        try {
            int n = parameters[3].integerValue().intValue();
            Generic a = parameters[0];
            for (int i = 0; i < n; i++) {
                a = a.derivative(variable);
            }
            return a.substitute(variable, parameters[2]);
        } catch (NotIntegerException e) {
        }
        return expressionValue();
    }

    // todo serso: think
    /*public String toString() {
         final StringBuilder result = new StringBuilder();
         int n = 4;
         if (parameters[3].compareTo(JsclInteger.valueOf(1)) == 0) {
             n = 3;
             if (parameters[2].compareTo(parameters[1]) == 0) n = 2;
         }
         result.append(name);
         result.append("(");
         for (int i = 0; i < n; i++) {
             result.append(parameters[i]).append(i < n - 1 ? ", " : "");
         }
         result.append(")");
         return result.toString();
     }*/

    public void toMathML(MathML element, Object data) {
        int exponent = data instanceof Integer ? (Integer) data : 1;
        if (exponent == 1) derivationToMathML(element, false);
        else {
            MathML e1 = element.element("msup");
            derivationToMathML(e1, true);
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
        MathML e1 = element.element("mfenced");
        parameters[0].toMathML(e1, null);
        if (parameters[2].compareTo(parameters[1]) != 0) parameters[2].toMathML(e1, null);
        element.appendChild(e1);
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Derivative(parameters);
    }

    void derivationToMathML(MathML element, boolean fenced) {
        if (fenced) {
            MathML e1 = element.element("mfenced");
            derivationToMathML(e1);
            element.appendChild(e1);
        } else {
            derivationToMathML(element);
        }
    }

    @Override
    public Generic numeric() {
        try {
            parameters[3].integerValue();
            return expand().numeric();
        } catch (NotIntegerException e) {
        }

        return expressionValue();
    }

    /*@Override
     public Generic simplify() {
         Generic result = null;
         try {
             result = expand().simplify();
         } catch (ArithmeticException e) {
             result = super.simplify();
         }
         return result;
     }*/

    void derivationToMathML(MathML element) {
        Variable v = parameters[1].variableValue();
        int n = 0;
        try {
            n = parameters[3].integerValue().intValue();
        } catch (NotIntegerException e) {
        }
        if (n == 1) {
            MathML e1 = element.element("mfrac");
            MathML e2 = element.element("mo");
            e2.appendChild(element.text(/*"\u2146"*/"d"));
            e1.appendChild(e2);
            e2 = element.element("mrow");
            MathML e3 = element.element("mo");
            e3.appendChild(element.text(/*"\u2146"*/"d"));
            e2.appendChild(e3);
            v.toMathML(e2, null);
            e1.appendChild(e2);
            element.appendChild(e1);
        } else {
            MathML e1 = element.element("mfrac");
            MathML e2 = element.element("msup");
            MathML e3 = element.element("mo");
            e3.appendChild(element.text(/*"\u2146"*/"d"));
            e2.appendChild(e3);
            parameters[3].toMathML(e2, null);
            e1.appendChild(e2);
            e2 = element.element("mrow");
            e3 = element.element("mo");
            e3.appendChild(element.text(/*"\u2146"*/"d"));
            e2.appendChild(e3);
            e3 = element.element("msup");
            parameters[1].toMathML(e3, null);
            parameters[3].toMathML(e3, null);
            e2.appendChild(e3);
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    @Nonnull
    public Variable newInstance() {
        return new Derivative(null, null, null, null);
    }
}
