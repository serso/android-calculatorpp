package jscl.math;

import jscl.mathml.MathML;

public abstract class Generic implements Arithmetic, Comparable {
    public abstract Generic add(Generic generic);

    public Generic subtract(Generic generic) {
        return add(generic.negate());
    }

    public abstract Generic multiply(Generic generic);

    public boolean multiple(Generic generic) throws ArithmeticException {
        return remainder(generic).signum()==0;
    }

    public abstract Generic divide(Generic generic) throws ArithmeticException;

    public Arithmetic add(Arithmetic arithmetic) {
        return add((Generic)arithmetic);
    }

    public Arithmetic subtract(Arithmetic arithmetic) {
        return subtract((Generic)arithmetic);
    }

    public Arithmetic multiply(Arithmetic arithmetic) {
        return multiply((Generic)arithmetic);
    }

    public Arithmetic divide(Arithmetic arithmetic) throws ArithmeticException {
        return divide((Generic)arithmetic);
    }

    public Generic[] divideAndRemainder(Generic generic) throws ArithmeticException {
        try {
            return new Generic[] {divide(generic),JSCLInteger.valueOf(0)};
        } catch (NotDivisibleException e) {
            return new Generic[] {JSCLInteger.valueOf(0),this};
        }
    }

    public Generic remainder(Generic generic) throws ArithmeticException {
        return divideAndRemainder(generic)[1];
    }

    public Generic inverse() {
        return JSCLInteger.valueOf(1).divide(this);
    }

    public abstract Generic gcd(Generic generic);

    public Generic scm(Generic generic) {
        return divide(gcd(generic)).multiply(generic);
    }

    public abstract Generic gcd();

    public Generic[] gcdAndNormalize() {
        Generic gcd=gcd();
        if(gcd.signum()==0) return new Generic[] {gcd,this};
        if(gcd.signum()!=signum()) gcd=gcd.negate();
        return new Generic[] {gcd,divide(gcd)};
    }

    public Generic normalize() {
        return gcdAndNormalize()[1];
    }

    public Generic pow(int exponent) {
        Generic a=JSCLInteger.valueOf(1);
        for(int i=0;i<exponent;i++) a=a.multiply(this);
        return a;
    }

    public Generic abs() {
        return signum()<0?negate():this;
    }

    public abstract Generic negate();
    public abstract int signum();
    public abstract int degree();

//    public abstract Generic mod(Generic generic);
//    public abstract Generic modPow(Generic exponent, Generic generic);
//    public abstract Generic modInverse(Generic generic);
//    public abstract boolean isProbablePrime(int certainty);
    public abstract Generic antiderivative(Variable variable) throws NotIntegrableException;
    public abstract Generic derivative(Variable variable);
    public abstract Generic substitute(Variable variable, Generic generic);
    public abstract Generic expand();
    public abstract Generic factorize();
    public abstract Generic elementary();
    public abstract Generic simplify();
    public abstract Generic numeric();
    public abstract Generic valueof(Generic generic);
    public abstract Generic[] sumValue();
    public abstract Generic[] productValue() throws NotProductException;
    public abstract Power powerValue() throws NotPowerException;
    public abstract Expression expressionValue() throws NotExpressionException;
    public abstract JSCLInteger integerValue() throws NotIntegerException;
    public abstract Variable variableValue() throws NotVariableException;
    public abstract Variable[] variables();
    public abstract boolean isPolynomial(Variable variable);
    public abstract boolean isConstant(Variable variable);

    public boolean isIdentity(Variable variable) {
        try {
            return variableValue().isIdentity(variable);
        } catch (NotVariableException e) {
            return false;
        }
    }

    public abstract int compareTo(Generic generic);

    public int compareTo(Object o) {
        return compareTo((Generic)o);
    }

    public boolean equals(Object obj) {
        if(obj instanceof Generic) {
            return compareTo((Generic)obj)==0;
        } else return false;
    }

    public abstract String toJava();

    public String toMathML() {
        MathML document=new MathML("math","-//W3C//DTD MathML 2.0//EN","http://www.w3.org/TR/MathML2/dtd/mathml2.dtd");
        MathML e=document.element("math");
        toMathML(e,null);
        return e.toString();
    }

    public abstract void toMathML(MathML element, Object data);
}
