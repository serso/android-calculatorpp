package jscl.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jscl.math.polynomial.Basis;
import jscl.math.polynomial.Monomial;
import jscl.math.polynomial.Ordering;
import jscl.math.polynomial.Polynomial;
import jscl.util.ArrayComparator;
import jscl.util.ArrayUtils;

public class Factorization {
    Polynomial factory;
    Generic result;

    Factorization(Polynomial factory) {
        this.factory=factory;
    }

    public static Generic compute(Generic generic) {
        try {
            return GenericVariable.content(factorize(generic.integerValue()));
        } catch (NotIntegerException e) {
            Factorization f=new Factorization(Polynomial.factory(generic.variables(),Monomial.iteratorOrdering,-1));
            f.computeValue(generic);
            return f.getValue();
        }
    }

    static Generic factorize(JSCLInteger integer) {
        Generic n[]=integer.gcdAndNormalize();
        Generic s=n[1];
        Generic a=JSCLInteger.valueOf(1);
        Generic p=JSCLInteger.valueOf(2);
        while(s.compareTo(JSCLInteger.valueOf(1))>0) {
            Generic q[]=s.divideAndRemainder(p);
            if(q[0].compareTo(p)<0) {
                p=s;
                q=s.divideAndRemainder(p);
            }
            if(q[1].signum()==0) {
                a=a.multiply(expression(p,true));
                s=q[0];
            } else p=p.add(JSCLInteger.valueOf(1));
        }
        return a.multiply(n[0]);
    }

    void computeValue(Generic generic) {
        Debug.println("factorization");
        Polynomial n[]=factory.valueof(generic).gcdAndNormalize();
        Monomial m=n[1].monomialGcd();
        Polynomial s=n[1].divide(m);
        Generic a=JSCLInteger.valueOf(1);
        Divisor d[]=new Divisor[2];
        Monomial p[]=new Monomial[2];
        Monomial q[]=new Monomial[2];
        d[1]=new Divisor(s.head().monomial());
        loop: while(d[1].hasNext()) {
            p[1]=(Monomial)d[1].next();
            q[1]=d[1].complementary();
            d[0]=new Divisor(s.tail().monomial());
            while(d[0].hasNext()) {
                p[0]=(Monomial)d[0].next();
                q[0]=d[0].complementary();
                if(p[1].compareTo(p[0])<=0) continue loop;
                Debug.println(toString(p)+" * "+toString(q)+" = "+s);
                if(ArrayComparator.comparator.compare(q,p)<0) {
                    a=a.multiply(expression(s.genericValue()));
                    break loop;
                } else {
                    Debug.increment();
                    Polynomial r[]=remainder(s,polynomial(s,p),terminator(s));
                    Debug.decrement();
                    if(r[0].signum()==0) {
                        a=a.multiply(expression(r[1].genericValue()));
                        s=r[2];
                        d[1].divide();
                        d[0].divide();
                        continue loop;
                    }
                }
            }
        }
        result=a.multiply(n[0].multiply(m).genericValue());
    }

    static Polynomial[] remainder(Polynomial s, Polynomial p, Generic t[]) {
        Polynomial zero=s.valueof(JSCLInteger.valueOf(0));
        Generic a[]=Basis.augment(t,s.remainderUpToCoefficient(p).elements());
        Variable unk[]=Basis.augmentUnknown(new Variable[] {},p.elements());
        {
            Variable u=unk[unk.length-1];
            System.arraycopy(unk,0,unk,1,unk.length-1);
            unk[0]=u;
        }
        Generic be[][]=Linearization.compute(Basis.compute(a,unk,Monomial.lexicographic,0,Basis.DEGREE).elements(),unk);
        for(int i=0;i<be.length;i++) {
            Polynomial r=substitute(p,be[i],unk);
            try {
                return new Polynomial[] {zero,r,s.divide(r)};
            } catch(NotDivisibleException e) {}
        } 
        return new Polynomial[] {s,zero,zero};
    }

    static Polynomial substitute(Polynomial p, Generic a[], Variable unk[]) {
        Generic s[]=new Generic[] {p.genericValue()};
        return p.valueof(Basis.compute(Basis.augment(a,s),Basis.augmentUnknown(unk,s)).elements()[0]);
    }

    private static final String ter="t";

    static Polynomial polynomial(Polynomial s, Monomial monomial[]) {
        Polynomial p=s.valueof(JSCLInteger.valueOf(0));
        Iterator it=monomial[1].iterator(monomial[0]);
        for(int i=0;it.hasNext();i++) {
            Monomial m=(Monomial)it.next();
            Variable t=it.hasNext()?new TechnicalVariable(ter,new int[] {i}):new TechnicalVariable(ter);
            p=p.add(p.valueof(m).multiply(t.expressionValue()));
        }
        return p;
    }

    static Generic[] terminator(Polynomial polynomial) {
        Generic t[]=new Generic[2];
        t[1]=terminator(polynomial.head().coef().abs(),new TechnicalVariable(ter),false);
        t[0]=terminator(polynomial.tail().coef(),new TechnicalVariable(ter,new int[] {0}),true);
        return t;
    }

    static Generic terminator(Generic generic, Variable var, boolean tail) {
        Generic x=var.expressionValue();
        Generic a=JSCLInteger.valueOf(1);
        Iterator it=IntegerDivisor.create(generic.integerValue());
        while(it.hasNext()) {
            Generic s=(Generic)it.next();
            a=a.multiply(x.subtract(s));
            if(!tail) a=a.multiply(x.add(s));
        }
        return a;
    }

    static Generic expression(Generic generic) {
        return expression(generic,false);
    }

    static Generic expression(Generic generic, boolean integer) {
        if(generic.compareTo(JSCLInteger.valueOf(1))==0) return generic;
        else return GenericVariable.valueOf(generic,integer).expressionValue();
    }

    static String toString(Monomial monomial[]) {
        return "{"+monomial[0]+", "+monomial[1]+"}";
    }

    Generic getValue() {
        return GenericVariable.content(result,true);
    }
}

class Linearization {
    Variable unknown[];
    List result=new ArrayList();
    
    Linearization(Variable unknown[]) {
        this.unknown=unknown;
    }

    static Generic[][] compute(Generic generic[], Variable unknown[]) {
        Linearization l=new Linearization(unknown);
        Debug.println("linearization");
        Debug.increment();
        l.process(generic);
        Debug.decrement();
        return l.getValue();
    }

    void process(Generic generic[]) {
        boolean flag=true;
        for(int i=0;i<generic.length;i++) {
            Generic s=generic[i];
            Variable va[]=s.variables();
            if(va.length==1) {
                Variable t=va[0];
                Polynomial p=Polynomial.factory(t).valueof(s);
                if(p.degree()>1) {
                    flag=false;
                    Polynomial r[]=linearize(p,t);
                    for(int j=0;j<r.length;j++) {
                        process(Basis.compute(Basis.augment(new Generic[] {r[j].genericValue()},generic),unknown).elements());
                    }
                }
            } else flag=false;
        }
        if(flag) result.add(generic);
    }

    static Polynomial[] linearize(Polynomial polynomial, Variable variable) {
        List l=new ArrayList();
        Generic x=variable.expressionValue();
        Polynomial s=polynomial;
        try {
            Polynomial r=s.valueof(x);
            s=s.divide(r);
            l.add(r);
            while(true) s=s.divide(r);
        } catch (NotDivisibleException e) {}
        IntegerDivisor d[]=new IntegerDivisor[2];
        Generic p[]=new Generic[2];
        Generic q[]=new Generic[2];
        d[1]=IntegerDivisor.create(JSCLInteger.valueOf(1));
        loop: while(d[1].hasNext()) {
            p[1]=(Generic)d[1].next();
            q[1]=d[1].integer(d[1].complementary());
            d[0]=IntegerDivisor.create(s.tail().coef().integerValue());
            while(d[0].hasNext()) {
                p[0]=(Generic)d[0].next();
                q[0]=d[0].integer(d[0].complementary());
                if(ArrayComparator.comparator.compare(q,p)<0) break loop;
                for(int i=0;i<2;i++) {
                    Polynomial r=s.valueof(i==0?p[1].multiply(x).subtract(p[0]):p[1].multiply(x).add(p[0]));
                    for(boolean flag=true;true;flag=false) {
                        try { s=s.divide(r); } catch (NotDivisibleException e) { break; }
                        d[1].divide();
                        d[0].divide();
                        if(flag) l.add(r);
                    }
                }
            }
        }
        return (Polynomial[])ArrayUtils.toArray(l,new Polynomial[l.size()]);
    }

    Generic[][] getValue() {
        return (Generic[][])ArrayUtils.toArray(result,new Generic[result.size()][]);
    }
}

class Divisor implements Iterator {
    Monomial monomial;
    Monomial current;
    Iterator iterator;

    Divisor(Monomial monomial) {
        this.monomial=monomial;
        iterator=monomial.divisor();
    }

    Monomial complementary() {
        return monomial.divide(current);
    }

    void divide() {
        monomial=complementary();
        iterator=monomial.divisor(current);
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Object next() {
        return current=(Monomial)iterator.next();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}

class IntegerDivisor extends Divisor {
    IntegerDivisor(Generic generic, Variable unknown[], Ordering ordering) {
        super(Polynomial.factory(unknown,ordering).valueof(generic).head().monomial());
    }

    public Object next() {
        return integer((Monomial)super.next());
    }

    Generic integer(Monomial monomial) {
        return Expression.valueOf(Literal.valueOf(monomial)).expand();
    }

    static IntegerDivisor create(JSCLInteger integer) {
        Generic a=Factorization.factorize(integer);
        return new IntegerDivisor(a,a.variables(),Monomial.iteratorOrdering);
    }
}
