package jscl.math.polynomial;

import java.util.Iterator;
import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.mathml.MathML;

final class GeoBucket extends Polynomial {
    final Polynomial factory;
    Polynomial content[];
    int size;
    boolean mutable=true;
    boolean canonicalized=true;

    GeoBucket(Polynomial factory) {
        super(factory.monomialFactory,factory.coefFactory);
        this.factory=factory;
    }

    GeoBucket(int size, Polynomial factory) {
        this(factory);
        init(size);
    }

    public int size() {
        return size;
    }

    void init(int size) {
        content=new Polynomial[size];
        this.size=size;
    }

    void resize(int size) {
        Polynomial content[]=new Polynomial[size];
        System.arraycopy(this.content,0,content,0,Math.min(this.size,size));
        this.content=content;
        this.size=size;
    }

    public Iterator iterator(boolean direction, Monomial current) {
        return new ContentIterator(direction,current);
    }

    class ContentIterator implements Iterator {
        final boolean direction;
        Term term;

        ContentIterator(boolean direction, Monomial current) {
            this.direction=direction;
            term=new Term(current,coefficient(JSCLInteger.valueOf(0)));
            seek();
        }

        void seek() {
            while(true) {
                int n=0;
                Term t=null;
                for(int i=0;i<size;i++) {
                    Polynomial p=content[i];
                    if(p==null) continue;
                    Iterator it=p.iterator(direction,term.monomial());
                    Term u=it.hasNext()?(Term)it.next():null;
                    if(u==null) continue;
                    if(t==null || (direction?-1:1)*ordering.compare(t.monomial(),u.monomial())>0) {
                        t=u;
                        n=i;
                    } else if(ordering.compare(t.monomial(),u.monomial())==0) {
                        t=behead(t,n,i);
                        n=i;
                    }
                }
                if(t==null || t.coef().signum()!=0) {
                    term=t;
                    return;
                }
            }
        }
        
        public boolean hasNext() {
            return term!=null;
        }
        
        public Object next() {
            Term t=term;
            seek();
            return t;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    Term behead(Term t, int n, int i) {
        Monomial m=t.monomial();
        Polynomial p=factory.valueof(m).multiply(t.coef());
        content[n]=content[n].subtract(p);
        content[i]=content[i].add(p);
        return new Term(m,content[i].coefficient(m));
    }

    void canonicalize() {
        Polynomial s=factory.valueof(JSCLInteger.valueOf(0));
        int sugar=0;
        for(int i=0;i<size;i++) {
            Polynomial p=content[i];
            if(p==null) continue;
            s=s.add(p);
            sugar=Math.max(sugar,p.sugar());
            content[i]=null;
        }
        resize(log(s.size())+1);
        set(s.normalize());
        canonicalized=true;
        setSugar(sugar);
        mutable=false;
    }

    static int log(int n) {
        int i;
        for(i=0;n>3;n>>=2) i++;
        return i;
    }

    Polynomial polynomial() {
        if(canonicalized) return content[size-1];
        else throw new UnsupportedOperationException();
    }

    void set(Polynomial polynomial) {
        content[size-1]=polynomial;
    }

    public Polynomial subtract(Polynomial polynomial) {
        if(mutable) {
            Polynomial q=((GeoBucket)polynomial).polynomial();
            int n=log(q.size());
            if(n>=size) resize(n+1);
            Polynomial p=content[n];
            Polynomial s=(p==null?factory.valueof(JSCLInteger.valueOf(0)):p).subtract(q);
            content[n]=null;
            while(n<log(s.size())) {
                n++;
                if(n>=size) resize(n+1);
                p=content[n];
                if(p!=null) s=p.add(s);
                content[n]=null;
            }
            content[n]=s;
            canonicalized=false;
            normalized=false;
            return this;
        } else return copy().subtract(polynomial);
    }

    public Polynomial multiplyAndSubtract(Generic generic, Polynomial polynomial) {
        if(mutable) {
            Polynomial q=((GeoBucket)polynomial).polynomial();
            int n=log(q.size());
            if(n>=size) resize(n+1);
            Polynomial p=content[n];
            Polynomial s=(p==null?factory.valueof(JSCLInteger.valueOf(0)):p).multiplyAndSubtract(generic,q);
            content[n]=null;
            while(n<log(s.size())) {
                n++;
                if(n>=size) resize(n+1);
                p=content[n];
                if(p!=null) s=p.add(s);
                content[n]=null;
            }
            content[n]=s;
            canonicalized=false;
            normalized=false;
            return this;
        } else return copy().multiplyAndSubtract(generic,polynomial);
    }

    public Polynomial multiplyAndSubtract(Monomial monomial, Generic generic, Polynomial polynomial) {
        if(mutable) {
            Polynomial q=((GeoBucket)polynomial).polynomial();
            int n=log(q.size());
            if(n>=size) resize(n+1);
            Polynomial p=content[n];
            Polynomial s=(p==null?factory.valueof(JSCLInteger.valueOf(0)):p).multiplyAndSubtract(monomial,generic,q);
            content[n]=null;
            while(n<log(s.size())) {
                n++;
                if(n>=size) resize(n+1);
                p=content[n];
                if(p!=null) s=p.add(s);
                content[n]=null;
            }
            content[n]=s;
            canonicalized=false;
            normalized=false;
            return this;
        } else return copy().multiplyAndSubtract(monomial,generic,polynomial);
    }

    public Polynomial multiply(Generic generic) {
        if(mutable) {
            if(canonicalized) set(polynomial().multiply(generic));
            else for(int i=0;i<size;i++) {
                Polynomial p=content[i];
                if(p!=null) content[i]=p.multiply(generic);
            }
            normalized=false;
            return this;
        } else return copy().multiply(generic);
    }

    public Polynomial multiply(Monomial monomial) {
        if(mutable) {
            set(polynomial().multiply(monomial));
            return this;
        } else return copy().multiply(monomial);
    }

    public Polynomial divide(Generic generic) throws ArithmeticException {
        if(mutable) {
            if(canonicalized) set(polynomial().divide(generic));
            else for(int i=0;i<size;i++) {
                Polynomial p=content[i];
                if(p!=null) content[i]=p.divide(generic);
            }
            normalized=false;
            return this;
        } else return copy().divide(generic);
    }

    public Polynomial divide(Monomial monomial) throws ArithmeticException {
        if(mutable) {
            set(polynomial().divide(monomial));
            return this;
        } else return copy().divide(monomial);
    }

    public Polynomial gcd(Polynomial polynomial) {
        throw new UnsupportedOperationException();
    }

    public Generic gcd() {
        if(field) return coefficient(tail());
        return canonicalized?polynomial().gcd():coefficient(JSCLInteger.valueOf(0));
    }

    public int degree() {
        return polynomial().degree();
    }

    public Polynomial valueof(GeoBucket bucket) {
        return valueof(bucket.polynomial().copy());
    }

    public Polynomial valueof(Polynomial polynomial) {
        if(polynomial instanceof GeoBucket) {
            return valueof((GeoBucket)polynomial);
        } else {
            GeoBucket b=new GeoBucket(log(polynomial.size())+1,factory);
            b.set(polynomial);
            return b;
        }
    }

    public Polynomial valueof(Generic generic) {
        return valueof(factory.valueof(generic));
    }

    public Polynomial valueof(Monomial monomial) {
        return valueof(factory.valueof(monomial));
    }

    public Polynomial freeze() {
        canonicalize();
        return this;
    }

    public Term head() {
        return canonicalized?polynomial().head():super.head();
    }

    public Term tail() {
        return canonicalized?polynomial().tail():super.tail();
    }

    public Generic coefficient(Monomial monomial) {
        return canonicalized?polynomial().coefficient(monomial):super.coefficient(monomial);
    }

    public int sugar() {
        return polynomial().sugar();
    }

    public int index() {
        return polynomial().index();
    }

    public void setSugar(int n) {
        polynomial().setSugar(n);
    }

    public void setIndex(int n) {
        polynomial().setIndex(n);
    }

    public Generic genericValue() {
        return polynomial().genericValue();
    }

    public Generic[] elements() {
        return polynomial().elements();
    }

    public int compareTo(GeoBucket bucket) {
        return polynomial().compareTo(bucket.polynomial());
    }

    public int compareTo(Polynomial polynomial) {
        return compareTo((GeoBucket)polynomial);
    }

    public String toString() {
        if(canonicalized) return polynomial().toString();
        else {
            StringBuffer buffer=new StringBuffer();
            buffer.append("{");
            for(int i=0;i<size;i++) {
                Polynomial p=content[i];
                buffer.append(p==null?factory.valueof(JSCLInteger.valueOf(0)):p).append(i<size-1?", ":"");
            }
            buffer.append("}");
            return buffer.toString();
        }
    }

    public void toMathML(MathML element, Object data) {
        if(canonicalized) polynomial().toMathML(element,data);
        else {
            MathML e1=element.element("mfenced");
            MathML e2=element.element("mtable");
            for(int i=0;i<size;i++) {
                MathML e3=element.element("mtr");
                MathML e4=element.element("mtd");
                Polynomial p=content[i];
                (p==null?factory.valueof(JSCLInteger.valueOf(0)):p).toMathML(e4,null);
                e3.appendChild(e4);
                e2.appendChild(e3);
            }
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }
}
