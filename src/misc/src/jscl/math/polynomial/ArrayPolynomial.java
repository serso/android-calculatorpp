package jscl.math.polynomial;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.Literal;

final class ArrayPolynomial extends Polynomial {
    Term content[];
    int size;
    int degree;

    ArrayPolynomial(Monomial monomialFactory, Generic coefFactory) {
        super(monomialFactory,coefFactory);
    }

    ArrayPolynomial(int size, Monomial monomialFactory, Generic coefFactory) {
        this(monomialFactory,coefFactory);
        init(size);
    }

    public int size() {
        return size;
    }

    void init(int size) {
        content=new Term[size];
        this.size=size;
    }

    void resize(int size) {
        int length=content.length;
        if(size<length) {
            Term content[]=new Term[size];
            System.arraycopy(this.content,length-size,content,0,size);
            this.content=content;
            this.size=size;
        }
    }

    public Iterator iterator(boolean direction, Monomial current) {
        return new ContentIterator(direction,current);
    }

    class ContentIterator implements Iterator {
        final boolean direction;
        int index;

        ContentIterator(boolean direction, Monomial current) {
            this.direction=direction;
            index=indexOf(current,direction);
        }
        
        public boolean hasNext() {
            return direction?index>0:index<size;
        }
        
        public Object next() {
            return direction?content[--index]:content[index++];
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    int indexOf(Monomial monomial, boolean direction) {
        if(monomial==null) return direction?size:0;
        int n=Arrays.binarySearch(content,new Term(monomial,null));
        return n<0?-n-1:direction?n:n+1;
    }

    public Polynomial subtract(Polynomial polynomial) {
        if(polynomial.signum()==0) return this;
        ArrayPolynomial q=(ArrayPolynomial)polynomial;
        ArrayPolynomial p=newinstance(size+q.size);
        int i=p.size;
        int i1=size;
        int i2=q.size;
        Term t1=i1>0?content[--i1]:null;
        Term t2=i2>0?q.content[--i2]:null;
        while(t1!=null || t2!=null) {
            int c=t1==null?1:(t2==null?-1:-ordering.compare(t1.monomial(),t2.monomial()));
            if(c<0) {
                p.content[--i]=t1;
                t1=i1>0?content[--i1]:null;
            } else if(c>0) {
                p.content[--i]=t2.negate();
                t2=i2>0?q.content[--i2]:null;
            } else {
                Term t=t1.subtract(t2);
                if(t.signum()!=0) p.content[--i]=t;
                t1=i1>0?content[--i1]:null;
                t2=i2>0?q.content[--i2]:null;
            }
        }
        p.resize(p.size-i);
        p.degree=degree(p);
        p.sugar=Math.max(sugar,q.sugar);
        return p;
    }

    public Polynomial multiplyAndSubtract(Generic generic, Polynomial polynomial) {
        if(generic.signum()==0) return this;
        if(generic.compareTo(JSCLInteger.valueOf(1))==0) return subtract(polynomial);
        ArrayPolynomial q=(ArrayPolynomial)polynomial;
        ArrayPolynomial p=newinstance(size+q.size);
        int i=p.size;
        int i1=size;
        int i2=q.size;
        Term t1=i1>0?content[--i1]:null;
        Term t2=i2>0?q.content[--i2].multiply(generic):null;
        while(t1!=null || t2!=null) {
            int c=t1==null?1:(t2==null?-1:-ordering.compare(t1.monomial(),t2.monomial()));
            if(c<0) {
                p.content[--i]=t1;
                t1=i1>0?content[--i1]:null;
            } else if(c>0) {
                p.content[--i]=t2.negate();
                t2=i2>0?q.content[--i2].multiply(generic):null;
            } else {
                Term t=t1.subtract(t2);
                if(t.signum()!=0) p.content[--i]=t;
                t1=i1>0?content[--i1]:null;
                t2=i2>0?q.content[--i2].multiply(generic):null;
            }
        }
        p.resize(p.size-i);
        p.degree=degree(p);
        p.sugar=Math.max(sugar,q.sugar);
        return p;
    }

    public Polynomial multiplyAndSubtract(Monomial monomial, Generic generic, Polynomial polynomial) {
        if(defined) throw new UnsupportedOperationException();
        if(generic.signum()==0) return this;
        if(monomial.degree()==0) return multiplyAndSubtract(generic,polynomial);
        ArrayPolynomial q=(ArrayPolynomial)polynomial;
        ArrayPolynomial p=newinstance(size+q.size);
        int i=p.size;
        int i1=size;
        int i2=q.size;
        Term t1=i1>0?content[--i1]:null;
        Term t2=i2>0?q.content[--i2].multiply(monomial,generic):null;
        while(t1!=null || t2!=null) {
            int c=t1==null?1:(t2==null?-1:-ordering.compare(t1.monomial(),t2.monomial()));
            if(c<0) {
                p.content[--i]=t1;
                t1=i1>0?content[--i1]:null;
            } else if(c>0) {
                p.content[--i]=t2.negate();
                t2=i2>0?q.content[--i2].multiply(monomial,generic):null;
            } else {
                Term t=t1.subtract(t2);
                if(t.signum()!=0) p.content[--i]=t;
                t1=i1>0?content[--i1]:null;
                t2=i2>0?q.content[--i2].multiply(monomial,generic):null;
            }
        }
        p.resize(p.size-i);
        p.degree=degree(p);
        p.sugar=Math.max(sugar,q.sugar+monomial.degree());
        return p;
    }

    public Polynomial multiply(Polynomial polynomial) {
        Polynomial p=valueof(JSCLInteger.valueOf(0));
        for(int i=0;i<size;i++) {
            Term t=content[i];
            p=p.multiplyAndSubtract(t.monomial(),t.coef().negate(),polynomial);
        }
        return p;
    }

    public Polynomial multiply(Generic generic) {
        if(generic.signum()==0) return valueof(JSCLInteger.valueOf(0));
        if(generic.compareTo(JSCLInteger.valueOf(1))==0) return this;
        ArrayPolynomial p=newinstance(size);
        for(int i=0;i<size;i++) p.content[i]=content[i].multiply(generic);
        p.degree=degree;
        p.sugar=sugar;
        return p;
    }

    public Polynomial multiply(Monomial monomial) {
        if(defined) throw new UnsupportedOperationException();
        if(monomial.degree()==0) return this;
        ArrayPolynomial p=newinstance(size);
        for(int i=0;i<size;i++) p.content[i]=content[i].multiply(monomial);
        p.degree=degree+monomial.degree();
        p.sugar=sugar+monomial.degree();
        return p;
    }

    public Polynomial divide(Generic generic) throws ArithmeticException {
        if(generic.compareTo(JSCLInteger.valueOf(1))==0) return this;
        ArrayPolynomial p=newinstance(size);
        for(int i=0;i<size;i++) p.content[i]=content[i].divide(generic);
        p.degree=degree;
        p.sugar=sugar;
        return p;
    }

    public Polynomial divide(Monomial monomial) throws ArithmeticException {
        if(monomial.degree()==0) return this;
        ArrayPolynomial p=newinstance(size);
        for(int i=0;i<size;i++) p.content[i]=content[i].divide(monomial);
        p.degree=degree-monomial.degree();
        p.sugar=sugar-monomial.degree();
        return p;
    }

    public Polynomial gcd(Polynomial polynomial) {
        throw new UnsupportedOperationException();
    }

    public Generic gcd() {
        if(field) return coefficient(tail());
        Generic a=coefficient(JSCLInteger.valueOf(0));
        for(int i=size-1;i>=0;i--) a=a.gcd(content[i].coef());
        return a.signum()==signum()?a:a.negate();
    }

    public Monomial monomialGcd() {
        Monomial m=monomial(tail());
        for(int i=0;i<size;i++) m=m.gcd(content[i].monomial());
        return m;
    }

    public int degree() {
        return degree;
    }

    public Polynomial valueof(Polynomial polynomial) {
        ArrayPolynomial p=newinstance(0);
        p.init(polynomial);
        return p;
    }

    public Polynomial valueof(Generic generic) {
        ArrayPolynomial p=newinstance(0);
        p.init(generic);
        return p;
    }

    public Polynomial valueof(Monomial monomial) {
        ArrayPolynomial p=newinstance(0);
        p.init(monomial);
        return p;
    }

    public Polynomial freeze() {
        return this;
    }

    public Term head() {
        return size>0?content[size-1]:null;
    }

    public Term tail() {
        return size>0?content[0]:null;
    }

    void init(Polynomial polynomial) {
        ArrayPolynomial q=(ArrayPolynomial)polynomial;
        init(q.size);
        System.arraycopy(q.content,0,content,0,size);
        degree=q.degree;
        sugar=q.sugar;
    }

    void init(Expression expression) {
        Map map=new TreeMap(ordering);
        int n=expression.size();
        for(int i=0;i<n;i++) {
            Literal l=expression.literal(i);
            JSCLInteger en=expression.coef(i);
            Monomial m=monomial(l);
            l=l.divide(m.literalValue());
            Generic a2=coefficient(l.degree()>0?en.multiply(Expression.valueOf(l)):en);
            Generic a1=(Generic)map.get(m);
            Generic a=a1==null?a2:a1.add(a2);
            if(a.signum()==0) map.remove(m);
            else map.put(m,a);
        }
        init(map.size());
        int sugar=0;
        Iterator it=map.entrySet().iterator();
        for(int i=0;i<size;i++) {
            Map.Entry e=(Map.Entry)it.next();
            Monomial m=(Monomial)e.getKey();
            Generic a=(Generic)e.getValue();
            content[i]=new Term(m,a);
            sugar=Math.max(sugar,m.degree());
        }
        degree=degree(this);
        this.sugar=sugar;
    }

    void init(Generic generic) {
        if(generic instanceof Expression) {
            init((Expression)generic);
        } else {
            Generic a=coefficient(generic);
            if(a.signum()!=0) {
                init(1);
                content[0]=new Term(monomial(Literal.valueOf()),a);
            } else init(0);
            degree=0;
            sugar=0;
        }
    }

    void init(Monomial monomial) {
        init(1);
        content[0]=new Term(monomial,coefficient(JSCLInteger.valueOf(1)));
        degree=monomial.degree();
        sugar=monomial.degree();
    }

    protected ArrayPolynomial newinstance(int n) {
        return new ArrayPolynomial(n,monomialFactory,coefFactory);
    }
}
