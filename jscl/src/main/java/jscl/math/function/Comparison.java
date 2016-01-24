package jscl.math.function;

import jscl.math.*;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class Comparison extends Function {

    public static final List<String> names = Arrays.asList("eq", "le", "ge", "ne", "lt", "gt", "ap");
    private static final String eass[] = {"=", "<=", ">=", "<>", "<", ">", "~"};
    private static final String easj[] = {"==", "<=", ">=", "!=", "<", ">", "=="};
    private static final String easm[] = {"=", "\u2264", "\u2265", "\u2260", "<", ">", "\u2248"};
    int operator;

    public Comparison(String name, Generic expression1, Generic expression2) {
        super(name, new Generic[]{expression1, expression2});
        operator = names.indexOf(name);
        if (operator < 0) {
            throw new ArithmeticException(name + " comparison function doesn't exist!");
        }
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        throw new NotIntegrableException(this);
    }

    public Generic derivative(int n) {
        return JsclInteger.valueOf(0);
    }

    public Generic selfExpand() {
        try {
            return compare(parameters[0].integerValue(), parameters[1].integerValue());
        } catch (NotIntegerException e) {
        }
        return expressionValue();
    }

    public Generic selfElementary() {
        return expressionValue();
    }

    public Generic selfSimplify() {
        return expressionValue();
    }

    public Generic selfNumeric() {
        return compare((NumericWrapper) parameters[0], (NumericWrapper) parameters[1]);
    }

    JsclInteger compare(JsclInteger a1, JsclInteger a2) {
        return JsclInteger.valueOf(compare((Generic) a1, a2) ? 1 : 0);
    }

    NumericWrapper compare(NumericWrapper a1, NumericWrapper a2) {
        return new NumericWrapper(JsclInteger.valueOf(compare(a1, (Generic) a2) ? 1 : 0));
    }

    boolean compare(Generic a1, Generic a2) {
        switch (operator) {
            case 0:
                return a1.compareTo(a2) == 0;
            case 1:
                return a1.compareTo(a2) <= 0;
            case 2:
                return a1.compareTo(a2) >= 0;
            case 3:
                return a1.compareTo(a2) != 0;
            case 4:
                return a1.compareTo(a2) < 0;
            case 5:
                return a1.compareTo(a2) > 0;
            case 6:
                return a1.compareTo(a2) == 0;
            default:
                return false;
        }
    }

    public String toJava() {
        final StringBuilder result = new StringBuilder();
        result.append(parameters[0].toJava()).append(easj[operator]).append(parameters[1].toJava());
        return result.toString();
    }

    public void toMathML(MathML element, Object data) {
        parameters[0].toMathML(element, null);
        MathML e1 = element.element("mo");
        e1.appendChild(element.text(easm[operator]));
        element.appendChild(e1);
        parameters[1].toMathML(element, null);
    }

    @Nonnull
    public Variable newInstance() {
        return new Comparison(name, null, null);
    }
}
