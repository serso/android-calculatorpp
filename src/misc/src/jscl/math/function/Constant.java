package jscl.math.function;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.mathml.MathML;
import jscl.util.ArrayComparator;

public class Constant extends Variable {
    public static final Generic e=new Exp(JSCLInteger.valueOf(1)).expressionValue();
    public static final Generic pi=new Constant("pi").expressionValue();
    public static final Generic i=new Sqrt(JSCLInteger.valueOf(-1)).expressionValue();
    public static final Generic half=new Inv(JSCLInteger.valueOf(2)).expressionValue();
    public static final Generic third=new Inv(JSCLInteger.valueOf(3)).expressionValue();
    public static final Generic j=half.negate().multiply(JSCLInteger.valueOf(1).subtract(i.multiply(new Sqrt(JSCLInteger.valueOf(3)).expressionValue())));
    public static final Generic jbar=half.negate().multiply(JSCLInteger.valueOf(1).add(i.multiply(new Sqrt(JSCLInteger.valueOf(3)).expressionValue())));
    public static final Generic infinity=new Constant("infin").expressionValue();
    static final int PRIMECHARS=3;
    protected int prime;
    protected Generic subscript[];

    public Constant(String name) {
        this(name,0,new Generic[0]);
    }

    public Constant(String name, int prime, Generic subscript[]) {
        super(name);
        this.prime=prime;
        this.subscript=subscript;
    }

    public int prime() {
        return prime;
    }

    public Generic[] subscript() {
        return subscript;
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        return null;
    }

    public Generic derivative(Variable variable) {
        if(isIdentity(variable)) return JSCLInteger.valueOf(1);
        else return JSCLInteger.valueOf(0);
    }

    public Generic substitute(Variable variable, Generic generic) {
        Constant v=(Constant)newinstance();
        for(int i=0;i<subscript.length;i++) {
            v.subscript[i]=subscript[i].substitute(variable,generic);
        }
        if(v.isIdentity(variable)) return generic;
        else return v.expressionValue();
    }

    public Generic expand() {
        Constant v=(Constant)newinstance();
        for(int i=0;i<subscript.length;i++) {
            v.subscript[i]=subscript[i].expand();
        }
        return v.expressionValue();
    }

    public Generic factorize() {
        Constant v=(Constant)newinstance();
        for(int i=0;i<subscript.length;i++) {
            v.subscript[i]=subscript[i].factorize();
        }
        return v.expressionValue();
    }

    public Generic elementary() {
        Constant v=(Constant)newinstance();
        for(int i=0;i<subscript.length;i++) {
            v.subscript[i]=subscript[i].elementary();
        }
        return v.expressionValue();
    }

    public Generic simplify() {
        Constant v=(Constant)newinstance();
        for(int i=0;i<subscript.length;i++) {
            v.subscript[i]=subscript[i].simplify();
        }
        return v.expressionValue();
    }

    public Generic numeric() {
        return new NumericWrapper(this);
    }

    public boolean isConstant(Variable variable) {
        return !isIdentity(variable);
    }

    public int compareTo(Variable variable) {
        if(this==variable) return 0;
        int c=comparator.compare(this,variable);
        if(c<0) return -1;
        else if(c>0) return 1;
        else {
            Constant v=(Constant)variable;
            c=name.compareTo(v.name);
            if(c<0) return -1;
            else if(c>0) return 1;
            else {
                c=ArrayComparator.comparator.compare(subscript,v.subscript);
                if(c<0) return -1;
                else if(c>0) return 1;
                else {
                    if(prime<v.prime) return -1;
                    else if(prime>v.prime) return 1;
                    else return 0;
                }
            }
        }
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(name);
        for(int i=0;i<subscript.length;i++) {
            buffer.append("[").append(subscript[i]).append("]");
        }
        if(prime==0);
        else if(prime<=PRIMECHARS) buffer.append(primechars(prime));
        else buffer.append("{").append(prime).append("}");
        return buffer.toString();
    }

    static String primechars(int n) {
        StringBuffer buffer=new StringBuffer();
        for(int i=0;i<n;i++) buffer.append("'");
        return buffer.toString();
    }

    public String toJava() {
        if(compareTo(new Constant("pi"))==0) return "JSCLDouble.valueOf(Math.PI)";
        else if(compareTo(new Constant("infin"))==0) return "JSCLDouble.valueOf(Double.POSITIVE_INFINITY)";
        StringBuffer buffer=new StringBuffer();
        buffer.append(name);
        if(prime==0);
        else if(prime<=PRIMECHARS) buffer.append(underscores(prime));
        else buffer.append("_").append(prime);
        for(int i=0;i<subscript.length;i++) {
            buffer.append("[").append(subscript[i].integerValue().intValue()).append("]");
        }
        return buffer.toString();
    }

    static String underscores(int n) {
        StringBuffer buffer=new StringBuffer();
        for(int i=0;i<n;i++) buffer.append("_");
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) bodyToMathML(element);
        else {
            MathML e1=element.element("msup");
            bodyToMathML(e1);
            MathML e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    public void bodyToMathML(MathML element) {
        if(subscript.length==0) {
            if(prime==0) {
                nameToMathML(element);
            } else {
                MathML e1=element.element("msup");
                nameToMathML(e1);
                primeToMathML(e1);
                element.appendChild(e1);
            }
        } else {
            if(prime==0) {
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
                primeToMathML(e1);
                element.appendChild(e1);
            }
        }
    }

    void primeToMathML(MathML element) {
        if(prime<=PRIMECHARS) {
            primecharsToMathML(element,prime);
        } else {
            MathML e1=element.element("mfenced");
            MathML e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(prime)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    static void primecharsToMathML(MathML element, int n) {
        MathML e1=element.element("mo");
        for(int i=0;i<n;i++) e1.appendChild(element.text("\u2032"));
        element.appendChild(e1);
    }

    protected Variable newinstance() {
        return new Constant(name,prime,new Generic[subscript.length]);
    }
}
