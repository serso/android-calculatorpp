package jscl.math;

import jscl.math.function.Constant;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public abstract class GenericVariable extends Variable {
    Generic content;

    GenericVariable(Generic generic) {
        super("");
        content = generic;
    }

    public static Generic content(Generic generic) {
        return content(generic, false);
    }

    public static Generic content(Generic generic, boolean expression) {
        try {
            Variable v = generic.variableValue();
            if (expression) {
                if (v instanceof ExpressionVariable) generic = ((ExpressionVariable) v).content;
            } else {
                if (v instanceof GenericVariable) generic = ((GenericVariable) v).content;
            }
        } catch (NotVariableException e) {
        }
        return generic;
    }

    public static GenericVariable valueOf(Generic generic) {
        return valueOf(generic, false);
    }

    public static GenericVariable valueOf(Generic generic, boolean integer) {
        if (integer) return new IntegerVariable(generic);
        else return new ExpressionVariable(generic);
    }

    public Generic antiDerivative(Variable variable) throws NotIntegrableException {
        return content.antiDerivative(variable);
    }

    @Nonnull
    public Generic derivative(Variable variable) {
        return content.derivative(variable);
    }

    public Generic substitute(Variable variable, Generic generic) {
        GenericVariable v = (GenericVariable) newInstance();
        v.content = content.substitute(variable, generic);
        if (v.isIdentity(variable)) return generic;
        else return v.expressionValue();
    }

    public Generic expand() {
        return content.expand();
    }

    public Generic factorize() {
        GenericVariable v = (GenericVariable) newInstance();
        v.content = content.factorize();
        return v.expressionValue();
    }

    public Generic elementary() {
        GenericVariable v = (GenericVariable) newInstance();
        v.content = content.elementary();
        return v.expressionValue();
    }

    public Generic simplify() {
        GenericVariable v = (GenericVariable) newInstance();
        v.content = content.simplify();
        return v.expressionValue();
    }

    public Generic numeric() {
        return content.numeric();
    }

    public boolean isConstant(Variable variable) {
        return content.isConstant(variable);
    }

    public int compareTo(Variable variable) {
        if (this == variable) return 0;
        int c = comparator.compare(this, variable);
        if (c < 0) return -1;
        else if (c > 0) return 1;
        else {
            GenericVariable v = (GenericVariable) variable;
            return content.compareTo(v.content);
        }
    }

    public String toString() {
        return content.toString();
    }

    public String toJava() {
        return content.toJava();
    }

    public void toMathML(MathML element, @Nullable Object data) {
        content.toMathML(element, data);
    }

    @Nonnull
    @Override
    public Set<? extends Constant> getConstants() {
        return content.getConstants();
    }
}
