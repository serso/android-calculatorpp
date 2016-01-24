package jscl.math.function;

import jscl.math.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Sgn extends Function {

    public Sgn(Generic generic) {
        super("sgn", new Generic[]{generic});
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        return new Abs(parameters[0]).selfExpand();
    }

    public Generic derivative(int n) {
        return JsclInteger.valueOf(0);
    }

    public Generic selfExpand() {
        final Generic result = selfEvaluate();

        if (result == null) {
            return expressionValue();
        } else {
            return result;
        }

    }

    @Nullable
    private Generic selfEvaluate() {
        Generic result = null;

        if (parameters[0].signum() < 0) {
            result = new Sgn(parameters[0].negate()).selfExpand().negate();
        } else if (parameters[0].signum() == 0) {
            result = JsclInteger.valueOf(0);
        }

        if (result == null) {
            try {
                result = JsclInteger.valueOf(parameters[0].integerValue().signum());
            } catch (NotIntegerException e) {
            }
        }

        return result;
    }

    public Generic selfElementary() {
        return new Fraction(parameters[0], new Abs(parameters[0]).selfElementary()).selfElementary();
    }

    public Generic selfSimplify() {
        final Generic result = selfEvaluate();

        if (result == null) {
            try {
                Variable v = parameters[0].variableValue();
                if (v instanceof Abs) {
                    return JsclInteger.valueOf(1);
                } else if (v instanceof Sgn) {
                    Function f = (Function) v;
                    return f.selfSimplify();
                }
            } catch (NotVariableException e) {
            }

            return expressionValue();
        } else {
            return result;
        }
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).sgn();
    }

    public String toJava() {
        final StringBuilder result = new StringBuilder();

        result.append(parameters[0].toJava());
        result.append(".sgn()");
        return result.toString();
    }

    @Nonnull
    public Variable newInstance() {
        return new Sgn(null);
    }
}
