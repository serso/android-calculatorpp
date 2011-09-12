package jscl.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import jscl.math.function.Cubic;
import jscl.math.function.Frac;
import jscl.math.function.NotRootException;
import jscl.math.function.Pow;
import jscl.math.function.Root;
import jscl.math.function.Sqrt;
import jscl.math.polynomial.Basis;
import jscl.math.polynomial.Monomial;
import jscl.math.polynomial.Polynomial;
import jscl.math.polynomial.UnivariatePolynomial;

public class Simplification {
    Map cache=new TreeMap();
    Generic result;
    List constraint;
    boolean linear;

    Simplification() {}

    public static Generic compute(Generic generic) {
        Simplification s=new Simplification();
        s.computeValue(generic);
        return s.getValue();
    }

    void computeValue(Generic generic) {
        Debug.println("simplification");
        Debug.increment();
        Variable t=new TechnicalVariable("t");
        linear=false;
        constraint=new ArrayList();
        process(new Constraint(t,t.expressionValue().subtract(generic),false));
        UnivariatePolynomial p=polynomial(t);
        switch(p.degree()) {
            case 0:
                result=generic;
                break;
            case 1:
                result=new Root(p,0).evalsimp();
                break;
//          case 2:
//              int n=branch(generic,p);
//              if(n<p.degree()) linear(new Root(p,n).expressionValue());
//              else linear(generic);
//              break;
            default:
                linear(generic);
        }
        Debug.decrement();
    }

    void linear(Generic generic) {
        Variable t=new TechnicalVariable("t");
        linear=true;
        constraint.clear();
        process(new Constraint(t,t.expressionValue().subtract(generic),false));
        UnivariatePolynomial p=polynomial(t);
        switch(p.degree()) {
            case 0:
                result=generic;
                break;
            default:
                result=new Root(p,0).evalsimp();
        }
    }

    int branch(Generic generic, UnivariatePolynomial polynomial) {
        int n=polynomial.degree();
        Variable t=new TechnicalVariable("t");
        linear=true;
        for(int i=0;i<n;i++) {
            constraint.clear();
            process(new Constraint(t,t.expressionValue().subtract(generic.subtract(new Root(polynomial,i).expressionValue())),false));
            Generic a=polynomial(t).solve();
            if(a!=null?a.signum()==0:false) return i;
        }
        return n;
    }

    UnivariatePolynomial polynomial(Variable t) {
        Polynomial fact=Polynomial.factory(t);
        int n=constraint.size();
        Generic a[]=new Generic[n];
        Variable unk[]=new Variable[n];
        if(linear) {
            int j=0;
            for(int i=0;i<n;i++) {
                Constraint c=(Constraint)constraint.get(i);
                if(c.reduce) {
                    a[j]=c.generic;
                    unk[j]=c.unknown;
                    j++;
                }
            }
            int k=0;
            for(int i=0;i<n;i++) {
                Constraint c=(Constraint)constraint.get(i);
                if(!c.reduce) {
                    a[j]=c.generic;
                    unk[j]=c.unknown;
                    j++;
                    k++;
                }
            }
            a=solve(a,unk,k);
            for(int i=0;i<a.length;i++) {
                UnivariatePolynomial p=(UnivariatePolynomial)fact.valueof(a[i]);
                if(p.degree()==1) return p;
            }
            return null;
        } else {
            for(int i=0;i<n;i++) {
                Constraint c=(Constraint)constraint.get(i);
                a[i]=c.generic;
                unk[i]=c.unknown;
            }
            a=solve(a,unk,n);
            return (UnivariatePolynomial)fact.valueof(a[0]);
        }
    }

    Generic[] solve(Generic generic[], Variable unknown[], int n) {
        Variable unk[]=Basis.augmentUnknown(unknown,generic);
        return Basis.compute(generic,unk,Monomial.kthElimination(n)).elements();
    }

    void process(Constraint co) {
        int n1=0;
        int n2=0;
        constraint.add(co);
        do {
            n1=n2;
            n2=constraint.size();
            for(int i=n1;i<n2;i++) {
                co=(Constraint)constraint.get(i);
                subProcess(co);
            }
        } while(n1<n2);
    }

    void subProcess(Constraint co) {
        Variable va[]=co.generic.variables();
        for(int i=0;i<va.length;i++) {
            Variable v=va[i];
            if(constraint.contains(new Constraint(v))) continue;
            co=null;
            if(v instanceof Frac) {
                Generic g[]=((Frac)v).parameters();
                co=new Constraint(v,v.expressionValue().multiply(g[1]).subtract(g[0]),false);
            } else if(v instanceof Sqrt) {
                Generic g[]=((Sqrt)v).parameters();
                if(linear) co=linearConstraint(v);
                if(co==null) co=new Constraint(v,v.expressionValue().pow(2).subtract(g[0]),true);
            } else if(v instanceof Cubic) {
                Generic g[]=((Cubic)v).parameters();
                if(linear) co=linearConstraint(v);
                if(co==null) co=new Constraint(v,v.expressionValue().pow(3).subtract(g[0]),true);
            } else if(v instanceof Pow) {
                try {
                    Root r=((Pow)v).rootValue();
                    int d=r.degree();
                    Generic g[]=r.parameters();
                    if(linear) co=linearConstraint(v);
                    if(co==null) co=new Constraint(v,v.expressionValue().pow(d).subtract(g[0].negate()),d>1);
                } catch (NotRootException e) {
                    co=linearConstraint(v);
                }
            } else if(v instanceof Root) {
                try {
                    Root r=(Root)v;
                    int d=r.degree();
                    int n=r.subscript().integerValue().intValue();
                    Generic g[]=r.parameters();
                    if(linear) co=linearConstraint(v);
                    if(co==null) co=new Constraint(v,Root.sigma(g,d-n).multiply(JSCLInteger.valueOf(-1).pow(d-n)).multiply(g[d]).subtract(g[n]),d>1);
                } catch (NotIntegerException e) {
                    co=linearConstraint(v);
                }
            } else co=linearConstraint(v);
            if(co!=null) constraint.add(co);
        }
    }

    Constraint linearConstraint(Variable v) {
        Generic s;
        Object o=cache.get(v);
        if(o!=null) s=(Generic)o;
        else {
            s=v.simplify();
            cache.put(v,s);
        }
        Generic a=v.expressionValue().subtract(s);
        if(a.signum()!=0) return new Constraint(v,a,false);
        else return null;
    }

    Generic getValue() {
        return result;
    }
}

class Constraint {
    Variable unknown;
    Generic generic;
    boolean reduce;

    Constraint(Variable unknown, Generic generic, boolean reduce) {
        this.unknown=unknown;
        this.generic=generic;
        this.reduce=reduce;
    }

    Constraint(Variable unknown) {
        this(unknown,null,false);
    }

    public boolean equals(Object obj) {
        return unknown.compareTo(((Constraint)obj).unknown)==0;
    }
}
