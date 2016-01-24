package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JsclVector;
import jscl.math.Variable;

import javax.annotation.Nonnull;

public class Substitute extends Operator {

    public static final String NAME = "subst";

    public Substitute(Generic expression, Generic variable, Generic value) {
        super(NAME, new Generic[]{expression, variable, value});
    }

    private Substitute(Generic parameters[]) {
        super(NAME, parameters);
    }

    @Override
    public int getMinParameters() {
        return 3;
    }

    public Generic selfExpand() {
        if (parameters[1] instanceof JsclVector && parameters[2] instanceof JsclVector) {
            Generic a = parameters[0];
            Variable variable[] = toVariables((JsclVector) parameters[1]);
            Generic s[] = ((JsclVector) parameters[2]).elements();
            for (int i = 0; i < variable.length; i++) a = a.substitute(variable[i], s[i]);
            return a;
        } else {
            Variable variable = parameters[1].variableValue();
            return parameters[0].substitute(variable, parameters[2]);
        }
    }

    public Operator transmute() {
        Generic p[] = new Generic[]{null, GenericVariable.content(parameters[1]), GenericVariable.content(parameters[2])};
        if (p[1] instanceof JsclVector && p[2] instanceof JsclVector) {
            return new Substitute(parameters[0], p[1], p[2]);
        }
        return this;
    }

    public Generic expand() {
        return selfExpand();
    }

    @Nonnull
    @Override
    public Operator newInstance(@Nonnull Generic[] parameters) {
        return new Substitute(parameters).transmute();
    }

    @Nonnull
    public Variable newInstance() {
        return new Substitute(null, null, null);
    }
}
