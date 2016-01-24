package jscl.math.function.trigonometric;

import jscl.math.*;
import jscl.math.function.Fraction;
import jscl.math.function.Ln;
import jscl.math.function.Trigonometric;

import javax.annotation.Nonnull;

public class Cot extends Trigonometric {
    public Cot(Generic generic) {
        super("cot", new Generic[]{generic});
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        return new Ln(
                JsclInteger.valueOf(4).multiply(
                        new Sin(parameters[0]).selfExpand()
                )
        ).selfExpand();
    }

    public Generic derivative(int n) {
        return JsclInteger.valueOf(1).add(
                new Cot(parameters[0]).selfExpand().pow(2)
        ).negate();
    }

    public Generic selfExpand() {
        if (parameters[0].signum() < 0) {
            return new Cot(parameters[0].negate()).selfExpand().negate();
        }
        return expressionValue();
    }

    public Generic selfElementary() {
        return new Fraction(
                new Cos(parameters[0]).selfElementary(),
                new Sin(parameters[0]).selfElementary()
        ).selfElementary();
    }

    public Generic selfSimplify() {
        if (parameters[0].signum() < 0) {
            return new Cot(parameters[0].negate()).selfExpand().negate();
        }
        try {
            Variable v = parameters[0].variableValue();
            if (v instanceof Acot) {
                Generic g[] = ((Acot) v).getParameters();
                return g[0];
            }
        } catch (NotVariableException e) {
        }
        return identity();
    }

    public Generic identity(Generic a, Generic b) {
        Generic ta = new Cot(a).selfSimplify();
        Generic tb = new Cot(b).selfSimplify();
        return new Fraction(
                ta.multiply(tb).subtract(JsclInteger.valueOf(1)),
                ta.add(tb)
        ).selfSimplify();
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).cot();
    }

    @Nonnull
    public Variable newInstance() {
        return new Cot(null);
    }
}
