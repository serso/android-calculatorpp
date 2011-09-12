package jscl.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jscl.math.function.Frac;
import jscl.math.function.Inv;
import jscl.math.polynomial.Polynomial;
import jscl.math.polynomial.UnivariatePolynomial;
import jscl.mathml.MathML;
import jscl.text.ExpressionParser;
import jscl.text.ParseException;
import jscl.text.Parser;
import jscl.util.ArrayUtils;

public class Expression extends Generic {
    Literal literal[];
    JSCLInteger coef[];
    int size;

    Expression() {}

    Expression(int size) {
        init(size);
    }

    public int size() {
        return size;
    }

    public Literal literal(int n) {
        return literal[n];
    }

    public JSCLInteger coef(int n) {
        return coef[n];
    }

    void init(int size) {
        literal=new Literal[size];
        coef=new JSCLInteger[size];
        this.size=size;
    }

    void resize(int size) {
        int length=literal.length;
        if(size<length) {
            Literal literal[]=new Literal[size];
            JSCLInteger coef[]=new JSCLInteger[size];
            System.arraycopy(this.literal,length-size,literal,0,size);
            System.arraycopy(this.coef,length-size,coef,0,size);
            this.literal=literal;
            this.coef=coef;
            this.size=size;
        }
    }

    public Expression add(Expression expression) {
        Expression ex=newinstance(size+expression.size);
        int i=ex.size;
        int i1=size;
        int i2=expression.size;
        Literal l1=i1>0?literal[--i1]:null;
        Literal l2=i2>0?expression.literal[--i2]:null;
        while(l1!=null || l2!=null) {
            int c=l1==null?1:(l2==null?-1:-l1.compareTo(l2));
            if(c<0) {
                JSCLInteger en=coef[i1];
                --i;
                ex.literal[i]=l1;
                ex.coef[i]=en;
                l1=i1>0?literal[--i1]:null;
            } else if(c>0) {
                JSCLInteger en=expression.coef[i2];
                --i;
                ex.literal[i]=l2;
                ex.coef[i]=en;
                l2=i2>0?expression.literal[--i2]:null;
            } else {
                JSCLInteger en=coef[i1].add(expression.coef[i2]);
                if(en.signum()!=0) {
                    --i;
                    ex.literal[i]=l1;
                    ex.coef[i]=en;
                }
                l1=i1>0?literal[--i1]:null;
                l2=i2>0?expression.literal[--i2]:null;
            }
        }
        ex.resize(ex.size-i);
        return ex;
    }

    public Generic add(Generic generic) {
        if(generic instanceof Expression) {
            return add((Expression)generic);
        } else if(generic instanceof JSCLInteger || generic instanceof Rational) {
            return add(valueof(generic));
        } else {
            return generic.valueof(this).add(generic);
        }
    }

    public Expression subtract(Expression expression) {
        return multiplyAndAdd(Literal.valueOf(),JSCLInteger.valueOf(-1),expression);
    }

    public Generic subtract(Generic generic) {
        if(generic instanceof Expression) {
            return subtract((Expression)generic);
        } else if(generic instanceof JSCLInteger || generic instanceof Rational) {
            return subtract(valueof(generic));
        } else {
            return generic.valueof(this).subtract(generic);
        }
    }

    Expression multiplyAndAdd(Literal lit, JSCLInteger integer, Expression expression) {
        if(integer.signum()==0) return this;
        Expression ex=newinstance(size+expression.size);
        int i=ex.size;
        int i1=size;
        int i2=expression.size;
        Literal l1=i1>0?literal[--i1]:null;
        Literal l2=i2>0?expression.literal[--i2].multiply(lit):null;
        while(l1!=null || l2!=null) {
            int c=l1==null?1:(l2==null?-1:-l1.compareTo(l2));
            if(c<0) {
                JSCLInteger en=coef[i1];
                --i;
                ex.literal[i]=l1;
                ex.coef[i]=en;
                l1=i1>0?literal[--i1]:null;
            } else if(c>0) {
                JSCLInteger en=expression.coef[i2].multiply(integer);
                --i;
                ex.literal[i]=l2;
                ex.coef[i]=en;
                l2=i2>0?expression.literal[--i2].multiply(lit):null;
            } else {
                JSCLInteger en=coef[i1].add(expression.coef[i2].multiply(integer));
                if(en.signum()!=0) {
                    --i;
                    ex.literal[i]=l1;
                    ex.coef[i]=en;
                }
                l1=i1>0?literal[--i1]:null;
                l2=i2>0?expression.literal[--i2].multiply(lit):null;
            }
        }
        ex.resize(ex.size-i);
        return ex;
    }

    public Expression multiply(Expression expression) {
        Expression ex=newinstance(0);
        for(int i=0;i<size;i++) ex=ex.multiplyAndAdd(literal[i],coef[i],expression);
        return ex;
    }

    public Generic multiply(Generic generic) {
        if(generic instanceof Expression) {
            return multiply((Expression)generic);
        } else if(generic instanceof JSCLInteger) {
            return multiply(valueof(generic));
        } else if(generic instanceof Rational) {
            return multiply(valueof(generic));
        } else {
            return generic.multiply(this);
        }
    }

    public Generic divide(Generic generic) throws ArithmeticException {
        Generic a[]=divideAndRemainder(generic);
        if(a[1].signum()==0) return a[0];
        else throw new NotDivisibleException();
    }

    public Generic[] divideAndRemainder(Generic generic) throws ArithmeticException {
        if(generic instanceof Expression) {
            Expression ex=(Expression)generic;
            Literal l1=literalScm();
            Literal l2=ex.literalScm();
            Literal l=(Literal)l1.gcd(l2);
            Variable va[]=l.variables();
            if(va.length==0) {
                if(signum()==0 && ex.signum()!=0) return new Generic[] {this,JSCLInteger.valueOf(0)};
                else try {
                    return divideAndRemainder(ex.integerValue());
                } catch (NotIntegerException e) {
                    return new Generic[] {JSCLInteger.valueOf(0),this};
                }
            } else {
                Polynomial fact=Polynomial.factory(va[0]);
                Polynomial p[]=fact.valueof(this).divideAndRemainder(fact.valueof(ex));
                return new Generic[] {p[0].genericValue(),p[1].genericValue()};
            }
        } else if(generic instanceof JSCLInteger) {
            try {
                Expression ex=newinstance(size);
                for(int i=0;i<size;i++) {
                    ex.literal[i]=literal[i];
                    ex.coef[i]=coef[i].divide((JSCLInteger)generic);
                }
                return new Generic[] {ex,JSCLInteger.valueOf(0)};
            } catch (NotDivisibleException e) {
                return new Generic[] {JSCLInteger.valueOf(0),this};
            }
        } else if(generic instanceof Rational) {
            return divideAndRemainder(valueof(generic));
        } else {
            return generic.valueof(this).divideAndRemainder(generic);
        }
    }

    public Generic gcd(Generic generic) {
        if(generic instanceof Expression) {
            Expression ex=(Expression)generic;
            Literal l1=literalScm();
            Literal l2=ex.literalScm();
            Literal l=(Literal)l1.gcd(l2);
            Variable va[]=l.variables();
            if(va.length==0) {
                if(signum()==0) return ex;
                else return gcd(ex.gcd());
            } else {
                Polynomial fact=Polynomial.factory(va[0]);
                return fact.valueof(this).gcd(fact.valueof(ex)).genericValue();
            }
        } else if(generic instanceof JSCLInteger) {
            if(generic.signum()==0) return this;
            else return gcd().gcd(generic);
        } else if(generic instanceof Rational) {
            return gcd(valueof(generic));
        } else {
            return generic.valueof(this).gcd(generic);
        }
    }

    public Generic gcd() {
        JSCLInteger en=JSCLInteger.valueOf(0);
        for(int i=size-1;i>=0;i--) en=en.gcd(coef[i]);
        return en;
    }

    public Literal literalScm() {
        Literal l=Literal.valueOf();
        for(int i=0;i<size;i++) l=l.scm(literal[i]);
        return l;
    }

    public Generic negate() {
        return multiply(JSCLInteger.valueOf(-1));
    }

    public int signum() {
        return size==0?0:coef[0].signum();
    }

    public int degree() {
        return 0;
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        if(isPolynomial(variable)) {
            return ((UnivariatePolynomial)Polynomial.factory(variable).valueof(this)).antiderivative().genericValue();
        } else {
            try {
                Variable v=variableValue();
                try {
                    return v.antiderivative(variable);
                } catch (NotIntegrableException e) {
                    if(v instanceof Frac) {
                        Generic g[]=((Frac)v).parameters();
                        if(g[1].isConstant(variable)) {
                            return new Inv(g[1]).evaluate().multiply(g[0].antiderivative(variable));
                        }
                    }
                }
            } catch (NotVariableException e) {
                Generic a[]=sumValue();
                if(a.length>1) {
                    Generic s=JSCLInteger.valueOf(0);
                    for(int i=0;i<a.length;i++) {
                        s=s.add(a[i].antiderivative(variable));
                    }
                    return s;
                } else {
                    Generic p[]=a[0].productValue();
                    Generic s=JSCLInteger.valueOf(1);
                    Generic t=JSCLInteger.valueOf(1);
                    for(int i=0;i<p.length;i++) {
                        if(p[i].isConstant(variable)) s=s.multiply(p[i]);
                        else t=t.multiply(p[i]);
                    }
                    if(s.compareTo(JSCLInteger.valueOf(1))==0);
                    else return s.multiply(t.antiderivative(variable));
                }
            }
        }
        throw new NotIntegrableException();
    }

    public Generic derivative(Variable variable) {
        Generic s=JSCLInteger.valueOf(0);
        Literal l=literalScm();
        int n=l.size;
        for(int i=0;i<n;i++) {
            Variable v=l.variable[i];
            Generic a=((UnivariatePolynomial)Polynomial.factory(v).valueof(this)).derivative(variable).genericValue();
            s=s.add(a);
        }
        return s;
    }

    public Generic substitute(Variable variable, Generic generic) {
        Map m=literalScm().content();
        Iterator it=m.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry e=(Map.Entry)it.next();
            Variable v=(Variable)e.getKey();
            e.setValue(v.substitute(variable,generic));
        }
        return substitute(m);
    }

    Generic substitute(Map map) {
        Generic s=JSCLInteger.valueOf(0);
        for(int i=0;i<size;i++) {
            Literal l=literal[i];
            Generic a=coef[i];
            int m=l.size;
            for(int j=0;j<m;j++) {
                Variable v=l.variable[j];
                int c=l.power[j];
                Generic b=(Generic)map.get(v);
                b=b.pow(c);
                if(Matrix.product(a,b)) throw new ArithmeticException();
                a=a.multiply(b);
            }
            s=s.add(a);
        }
        return s;
    }

    public Generic expand() {
        Map m=literalScm().content();
        Iterator it=m.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry e=(Map.Entry)it.next();
            Variable v=(Variable)e.getKey();
            e.setValue(v.expand());
        }
        return substitute(m);
    }

    public Generic factorize() {
        Map m=literalScm().content();
        Iterator it=m.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry e=(Map.Entry)it.next();
            Variable v=(Variable)e.getKey();
            e.setValue(v.factorize());
        }
        Generic a=substitute(m);
        return Factorization.compute(a);
    }

    public Generic elementary() {
        Map m=literalScm().content();
        Iterator it=m.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry e=(Map.Entry)it.next();
            Variable v=(Variable)e.getKey();
            e.setValue(v.elementary());
        }
        return substitute(m);
    }

    public Generic simplify() {
        return Simplification.compute(this);
    }

    public Generic numeric() {
        try {
            return integerValue().numeric();
        } catch (NotIntegerException ex) {
            Map m=literalScm().content();
            Iterator it=m.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry e=(Map.Entry)it.next();
                Variable v=(Variable)e.getKey();
                e.setValue(v.numeric());
            }
            return substitute(m);
        }
    }

    public Generic valueof(Generic generic) {
        Expression ex=newinstance(0);
        ex.init(generic);
        return ex;
    }

    public Generic[] sumValue() {
        Generic a[]=new Generic[size];
        for(int i=0;i<a.length;i++) a[i]=valueOf(literal[i],coef[i]);
        return a;
    }

    public Generic[] productValue() throws NotProductException {
        if(size==0) return new Generic[] {JSCLInteger.valueOf(0)};
        else if(size==1) {
            Literal l=literal[0];
            JSCLInteger en=coef[0];
            Generic p[]=l.productValue();
            if(en.compareTo(JSCLInteger.valueOf(1))==0) return p;
            else {
                Generic a[]=new Generic[p.length+1];
                for(int i=0;i<p.length;i++) a[i+1]=p[i];
                a[0]=en;
                return a;
            }
        } else throw new NotProductException();
    }

    public Power powerValue() throws NotPowerException {
        if(size==0) return new Power(JSCLInteger.valueOf(0),1);
        else if(size==1) {
            Literal l=literal[0];
            JSCLInteger en=coef[0];
            if(en.compareTo(JSCLInteger.valueOf(1))==0) return l.powerValue();
            else if(l.degree()==0) return en.powerValue();
            else throw new NotPowerException();
        } else throw new NotPowerException();
    }

    public Expression expressionValue() throws NotExpressionException {
        return this;
    }

    public JSCLInteger integerValue() throws NotIntegerException {
        if(size==0) return JSCLInteger.valueOf(0);
        else if(size==1) {
            Literal l=literal[0];
            JSCLInteger en=coef[0];
            if(l.degree()==0) return en;
            else throw new NotIntegerException();
        } else throw new NotIntegerException();
    }

    public Variable variableValue() throws NotVariableException {
        if(size==0) throw new NotVariableException();
        else if(size==1) {
            Literal l=literal[0];
            JSCLInteger en=coef[0];
            if(en.compareTo(JSCLInteger.valueOf(1))==0) return l.variableValue();
            else throw new NotVariableException();
        } else throw new NotVariableException();
    }

    public Variable[] variables() {
        return literalScm().variables();
    }

    public static Variable[] variables(Generic generic[]) {
        List l=new ArrayList();
        for(int i=0;i<generic.length;i++) {
            Variable va[]=generic[i].variables();
            for(int j=0;j<va.length;j++) {
                Variable v=va[j];
                if(l.contains(v));
                else l.add(v);
            }
        }
        return (Variable[])ArrayUtils.toArray(l,new Variable[l.size()]);
    }

    public boolean isPolynomial(Variable variable) {
        boolean s=true;
        Literal l=literalScm();
        int n=l.size;
        for(int i=0;i<n;i++) {
            Variable v=l.variable[i];
            s=s && (v.isConstant(variable) || v.isIdentity(variable));
        }
        return s;
    }

    public boolean isConstant(Variable variable) {
        boolean s=true;
        Literal l=literalScm();
        int n=l.size;
        for(int i=0;i<n;i++) {
            Variable v=l.variable[i];
            s=s && v.isConstant(variable);
        }
        return s;
    }

    public JSCLVector grad(Variable variable[]) {
        Generic v[]=new Generic[variable.length];
        for(int i=0;i<variable.length;i++) v[i]=derivative(variable[i]);
        return new JSCLVector(v);
    }

    public Generic laplacian(Variable variable[]) {
        return grad(variable).divergence(variable);
    }

    public Generic dalembertian(Variable variable[]) {
        Generic a=derivative(variable[0]).derivative(variable[0]);
        for(int i=1;i<4;i++) a=a.subtract(derivative(variable[i]).derivative(variable[i]));
        return a;
    }

    public int compareTo(Expression expression) {
        int i1=size;
        int i2=expression.size;
        Literal l1=i1==0?null:literal[--i1];
        Literal l2=i2==0?null:expression.literal[--i2];
        while(l1!=null || l2!=null) {
            int c=l1==null?-1:(l2==null?1:l1.compareTo(l2));
            if(c<0) return -1;
            else if(c>0) return 1;
            else {
                c=coef[i1].compareTo(expression.coef[i2]);
                if(c<0) return -1;
                else if(c>0) return 1;
                l1=i1==0?null:literal[--i1];
                l2=i2==0?null:expression.literal[--i2];
            }
        }
        return 0;
    }

    public int compareTo(Generic generic) {
        if(generic instanceof Expression) {
            return compareTo((Expression)generic);
        } else if(generic instanceof JSCLInteger || generic instanceof Rational) {
            return compareTo(valueof(generic));
        } else {
            return generic.valueof(this).compareTo(generic);
        }
    }

    public static Expression valueOf(Variable variable) {
        return valueOf(Literal.valueOf(variable));
    }

    public static Expression valueOf(Literal literal) {
        return valueOf(literal,JSCLInteger.valueOf(1));
    }

    public static Expression valueOf(JSCLInteger integer) {
        return valueOf(Literal.valueOf(),integer);
    }

    public static Expression valueOf(Literal literal, JSCLInteger integer) {
        Expression ex=new Expression();
        ex.init(literal,integer);
        return ex;
    }

    void init(Literal lit, JSCLInteger integer) {
        if(integer.signum()!=0) {
            init(1);
            literal[0]=lit;
            coef[0]=integer;
        } else init(0);
    }

    public static Expression valueOf(Rational rational) {
        Expression ex=new Expression();
        ex.init(rational);
        return ex;
    }

    public static Expression valueOf(String str) throws ParseException {
        int pos[]=new int[1];
        Generic a;
        try {
            a=(Generic)ExpressionParser.parser.parse(str,pos);
        } catch (ParseException e) {
            throw e;
        }
        Parser.skipWhitespaces(str,pos);
        if(pos[0]<str.length()) {
            throw new ParseException();
        }
        Expression ex=new Expression();
        ex.init(a);
        return ex;
    }

    void init(Expression expression) {
        init(expression.size);
        System.arraycopy(expression.literal,0,literal,0,size);
        System.arraycopy(expression.coef,0,coef,0,size);
    }

    void init(JSCLInteger integer) {
        init(Literal.valueOf(),integer);
    }

    void init(Rational rational) {
        try {
            init(Literal.valueOf(),rational.integerValue());
        } catch (NotIntegerException e) {
            init(Literal.valueOf(rational.variableValue()),JSCLInteger.valueOf(1));
        }
    }

    void init(Generic generic) {
        if(generic instanceof Expression) {
            init((Expression)generic);
        } else if(generic instanceof JSCLInteger) {
            init((JSCLInteger)generic);
        } else if(generic instanceof Rational) {
            init((Rational)generic);
        } else throw new ArithmeticException();
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        if(signum()==0) buffer.append("0");
        for(int i=0;i<size;i++) {
            Literal l=literal[i];
            JSCLInteger en=coef[i];
            if(en.signum()>0 && i>0) buffer.append("+");
            if(l.degree()==0) buffer.append(en);
            else {
                if(en.abs().compareTo(JSCLInteger.valueOf(1))==0) {
                    if(en.signum()<0) buffer.append("-");
                } else buffer.append(en).append("*");
                buffer.append(l);
            }
        }
        return buffer.toString();
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        if(signum()==0) buffer.append("JSCLDouble.valueOf(0)");
        for(int i=0;i<size;i++) {
            Literal l=literal[i];
            JSCLInteger en=coef[i];
            if(i>0) {
                if(en.signum()<0) {
                    buffer.append(".subtract(");
                    en=(JSCLInteger)en.negate();
                } else buffer.append(".add(");
            }
            if(l.degree()==0) buffer.append(en.toJava());
            else {
                if(en.abs().compareTo(JSCLInteger.valueOf(1))==0) {
                    if(en.signum()>0) buffer.append(l.toJava());
                    else if(en.signum()<0) buffer.append(l.toJava()).append(".negate()");
                } else buffer.append(en.toJava()).append(".multiply(").append(l.toJava()).append(")");
            }
            if(i>0) buffer.append(")");
        }
        return buffer.toString();
    }

    public void toMathML(MathML element, Object data) {
        MathML e1=element.element("mrow");
        if(signum()==0) {
            MathML e2=element.element("mn");
            e2.appendChild(element.text("0"));
            e1.appendChild(e2);
        }
        for(int i=0;i<size;i++) {
            Literal l=literal[i];
            JSCLInteger en=coef[i];
            if(en.signum()>0 && i>0) {
                MathML e2=element.element("mo");
                e2.appendChild(element.text("+"));
                e1.appendChild(e2);
            }
            if(l.degree()==0) separateSign(e1,en);
            else {
                if(en.abs().compareTo(JSCLInteger.valueOf(1))==0) {
                    if(en.signum()<0) {
                        MathML e2=element.element("mo");
                        e2.appendChild(element.text("-"));
                        e1.appendChild(e2);
                    }
                } else separateSign(e1,en);
                l.toMathML(e1,null);
            }
        }
        element.appendChild(e1);
    }

    public static void separateSign(MathML element, Generic generic) {
        if(generic.signum()<0) {
            MathML e1=element.element("mo");
            e1.appendChild(element.text("-"));
            element.appendChild(e1);
            generic.negate().toMathML(element,null);
        } else generic.toMathML(element,null);
    }

    protected Expression newinstance(int n) {
        return new Expression(n);
    }
}
