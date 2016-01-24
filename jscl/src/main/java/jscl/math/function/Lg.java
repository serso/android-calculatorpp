package jscl.math.function;

import jscl.math.*;

import javax.annotation.Nonnull;

import static jscl.math.JsclInteger.ONE;
import static jscl.math.JsclInteger.ZERO;

public class Lg extends Function {

    public Lg(Generic generic) {
        super("lg", new Generic[]{generic});
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        // tmp = ln(x) - 1
        final Generic tmp = new Ln(parameters[0]).expressionValue().subtract(ONE);

        // ln10 = ln (10)
        final Generic ln10 = new Ln(JsclInteger.valueOf(10L)).expressionValue();
        return new Fraction(parameters[0].multiply(tmp), ln10).expressionValue();
    }

    public Generic derivative(int n) {
        return new Inverse(parameters[0].multiply(new Ln(JsclInteger.valueOf(10L)).expressionValue())).expressionValue();
    }

    public Generic selfExpand() {
        if (parameters[0].compareTo(JsclInteger.valueOf(1)) == 0) {
            return JsclInteger.valueOf(0);
        }
        return expressionValue();
    }

    public Generic selfElementary() {
        return selfExpand();
    }

    public Generic selfSimplify() {

        Generic coefficents[] = Fraction.separateCoefficient(parameters[0]);
        final Generic a = coefficents[0];
        final Generic b = coefficents[1];
        final Generic c = coefficents[2];

        final boolean aOne = a.compareTo(ONE) == 0;
        final boolean bOne = b.compareTo(ONE) == 0;
        final boolean cOne = c.compareTo(ONE) == 0;

        if (aOne && bOne && cOne) {
            return ZERO;
        } else {
            if (aOne && bOne) {
                return expressionValue();
            } else if (bOne && cOne) {
                return expressionValue();
            } else {
                // lg ( a * c / b ) = lg ( c ) + lg( a ) - lg (b)
                final Generic lga = lg(a, aOne);
                final Generic lgb = lg(b, bOne);
                final Generic lgc = lg(c, cOne);
                return lgc.add(lga).subtract(lgb);
            }
        }
    }

    private Generic lg(Generic a, boolean aOne) {
        Generic lga;
        if (aOne) {
            lga = ZERO;
        } else {
            lga = new Lg(a).selfSimplify();
        }
        return lga;
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).lg();
    }

    @Nonnull
    public Variable newInstance() {
        return new Lg(null);
    }
}
