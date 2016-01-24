package jscl.math.function;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.math.Generic;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.math.polynomial.Polynomial;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;

public abstract class Trigonometric extends Function {

    public Trigonometric(String name, Generic parameter[]) {
        super(name, parameter);
    }

    public Generic antiDerivative(@Nonnull Variable variable) throws NotIntegrableException {
        if (JsclMathEngine.getInstance().getAngleUnits() != AngleUnit.rad) {
            throw new NotIntegrableException(Messages.msg_20, getName());
        }

        final Generic parameter = parameters[0];
        if (parameter.isPolynomial(variable)) {
            final Polynomial polynomial = Polynomial.factory(variable).valueOf(parameter);
            if (polynomial.degree() == 1) {
                final Generic elements[] = polynomial.elements();
                return new Inverse(elements[1]).selfExpand().multiply(antiDerivative(0));
            } else {
                throw new NotIntegrableException(this);
            }
        } else {
            throw new NotIntegrableException(this);
        }
    }

    public Generic identity() {
//      Generic a[]=parameter[0].sumValue();
//      if(a.length>1) {
//          Generic s=JsclInteger.valueOf(0);
//          for(int i=1;i<a.length;i++) s=s.add(a[i]);
//          return identity(a[0],s);
//      }
//      Generic n[]=Frac.separateCoefficient(parameter[0]);
//      if(n[0].compareTo(JsclInteger.valueOf(1))==0);
//      else {
//          Generic s=new Frac(n[2],n[1]).evaluateSimplify();
//          return identity(s,n[0].subtract(JsclInteger.valueOf(1)).multiply(s));
//      }
        return expressionValue();
    }

    public abstract Generic identity(Generic a, Generic b);
}
