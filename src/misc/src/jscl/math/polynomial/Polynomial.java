package jscl.math.polynomial;

import java.util.Collection;
import java.util.Iterator;
import jscl.math.Arithmetic;
import jscl.math.Expression;
import jscl.math.Field;
import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JSCLBoolean;
import jscl.math.JSCLInteger;
import jscl.math.Literal;
import jscl.math.ModularInteger;
import jscl.math.NotDivisibleException;
import jscl.math.Rational;
import jscl.math.Variable;
import jscl.mathml.MathML;

public abstract class Polynomial implements Arithmetic, Comparable {
    final Monomial monomialFactory;
    final Generic coefFactory;
    final Ordering ordering;
    final boolean defined;
    final boolean field;
    boolean normalized;
    int sugar;
    int index=-1;

    Polynomial(Monomial monomialFactory, Generic coefFactory) {
        this.monomialFactory=monomialFactory;
        this.coefFactory=coefFactory;
        ordering=monomialFactory.ordering();
        defined=monomialFactory instanceof DefinedBooleanMonomial;
        field=coefFactory instanceof Field;
    }

    public abstract int size();

    public Ordering ordering() {
        return ordering;
    }

    public final Iterator iterator() {
        return iterator(false);
    }

    public final Iterator iterator(boolean direction) {
        return iterator(direction,null);
    }

    public final Iterator iterator(Monomial current) {
        return iterator(true,current);
    }

    public abstract Iterator iterator(boolean direction, Monomial current);

    public Polynomial add(Polynomial polynomial) {
        return multiplyAndSubtract(coefficient(JSCLInteger.valueOf(-1)),polynomial);
    }

    public abstract Polynomial subtract(Polynomial polynomial);

    public Polynomial multiplyAndSubtract(Generic generic, Polynomial polynomial) {
        return subtract(polynomial.multiply(generic));
    }

    public Polynomial multiplyAndSubtract(Monomial monomial, Generic generic, Polynomial polynomial) {
        return subtract(polynomial.multiply(monomial).multiply(generic));
    }

    public Polynomial multiply(Polynomial polynomial) {
        Polynomial p=valueof(JSCLInteger.valueOf(0));
        Iterator it=iterator();
        while(it.hasNext()) {
            Term t=(Term)it.next();
            p=p.multiplyAndSubtract(t.monomial(),t.coef().negate(),polynomial);
        }
        return p;
    }

    public abstract Polynomial multiply(Generic generic);
    public abstract Polynomial multiply(Monomial monomial);

    public boolean multiple(Polynomial polynomial) throws ArithmeticException {
        return remainder(polynomial).signum()==0;
    }

    public Polynomial divide(Polynomial polynomial) throws ArithmeticException {
        Polynomial p[]=divideAndRemainder(polynomial);
        if(p[1].signum()==0) return p[0];
        else throw new NotDivisibleException();
    }

    public abstract Polynomial divide(Generic generic) throws ArithmeticException;
    public abstract Polynomial divide(Monomial monomial) throws ArithmeticException;

    public Arithmetic add(Arithmetic arithmetic) {
        return add((Polynomial)arithmetic);
    }

    public Arithmetic subtract(Arithmetic arithmetic) {
        return subtract((Polynomial)arithmetic);
    }

    public Arithmetic multiply(Arithmetic arithmetic) {
        return multiply((Polynomial)arithmetic);
    }

    public Arithmetic divide(Arithmetic arithmetic) throws ArithmeticException {
        return divide((Polynomial)arithmetic);
    }

    public Polynomial[] divideAndRemainder(Polynomial polynomial) throws ArithmeticException {
        Polynomial p[]={valueof(JSCLInteger.valueOf(0)),this};
        Polynomial q=polynomial;
        Iterator it=p[1].iterator(true);
        while(it.hasNext()) {
            Term t=(Term)it.next();
            Monomial m1=t.monomial();
            Monomial m2=q.head().monomial();
            if(m1.multiple(m2)) {
                Monomial m=m1.divide(m2);
                Generic c1=t.coef();
                Generic c2=q.head().coef();
                Generic c=c1.divide(c2);
                p[0]=p[0].multiplyAndSubtract(m,c,valueof(JSCLInteger.valueOf(-1)));
                p[1]=p[1].multiplyAndSubtract(m,c,q);
                it=p[1].iterator(true);
            }
        }
        return p;
    }

    public Polynomial remainder(Polynomial polynomial) throws ArithmeticException {
        return divideAndRemainder(polynomial)[1];
    }

    public Polynomial remainderUpToCoefficient(Polynomial polynomial) throws ArithmeticException {
        Polynomial p=this;
        Polynomial q=polynomial;
        Iterator it=p.iterator(true);
        while(it.hasNext()) {
            Term t=(Term)it.next();
            Monomial m1=t.monomial();
            Monomial m2=q.head().monomial();
            if(m1.multiple(m2)) {
                Monomial m=m1.divide(m2);
                Generic c1=t.coef();
                Generic c2=q.head().coef();
//              Generic c=c1.gcd(c2);
//              c1=c1.divide(c);
//              c2=c2.divide(c);
                p=p.multiply(c2).multiplyAndSubtract(m,c1,q);
                it=p.iterator(true);
            }
        }
        return p;
    }

    public abstract Polynomial gcd(Polynomial polynomial);

    public Polynomial scm(Polynomial polynomial) {
        return divide(gcd(polynomial)).multiply(polynomial);
    }

    public Generic gcd() {
        if(field) return coefficient(tail());
        Generic a=coefficient(JSCLInteger.valueOf(0));
        for(Iterator it=iterator();it.hasNext();) a=a.gcd(((Term)it.next()).coef());
        return a.signum()==signum()?a:a.negate();
    }

    public final Polynomial[] gcdAndNormalize() {
        Generic gcd=gcd();
        return new Polynomial[] {valueof(gcd),gcd.signum()==0?this:divide(gcd)};
    }

    public final Polynomial normalize() {
        if(normalized) return this;
        else {
            Polynomial p=gcdAndNormalize()[1];
            p.normalized=true;
            return p;
        }
    }

    public Monomial monomialGcd() {
        Monomial m=monomial(tail());
        for(Iterator it=iterator();it.hasNext();) m=m.gcd(((Term)it.next()).monomial());
        return m;
    }

    public Polynomial pow(int exponent) {
        Polynomial a=valueof(JSCLInteger.valueOf(1));
        for(int i=0;i<exponent;i++) a=a.multiply(this);
        return a;
    }

    public Polynomial abs() {
        return signum()<0?negate():this;
    }

    public Polynomial negate() {
        return multiply(coefficient(JSCLInteger.valueOf(-1)));
    }

    public final int signum() {
        return coefficient(tail()).signum();
    }

    static int degree(Polynomial polynomial) {
        return polynomial.monomial(polynomial.head()).degree();
    }

    public abstract int degree();
    public abstract Polynomial valueof(Polynomial polynomial);
    public abstract Polynomial valueof(Generic generic);
    public abstract Polynomial valueof(Monomial monomial);

    public final Polynomial copy() {
        return valueof(this);
    }

    public abstract Polynomial freeze();

    public Term head() {
        Iterator it=iterator(true);
        return it.hasNext()?(Term)it.next():null;
    }

    public Term tail() {
        Iterator it=iterator();
        return it.hasNext()?(Term)it.next():null;
    }

    public Generic coefficient(Monomial monomial) {
        Iterator it=iterator(false,monomial);
        Term t=it.hasNext()?(Term)it.next():null;
        return coefficient(t==null || ordering.compare(t.monomial(),monomial)==0?t:null);
    }

    Monomial monomial(Term term) {
        return term==null?monomial(Literal.valueOf()):term.monomial();
    }

    Generic coefficient(Term term) {
        return term==null?coefficient(JSCLInteger.valueOf(0)):term.coef();
    }

    protected Monomial monomial(Literal literal) {
        return monomialFactory.valueof(literal);
    }

    protected Generic coefficient(Generic generic) {
        return coefFactory==null?generic:coefFactory.valueof(generic);
    }

    public Polynomial reduce(Collection ideal, boolean tail) {
        Polynomial p=this;
        Iterator it=tail?p.iterator(p.head().monomial()):p.iterator(true);
        loop: while(it.hasNext()) {
            Term t=(Term)it.next();
            Monomial m1=t.monomial();
            Iterator iq=ideal.iterator();
            while(iq.hasNext()) {
                Polynomial q=(Polynomial)iq.next();
                Monomial m2=q.head().monomial();
                if(m1.multiple(m2)) {
                    Monomial m=m1.divide(m2);
                    p=p.reduce(t.coef(),m,q);
                    it=tail?p.iterator(m1):p.iterator(true);
                    continue loop;
                }
            }
            tail=true;
        }
        return p;
    }

    public Polynomial reduce(Generic generic, Monomial monomial, Polynomial polynomial) {
        if(field) return multiplyAndSubtract(monomial,generic.divide(polynomial.head().coef()),polynomial);
        else {
            Generic c1=generic;
            Generic c2=polynomial.head().coef();
            Generic c=c1.gcd(c2);
            c1=c1.divide(c);
            c2=c2.divide(c);
            return multiply(c2).multiplyAndSubtract(monomial,c1,polynomial).normalize();
        }
    }

    public Polynomial reduce(Generic generic, Polynomial polynomial) {
        return reduce(generic,monomial(Literal.valueOf()),polynomial);
    }

    public int sugar() {
        return sugar;
    }

    public int index() {
        return index;
    }

    public void setSugar(int n) {
        sugar=n;
    }

    public void setIndex(int n) {
        if(index!=-1) throw new ArithmeticException();
        index=n;
    }

    public Generic genericValue() {
        Generic s=JSCLInteger.valueOf(0);
        Iterator it=iterator();
        while(it.hasNext()) {
            Term t=(Term)it.next();
            Monomial m=t.monomial();
            Generic a=t.coef().expressionValue();
            s=s.add(m.degree()>0?a.multiply(Expression.valueOf(m.literalValue())):a);
        }
        return s;
    }

    public Generic[] elements() {
        int size=size();
        Generic a[]=new Generic[size];
        Iterator it=iterator();
        for(int i=0;i<size;i++) a[i]=((Term)it.next()).coef();
        return a;
    }

    public static Polynomial factory(Variable variable) {
        return new UnivariatePolynomial(variable);
    }

    public static Polynomial factory(Variable variable[]) {
        return new NestedPolynomial(variable);
    }

    public static Polynomial factory(Variable unknown[], Ordering ordering) {
        return factory(unknown,ordering,0);
    }

    public static Polynomial factory(Variable unknown[], Ordering ordering, int modulo) {
        return factory(unknown,ordering,modulo,0);
    }

    public static Polynomial factory(Variable unknown[], Ordering ordering, int modulo, int flags) {
        return factory(Monomial.factory(unknown,ordering,flags&Basis.POWER_SIZE),modulo,flags&Basis.DATA_STRUCT,(flags&Basis.GEO_BUCKETS)>0);
    }

    static Polynomial factory(Monomial monomialFactory, int modulo, int data_struct, boolean buckets) {
        if(buckets) return new GeoBucket(factory(monomialFactory,modulo,data_struct,false));
        else switch(data_struct) {
        case Basis.ARRAY:
            return new ArrayPolynomial(monomialFactory,generic(modulo));
        case Basis.TREE:
            return new TreePolynomial(monomialFactory,generic(modulo));
        case Basis.LIST:
            return new ListPolynomial(monomialFactory,generic(modulo));
        default:
            switch(modulo) {
            case -1:
                return new ArrayPolynomialGeneric(monomialFactory,null);
            case 0:
                return new ArrayPolynomialInteger(monomialFactory);
            case 1:
                return new ArrayPolynomialRational(monomialFactory);
            case 2:
                return new ArrayPolynomialBoolean(monomialFactory);
            default:
                return new ArrayPolynomialModular(monomialFactory,ModularInteger.factory(modulo));
            }
        }
    }

    static Generic generic(int modulo) {
        switch(modulo) {
        case -1:
            return null;
        case 0:
            return JSCLInteger.factory;
        case 1:
            return Rational.factory;
        case 2:
            return JSCLBoolean.factory;
        default:
            return ModularInteger.factory(modulo);
        }
    }

    static Polynomial factory(Polynomial polynomial, int modulo) {
        Monomial m=polynomial.monomialFactory;
        return factory(m.unknown(),m.ordering(),modulo);
    }

    public int compareTo(Polynomial polynomial) {
        Iterator it1=iterator(true);
        Iterator it2=polynomial.iterator(true);
        Term t1=it1.hasNext()?(Term)it1.next():null;
        Term t2=it2.hasNext()?(Term)it2.next():null;
        while(t1!=null || t2!=null) {
            int c=t1==null?1:(t2==null?-1:ordering.compare(t1.monomial(),t2.monomial()));
            if(c<0) return -1;
            else if(c>0) return 1;
            else {
                c=t1.coef().compareTo(t2.coef());
                if(c<0) return -1;
                else if(c>0) return 1;
                t1=it1.hasNext()?(Term)it1.next():null;
                t2=it2.hasNext()?(Term)it2.next():null;
            }
        }
        return 0;
    }

    public int compareTo(Object o) {
        return compareTo((Polynomial)o);
    }

    public boolean equals(Object obj) {
        if(obj instanceof Polynomial) {
            return compareTo((Polynomial)obj)==0;
        } else return false;
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        if(signum()==0) buffer.append("0");
        int i=0;
        for(Iterator it=iterator();it.hasNext();i++) {
            Term t=(Term)it.next();
            Monomial m=t.monomial();
            Generic a=t.coef();
            if(a instanceof Expression) a=a.signum()>0?GenericVariable.valueOf(a).expressionValue():GenericVariable.valueOf(a.negate()).expressionValue().negate();
            if(a.signum()>0 && i>0) buffer.append("+");
            if(m.degree()==0) buffer.append(a);
            else {
                if(a.abs().compareTo(JSCLInteger.valueOf(1))==0) {
                    if(a.signum()<0) buffer.append("-");
                } else buffer.append(a).append("*");
                buffer.append(m);
            }
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
        int i=0;
        for(Iterator it=iterator();it.hasNext();i++) {
            Term t=(Term)it.next();
            Monomial m=t.monomial();
            Generic a=t.coef();
            if(a instanceof Expression) a=a.signum()>0?GenericVariable.valueOf(a).expressionValue():GenericVariable.valueOf(a.negate()).expressionValue().negate();
            if(a.signum()>0 && i>0) {
                MathML e2=element.element("mo");
                e2.appendChild(element.text("+"));
                e1.appendChild(e2);
            }
            if(m.degree()==0) Expression.separateSign(e1,a);
            else {
                if(a.abs().compareTo(JSCLInteger.valueOf(1))==0) {
                    if(a.signum()<0) {
                        MathML e2=element.element("mo");
                        e2.appendChild(element.text("-"));
                        e1.appendChild(e2);
                    }
                } else Expression.separateSign(e1,a);
                m.toMathML(e1,null);
            }
        }
        element.appendChild(e1);
    }
}
