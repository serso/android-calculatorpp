package jscl.math.polynomial;

import java.math.BigInteger;
import jscl.math.Generic;
import jscl.math.JSCLInteger;

class ArrayPolynomialInteger extends ArrayPolynomialGeneric {
    BigInteger coef[];

    ArrayPolynomialInteger(Monomial monomialFactory) {
        super(monomialFactory,JSCLInteger.factory);
    }

    ArrayPolynomialInteger(int size, Monomial monomialFactory) {
        this(monomialFactory);
        init(size);
    }

    void init(int size) {
        monomial=new Monomial[size];
        coef=new BigInteger[size];
        this.size=size;
    }

    void resize(int size) {
        int length=monomial.length;
        if(size<length) {
            Monomial monomial[]=new Monomial[size];
            BigInteger coef[]=new BigInteger[size];
            System.arraycopy(this.monomial,length-size,monomial,0,size);
            System.arraycopy(this.coef,length-size,coef,0,size);
            this.monomial=monomial;
            this.coef=coef;
            this.size=size;
        }
    }

    public Polynomial subtract(Polynomial polynomial) {
        if(polynomial.signum()==0) return this;
        ArrayPolynomialInteger q=(ArrayPolynomialInteger)polynomial;
        ArrayPolynomialInteger p=(ArrayPolynomialInteger)newinstance(size+q.size);
        int i=p.size;
        int i1=size;
        int i2=q.size;
        Monomial m1=i1>0?monomial[--i1]:null;
        Monomial m2=i2>0?q.monomial[--i2]:null;
        while(m1!=null || m2!=null) {
            int c=m1==null?1:(m2==null?-1:-ordering.compare(m1,m2));
            if(c<0) {
                BigInteger a=coef[i1];
                --i;
                p.monomial[i]=m1;
                p.coef[i]=a;
                m1=i1>0?monomial[--i1]:null;
            } else if(c>0) {
                BigInteger a=q.coef[i2].negate();
                --i;
                p.monomial[i]=m2;
                p.coef[i]=a;
                m2=i2>0?q.monomial[--i2]:null;
            } else {
                BigInteger a=coef[i1].subtract(q.coef[i2]);
                if(a.signum()!=0) {
                    --i;
                    p.monomial[i]=m1;
                    p.coef[i]=a;
                }
                m1=i1>0?monomial[--i1]:null;
                m2=i2>0?q.monomial[--i2]:null;
            }
        }
        p.resize(p.size-i);
        p.degree=degree(p);
        p.sugar=Math.max(sugar,q.sugar);
        return p;
    }

    public Polynomial multiplyAndSubtract(Generic generic, Polynomial polynomial) {
        if(generic.signum()==0) return this;
        BigInteger g=generic.integerValue().content();
        if(g.compareTo(BigInteger.valueOf(1))==0) return subtract(polynomial);
        ArrayPolynomialInteger q=(ArrayPolynomialInteger)polynomial;
        ArrayPolynomialInteger p=(ArrayPolynomialInteger)newinstance(size+q.size);
        int i=p.size;
        int i1=size;
        int i2=q.size;
        Monomial m1=i1>0?monomial[--i1]:null;
        Monomial m2=i2>0?q.monomial[--i2]:null;
        while(m1!=null || m2!=null) {
            int c=m1==null?1:(m2==null?-1:-ordering.compare(m1,m2));
            if(c<0) {
                BigInteger a=coef[i1];
                --i;
                p.monomial[i]=m1;
                p.coef[i]=a;
                m1=i1>0?monomial[--i1]:null;
            } else if(c>0) {
                BigInteger a=q.coef[i2].multiply(g).negate();
                --i;
                p.monomial[i]=m2;
                p.coef[i]=a;
                m2=i2>0?q.monomial[--i2]:null;
            } else {
                BigInteger a=coef[i1].subtract(q.coef[i2].multiply(g));
                if(a.signum()!=0) {
                    --i;
                    p.monomial[i]=m1;
                    p.coef[i]=a;
                }
                m1=i1>0?monomial[--i1]:null;
                m2=i2>0?q.monomial[--i2]:null;
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
        BigInteger g=generic.integerValue().content();
        ArrayPolynomialInteger q=(ArrayPolynomialInteger)polynomial;
        ArrayPolynomialInteger p=(ArrayPolynomialInteger)newinstance(size+q.size);
        int i=p.size;
        int i1=size;
        int i2=q.size;
        Monomial m1=i1>0?this.monomial[--i1]:null;
        Monomial m2=i2>0?q.monomial[--i2].multiply(monomial):null;
        while(m1!=null || m2!=null) {
            int c=m1==null?1:(m2==null?-1:-ordering.compare(m1,m2));
            if(c<0) {
                BigInteger a=coef[i1];
                --i;
                p.monomial[i]=m1;
                p.coef[i]=a;
                m1=i1>0?this.monomial[--i1]:null;
            } else if(c>0) {
                BigInteger a=q.coef[i2].multiply(g).negate();
                --i;
                p.monomial[i]=m2;
                p.coef[i]=a;
                m2=i2>0?q.monomial[--i2].multiply(monomial):null;
            } else {
                BigInteger a=coef[i1].subtract(q.coef[i2].multiply(g));
                if(a.signum()!=0) {
                    --i;
                    p.monomial[i]=m1;
                    p.coef[i]=a;
                }
                m1=i1>0?this.monomial[--i1]:null;
                m2=i2>0?q.monomial[--i2].multiply(monomial):null;
            }
        }
        p.resize(p.size-i);
        p.degree=degree(p);
        p.sugar=Math.max(sugar,q.sugar+monomial.degree());
        return p;
    }

    public Polynomial multiply(Generic generic) {
        if(generic.signum()==0) return valueof(JSCLInteger.valueOf(0));
        BigInteger g=generic.integerValue().content();
        if(g.compareTo(BigInteger.valueOf(1))==0) return this;
        ArrayPolynomialInteger p=(ArrayPolynomialInteger)newinstance(size);
        for(int i=0;i<size;i++) {
            p.monomial[i]=monomial[i];
            p.coef[i]=coef[i].multiply(g);
        }
        p.degree=degree;
        p.sugar=sugar;
        return p;
    }

    public Polynomial multiply(Monomial monomial) {
        if(defined) throw new UnsupportedOperationException();
        if(monomial.degree()==0) return this;
        ArrayPolynomialInteger p=(ArrayPolynomialInteger)newinstance(size);
        for(int i=0;i<size;i++) {
            p.monomial[i]=this.monomial[i].multiply(monomial);
            p.coef[i]=coef[i];
        }
        p.degree=degree+monomial.degree();
        p.sugar=sugar+monomial.degree();
        return p;
    }

    public Polynomial divide(Generic generic) throws ArithmeticException {
        BigInteger g=generic.integerValue().content();
        if(g.compareTo(BigInteger.valueOf(1))==0) return this;
        ArrayPolynomialInteger p=(ArrayPolynomialInteger)newinstance(size);
        for(int i=0;i<size;i++) {
            p.monomial[i]=monomial[i];
            p.coef[i]=coef[i].divide(g);
        }
        p.degree=degree;
        p.sugar=sugar;
        return p;
    }

    public Polynomial gcd(Polynomial polynomial) {
        return valueof(genericValue().gcd(polynomial.genericValue()));
    }

    public Generic gcd() {
        BigInteger a=BigInteger.valueOf(0);
        for(int i=size-1;i>=0;i--) if((a=a.gcd(coef[i])).compareTo(BigInteger.valueOf(1))==0) break;
        return new JSCLInteger(a.signum()==signum()?a:a.negate());
    }

    protected Generic coefficient(Generic generic) {
        return coefFactory.valueof(generic);
    }

    protected Generic getCoef(int n) {
        return new JSCLInteger(coef[n]);
    }

    protected void setCoef(int n, Generic generic) {
        coef[n]=generic.integerValue().content();
    }

    protected ArrayPolynomialGeneric newinstance(int n) {
        return new ArrayPolynomialInteger(n,monomialFactory);
    }
}
