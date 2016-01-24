package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class Product extends Operator {

    public static final String NAME = "∏";

    public Product(Generic expression, Generic variable, Generic n1, Generic n2) {
        super(NAME, new Generic[]{expression, variable, n1, n2});
    }

    private Product(Generic parameters[]) {
        super(NAME, parameters);
    }

    @Override
    public int getMinParameters() {
        return 4;
    }

    public Generic selfExpand() {
        Variable variable = parameters[1].variableValue();
        try {
            int n1 = parameters[2].integerValue().intValue();
            int n2 = parameters[3].integerValue().intValue();
            Generic a = JsclInteger.valueOf(1);
            for (int i = n1; i <= n2; i++) {
                a = a.multiply(parameters[0].substitute(variable, JsclInteger.valueOf(i)));
            }
            return a;
        } catch (NotIntegerException e) {
        }
        return expressionValue();
    }

    @Nonnull
    @Override
    protected String formatUndefinedParameter(int i) {
        switch (i) {
            case 0:
                return "f(i)";
            case 1:
                return "i";
            case 2:
                return "from";
            case 3:
                return "to";
            default:
                return super.formatUndefinedParameter(i);
        }
    }

    public void toMathML(MathML element, Object data) {
        int exponent = data instanceof Integer ? ((Integer) data).intValue() : 1;
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
        return new Product(parameters);
    }

    void bodyToMathML(MathML element) {
        MathML e1 = element.element("mrow");
        MathML e2 = element.element("munderover");
        MathML e3 = element.element("mo");
        e3.appendChild(element.text("\u220F"));
        e2.appendChild(e3);
        e3 = element.element("mrow");
        parameters[1].toMathML(e3, null);
        MathML e4 = element.element("mo");
        e4.appendChild(element.text("="));
        e3.appendChild(e4);
        parameters[2].toMathML(e3, null);
        e2.appendChild(e3);
        parameters[3].toMathML(e2, null);
        e1.appendChild(e2);
        parameters[0].toMathML(e1, null);
        element.appendChild(e1);
    }

    @Nonnull
    public Variable newInstance() {
        return new Product(null, null, null, null);
    }
}
