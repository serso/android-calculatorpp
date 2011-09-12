package jscl.math;

import java.math.BigInteger;
import jscl.mathml.MathML;

public final class JSCLInteger extends Generic {
    public static final JSCLInteger factory=new JSCLInteger(BigInteger.valueOf(0));
    final BigInteger content;

    public JSCLInteger(BigInteger content) {
        this.content=content;
    }

    public BigInteger content() {
        return content;
    }

    public JSCLInteger add(JSCLInteger integer) {
        return new JSCLInteger(content.add(integer.content));
    }

    public Generic add(Generic generic) {
        if(generic instanceof JSCLInteger) {
            return add((JSCLInteger)generic);
        } else {
            return generic.valueof(this).add(generic);
        }
    }

    public JSCLInteger subtract(JSCLInteger integer) {
        return new JSCLInteger(content.subtract(integer.content));
    }

    public Generic subtract(Generic generic) {
        if(generic instanceof JSCLInteger) {
            return subtract((JSCLInteger)generic);
        } else {
            return generic.valueof(this).subtract(generic);
        }
    }

    public JSCLInteger multiply(JSCLInteger integer) {
        return new JSCLInteger(content.multiply(integer.content));
    }

    public Generic multiply(Generic generic) {
        if(generic instanceof JSCLInteger) {
            return multiply((JSCLInteger)generic);
        } else {
            return generic.multiply(this);
        }
    }

    public JSCLInteger divide(JSCLInteger integer) throws ArithmeticException {
        JSCLInteger e[]=divideAndRemainder(integer);
        if(e[1].signum()==0) return e[0];
        else throw new NotDivisibleException();
    }

    public Generic divide(Generic generic) throws ArithmeticException {
        if(generic instanceof JSCLInteger) {
            return divide((JSCLInteger)generic);
        } else {
            return generic.valueof(this).divide(generic);
        }
    }

    public JSCLInteger[] divideAndRemainder(JSCLInteger integer) throws ArithmeticException {
        BigInteger b[]=content.divideAndRemainder(integer.content);
        return new JSCLInteger[] {new JSCLInteger(b[0]),new JSCLInteger(b[1])};
    }

    public Generic[] divideAndRemainder(Generic generic) throws ArithmeticException {
        if(generic instanceof JSCLInteger) {
            return divideAndRemainder((JSCLInteger)generic);
        } else {
            return generic.valueof(this).divideAndRemainder(generic);
        }
    }

    public JSCLInteger remainder(JSCLInteger integer) throws ArithmeticException {
        return new JSCLInteger(content.remainder(integer.content));
    }

    public Generic remainder(Generic generic) throws ArithmeticException {
        if(generic instanceof JSCLInteger) {
            return remainder((JSCLInteger)generic);
        } else {
            return generic.valueof(this).remainder(generic);
        }
    }

    public JSCLInteger gcd(JSCLInteger integer) {
        return new JSCLInteger(content.gcd(integer.content));
    }

    public Generic gcd(Generic generic) {
        if(generic instanceof JSCLInteger) {
            return gcd((JSCLInteger)generic);
        } else {
            return generic.valueof(this).gcd(generic);
        }
    }

    public Generic gcd() {
        return new JSCLInteger(BigInteger.valueOf(signum()));
    }

    public Generic pow(int exponent) {
        return new JSCLInteger(content.pow(exponent));
    }

    public Generic negate() {
        return new JSCLInteger(content.negate());
    }

    public int signum() {
        return content.signum();
    }

    public int degree() {
        return 0;
    }

    public JSCLInteger mod(JSCLInteger integer) {
        return new JSCLInteger(content.mod(integer.content));
    }

    public JSCLInteger modPow(JSCLInteger exponent, JSCLInteger integer) {
        return new JSCLInteger(content.modPow(exponent.content,integer.content));
    }

    public JSCLInteger modInverse(JSCLInteger integer) {
        return new JSCLInteger(content.modInverse(integer.content));
    }

    public JSCLInteger phi() {
        if(signum()==0) return this;
        Generic a=factorize();
        Generic p[]=a.productValue();
        Generic s=JSCLInteger.valueOf(1);
        for(int i=0;i<p.length;i++) {
            Power o=p[i].powerValue();
            Generic q=o.value(true);
            int c=o.exponent();
            s=s.multiply(q.subtract(JSCLInteger.valueOf(1)).multiply(q.pow(c-1)));
        }
        return s.integerValue();
    }

    public JSCLInteger[] primitiveRoots() {
        JSCLInteger phi=phi();
        Generic a=phi.factorize();
        Generic p[]=a.productValue();
        JSCLInteger d[]=new JSCLInteger[p.length];
        for(int i=0;i<p.length;i++) {
            d[i]=phi.divide(p[i].powerValue().value(true).integerValue());
        }
        int k=0;
        JSCLInteger n=this;
        JSCLInteger m=JSCLInteger.valueOf(1);
        JSCLInteger r[]=new JSCLInteger[phi.phi().intValue()];
        while(m.compareTo(n)<0) {
            boolean b=m.gcd(n).compareTo(JSCLInteger.valueOf(1))==0;
            for(int i=0;i<d.length;i++) {
                b=b && m.modPow(d[i],n).compareTo(JSCLInteger.valueOf(1))>0;
            }
            if(b) r[k++]=m;
            m=m.add(JSCLInteger.valueOf(1));
        }
        return k>0?r:new JSCLInteger[0];
    }

    public JSCLInteger sqrt() {
        return nthrt(2);
    }

    public JSCLInteger nthrt(int n) {
//      return JSCLInteger.valueOf((int)Math.pow((double)intValue(),1./n));
        if(signum()==0) return JSCLInteger.valueOf(0);
        else if(signum()<0) {
            if(n%2==0) throw new ArithmeticException();
            else return (JSCLInteger)((JSCLInteger)negate()).nthrt(n).negate();
        } else {
            Generic x0;
            Generic x=this;
            do {
                x0=x;
                x=divideAndRemainder(x.pow(n-1))[0].add(x.multiply(JSCLInteger.valueOf(n-1))).divideAndRemainder(JSCLInteger.valueOf(n))[0];
            } while(x.compareTo(x0)<0);
            return x0.integerValue();
        }
    }

    public Generic antiderivative(Variable variable) throws NotIntegrableException {
        return multiply(variable.expressionValue());
    }

    public Generic derivative(Variable variable) {
        return JSCLInteger.valueOf(0);
    }

    public Generic substitute(Variable variable, Generic generic) {
        return this;
    }

    public Generic expand() {
        return this;
    }

    public Generic factorize() {
        return Factorization.compute(this);
    }

    public Generic elementary() {
        return this;
    }

    public Generic simplify() {
        return this;
    }

    public Generic numeric() {
        return new NumericWrapper(this);
    }

    public Generic valueof(Generic generic) {
        return new JSCLInteger(((JSCLInteger)generic).content);
    }

    public Generic[] sumValue() {
        if(content.signum()==0) return new Generic[0];
        else return new Generic[] {this};
    }

    public Generic[] productValue() throws NotProductException {
        if(content.compareTo(BigInteger.valueOf(1))==0) return new Generic[0];
        else return new Generic[] {this};
    }

    public Power powerValue() throws NotPowerException {
        if(content.signum()<0) throw new NotPowerException();
        else return new Power(this,1);
    }

    public Expression expressionValue() throws NotExpressionException {
        return Expression.valueOf(this);
    }

    public JSCLInteger integerValue() throws NotIntegerException {
        return this;
    }

    public Variable variableValue() throws NotVariableException {
        throw new NotVariableException();
    }

    public Variable[] variables() {
        return new Variable[0];
    }

    public boolean isPolynomial(Variable variable) {
        return true;
    }

    public boolean isConstant(Variable variable) {
        return true;
    }

    public int intValue() {
        return content.intValue();
    }

    public int compareTo(JSCLInteger integer) {
        return content.compareTo(integer.content);
    }

    public int compareTo(Generic generic) {
        if(generic instanceof JSCLInteger) {
            return compareTo((JSCLInteger)generic);
        } else {
            return generic.valueof(this).compareTo(generic);
        }
    }

    private static final JSCLInteger ZERO=new JSCLInteger(BigInteger.valueOf(0));
    private static final JSCLInteger ONE=new JSCLInteger(BigInteger.valueOf(1));

    public static JSCLInteger valueOf(long val) {
        switch((int)val) {
        case 0:
            return ZERO;
        case 1:
            return ONE;
        default:
            return new JSCLInteger(BigInteger.valueOf(val));
        }
    }

    public static JSCLInteger valueOf(String str) {
        return new JSCLInteger(new BigInteger(str));
    }

    public String toString() {
        return content.toString();
    }

    public String toJava() {
        return "JSCLDouble.valueOf("+content+")";
    }

    public void toMathML(MathML element, Object data) {
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) bodyToMathML(element);
        else {
            MathML e1=element.element("msup");
            bodyToMathML(e1);
            MathML e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    void bodyToMathML(MathML element) {
        MathML e1=element.element("mn");
        e1.appendChild(element.text(String.valueOf(content)));
        element.appendChild(e1);
    }
}
