package jscl.math.function;

import jscl.AngleUnit;
import jscl.math.Generic;
import jscl.math.Variable;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 11/12/11
 * Time: 4:16 PM
 */
public class Deg extends Algebraic {

    public Deg(Generic generic) {
        super("deg", new Generic[]{generic});
    }

    @Override
    public Root rootValue() throws NotRootException {
        throw new UnsupportedOperationException("Root for deg() is not supported!");
    }

    @Override
    void bodyToMathML(MathML element, boolean fenced) {
        final MathML child = element.element("deg");
        parameters[0].toMathML(child, null);
        element.appendChild(child);
    }

    @Override
    public Generic selfExpand() {
        return expressionValue();
    }

    @Override
    public Generic selfElementary() {
        return selfExpand();
    }

    @Override
    public Generic selfSimplify() {
        return selfExpand();
    }

    @Override
    public Generic selfNumeric() {
        return AngleUnit.rad.transform(AngleUnit.deg, parameters[0]);
    }

    @Override
    public Generic derivative(int n) {
        throw new UnsupportedOperationException("Derivative for deg() is not supported!");
    }

    @Nonnull
    @Override
    public Variable newInstance() {
        return new Deg(null);
    }
}
