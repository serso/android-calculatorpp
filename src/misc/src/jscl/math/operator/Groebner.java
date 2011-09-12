package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JSCLInteger;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.math.function.ImplicitFunction;
import jscl.math.polynomial.Basis;
import jscl.math.polynomial.Monomial;
import jscl.math.polynomial.Ordering;
import jscl.math.polynomial.Polynomial;
import jscl.mathml.MathML;

public class Groebner extends Operator {
    public Groebner(Generic generic, Generic variable, Generic ordering, Generic modulo) {
        super("groebner",new Generic[] {generic,variable,ordering,modulo});
    }

    public Generic compute() {
        Generic generic[]=((JSCLVector)parameter[0]).elements();
        Variable variable[]=variables(parameter[1]);
        Ordering ord=ordering(parameter[2]);
        int m=parameter[3].integerValue().intValue();
        return new PolynomialVector(Basis.compute(generic,variable,ord,m));
    }

    public Operator transmute() {
        Generic p[]=new Generic[] {GenericVariable.content(parameter[0]),GenericVariable.content(parameter[1])};
        if(p[0] instanceof JSCLVector && p[1] instanceof JSCLVector) {
            Generic generic[]=((JSCLVector)p[0]).elements();
            Variable variable[]=variables(p[1]);
            Ordering ord=ordering(parameter[2]);
            int m=parameter[3].integerValue().intValue();
            return new Groebner(new PolynomialVector(new Basis(generic,Polynomial.factory(variable,ord,m))),p[1],parameter[2],parameter[3]);
        }
        return this;
    }

    static Ordering ordering(Generic generic) {
        Variable v=generic.variableValue();
        if(v.compareTo(new Constant("lex"))==0) return Monomial.lexicographic;
        else if(v.compareTo(new Constant("tdl"))==0) return Monomial.totalDegreeLexicographic;
        else if(v.compareTo(new Constant("drl"))==0) return Monomial.degreeReverseLexicographic;
        else if(v instanceof ImplicitFunction) {
            Generic g[]=((ImplicitFunction)v).parameters();
            int k=g[0].integerValue().intValue();
            if(v.compareTo(new ImplicitFunction("elim",new Generic[] {JSCLInteger.valueOf(k)},new int[] {0},new Generic[] {}))==0) return Monomial.kthElimination(k);
        }
        throw new ArithmeticException();
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        int n=4;
        if(parameter[3].signum()==0) {
            n=3;
            if(ordering(parameter[2])==Monomial.lexicographic) n=2;
        }
        buffer.append(name);
        buffer.append("(");
        for(int i=0;i<n;i++) {
            buffer.append(parameter[i]).append(i<n-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        MathML e1;
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        int n=4;
        if(parameter[3].signum()==0) {
            n=3;
            if(ordering(parameter[2])==Monomial.lexicographic) n=2;
        }
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
        for(int i=0;i<n;i++) {
            parameter[i].toMathML(e1,null);
        }
        element.appendChild(e1);
    }

    protected Variable newinstance() {
        return new Groebner(null,null,null,null);
    }
}

class PolynomialVector extends JSCLVector {
    final Basis basis;

    PolynomialVector(Basis basis) {
        this(basis,basis.elements());
    }

    PolynomialVector(Basis basis, Generic generic[]) {
        super(generic.length>0?generic:new Generic[] {JSCLInteger.valueOf(0)});
        this.basis=basis;
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append("{");
        for(int i=0;i<n;i++) {
            buffer.append(basis.polynomial(element[i])).append(i<n-1?", ":"");
        }
        buffer.append("}");
        return buffer.toString();
    }

    protected void bodyToMathML(MathML e0) {
        MathML e1=e0.element("mfenced");
        MathML e2=e0.element("mtable");
        for(int i=0;i<n;i++) {
            MathML e3=e0.element("mtr");
            MathML e4=e0.element("mtd");
            basis.polynomial(element[i]).toMathML(e4,null);
            e3.appendChild(e4);
            e2.appendChild(e3);
        }
        e1.appendChild(e2);
        e0.appendChild(e1);
    }

    protected Generic newinstance(Generic element[]) {
        return new PolynomialVector(basis,element);
    }
}
