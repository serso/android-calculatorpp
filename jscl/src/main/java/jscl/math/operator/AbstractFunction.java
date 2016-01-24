package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.mathml.MathML;
import jscl.util.ArrayComparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * User: serso
 * Date: 11/29/11
 * Time: 9:50 PM
 */
public abstract class AbstractFunction extends Variable {

    protected static final Generic UNDEFINED_PARAMETER = JsclInteger.valueOf(Long.MIN_VALUE + 1);
    private static final String DEFAULT_PARAMETER_NAMES = "xyzabcdefghijklmnopqrstuvw";
    protected Generic parameters[];

    protected AbstractFunction(@Nonnull String name, Generic[] parameters) {
        super(name);

        checkParameters(parameters);

        this.parameters = parameters;
    }

    @Nullable
    protected static Generic getParameter(@Nullable Generic[] parameters, final int i) {
        return parameters == null ? null : (parameters.length > i ? parameters[i] : null);
    }

    private void checkParameters(@Nullable Generic[] parameters) {
        assert parameters == null || (getMinParameters() <= parameters.length && parameters.length <= getMaxParameters());
    }

    public Generic[] getParameters() {
        return parameters;
    }

    public void setParameters(@Nullable Generic[] parameters) {
        checkParameters(parameters);

        this.parameters = parameters;
    }

    public abstract int getMinParameters();

    public int getMaxParameters() {
        return getMinParameters();
    }

    public abstract Generic selfExpand();

    public Generic expand() {
        final AbstractFunction function = newExpandedFunction();

        return function.selfExpand();
    }

    @Nonnull
    protected AbstractFunction newExpandedFunction() {
        final AbstractFunction function = (AbstractFunction) newInstance();

        for (int i = 0; i < parameters.length; i++) {
            function.parameters[i] = parameters[i].expand();
        }
        return function;
    }

    public Generic elementary() {
        final AbstractFunction function = newElementarizedFunction();

        return function.selfElementary();
    }

    @Nonnull
    protected AbstractFunction newElementarizedFunction() {
        final AbstractFunction function = (AbstractFunction) newInstance();

        for (int i = 0; i < parameters.length; i++) {
            function.parameters[i] = parameters[i].elementary();
        }
        return function;
    }

    public abstract Generic selfElementary();

    public Generic factorize() {
        final AbstractFunction function = newFactorizedFunction();

        return function.expressionValue();
    }

    @Nonnull
    protected AbstractFunction newFactorizedFunction() {
        final AbstractFunction function = (AbstractFunction) newInstance();

        for (int i = 0; i < parameters.length; i++) {
            function.parameters[i] = parameters[i].factorize();
        }
        return function;
    }

    public Generic simplify() {
        final AbstractFunction function = newSimplifiedFunction();

        return function.selfSimplify();
    }

    @Nonnull
    protected final AbstractFunction newSimplifiedFunction() {
        final AbstractFunction function = (AbstractFunction) newInstance();

        for (int i = 0; i < parameters.length; i++) {
            function.parameters[i] = parameters[i].simplify();
        }
        return function;
    }

    public abstract Generic selfSimplify();

    public Generic numeric() {
        final AbstractFunction result = newNumericFunction();

        return result.selfNumeric();
    }

    @Nonnull
    protected final AbstractFunction newNumericFunction() {
        final AbstractFunction result = (AbstractFunction) newInstance();

        for (int i = 0; i < parameters.length; i++) {
            result.parameters[i] = parameters[i].numeric();
        }

        return result;
    }

    public abstract Generic selfNumeric();

    public String toString() {
        final StringBuilder result = new StringBuilder();

        // f(x, y, z)
        result.append(name);
        result.append("(");
        for (int i = 0; i < parameters.length; i++) {
            result.append(formatParameter(i));
            if (i < parameters.length - 1) {
                result.append(", ");
            }
        }
        result.append(")");

        return result.toString();
    }

    @Nonnull
    protected final String formatParameter(int i) {
        Generic parameter = parameters[i];

        String result;
        if (parameter != null) {
            result = parameter.toString();
        } else {
            result = formatUndefinedParameter(i);
        }

        return result;
    }

    @Nonnull
    protected String formatUndefinedParameter(int i) {
        return String.valueOf(DEFAULT_PARAMETER_NAMES.charAt(i - (i / DEFAULT_PARAMETER_NAMES.length()) * DEFAULT_PARAMETER_NAMES.length()));
    }

    public String toJava() {
        StringBuilder result = new StringBuilder();

        result.append(parameters[0].toJava());
        result.append(".").append(name).append("()");

        return result.toString();
    }

    public int compareTo(Variable that) {
        if (this == that) return 0;

        int c = comparator.compare(this, that);

        if (c < 0) {
            return -1;
        } else if (c > 0) {
            return 1;
        } else {
            final AbstractFunction thatFunction = (AbstractFunction) that;
            c = name.compareTo(thatFunction.name);
            if (c < 0) {
                return -1;
            } else if (c > 0) {
                return 1;
            } else {
                return ArrayComparator.comparator.compare(parameters, thatFunction.parameters);
            }
        }
    }

    public Generic substitute(@Nonnull Variable variable, @Nonnull Generic generic) {
        final AbstractFunction function = (AbstractFunction) newInstance();

        for (int i = 0; i < parameters.length; i++) {
            function.parameters[i] = parameters[i].substitute(variable, generic);
        }

        if (function.isIdentity(variable)) {
            return generic;
        } else {
            return function.selfExpand();
        }
    }

    public void toMathML(MathML element, Object data) {
        int exponent = data instanceof Integer ? (Integer) data : 1;

        MathML result;
        if (exponent == 1) {
            nameToMathML(element);
        } else {
            result = element.element("msup");
            nameToMathML(result);
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            result.appendChild(e2);
            element.appendChild(result);
        }

        result = element.element("mfenced");
        for (Generic parameter : parameters) {
            parameter.toMathML(result, null);
        }

        element.appendChild(result);
    }

    @Nonnull
    @Override
    public Set<? extends Constant> getConstants() {
        final Set<Constant> result = new HashSet<Constant>();

        for (Generic parameter : parameters) {
            result.addAll(parameter.getConstants());
        }

        return result;
    }
}
