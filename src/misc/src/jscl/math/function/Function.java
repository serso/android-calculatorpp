package jscl.math.function;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.mathml.MathML;
import jscl.util.ArrayComparator;

public abstract class Function extends Variable {
    protected Generic parameter[];

    public Function(String name, Generic parameter[]) {
        super(name);
        this.parameter=parameter;
    }

    public Generic[] parameters() {
        return parameter;
    }

    public abstract Generic evaluate();

    public abstract Generic evalelem();

    public abstract Generic evalsimp();

    public abstract Generic evalnum();

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        int n=-1;
        for(int i=0;i<parameter.length;i++) {
            if(n==-1 && parameter[i].isIdentity(variable)) n=i;
            else if(parameter[i].isConstant(variable));
            else {
                n=-1;
                break;
            }
        }
        if(n<0) throw new NotIntegrableException();
        else return antiderivative(n);
    }

    public abstract Generic antiderivative(int n) throws NotIntegrableException;

    public Generic derivative(Variable variable) {
        if(isIdentity(variable)) return JSCLInteger.valueOf(1);
        else {
            Generic a=JSCLInteger.valueOf(0);
            for(int i=0;i<parameter.length;i++) {
                a=a.add(parameter[i].derivative(variable).multiply(derivative(i)));
            }
            return a;
        }
    }

    public abstract Generic derivative(int n);

    public Generic substitute(Variable variable, Generic generic) {
        Function v=(Function)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].substitute(variable,generic);
        }
        if(v.isIdentity(variable)) return generic;
        else return v.evaluate();
    }

    public Generic expand() {
        Function v=(Function)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].expand();
        }
        return v.evaluate();
    }

    public Generic factorize() {
        Function v=(Function)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].factorize();
        }
        return v.expressionValue();
    }

    public Generic elementary() {
        Function v=(Function)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].elementary();
        }
        return v.evalelem();
    }

    public Generic simplify() {
        Function v=(Function)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].simplify();
        }
        return v.evalsimp();
    }

    public Generic numeric() {
        Function v=(Function)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].numeric();
        }
        return v.evalnum();
    }

    public boolean isConstant(Variable variable) {
        boolean s=!isIdentity(variable);
        for(int i=0;i<parameter.length;i++) {
            s=s && parameter[i].isConstant(variable);
        }
        return s;
    }

    public int compareTo(Variable variable) {
        if(this==variable) return 0;
        int c=comparator.compare(this,variable);
        if(c<0) return -1;
        else if(c>0) return 1;
        else {
            Function v=(Function)variable;
            c=name.compareTo(v.name);
            if(c<0) return -1;
            else if(c>0) return 1;
            else return ArrayComparator.comparator.compare(parameter,v.parameter);
        }
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(name);
        buffer.append("(");
        for(int i=0;i<parameter.length;i++) {
            buffer.append(parameter[i]).append(i<parameter.length-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(parameter[0].toJava());
        buffer.append(".").append(name).append("()");
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        MathML e1;
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) nameToMathML(element);
        else {
            e1=element.element("msup");
            nameToMathML(e1);
            MathML e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
        e1=element.element("mfenced");
        for(int i=0;i<parameter.length;i++) {
            parameter[i].toMathML(e1,null);
        }
        element.appendChild(e1);
    }
}
