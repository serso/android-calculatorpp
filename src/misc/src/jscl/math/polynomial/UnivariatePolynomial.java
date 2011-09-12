package jscl.math.polynomial;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.Literal;
import jscl.math.NotDivisibleException;
import jscl.math.Variable;
import jscl.math.function.Inv;
import jscl.util.ArrayUtils;

public class UnivariatePolynomial extends Polynomial {
    protected final Variable variable;
    Generic content[]=new Generic[8];
    int degree;

    protected UnivariatePolynomial(Variable variable) {
        this(variable,null);
    }

    UnivariatePolynomial(Variable variable, Generic coefFactory) {
        super(Monomial.factory(new Variable[] {variable}),coefFactory);
        this.variable=variable;
    }

    public Variable variable() {
        return variable;
    }

    public int size() {
        return degree+1;
    }

    public Iterator iterator(boolean direction, Monomial current) {
        return new ContentIterator(direction,current);
    }

    class ContentIterator implements Iterator {
        final boolean direction;
        int index;

        ContentIterator(boolean direction, Monomial current) {
            this.direction=direction;
            if(direction) {
                index=indexOf(current,true);
            } else {
                index=indexOf(current,false);
                if(current!=null && get(index).signum()!=0) index++;
            }
            seek();
        }

        void seek() {
            if(direction) while(index>0 && get(index).signum()==0) index--;
            else while(index<=degree && get(index).signum()==0) index++;
        }
        
        public boolean hasNext() {
            return direction?index>0:index<=degree;
        }
        
        public Object next() {
            Term t=direction?term(--index):term(index++);
            seek();
            return t;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    Term term(int n) {
        return new Term(monomial(Literal.valueOf(variable,n)),get(n));
    }

    int indexOf(Monomial monomial, boolean direction) {
        if(monomial==null) return direction?degree+1:0;
        return monomial.degree();
    }

    public Polynomial add(Polynomial polynomial) {
        UnivariatePolynomial p=newinstance();
        UnivariatePolynomial q=(UnivariatePolynomial)polynomial;
        int d=Math.max(degree,q.degree);
        for(int i=d;i>=0;i--) {
            p.put(i,get(i).add(q.get(i)));
        }
        return p;
    }

    public Polynomial subtract(Polynomial polynomial) {
        UnivariatePolynomial p=newinstance();
        UnivariatePolynomial q=(UnivariatePolynomial)polynomial;
        int d=Math.max(degree,q.degree);
        for(int i=d;i>=0;i--) {
            p.put(i,get(i).subtract(q.get(i)));
        }
        return p;
    }

    public Polynomial multiply(Polynomial polynomial) {
        UnivariatePolynomial p=newinstance();
        UnivariatePolynomial q=(UnivariatePolynomial)polynomial;
        for(int i=degree;i>=0;i--) {
            for(int j=q.degree;j>=0;j--) {
                p.put(i+j,get(i).multiply(q.get(j)));
            }
        }
        return p;
    }

    public Polynomial multiply(Generic generic) {
        UnivariatePolynomial p=newinstance();
        for(int i=degree;i>=0;i--) {
            p.put(i,get(i).multiply(generic));
        }
        return p;
    }

    public Polynomial multiply(Monomial monomial, Generic generic) {
        UnivariatePolynomial p=newinstance();
        int d=monomial.degree();
        for(int i=degree;i>=0;i--) {
            p.put(i+d,get(i).multiply(generic));
        }
        for(int i=d-1;i>=0;i--) {
            p.put(i,JSCLInteger.valueOf(0));
        }
        return p;
    }

    public Polynomial multiply(Monomial monomial) {
        return multiply(monomial,JSCLInteger.valueOf(1));
    }

    public Polynomial divide(Generic generic) throws ArithmeticException {
        UnivariatePolynomial p=newinstance();
        for(int i=degree;i>=0;i--) {
            p.put(i,get(i).divide(generic));
        }
        return p;
    }

    public Polynomial divide(Monomial monomial) throws ArithmeticException {
        UnivariatePolynomial p=newinstance();
        int d=monomial.degree();
        for(int i=d-1;i>=0;i--) {
            if(get(i).signum()==0);
            else throw new NotDivisibleException();
        }
        for(int i=degree;i>=d;i--) {
            p.put(i-d,get(i));
        }
        return p;
    }

    public Polynomial[] divideAndRemainder(Polynomial polynomial) throws ArithmeticException {
        UnivariatePolynomial p[]={newinstance(),this};
        UnivariatePolynomial q=(UnivariatePolynomial)polynomial;
        if(p[1].signum()==0) return p;
        for(int i=p[1].degree-q.degree;i>=0;i--) {
            p[0].put(i,p[1].get(i+q.degree).divide(q.get(q.degree)));
            UnivariatePolynomial r=newinstance();
            for(int j=i+q.degree-1;j>=0;j--) {
                Generic a=p[1].get(j);
                r.put(j,a.subtract(q.get(j-i).multiply(p[0].get(i))));
            }
            p[1]=r;
        }
        return p;
    }

    public Polynomial remainderUpToCoefficient(Polynomial polynomial) throws ArithmeticException {
        UnivariatePolynomial p=this;
        UnivariatePolynomial q=(UnivariatePolynomial)polynomial;
        if(p.signum()==0) return p;
        for(int i=p.degree-q.degree;i>=0;i--) {
            UnivariatePolynomial r=newinstance();
            for(int j=i+q.degree-1;j>=0;j--) {
                Generic a=p.get(j).multiply(q.get(q.degree));
                r.put(j,a.subtract(q.get(j-i).multiply(p.get(i+q.degree))));
            }
            p=r;
        }
        return p;
    }

    public Polynomial gcd(Polynomial polynomial) {
        UnivariatePolynomial p=this;
        UnivariatePolynomial q=(UnivariatePolynomial)polynomial;
        if(p.signum()==0) return q;
        else if(q.signum()==0) return p;
        if(p.degree<q.degree) {
            UnivariatePolynomial r=p;
            p=q;
            q=r;
        }
        int d=p.degree-q.degree;
        Generic phi=JSCLInteger.valueOf(-1);
        Generic beta=JSCLInteger.valueOf(-1).pow(d+1);
        Polynomial a1[]=p.gcdAndNormalize();
        Polynomial a2[]=q.gcdAndNormalize();
        Generic gcd1=a1[0].genericValue();
        Generic gcd2=a2[0].genericValue();
        p=(UnivariatePolynomial)a1[1];
        q=(UnivariatePolynomial)a2[1];
        while(q.degree>0) {
            UnivariatePolynomial r=(UnivariatePolynomial)p.remainderUpToCoefficient(q).divide(beta);
            if(d>1) phi=q.get(q.degree).negate().pow(d).divide(phi.pow(d-1));
            else phi=q.get(q.degree).negate().pow(d).multiply(phi.pow(1-d));
            p=q;
            q=r;
            d=p.degree-q.degree;
            beta=p.get(p.degree).negate().multiply(phi.pow(d));
        }
        if(q.signum()==0) {
            p=(UnivariatePolynomial)p.normalize();
        } else {
            p=newinstance();
            p.put(0,JSCLInteger.valueOf(1));
        }
        return p.multiply(gcd1.gcd(gcd2));
    }

    public Generic gcd() {
        Generic a=coefficient(JSCLInteger.valueOf(0));
        for(int i=degree;i>=0;i--) a=a.gcd(get(i));
        return a.signum()==signum()?a:a.negate();
    }

    public Monomial monomialGcd() {
        return monomial(tail());
    }

    public int degree() {
        return degree;
    }

    public UnivariatePolynomial valueof(Generic generic[]) {
        UnivariatePolynomial p=newinstance();
        p.init(generic);
        return p;
    }

    public Polynomial valueof(Polynomial polynomial) {
        throw new UnsupportedOperationException();
    }

    public Polynomial valueof(Generic generic) {
        UnivariatePolynomial p=newinstance();
        p.init(generic);
        return p;
    }

    public Polynomial valueof(Monomial monomial) {
        throw new UnsupportedOperationException();
    }

    public Polynomial freeze() {
        return this;
    }

    public Term head() {
        return term(degree);
    }

    public Generic coefficient(Monomial monomial) {
        return term(monomial.degree()).coef();
    }

    public Polynomial reduce(Generic generic, Monomial monomial, Polynomial polynomial, boolean inPlace) {
        throw new UnsupportedOperationException();
    }

    public Generic genericValue() {
        Generic s=JSCLInteger.valueOf(0);
        for(int i=degree;i>=0;i--) {
            Generic a=get(i).expressionValue();
            s=s.add(i>0?a.multiply(Expression.valueOf(Literal.valueOf(variable,i))):a);
        }
        return s;
    }

    public Generic[] elements() {
        Generic a[]=new Generic[degree+1];
        for(int i=degree;i>=0;i--) a[i]=get(i);
        return a;
    }

    public UnivariatePolynomial derivative(Variable variable) {
        return (UnivariatePolynomial)derivative().multiply(this.variable.derivative(variable));
    }

    public Generic substitute(Generic generic) {
        Generic s=JSCLInteger.valueOf(0);
        for(int i=degree;i>=0;i--) {
            s=s.add(get(i).multiply(generic.pow(i)));
        }
        return s;
    }

    public Generic solve() {
        if(degree==1) {
            return get(0).multiply(new Inv(get(1)).evaluate()).negate();
        } else return null;
    }

    public Generic[] identification(UnivariatePolynomial polynomial) {
        UnivariatePolynomial p=this;
        UnivariatePolynomial q=polynomial;
        if(p.degree<q.degree || (p.degree==0 && q.signum()==0)) {
            UnivariatePolynomial r=p;
            p=q;
            q=r;
        }
        UnivariatePolynomial r=(UnivariatePolynomial)p.remainderUpToCoefficient(q);
        Generic a[]=new Generic[r.degree+1];
        for(int i=r.degree;i>=0;i--) a[r.degree-i]=r.get(i);
        return a;
    }

    public Generic resultant(UnivariatePolynomial polynomial) {
        UnivariatePolynomial p=this;
        UnivariatePolynomial q=polynomial;
        if(p.degree<q.degree || (p.degree==0 && q.signum()==0)) {
            UnivariatePolynomial r=p;
            p=q;
            q=r;
        }
        int d=p.degree-q.degree;
        Generic phi=JSCLInteger.valueOf(-1);
        Generic beta=JSCLInteger.valueOf(-1).pow(d+1);
        while(q.degree>0) {
            UnivariatePolynomial r=(UnivariatePolynomial)p.remainderUpToCoefficient(q).divide(beta);
            if(d>1) phi=q.get(q.degree).negate().pow(d).divide(phi.pow(d-1));
            else phi=q.get(q.degree).negate().pow(d).multiply(phi.pow(1-d));
            p=q;
            q=r;
            d=p.degree-q.degree;
            beta=p.get(p.degree).negate().multiply(phi.pow(d));
        }
        return q.get(0);
    }

    public UnivariatePolynomial[] remainderSequence(UnivariatePolynomial polynomial) {
        UnivariatePolynomial p=this;
        UnivariatePolynomial q=polynomial;
        if(p.degree<q.degree || (p.degree==0 && q.signum()==0)) {
            UnivariatePolynomial r=p;
            p=q;
            q=r;
        }
        UnivariatePolynomial s[]=new UnivariatePolynomial[q.degree+1];
        s[q.degree]=q;
        int d=p.degree-q.degree;
        Generic phi=JSCLInteger.valueOf(-1);
        Generic beta=JSCLInteger.valueOf(-1).pow(d+1);
        while(q.degree>0) {
            UnivariatePolynomial r=(UnivariatePolynomial)p.remainderUpToCoefficient(q).divide(beta);
            if(d>1) phi=q.get(q.degree).negate().pow(d).divide(phi.pow(d-1));
            else phi=q.get(q.degree).negate().pow(d).multiply(phi.pow(1-d));
            p=q;
            q=r;
            s[q.degree]=q;
            d=p.degree-q.degree;
            beta=p.get(p.degree).negate().multiply(phi.pow(d));
        }
        return s;
    }

    public UnivariatePolynomial squarefree() {
        return (UnivariatePolynomial)divide(gcd(derivative()));
    }

    public UnivariatePolynomial[] squarefreeDecomposition() {
        return SquarefreeDecomposition.compute(this);
    }

    public UnivariatePolynomial antiderivative() {
        UnivariatePolynomial p=newinstance();
        for(int i=degree;i>=0;i--) {
            p.put(i+1,get(i).multiply(new Inv(JSCLInteger.valueOf(i+1)).evaluate()));
        }
        return p;
    }

    public UnivariatePolynomial derivative() {
        UnivariatePolynomial p=newinstance();
        for(int i=degree-1;i>=0;i--) {
            p.put(i,get(i+1).multiply(JSCLInteger.valueOf(i+1)));
        }
        return p;
    }

    public int compareTo(Polynomial polynomial) {
        UnivariatePolynomial p=(UnivariatePolynomial)polynomial;
        int d=Math.max(degree,p.degree);
        for(int i=d;i>=0;i--) {
            Generic a1=get(i);
            Generic a2=p.get(i);
            int c=a1.compareTo(a2);
            if(c<0) return -1;
            else if(c>0) return 1;
        }
        return 0;
    }

    void init(Generic generic[]) {
        for(int i=0;i<generic.length;i++) put(i,coefficient(generic[i]));
    }

    void init(Expression expression) {
        int n=expression.size();
        for(int i=0;i<n;i++) {
            Literal l=expression.literal(i);
            JSCLInteger en=expression.coef(i);
            Monomial m=monomial(l);
            l=l.divide(m.literalValue());
            if(l.degree()>0) put(m.degree(),coefficient(en.multiply(Expression.valueOf(l))));
            else put(m.degree(),coefficient(en));
        }
    }

    protected void init(Generic generic) {
        if(generic instanceof Expression) {
            init((Expression)generic);
        } else put(0,coefficient(generic));
    }

    void put(int n, Generic generic) {
        Generic a=generic.add(get(n));
        if(a.signum()==0) {
            if(n<=degree) content[n]=null;
            if(n==degree) {
                while(n>0 && content[n]==null) n--;
                degree=n;
            }
        } else {
            if(n>=content.length) resize(n);
            content[n]=a;
            degree=Math.max(degree,n);
        }
    }

    void resize(int n) {
        int length=content.length<<1;
        while(n>=length) length<<=1;
        Generic content[]=new Generic[length];
        System.arraycopy(this.content,0,content,0,this.content.length);
        this.content=content;
    }
    
    public Generic get(int n) {
        Generic a=n<0 || n>degree?null:content[n];
        return a==null?JSCLInteger.valueOf(0):a;
    }

    protected UnivariatePolynomial newinstance() {
        return new UnivariatePolynomial(variable,coefFactory);
    }
}

class SquarefreeDecomposition {
    final List list=new ArrayList();

    static UnivariatePolynomial[] compute(UnivariatePolynomial polynomial) {
        SquarefreeDecomposition sd=new SquarefreeDecomposition();
        Polynomial p[]=polynomial.gcdAndNormalize();
        sd.init((UnivariatePolynomial)p[0]);
        sd.process((UnivariatePolynomial)p[1]);
        return sd.getValue();
    }

    void init(UnivariatePolynomial polynomial) {
        list.add(polynomial);
    }

    void process(UnivariatePolynomial polynomial) {
        UnivariatePolynomial r=(UnivariatePolynomial)polynomial.gcd(polynomial.derivative());
        UnivariatePolynomial s=(UnivariatePolynomial)polynomial.divide(r);
        list.add(s.divide(s.gcd(r)));
        if(r.degree()==0);
        else process(r);
    }

    UnivariatePolynomial[] getValue() {
        return (UnivariatePolynomial[])ArrayUtils.toArray(list,new UnivariatePolynomial[list.size()]);
    }
}
