package jscl.math;

import jscl.math.function.*;
import jscl.math.operator.Factorial;
import jscl.math.operator.Operator;
import jscl.mathml.MathML;
import jscl.text.ParseException;
import org.solovyev.common.math.MathEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Variable implements Comparable, MathEntity {

    @Nonnull
    public static final Comparator<Variable> comparator = VariableComparator.comparator;
    protected static final com.google.common.base.Function<Generic, Generic> FACTORIZE_CONVERTER = new com.google.common.base.Function<Generic, Generic>() {
        @Nonnull
        @Override
        public Generic apply(@Nonnull Generic generic) {
            return generic.factorize();
        }
    };
    protected static final com.google.common.base.Function<Generic, Generic> ELEMENTARY_CONVERTER = new com.google.common.base.Function<Generic, Generic>() {
        @Nonnull
        @Override
        public Generic apply(@Nonnull Generic generic) {
            return generic.elementary();
        }
    };
    protected static final com.google.common.base.Function<Generic, Generic> EXPAND_CONVERTER = new com.google.common.base.Function<Generic, Generic>() {
        @Nonnull
        @Override
        public Generic apply(@Nonnull Generic generic) {
            return generic.expand();
        }
    };
    protected static final com.google.common.base.Function<Generic, Generic> NUMERIC_CONVERTER = new com.google.common.base.Function<Generic, Generic>() {
        @Nonnull
        @Override
        public Generic apply(@Nonnull Generic generic) {
            return generic.numeric();
        }
    };
    static final Map<String, String> special = new HashMap<>();

    static {
        special.put("Alpha", "\u0391");
        special.put("Beta", "\u0392");
        special.put("Gamma", "\u0393");
        special.put("Delta", "\u0394");
        special.put("Epsilon", "\u0395");
        special.put("Zeta", "\u0396");
        special.put("Eta", "\u0397");
        special.put("Theta", "\u0398");
        special.put("Iota", "\u0399");
        special.put("Kappa", "\u039A");
        special.put("Lambda", "\u039B");
        special.put("Mu", "\u039C");
        special.put("Nu", "\u039D");
        special.put("Xi", "\u039E");
        special.put("Pi", "\u03A0");
        special.put("Rho", "\u03A1");
        special.put("Sigma", "\u03A3");
        special.put("Tau", "\u03A4");
        special.put("Upsilon", "\u03A5");
        special.put("Phi", "\u03A6");
        special.put("Chi", "\u03A7");
        special.put("Psi", "\u03A8");
        special.put("Omega", "\u03A9");
        special.put("alpha", "\u03B1");
        special.put("beta", "\u03B2");
        special.put("gamma", "\u03B3");
        special.put("delta", "\u03B4");
        special.put("epsilon", "\u03B5");
        special.put("zeta", "\u03B6");
        special.put("eta", "\u03B7");
        special.put("theta", "\u03B8");
        special.put("iota", "\u03B9");
        special.put("kappa", "\u03BA");
        special.put("lambda", "\u03BB");
        special.put("mu", "\u03BC");
        special.put("nu", "\u03BD");
        special.put("xi", "\u03BE");
        special.put("pi", "\u03C0");
        special.put("rho", "\u03C1");
        special.put("sigma", "\u03C3");
        special.put("tau", "\u03C4");
        special.put("upsilon", "\u03C5");
        special.put("phi", "\u03C6");
        special.put("chi", "\u03C7");
        special.put("psi", "\u03C8");
        special.put("omega", "\u03C9");
        special.put("infin", "\u221E");
        special.put("nabla", "\u2207");
        special.put("aleph", "\u2135");
        special.put("hbar", "\u210F");
        special.put("hamilt", "\u210B");
        special.put("lagran", "\u2112");
        special.put("square", "\u25A1");
    }

    @Nonnull
    protected String name;
    private Integer id;
    private boolean system = true;

    public Variable(@Nonnull String name) {
        this.name = name;
    }

    public static Variable valueOf(String str) throws ParseException, NotVariableException {
        return Expression.valueOf(str).variableValue();
    }

    public static Variable[] valueOf(String str[]) throws ParseException, NotVariableException {
        int n = str.length;
        Variable var[] = new Variable[n];
        for (int i = 0; i < n; i++) var[i] = valueOf(str[i]);
        return var;
    }

    @Nonnull
    @Override
    public Integer getId() {
        return id;
    }

    public void setId(@Nonnull Integer id) {
        this.id = id;
    }

    @Override
    public boolean isIdDefined() {
        return id != null;
    }

    @Nonnull
    public final String getName() {
        return name;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public void copy(@Nonnull MathEntity that) {
        if (that instanceof Variable) {
            this.name = ((Variable) that).name;
            this.id = ((Variable) that).id;
            this.system = ((Variable) that).system;
        }
    }

    public abstract Generic antiDerivative(Variable variable) throws NotIntegrableException;

    @Nonnull
    public abstract Generic derivative(Variable variable);

    public abstract Generic substitute(Variable variable, Generic generic);

    public abstract Generic expand();

    public abstract Generic factorize();

    public abstract Generic elementary();

    public abstract Generic simplify();

    public abstract Generic numeric();

    @Nonnull
    public Expression expressionValue() {
        return Expression.valueOf(this);
    }

    public abstract boolean isConstant(Variable variable);

    public boolean isIdentity(@Nonnull Variable variable) {
        return this.compareTo(variable) == 0;
    }

    public abstract int compareTo(Variable variable);

    public int compareTo(Object o) {
        return compareTo((Variable) o);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return obj instanceof Variable && compareTo((Variable) obj) == 0;
    }

    public String toString() {
        return name;
    }

    public String toJava() {
        return name;
    }

    public void toMathML(MathML element, @Nullable Object data) {
        int exponent = data instanceof Integer ? (Integer) data : 1;
        if (exponent == 1) nameToMathML(element);
        else {
            MathML e1 = element.element("msup");
            nameToMathML(e1);
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    protected void nameToMathML(MathML element) {
        MathML e1 = element.element("mi");
        e1.appendChild(element.text(special.containsKey(name) ? special.get(name) : name));
        element.appendChild(e1);
    }

    @Nonnull
    public abstract Variable newInstance();

    @Nonnull
    public abstract Set<? extends Constant> getConstants();
}

class VariableComparator implements Comparator<Variable> {

    public static final Comparator<Variable> comparator = new VariableComparator();

    private VariableComparator() {
    }

    static int value(Variable v) {
        int n;
        if (v instanceof TechnicalVariable) n = 0;
        else if (v instanceof IntegerVariable) n = 1;
        else if (v instanceof DoubleVariable) n = 2;
        else if (v instanceof Fraction && ((Fraction) v).integer()) n = 3;
        else if (v instanceof Sqrt && ((Sqrt) v).imaginary()) n = 4;
        else if (v instanceof Constant) n = 5;
        else if (v instanceof Root) n = 6;
        else if (v instanceof Algebraic) n = 7;
        else if (v instanceof ImplicitFunction) n = 8;
        else if (v instanceof Function) n = 9;
        else if (v instanceof Factorial) n = 10;
        else if (v instanceof Operator) n = 11;
        else if (v instanceof ExpressionVariable) n = 12;
        else if (v instanceof VectorVariable) n = 13;
        else if (v instanceof MatrixVariable) n = 14;
        else throw new ArithmeticException("Forget to add compare object of type: " + v.getClass());
        return n;
    }

    public int compare(Variable o1, Variable o2) {
        return value(o1) - value(o2);
    }
}
