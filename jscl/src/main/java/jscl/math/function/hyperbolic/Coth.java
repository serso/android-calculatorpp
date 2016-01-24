package jscl.math.function.hyperbolic;

import jscl.math.*;
import jscl.math.function.Fraction;
import jscl.math.function.Ln;
import jscl.math.function.Trigonometric;

import javax.annotation.Nonnull;

public class Coth extends Trigonometric {
    public Coth(Generic generic) {
        super("coth", new Generic[]{generic});
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        return new Ln(
                JsclInteger.valueOf(4).multiply(
                        new Sinh(parameters[0]).selfExpand()
                )
        ).selfExpand();
    }

    public Generic derivative(int n) {
        return JsclInteger.valueOf(1).subtract(
                new Coth(parameters[0]).selfExpand().pow(2)
        );
    }

    public Generic selfExpand() {
        if (parameters[0].signum() < 0) {
            return new Coth(parameters[0].negate()).selfExpand().negate();
        }
        return expressionValue();
    }

    public Generic selfElementary() {
        return new Fraction(
                new Cosh(parameters[0]).selfElementary(),
                new Sinh(parameters[0]).selfElementary()
        ).selfElementary();
    }

    public Generic selfSimplify() {
        if (parameters[0].signum() < 0) {
            return new Coth(parameters[0].negate()).selfExpand().negate();
        }
        try {
            Variable v = parameters[0].variableValue();
            if (v instanceof Acoth) {
                Generic g[] = ((Acoth) v).getParameters();
                return g[0];
            }
        } catch (NotVariableException e) {
        }
        return identity();
    }

    public Generic identity(Generic a, Generic b) {
        Generic ta = new Coth(a).selfSimplify();
        Generic tb = new Coth(b).selfSimplify();
        return new Fraction(
                ta.multiply(tb).add(JsclInteger.valueOf(1)),
                ta.add(tb)
        ).selfSimplify();
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).coth();
    }

    @Nonnull
    public Variable newInstance() {
        return new Coth(null);
    }
}
