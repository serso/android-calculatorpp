package jscl.math.function;

import jscl.math.Generic;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.mathml.MathML;
import jscl.util.ArrayComparator;

public class ImplicitFunction extends Function {
    protected int derivation[];
    protected Generic subscript[];

    public ImplicitFunction(String name, Generic parameter[], int derivation[], Generic subscript[]) {
        super(name,parameter);
        this.derivation=derivation;
        this.subscript=subscript;
    }

    public int[] derivation() {
        return derivation;
    }

    public Generic[] subscript() {
        return subscript;
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        int c[]=new int[derivation.length];
        for(int i=0;i<c.length;i++) {
            if(i==n) {
                if(derivation[i]>0) c[i]=derivation[i]-1;
                else throw new NotIntegrableException();
            } else c[i]=derivation[i];
        }
        return new ImplicitFunction(name,parameter,c,subscript).evaluate();
    }

    public Generic derivative(int n) {
        int c[]=new int[derivation.length];
        for(int i=0;i<c.length;i++) {
            if(i==n) c[i]=derivation[i]+1;
            else c[i]=derivation[i];
        }
        return new ImplicitFunction(name,parameter,c,subscript).evaluate();
    }

    public Generic evaluate() {
        return expressionValue();
    }

    public Generic evalelem() {
        return expressionValue();
    }

    public Generic evalsimp() {
        return expressionValue();
    }

    public Generic evalnum() {
        throw new ArithmeticException();
    }

    public int compareTo(Variable variable) {
        if(this==variable) return 0;
        int c=comparator.compare(this,variable);
        if(c<0) return -1;
        else if(c>0) return 1;
        else {
            ImplicitFunction v=(ImplicitFunction)variable;
            c=name.compareTo(v.name);
            if(c<0) return -1;
            else if(c>0) return 1;
            else {
                c=ArrayComparator.comparator.compare(subscript,v.subscript);
                if(c<0) return -1;
                else if(c>0) return 1;
                else {
                    c=compareDerivation(derivation,v.derivation);
                    if(c<0) return -1;
                    else if(c>0) return 1;
                    else return ArrayComparator.comparator.compare(parameter,v.parameter);
                }
            }
        }
    }

    static int compareDerivation(int c1[], int c2[]) {
        int n=c1.length;
        for(int i=n-1;i>=0;i--) {
            if(c1[i]<c2[i]) return -1;
            else if(c1[i]>c2[i]) return 1;
        }
        return 0;
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        int n=0;
        for(int i=0;i<derivation.length;i++) n+=derivation[i];
        buffer.append(name);
        for(int i=0;i<subscript.length;i++) {
            buffer.append("[").append(subscript[i]).append("]");
        }
        if(n==0);
        else if(parameter.length==1?n<=Constant.PRIMECHARS:false) buffer.append(Constant.primechars(n));
        else buffer.append(derivationToString());
        buffer.append("(");
        for(int i=0;i<parameter.length;i++) {
            buffer.append(parameter[i]).append(i<parameter.length-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    String derivationToString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append("{");
        for(int i=0;i<derivation.length;i++) {
            buffer.append(derivation[i]).append(i<derivation.length-1?", ":"");
        }
        buffer.append("}");
        return buffer.toString();
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        int n=0;
        for(int i=0;i<derivation.length;i++) n+=derivation[i];
        buffer.append(name);
        if(n==0);
        else if(parameter.length==1?n<=Constant.PRIMECHARS:false) buffer.append(Constant.underscores(n));
        else buffer.append(derivationToJava());
        buffer.append("(");
        for(int i=0;i<parameter.length;i++) {
            buffer.append(parameter[i].toJava()).append(i<parameter.length-1?", ":"");
        }
        buffer.append(")");
        for(int i=0;i<subscript.length;i++) {
            buffer.append("[").append(subscript[i].integerValue().intValue()).append("]");
        }
        return buffer.toString();
    }

    String derivationToJava() {
        StringBuffer buffer=new StringBuffer();
        for(int i=0;i<derivation.length;i++) {
            buffer.append("_").append(derivation[i]);
        }
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        MathML e1;
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) bodyToMathML(element);
        else {
            e1=element.element("msup");
            bodyToMathML(e1);
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

    void bodyToMathML(MathML element) {
        int n=0;
        for(int i=0;i<derivation.length;i++) n+=derivation[i];
        if(subscript.length==0) {
            if(n==0) {
                nameToMathML(element);
            } else {
                MathML e1=element.element("msup");
                nameToMathML(e1);
                derivationToMathML(e1,n);
                element.appendChild(e1);
            }
        } else {
            if(n==0) {
                MathML e1=element.element("msub");
                nameToMathML(e1);
                MathML e2=element.element("mrow");
                for(int i=0;i<subscript.length;i++) {
                    subscript[i].toMathML(e2,null);
                }
                e1.appendChild(e2);
                element.appendChild(e1);
            } else {
                MathML e1=element.element("msubsup");
                nameToMathML(e1);
                MathML e2=element.element("mrow");
                for(int i=0;i<subscript.length;i++) {
                    subscript[i].toMathML(e2,null);
                }
                e1.appendChild(e2);
                derivationToMathML(e1,n);
                element.appendChild(e1);
            }
        }
    }

    void derivationToMathML(MathML element, int n) {
        if(parameter.length==1?n<=Constant.PRIMECHARS:false) Constant.primecharsToMathML(element,n);
        else {
            MathML e1=element.element("mfenced");
            for(int i=0;i<derivation.length;i++) {
                MathML e2=element.element("mn");
                e2.appendChild(element.text(String.valueOf(derivation[i])));
                e1.appendChild(e2);
            }
            element.appendChild(e1);
        }
    }

    protected Variable newinstance() {
        return new ImplicitFunction(name,new Generic[parameter.length],derivation,subscript);
    }
}
