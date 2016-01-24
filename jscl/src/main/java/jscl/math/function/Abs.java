package jscl.math.function;

import jscl.math.*;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class Abs extends Function {

    public Abs(Generic generic) {
        super("abs", new Generic[]{generic});
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        return Constants.Generic.HALF.multiply(parameters[0]).multiply(new Abs(parameters[0]).selfExpand());
    }

    public Generic derivative(int n) {
        return new Sgn(parameters[0]).selfExpand();
    }

    public Generic selfExpand() {
        if (parameters[0].signum() < 0) {
            return new Abs(parameters[0].negate()).selfExpand();
        }
        try {
            return parameters[0].integerValue().abs();
        } catch (NotIntegerException e) {
        }
        return expressionValue();
    }

    public Generic selfElementary() {
        return new Sqrt(
                parameters[0].pow(2)
        ).selfElementary();
    }

    public Generic selfSimplify() {
        if (parameters[0].signum() < 0) {
            return new Abs(parameters[0].negate()).selfSimplify();
        }
        try {
            return parameters[0].integerValue().abs();
        } catch (NotIntegerException e) {
        }
        try {
            Variable v = parameters[0].variableValue();
            if (v instanceof Abs) {
                Function f = (Function) v;
                return f.selfSimplify();
            } else if (v instanceof Sgn) {
                return JsclInteger.valueOf(1);
            }
        } catch (NotVariableException e) {
        }
        return expressionValue();
    }

    public Generic selfNumeric() {
        return parameters[0].abs();
    }

    public String toJava() {
        final StringBuilder result = new StringBuilder();

        result.append(parameters[0].toJava());
        result.append(".abs()");

        return result.toString();
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

    void bodyToMathML(MathML element) {
        MathML e1 = element.element("mfenced");
        e1.setAttribute("open", "|");
        e1.setAttribute("close", "|");
        parameters[0].toMathML(e1, null);
        element.appendChild(e1);
    }

    @Nonnull
    public Variable newInstance() {
        return new Abs(null);
    }
}
