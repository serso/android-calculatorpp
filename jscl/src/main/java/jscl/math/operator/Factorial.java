package jscl.math.operator;

import jscl.math.*;
import jscl.math.function.Pow;
import jscl.mathml.MathML;
import jscl.text.ParserUtils;

import javax.annotation.Nonnull;

public class Factorial extends PostfixFunction {

    public static final String NAME = "!";

    public Factorial(Generic expression) {
        super(NAME, new Generic[]{expression});
    }

    private Factorial(Generic[] parameter) {
        super(NAME, ParserUtils.copyOf(parameter, 1));
    }

    @Override
    public int getMinParameters() {
        return 1;
    }

    public Generic selfExpand() {
        return expressionValue();
    }

    @Override
    public Generic selfNumeric() {
        final Generic parameter = parameters[0];
        if (parameter.isInteger()) {
            int n = parameter.integerValue().intValue();
            if (n < 0) {
                //return expressionValue();
                throw new ArithmeticException("Cannot take factorial from negative integer!");
            }

            Generic result = JsclInteger.valueOf(1);
            for (int i = 0; i < n; i++) {
                ParserUtils.checkInterruption();
                result = result.multiply(JsclInteger.valueOf(i + 1));
            }

            if (result instanceof JsclInteger) {
                return new NumericWrapper(((JsclInteger) result));
            } else {
                throw new NotIntegerException();
            }

        } else {
            throw new NotIntegerException();
        }
    }

    public void toMathML(MathML element, Object data) {
        int exponent = data instanceof Integer ? (Integer) data : 1;
        if (exponent == 1) bodyToMathML(element);
        else {
            MathML e1 = element.element("msup");
            bodyToMathML(e1);
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Factorial(parameters);
    }

    void bodyToMathML(MathML element) {
        MathML e1 = element.element("mrow");
        try {
            JsclInteger en = parameters[0].integerValue();
            en.toMathML(e1, null);
        } catch (NotIntegerException e) {
            try {
                Variable v = parameters[0].variableValue();
                if (v instanceof Pow) {
                    GenericVariable.valueOf(parameters[0]).toMathML(e1, null);
                } else v.toMathML(e1, null);
            } catch (NotVariableException e2) {
                GenericVariable.valueOf(parameters[0]).toMathML(e1, null);
            }
        }
        MathML e2 = element.element("mo");
        e2.appendChild(element.text("!"));
        e1.appendChild(e2);
        element.appendChild(e1);
    }

    @Nonnull
    public Variable newInstance() {
        return new Factorial((Generic) null);
    }
}
