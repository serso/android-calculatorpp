package jscl.math.function;

import jscl.math.*;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;

import static jscl.math.function.Constants.Generic.I;

public class Conjugate extends Function {
    public Conjugate(Generic generic) {
        super("conjugate", new Generic[]{generic});
    }

    public Generic antiDerivative(int n) throws NotIntegrableException {
        return Constants.Generic.HALF.multiply(selfExpand().pow(2));
    }

    public Generic derivative(int n) {
        return JsclInteger.valueOf(1);
    }

    public Generic selfExpand() {
        try {
            return parameters[0].integerValue();
        } catch (NotIntegerException e) {
        }
        if (parameters[0] instanceof Matrix) {
            return ((Matrix) parameters[0]).conjugate();
        } else if (parameters[0] instanceof JsclVector) {
            return ((JsclVector) parameters[0]).conjugate();
        }
        return expressionValue();
    }

    public Generic selfElementary() {
        try {
            return parameters[0].integerValue();
        } catch (NotIntegerException e) {
        }
        return expressionValue();
    }

    public Generic selfSimplify() {
        try {
            return parameters[0].integerValue();
        } catch (NotIntegerException e) {
        }

        if (parameters[0].signum() < 0) {
            return new Conjugate(parameters[0].negate()).selfSimplify().negate();
        } else if (parameters[0].compareTo(I) == 0) {
            return I.negate();
        }

        try {
            Variable v = parameters[0].variableValue();
            if (v instanceof Conjugate) {
                Generic g[] = ((Conjugate) v).getParameters();
                return g[0];
            } else if (v instanceof Exp) {
                Generic g[] = ((Exp) v).getParameters();
                return new Exp(new Conjugate(g[0]).selfSimplify()).selfSimplify();
            } else if (v instanceof Ln) {
                Generic g[] = ((Ln) v).getParameters();
                return new Ln(new Conjugate(g[0]).selfSimplify()).selfSimplify();
            } else if (v instanceof Lg) {
                Generic g[] = ((Lg) v).getParameters();
                return new Lg(new Conjugate(g[0]).selfSimplify()).selfSimplify();
            }
        } catch (NotVariableException e) {
            Generic a[] = parameters[0].sumValue();
            if (a.length > 1) {
                Generic s = JsclInteger.valueOf(0);
                for (int i = 0; i < a.length; i++) {
                    s = s.add(new Conjugate(a[i]).selfSimplify());
                }
                return s;
            } else {
                Generic p[] = a[0].productValue();
                Generic s = JsclInteger.valueOf(1);
                for (int i = 0; i < p.length; i++) {
                    Power o = p[i].powerValue();
                    s = s.multiply(new Conjugate(o.value()).selfSimplify().pow(o.exponent()));
                }
                return s;
            }
        }
        Generic n[] = Fraction.separateCoefficient(parameters[0]);
        if (n[0].compareTo(JsclInteger.valueOf(1)) == 0 && n[1].compareTo(JsclInteger.valueOf(1)) == 0) ;
        else return new Conjugate(n[2]).selfSimplify().multiply(
                new Fraction(n[0], n[1]).selfSimplify()
        );
        return expressionValue();
    }

    public Generic selfNumeric() {
        return ((NumericWrapper) parameters[0]).conjugate();
    }

    public String toJava() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(parameters[0].toJava());
        buffer.append(".conjugate()");
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        int exponent = data instanceof Integer ? ((Integer) data).intValue() : 1;
        if (exponent == 1) bodyToMathML(element);
        else {
            MathML e1 = element.element("msup");
            MathML e2 = element.element("mfenced");
            bodyToMathML(e2);
            e1.appendChild(e2);
            e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    void bodyToMathML(MathML element) {
        MathML e1 = element.element("mover");
        parameters[0].toMathML(e1, null);
        MathML e2 = element.element("mo");
        e2.appendChild(element.text("_"));
        e1.appendChild(e2);
        element.appendChild(e1);
    }

    @Nonnull
    public Variable newInstance() {
        return new Conjugate(null);
    }
}
