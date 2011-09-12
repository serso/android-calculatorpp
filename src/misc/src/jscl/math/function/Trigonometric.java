package jscl.math.function;

import jscl.math.Generic;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.math.polynomial.Polynomial;

public abstract class Trigonometric extends Function {
    public Trigonometric(String name, Generic parameter[]) {
        super(name,parameter);
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        Generic s=parameter[0];
        if(s.isPolynomial(variable)) {
            Polynomial p=Polynomial.factory(variable).valueof(s);
            if(p.degree()==1) {
                Generic a[]=p.elements();
                return new Inv(a[1]).evaluate().multiply(antiderivative(0));
            } else throw new NotIntegrableException();
        } else throw new NotIntegrableException();
    }

    public Generic identity() {
//      Generic a[]=parameter[0].sumValue();
//      if(a.length>1) {
//          Generic s=JSCLInteger.valueOf(0);
//          for(int i=1;i<a.length;i++) s=s.add(a[i]);
//          return identity(a[0],s);
//      }
//      Generic n[]=Frac.separateCoefficient(parameter[0]);
//      if(n[0].compareTo(JSCLInteger.valueOf(1))==0);
//      else {
//          Generic s=new Frac(n[2],n[1]).evalsimp();
//          return identity(s,n[0].subtract(JSCLInteger.valueOf(1)).multiply(s));
//      }
        return expressionValue();
    }

    public abstract Generic identity(Generic a, Generic b);
}
