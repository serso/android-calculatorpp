package jscl.math.function;

import jscl.math.Antiderivative;
import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.NotIntegrableException;
import jscl.math.NumericWrapper;
import jscl.math.TechnicalVariable;
import jscl.math.Variable;
import jscl.math.polynomial.Polynomial;
import jscl.math.polynomial.UnivariatePolynomial;
import jscl.mathml.MathML;
import jscl.util.ArrayComparator;

public class Root extends Algebraic {
    protected Generic subscript;

    public Root(Generic parameter[], Generic subscript) {
        super("root",parameter);
        this.subscript=subscript;
    }

    public Root(Generic parameter[], int s) {
        this(parameter, JSCLInteger.valueOf(s));
    }

    public Root(UnivariatePolynomial polynomial, int s) {
        this(polynomial.normalize().elements(),s);
    }

    public Generic subscript() {
        return subscript;
    }

    public Root rootValue() {
        return this;
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        boolean b=true;
        for(int i=0;i<parameter.length;i++) b=b && parameter[i].isPolynomial(variable);
        if(b) {
            return Antiderivative.compute(this,variable);
        } else throw new NotIntegrableException();
    }

    public Generic derivative(Variable variable) {
        if(compareTo(variable)==0) return JSCLInteger.valueOf(1);
        else {
            Variable t=new TechnicalVariable("t");
            Generic a[]=new Generic[parameter.length];
            for(int i=0;i<parameter.length;i++) a[i]=parameter[i].derivative(variable);
            UnivariatePolynomial fact=(UnivariatePolynomial)Polynomial.factory(this);
            UnivariatePolynomial p=fact.valueof(parameter);
            UnivariatePolynomial q=(UnivariatePolynomial)p.derivative().multiply(t.expressionValue()).add(fact.valueof(a));
            UnivariatePolynomial r=(UnivariatePolynomial)Polynomial.factory(t).valueof(p.resultant(q));
            return new Root(r.elements(),subscript).evaluate();
        }
    }

    public Generic derivative(int n) {
        return null;
    }

    public Generic substitute(Variable variable, Generic generic) {
        Root v=(Root)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].substitute(variable,generic);
        }
        v.subscript=subscript.substitute(variable,generic);
        if(v.isIdentity(variable)) return generic;
        else return v.evaluate();
    }

    public Generic expand() {
        Root v=(Root)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].expand();
        }
        v.subscript=subscript.expand();
        return v.evaluate();
    }

    public Generic factorize() {
        Root v=(Root)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].factorize();
        }
        v.subscript=subscript;
        return v.expressionValue();
    }

    public Generic elementary() {
        Root v=(Root)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].elementary();
        }
        v.subscript=subscript.elementary();
        return v.evalelem();
    }

    public Generic simplify() {
        Root v=(Root)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].simplify();
        }
        v.subscript=subscript.simplify();
        return v.evalsimp();
    }

    public Generic numeric() {
        Root v=(Root)newinstance();
        for(int i=0;i<parameter.length;i++) {
            v.parameter[i]=parameter[i].numeric();
        }
        v.subscript=subscript;
        return v.evalnum();
    }

    public Generic evaluate() {
        if(isZero()) return JSCLInteger.valueOf(0);
        try {
            int s=subscript.integerValue().intValue();
            switch(degree()) {
            case 1:
                return new Frac(parameter[0],parameter[1]).evaluate().negate();
            }
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    public Generic evalelem() {
        return evaluate();
    }

    public Generic evalsimp() {
        if(isZero()) return JSCLInteger.valueOf(0);
        try {
            int s=subscript.integerValue().intValue();
            switch(degree()) {
            case 1:
                return linear(parameter);
            case 2:
                return quadratic(parameter,s);
            case 3:
                return cubic(parameter,s);
            case 4:
                return quartic(parameter,s);
            default:
                if(isNth() && s==0) return nth(parameter);
            }
        } catch (NotIntegerException e) {}
        return expressionValue();
    }

    boolean isZero() {
        boolean b=degree()>0;
        for(int i=0;i<degree();i++) b=b && parameter[i].signum()==0;
        b=b && parameter[degree()].signum()!=0;
        return b;
    }

    boolean isNth() {
        boolean b=degree()>0;
        for(int i=1;i<degree();i++) b=b && parameter[i].signum()==0;
        b=b && parameter[degree()].signum()!=0;
        return b;
    }

    static Generic nth(Generic parameter[]) {
        int degree=parameter.length-1;
        Generic a=new Frac(parameter[0],parameter[degree]).evalsimp();
        return new Pow(
            a.negate(),
            new Inv(JSCLInteger.valueOf(degree)).evalsimp()
        ).evalsimp();
    }

    static Generic linear(Generic parameter[]) {
        Generic a=new Frac(parameter[0],parameter[1]).evalsimp();
        return a.negate();
    }

    static Generic quadratic(Generic parameter[], int subscript) {
        Generic a=new Frac(parameter[1],parameter[2]).evalsimp();
        Generic b=new Frac(parameter[0],parameter[2]).evalsimp();
        Generic y=new Sqrt(
            a.pow(2).subtract(JSCLInteger.valueOf(4).multiply(b))
        ).evalsimp();
        switch(subscript) {
        case 0:
            return new Frac(
                a.subtract(y),
                JSCLInteger.valueOf(2)
            ).evalsimp().negate();
        default:
            return new Frac(
                a.add(y),
                JSCLInteger.valueOf(2)
            ).evalsimp().negate();
        }
    }

    static Generic cubic(Generic parameter[], int subscript) {
        Generic a=new Frac(parameter[2],parameter[3]).evalsimp();
        Generic b=new Frac(parameter[1],parameter[3]).evalsimp();
        Generic c=new Frac(parameter[0],parameter[3]).evalsimp();
        Generic y[]=new Generic[2];
        for(int i=0;i<y.length;i++) {
            y[i]=new Cubic(
                new Root(
                    new Generic[] {
                        a.pow(6).subtract(JSCLInteger.valueOf(9).multiply(a.pow(4)).multiply(b)).add(JSCLInteger.valueOf(27).multiply(a.pow(2)).multiply(b.pow(2))).subtract(JSCLInteger.valueOf(27).multiply(b.pow(3))),
                        JSCLInteger.valueOf(2).multiply(a.pow(3)).subtract(JSCLInteger.valueOf(9).multiply(a).multiply(b)).add(JSCLInteger.valueOf(27).multiply(c)),
                        JSCLInteger.valueOf(1)
                    },
                    i
                ).evalsimp()
            ).evalsimp();
        }
        switch(subscript) {
        case 0:
            return new Frac(
                a.subtract(y[0]).subtract(y[1]),
                JSCLInteger.valueOf(3)
            ).evalsimp().negate();
        case 1:
            return new Frac(
                a.subtract(Constant.j.multiply(y[0])).subtract(Constant.jbar.multiply(y[1])),
                JSCLInteger.valueOf(3)
            ).evalsimp().negate();
        default:
            return new Frac(
                a.subtract(Constant.jbar.multiply(y[0])).subtract(Constant.j.multiply(y[1])),
                JSCLInteger.valueOf(3)
            ).evalsimp().negate();
        }
    }

    static Generic quartic(Generic parameter[], int subscript) {
        Generic a=new Frac(parameter[3],parameter[4]).evalsimp();
        Generic b=new Frac(parameter[2],parameter[4]).evalsimp();
        Generic c=new Frac(parameter[1],parameter[4]).evalsimp();
        Generic d=new Frac(parameter[0],parameter[4]).evalsimp();
        Generic y[]=new Generic[3];
        for(int i=0;i<y.length;i++) {
            y[i]=new Sqrt(
                new Root(
                    new Generic[] {
                        a.pow(6).subtract(JSCLInteger.valueOf(8).multiply(a.pow(4)).multiply(b)).add(JSCLInteger.valueOf(16).multiply(a.pow(2)).multiply(b.pow(2))).add(JSCLInteger.valueOf(16).multiply(a.pow(3)).multiply(c)).subtract(JSCLInteger.valueOf(64).multiply(a).multiply(b).multiply(c)).add(JSCLInteger.valueOf(64).multiply(c.pow(2))),
                        JSCLInteger.valueOf(-3).multiply(a.pow(4)).add(JSCLInteger.valueOf(16).multiply(a.pow(2)).multiply(b)).subtract(JSCLInteger.valueOf(16).multiply(b.pow(2))).subtract(JSCLInteger.valueOf(16).multiply(a).multiply(c)).add(JSCLInteger.valueOf(64).multiply(d)),
                        JSCLInteger.valueOf(3).multiply(a.pow(2)).subtract(JSCLInteger.valueOf(8).multiply(b)),
                        JSCLInteger.valueOf(-1)
                    },
                    i
                ).evalsimp()
            ).evalsimp();
        }
        switch(subscript) {
        case 0:
            return new Frac(
                a.add(y[0]).subtract(y[1]).subtract(y[2]),
                JSCLInteger.valueOf(4)
            ).evalsimp().negate();
        case 1:
            return new Frac(
                a.subtract(y[0]).subtract(y[1]).add(y[2]),
                JSCLInteger.valueOf(4)
            ).evalsimp().negate();
        case 2:
            return new Frac(
                a.add(y[0]).add(y[1]).add(y[2]),
                JSCLInteger.valueOf(4)
            ).evalsimp().negate();
        default:
            return new Frac(
                a.subtract(y[0]).add(y[1]).subtract(y[2]),
                JSCLInteger.valueOf(4)
            ).evalsimp().negate();
        }
    }

    public int degree() {
        return parameter.length-1;
    }

    public Generic evalnum() {
        return NumericWrapper.root(subscript.integerValue().intValue(),parameter);
    }

    public int compareTo(Variable variable) {
        if(this==variable) return 0;
        int c=comparator.compare(this,variable);
        if(c<0) return -1;
        else if(c>0) return 1;
        else {
            Root v=(Root)variable;
            c=ArrayComparator.comparator.compare(parameter,v.parameter);
            if(c<0) return -1;
            else if(c>0) return 1;
            else return subscript.compareTo(v.subscript);
        }
    }

    public static Generic sigma(Generic parameter[], int n) {
        Sigma s=new Sigma(parameter,n);
        s.compute();
        return s.getValue();
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(name);
        buffer.append("[").append(subscript).append("]");
        buffer.append("(");
        for(int i=0;i<parameter.length;i++) {
            buffer.append(parameter[i]).append(i<parameter.length-1?", ":"");
        }
        buffer.append(")");
        return buffer.toString();
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        buffer.append("Numeric.").append(name).append("(");
        buffer.append(subscript.integerValue().intValue());
        buffer.append(", new Numeric[] {");
        for(int i=0;i<parameter.length;i++) {
            buffer.append(parameter[i].toJava()).append(i<parameter.length-1?", ":"");
        }
        buffer.append("})");
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        MathML e1;
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) {
            e1=element.element("msub");
            nameToMathML(e1);
            subscript.toMathML(e1,null);
            element.appendChild(e1);
        } else {
            e1=element.element("msubsup");
            nameToMathML(e1);
            subscript.toMathML(e1,null);
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

    void bodyToMathML(MathML element, boolean fenced) {}

    protected Variable newinstance() {
        return new Root(new Generic[parameter.length],null);
    }
}

class Sigma {
    Generic root[];
    Generic generic;
    boolean place[];
    int n;

    Sigma(Generic parameter[], int n) {
        root=new Generic[parameter.length-1];
        for(int i=0;i<root.length;i++) root[i]=new Root(parameter,i).expressionValue();
        place=new boolean[root.length];
        this.n=n;
    }

    void compute() {
        generic=JSCLInteger.valueOf(0);
        compute(0,n);
    }

    void compute(int p, int nn) {
        if(nn>0) {
            for(int i=p;i<root.length;i++) {
                place[i]=true;
                compute(i+1,nn-1);
                place[i]=false;
            }
        } else {
            Generic s=JSCLInteger.valueOf(1);
            for(int i=0;i<root.length;i++) {
                if(place[i]) s=s.multiply(root[i]);
            }
            generic=generic.add(s);
        }
    }

    Generic getValue() {
        return generic;
    }
}
