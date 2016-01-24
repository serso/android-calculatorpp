package jscl.math.operator;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.math.Generic;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.mathml.MathML;
import jscl.text.msg.JsclMessage;
import jscl.text.msg.Messages;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;

public class Integral extends Operator {

    public static final String NAME = "∫ab";

    public Integral(Generic expression, Generic variable, Generic n1, Generic n2) {
        super(NAME, new Generic[]{expression, variable, n1, n2});
    }

    protected Integral(@Nonnull Generic[] parameters) {
        super(NAME, parameters);
    }

    @Override
    public int getMinParameters() {
        return 4;
    }

    public Generic selfExpand() {
        if (JsclMathEngine.getInstance().getAngleUnits() != AngleUnit.rad) {
            JsclMathEngine.getInstance().getMessageRegistry().addMessage(new JsclMessage(Messages.msg_24, MessageType.warning));
        }

        Variable variable = parameters[1].variableValue();
        try {
            Generic a = parameters[0].antiDerivative(variable);
            return a.substitute(variable, parameters[3]).subtract(a.substitute(variable, parameters[2]));
        } catch (NotIntegrableException e) {
        }
        return expressionValue();
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
                return "a";
            case 3:
                return "b";
            default:
                return super.formatUndefinedParameter(i);
        }
    }

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
        return new Integral(parameters);
    }

    void bodyToMathML(MathML element) {
        Variable v = parameters[1].variableValue();
        MathML e1 = element.element("mrow");
        MathML e2 = element.element("msubsup");
        MathML e3 = element.element("mo");
        e3.appendChild(element.text("\u222B"));
        e2.appendChild(e3);
        parameters[2].toMathML(e2, null);
        parameters[3].toMathML(e2, null);
        e1.appendChild(e2);
        parameters[0].toMathML(e1, null);
        e2 = element.element("mo");
        e2.appendChild(element.text(/*"\u2146"*/"d"));
        e1.appendChild(e2);
        v.toMathML(e1, null);
        element.appendChild(e1);
    }

    @Nonnull
    public Variable newInstance() {
        return new Integral(null, null, null, null);
    }
}
