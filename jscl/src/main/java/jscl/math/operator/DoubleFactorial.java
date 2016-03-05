package jscl.math.operator;

import jscl.math.*;
import jscl.math.function.Pow;
import jscl.mathml.MathML;
import jscl.text.ParserUtils;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 12/15/11
 * Time: 10:32 PM
 */
public class DoubleFactorial extends PostfixFunction {

    public static final String NAME = "!!";

    public DoubleFactorial(Generic expression) {
        super(NAME, new Generic[]{expression});
    }

    private DoubleFactorial(Generic[] parameter) {
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

            Generic result;
            if (n == 0) {
                result = JsclInteger.valueOf(1);
            } else {
                int i;
                if (n % 2 != 0) {
                    // odd
                    i = 1;
                } else {
                    // even
                    i = 2;
                }

                result = JsclInteger.valueOf(i);
                for (; i < n; i += 2) {
                    ParserUtils.checkInterruption();
                    result = result.multiply(JsclInteger.valueOf(i + 2));
                }
            }

            if (result instanceof JsclInteger) {
                return new NumericWrapper(((JsclInteger) result));
            } else {
                throw NotIntegerException.get();
            }

        } else {
            throw NotIntegerException.get();
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
        return new DoubleFactorial(parameters);
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
        e2.appendChild(element.text("!!"));
        e1.appendChild(e2);
        element.appendChild(e1);
    }

    @Nonnull
    public Variable newInstance() {
        return new DoubleFactorial((Generic) null);
    }
}
