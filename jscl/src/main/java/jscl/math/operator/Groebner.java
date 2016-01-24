package jscl.math.operator;

import jscl.math.*;
import jscl.math.function.Constant;
import jscl.math.function.ImplicitFunction;
import jscl.math.polynomial.Basis;
import jscl.math.polynomial.Monomial;
import jscl.math.polynomial.Ordering;
import jscl.math.polynomial.Polynomial;
import jscl.mathml.MathML;
import jscl.text.ParseException;

import javax.annotation.Nonnull;

public class Groebner extends Operator {

    public static final String NAME = "groebner";

    public Groebner(Generic generic, Generic variable, Generic ordering, Generic modulo) {
        super(NAME, new Generic[]{generic, variable, ordering, modulo});
    }

    private Groebner(Generic parameters[]) {
        super(NAME, createParameters(parameters));
    }

    private static Generic[] createParameters(Generic[] parameters) {
        final Generic[] result = new Generic[4];

        try {
            result[0] = parameters[0];
            result[1] = parameters[1];
            result[2] = parameters.length > 2 ? parameters[2] : Expression.valueOf("lex");
            result[3] = parameters.length > 3 ? parameters[3] : JsclInteger.valueOf(0);
        } catch (ParseException e) {
            throw new ArithmeticException(e.getMessage());
        }

        return result;
    }

    static Ordering ordering(Generic generic) {
        Variable v = generic.variableValue();
        if (v.compareTo(new Constant("lex")) == 0) return Monomial.lexicographic;
        else if (v.compareTo(new Constant("tdl")) == 0) return Monomial.totalDegreeLexicographic;
        else if (v.compareTo(new Constant("drl")) == 0) return Monomial.degreeReverseLexicographic;
        else if (v instanceof ImplicitFunction) {
            Generic g[] = ((ImplicitFunction) v).getParameters();
            int k = g[0].integerValue().intValue();
            if (v.compareTo(new ImplicitFunction("elim", new Generic[]{JsclInteger.valueOf(k)}, new int[]{0}, new Generic[]{})) == 0)
                return Monomial.kthElimination(k);
        }
        throw new ArithmeticException();
    }

    @Override
    public int getMinParameters() {
        return 2;
    }

    public Generic selfExpand() {
        Generic generic[] = ((JsclVector) parameters[0]).elements();
        Variable variable[] = toVariables((JsclVector) parameters[1]);
        Ordering ord = ordering(parameters[2]);
        int m = parameters[3].integerValue().intValue();
        return new PolynomialVector(Basis.compute(generic, variable, ord, m));
    }

    public Operator transmute() {
        Generic p[] = new Generic[]{GenericVariable.content(parameters[0]), GenericVariable.content(parameters[1])};
        if (p[0] instanceof JsclVector && p[1] instanceof JsclVector) {
            Generic generic[] = ((JsclVector) p[0]).elements();
            Variable variable[] = toVariables((JsclVector) p[1]);
            Ordering ord = ordering(parameters[2]);
            int m = parameters[3].integerValue().intValue();
            return new Groebner(new PolynomialVector(new Basis(generic, Polynomial.factory(variable, ord, m))), p[1], parameters[2], parameters[3]);
        }
        return this;
    }

    // todo serso: think
    /*public String toString() {
         StringBuilder buffer = new StringBuilder();
         int n = 4;
         if (parameters[3].signum() == 0) {
             n = 3;
             if (ordering(parameters[2]) == Monomial.lexicographic) n = 2;
         }
         buffer.append(name);
         buffer.append("(");
         for (int i = 0; i < n; i++) {
             buffer.append(parameters[i]).append(i < n - 1 ? ", " : "");
         }
         buffer.append(")");
         return buffer.toString();
     }*/

    public void toMathML(MathML element, Object data) {
        MathML e1;
        int exponent = data instanceof Integer ? (Integer) data : 1;
        int n = 4;
        if (parameters[3].signum() == 0) {
            n = 3;
            if (ordering(parameters[2]) == Monomial.lexicographic) n = 2;
        }
        if (exponent == 1) nameToMathML(element);
        else {
            e1 = element.element("msup");
            nameToMathML(e1);
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
        e1 = element.element("mfenced");
        for (int i = 0; i < n; i++) {
            parameters[i].toMathML(e1, null);
        }
        element.appendChild(e1);
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Groebner(parameters).transmute();
    }

    @Nonnull
    public Variable newInstance() {
        return new Groebner(null, null, null, null);
    }
}

class PolynomialVector extends JsclVector {
    final Basis basis;

    PolynomialVector(Basis basis) {
        this(basis, basis.elements());
    }

    PolynomialVector(Basis basis, Generic generic[]) {
        super(generic.length > 0 ? generic : new Generic[]{JsclInteger.valueOf(0)});
        this.basis = basis;
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();

        result.append("[");

        for (int i = 0; i < rows; i++) {
            result.append(basis.polynomial(elements[i])).append(i < rows - 1 ? ", " : "");
        }

        result.append("]");

        return result.toString();
    }

    protected void bodyToMathML(MathML e0) {
        MathML e1 = e0.element("mfenced");
        MathML e2 = e0.element("mtable");
        for (int i = 0; i < rows; i++) {
            MathML e3 = e0.element("mtr");
            MathML e4 = e0.element("mtd");
            basis.polynomial(elements[i]).toMathML(e4, null);
            e3.appendChild(e4);
            e2.appendChild(e3);
        }
        e1.appendChild(e2);
        e0.appendChild(e1);
    }

    @Nonnull
    protected Generic newInstance(@Nonnull Generic element[]) {
        return new PolynomialVector(basis, element);
    }
}
