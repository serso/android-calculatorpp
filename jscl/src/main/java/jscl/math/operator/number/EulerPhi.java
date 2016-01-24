package jscl.math.operator.number;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

public class EulerPhi extends Operator {

    public static final String NAME = "eulerphi";

    public EulerPhi(Generic integer) {
        super(NAME, new Generic[]{integer});
    }

    private EulerPhi(Generic parameters[]) {
        super(NAME, parameters);
    }

    @Override
    public int getMinParameters() {
        return 1;
    }

    public Generic selfExpand() {
        try {
            JsclInteger en = parameters[0].integerValue();
            return en.phi();
        } catch (NotIntegerException e) {
        }
        return expressionValue();
    }

    protected void nameToMathML(MathML element) {
        MathML e1 = element.element("mi");
        e1.appendChild(element.text("\u03C6"));
        element.appendChild(e1);
    }

    @Nonnull
    public Variable newInstance() {
        return new EulerPhi((Generic) null);
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new EulerPhi(parameters);
    }
}
