package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JSCLVector;
import jscl.math.Variable;

public class Substitute extends Operator {
    public Substitute(Generic expression, Generic variable, Generic value) {
        super("subst",new Generic[] {expression,variable,value});
    }

    public Generic compute() {
        if(parameter[1] instanceof JSCLVector && parameter[2] instanceof JSCLVector) {
            Generic a=parameter[0];
            Variable variable[]=variables(parameter[1]);
            Generic s[]=((JSCLVector)parameter[2]).elements();
            for(int i=0;i<variable.length;i++) a=a.substitute(variable[i],s[i]);
            return a;
        } else {
            Variable variable=parameter[1].variableValue();
            return parameter[0].substitute(variable,parameter[2]);
        }
    }

    public Operator transmute() {
        Generic p[]=new Generic[] {null,GenericVariable.content(parameter[1]),GenericVariable.content(parameter[2])};
        if(p[1] instanceof JSCLVector && p[2] instanceof JSCLVector) {
            return new Substitute(parameter[0],p[1],p[2]);
        }
        return this;
    }

    public Generic expand() {
        return compute();
    }

    protected Variable newinstance() {
        return new Substitute(null,null,null);
    }
}
