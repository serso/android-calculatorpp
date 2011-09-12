package jscl.math.polynomial;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.ModularInteger;

class ArrayPolynomialModular extends ArrayPolynomialGeneric {
    final int modulo;
    int coef[];

    ArrayPolynomialModular(Monomial monomialFactory, Generic coefFactory) {
        super(monomialFactory,coefFactory);
        modulo=((ModularInteger)coefFactory).modulo();
    }

    ArrayPolynomialModular(int size, Monomial monomialFactory, Generic coefFactory) {
        this(monomialFactory,coefFactory);
        init(size);
    }

    void init(int size) {
        monomial=new Monomial[size];
        coef=new int[size];
        this.size=size;
    }

    void resize(int size) {
        int length=monomial.length;
        if(size<length) {
            Monomial monomial[]=new Monomial[size];
            int coef[]=new int[size];
            System.arraycopy(this.monomial,length-size,monomial,0,size);
            System.arraycopy(this.coef,length-size,coef,0,size);
            this.monomial=monomial;
            this.coef=coef;
            this.size=size;
        }
    }

    public Polynomial subtract(Polynomial polynomial) {
        if(polynomial.signum()==0) return this;
        ArrayPolynomialModular q=(ArrayPolynomialModular)polynomial;
        ArrayPolynomialModular p=(ArrayPolynomialModular)newinstance(size+q.size);
        int i=p.size;
        int i1=size;
        int i2=q.size;
        Monomial m1=i1>0?monomial[--i1]:null;
        Monomial m2=i2>0?q.monomial[--i2]:null;
        while(m1!=null || m2!=null) {
            int c=m1==null?1:(m2==null?-1:-ordering.compare(m1,m2));
            if(c<0) {
                int a=coef[i1];
                --i;
                p.monomial[i]=m1;
                p.coef[i]=a;
                m1=i1>0?monomial[--i1]:null;
            } else if(c>0) {
                int a=(int)(((long)modulo-(long)q.coef[i2])%modulo);
                --i;
                p.monomial[i]=m2;
                p.coef[i]=a;
                m2=i2>0?q.monomial[--i2]:null;
            } else {
                int a=(int)(((long)coef[i1]+(long)modulo-(long)q.coef[i2])%modulo);
                if(a!=0) {
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
        int g=generic.integerValue().intValue();
        if(g==1) return subtract(polynomial);
        ArrayPolynomialModular q=(ArrayPolynomialModular)polynomial;
        ArrayPolynomialModular p=(ArrayPolynomialModular)newinstance(size+q.size);
        int i=p.size;
        int i1=size;
        int i2=q.size;
        Monomial m1=i1>0?monomial[--i1]:null;
        Monomial m2=i2>0?q.monomial[--i2]:null;
        while(m1!=null || m2!=null) {
            int c=m1==null?1:(m2==null?-1:-ordering.compare(m1,m2));
            if(c<0) {
                int a=coef[i1];
                --i;
                p.monomial[i]=m1;
                p.coef[i]=a;
                m1=i1>0?monomial[--i1]:null;
            } else if(c>0) {
                int a=(int)(((long)modulo-((long)q.coef[i2]*(long)g)%modulo)%modulo);
                --i;
                p.monomial[i]=m2;
                p.coef[i]=a;
                m2=i2>0?q.monomial[--i2]:null;
            } else {
                int a=(int)(((long)coef[i1]+(long)modulo-((long)q.coef[i2]*(long)g)%modulo)%modulo);
                if(a!=0) {
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
        int g=generic.integerValue().intValue();
        ArrayPolynomialModular q=(ArrayPolynomialModular)polynomial;
        ArrayPolynomialModular p=(ArrayPolynomialModular)newinstance(size+q.size);
        int i=p.size;
        int i1=size;
        int i2=q.size;
        Monomial m1=i1>0?this.monomial[--i1]:null;
        Monomial m2=i2>0?q.monomial[--i2].multiply(monomial):null;
        while(m1!=null || m2!=null) {
            int c=m1==null?1:(m2==null?-1:-ordering.compare(m1,m2));
            if(c<0) {
                int a=coef[i1];
                --i;
                p.monomial[i]=m1;
                p.coef[i]=a;
                m1=i1>0?this.monomial[--i1]:null;
            } else if(c>0) {
                int a=(int)(((long)modulo-((long)q.coef[i2]*(long)g)%modulo)%modulo);
                --i;
                p.monomial[i]=m2;
                p.coef[i]=a;
                m2=i2>0?q.monomial[--i2].multiply(monomial):null;
            } else {
                int a=(int)(((long)coef[i1]+(long)modulo-((long)q.coef[i2]*(long)g)%modulo)%modulo);
                if(a!=0) {
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
        int g=generic.integerValue().intValue();
        if(g==1) return this;
        ArrayPolynomialModular p=(ArrayPolynomialModular)newinstance(size);
        for(int i=0;i<size;i++) {
            p.monomial[i]=monomial[i];
            p.coef[i]=(int)(((long)coef[i]*(long)g)%modulo);
        }
        p.degree=degree;
        p.sugar=sugar;
        return p;
    }

    public Polynomial multiply(Monomial monomial) {
        if(defined) throw new UnsupportedOperationException();
        if(monomial.degree()==0) return this;
        ArrayPolynomialModular p=(ArrayPolynomialModular)newinstance(size);
        for(int i=0;i<size;i++) {
            p.monomial[i]=this.monomial[i].multiply(monomial);
            p.coef[i]=coef[i];
        }
        p.degree=degree+monomial.degree();
        p.sugar=sugar+monomial.degree();
        return p;
    }

    protected Generic coefficient(Generic generic) {
        return coefFactory.valueof(generic);
    }

    protected Generic getCoef(int n) {
        return new ModularInteger(coef[n],modulo);
    }

    protected void setCoef(int n, Generic generic) {
        coef[n]=generic.integerValue().intValue();
    }

    protected ArrayPolynomialGeneric newinstance(int n) {
        return new ArrayPolynomialModular(n,monomialFactory,coefFactory);
    }
}
