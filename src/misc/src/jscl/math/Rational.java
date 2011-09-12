package jscl.math;

import java.math.BigInteger;
import jscl.math.function.Frac;
import jscl.math.function.Inv;
import jscl.mathml.MathML;

public final class Rational extends Generic implements Field {
    public static final Rational factory=new Rational(BigInteger.valueOf(0),BigInteger.valueOf(1));
    final BigInteger numerator;
    final BigInteger denominator;

    public Rational(BigInteger numerator, BigInteger denominator) {
        this.numerator=numerator;
        this.denominator=denominator;
    }

    public BigInteger numerator() {
        return numerator;
    }

    public BigInteger denominator() {
        return denominator;
    }

    public Rational add(Rational rational) {
        BigInteger gcd=denominator.gcd(rational.denominator);
        BigInteger c=denominator.divide(gcd);
        BigInteger c2=rational.denominator.divide(gcd);
        return new Rational(numerator.multiply(c2).add(rational.numerator.multiply(c)),denominator.multiply(c2)).reduce();
    }

    Rational reduce() {
        BigInteger gcd=numerator.gcd(denominator);
        if(gcd.signum()!=denominator.signum()) gcd=gcd.negate();
        return gcd.signum()==0?this:new Rational(numerator.divide(gcd),denominator.divide(gcd));
    }

    public Generic add(Generic generic) {
        if(generic instanceof Rational) {
            return add((Rational)generic);
        } else if(generic instanceof JSCLInteger) {
            return add(valueof(generic));
        } else {
            return generic.valueof(this).add(generic);
        }
    }

    public Rational multiply(Rational rational) {
        BigInteger gcd=numerator.gcd(rational.denominator);
        BigInteger gcd2=denominator.gcd(rational.numerator);
        return new Rational(numerator.divide(gcd).multiply(rational.numerator.divide(gcd2)),denominator.divide(gcd2).multiply(rational.denominator.divide(gcd)));
    }

    public Generic multiply(Generic generic) {
        if(generic instanceof Rational) {
            return multiply((Rational)generic);
        } else if(generic instanceof JSCLInteger) {
            return multiply(valueof(generic));
        } else {
            return generic.multiply(this);
        }
    }

    public Generic divide(Generic generic) throws ArithmeticException {
        if(generic instanceof Rational) {
            return multiply(generic.inverse());
        } else if(generic instanceof JSCLInteger) {
            return divide(valueof(generic));
        } else {
            return generic.valueof(this).divide(generic);
        }
    }

    public Generic inverse() {
        if(signum()<0) return new Rational(denominator.negate(),numerator.negate());
        else return new Rational(denominator,numerator);
    }

    public Rational gcd(Rational rational) {
        return new Rational(numerator.gcd(rational.numerator),scm(denominator,rational.denominator));
    }

    public Generic gcd(Generic generic) {
        if(generic instanceof Rational) {
            return gcd((Rational)generic);
        } else if(generic instanceof JSCLInteger) {
            return gcd(valueof(generic));
        } else {
            return generic.valueof(this).gcd(generic);
        }
    }

    static BigInteger scm(BigInteger b1, BigInteger b2) {
        return b1.multiply(b2).divide(b1.gcd(b2));
    }

    public Generic gcd() {
        return null;
    }

    public Generic pow(int exponent) {
        return null;
    }

    public Generic negate() {
        return new Rational(numerator.negate(),denominator);
    }

    public int signum() {
        return numerator.signum();
    }

    public int degree() {
        return 0;
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
        return expressionValue().factorize();
    }

    public Generic elementary() {
        return this;
    }

    public Generic simplify() {
        return reduce();
    }

    public Generic numeric() {
        return new NumericWrapper(this);
    }

    public Generic valueof(Generic generic) {
        if(generic instanceof Rational) {
            Rational r=(Rational)generic;
            return new Rational(r.numerator,r.denominator);
        } else if(generic instanceof Expression) {
            boolean sign=generic.signum()<0;
            Generic g[]=((Frac)(sign?generic.negate():generic).variableValue()).parameters();
            JSCLInteger numerator=(JSCLInteger)(sign?g[0].negate():g[0]);
            JSCLInteger denominator=(JSCLInteger)g[1];
            return new Rational(numerator.content(),denominator.content());
        } else {
            JSCLInteger en=(JSCLInteger)generic;
            return new Rational(en.content(),BigInteger.valueOf(1));
        }
    }

    public Generic[] sumValue() {
        try {
            if(integerValue().signum()==0) return new Generic[0];
            else return new Generic[] {this};
        } catch (NotIntegerException e) {
            return new Generic[] {this};
        }
    }

    public Generic[] productValue() throws NotProductException {
        try {
            if(integerValue().compareTo(JSCLInteger.valueOf(1))==0) return new Generic[0];
            else return new Generic[] {this};
        } catch (NotIntegerException e) {
            return new Generic[] {this};
        }
    }

    public Power powerValue() throws NotPowerException {
        return new Power(this,1);
    }

    public Expression expressionValue() throws NotExpressionException {
        return Expression.valueOf(this);
    }

    public JSCLInteger integerValue() throws NotIntegerException {
        if(denominator.compareTo(BigInteger.valueOf(1))==0) return new JSCLInteger(numerator);
        else throw new NotIntegerException();
    }

    public Variable variableValue() throws NotVariableException {
        try {
            integerValue();
            throw new NotVariableException();
        } catch (NotIntegerException e) {
            if(numerator.compareTo(BigInteger.valueOf(1))==0) return new Inv(new JSCLInteger(denominator));
            else return new Frac(new JSCLInteger(numerator), new JSCLInteger(denominator));
        }
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

    public int compareTo(Rational rational) {
        int c=denominator.compareTo(rational.denominator);
        if(c<0) return -1;
        else if(c>0) return 1;
        else return numerator.compareTo(rational.numerator);
    }

    public int compareTo(Generic generic) {
        if(generic instanceof Rational) {
            return compareTo((Rational)generic);
        } else if(generic instanceof JSCLInteger) {
            return compareTo(valueof(generic));
        } else {
            return generic.valueof(this).compareTo(generic);
        }
    }

    public String toString() {
        StringBuffer buffer=new StringBuffer();
        try {
            buffer.append(integerValue());
        } catch (NotIntegerException e) {
            buffer.append(numerator);
            buffer.append("/");
            buffer.append(denominator);
        }
        return buffer.toString();
    }

    public String toJava() {
        return "JSCLDouble.valueOf("+numerator+"/"+denominator+")";
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
        try {
            MathML e1=element.element("mn");
            e1.appendChild(element.text(String.valueOf(integerValue())));
            element.appendChild(e1);
        } catch (NotIntegerException e) {
            MathML e1=element.element("mfrac");
            MathML e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(numerator)));
            e1.appendChild(e2);
            e2=element.element("mn");
            e2.appendChild(element.text(String.valueOf(denominator)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }
}
