package jscl.math;

import jscl.math.function.Constant;
import jscl.math.function.Fraction;
import jscl.math.function.Inverse;
import jscl.mathml.MathML;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

public final class Rational extends Generic implements Field {

    public static final Rational factory = new Rational(BigInteger.valueOf(0), BigInteger.valueOf(1));

    final BigInteger numerator;
    final BigInteger denominator;

    public Rational(BigInteger numerator, BigInteger denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    static BigInteger scm(BigInteger b1, BigInteger b2) {
        return b1.multiply(b2).divide(b1.gcd(b2));
    }

    public BigInteger numerator() {
        return numerator;
    }

    public BigInteger denominator() {
        return denominator;
    }

    public Rational add(Rational rational) {
        BigInteger gcd = denominator.gcd(rational.denominator);
        BigInteger c = denominator.divide(gcd);
        BigInteger c2 = rational.denominator.divide(gcd);
        return new Rational(numerator.multiply(c2).add(rational.numerator.multiply(c)), denominator.multiply(c2)).reduce();
    }

    Rational reduce() {
        BigInteger gcd = numerator.gcd(denominator);
        if (gcd.signum() != denominator.signum()) gcd = gcd.negate();
        return gcd.signum() == 0 ? this : new Rational(numerator.divide(gcd), denominator.divide(gcd));
    }

    @Nonnull
    public Generic add(@Nonnull Generic that) {
        if (that instanceof Rational) {
            return add((Rational) that);
        } else if (that instanceof JsclInteger) {
            return add(valueOf(that));
        } else {
            return that.valueOf(this).add(that);
        }
    }

    public Rational multiply(Rational rational) {
        BigInteger gcd = numerator.gcd(rational.denominator);
        BigInteger gcd2 = denominator.gcd(rational.numerator);
        return new Rational(numerator.divide(gcd).multiply(rational.numerator.divide(gcd2)), denominator.divide(gcd2).multiply(rational.denominator.divide(gcd)));
    }

    @Nonnull
    public Generic multiply(@Nonnull Generic that) {
        if (that instanceof Rational) {
            return multiply((Rational) that);
        } else if (that instanceof JsclInteger) {
            return multiply(valueOf(that));
        } else {
            return that.multiply(this);
        }
    }

    @Nonnull
    public Generic divide(@Nonnull Generic that) throws NotDivisibleException {
        if (that instanceof Rational) {
            return multiply(that.inverse());
        } else if (that instanceof JsclInteger) {
            return divide(valueOf(that));
        } else {
            return that.valueOf(this).divide(that);
        }
    }

    public Generic inverse() {
        if (signum() < 0) return new Rational(denominator.negate(), numerator.negate());
        else return new Rational(denominator, numerator);
    }

    public Rational gcd(Rational rational) {
        return new Rational(numerator.gcd(rational.numerator), scm(denominator, rational.denominator));
    }

    public Generic gcd(@Nonnull Generic generic) {
        if (generic instanceof Rational) {
            return gcd((Rational) generic);
        } else if (generic instanceof JsclInteger) {
            return gcd(valueOf(generic));
        } else {
            return generic.valueOf(this).gcd(generic);
        }
    }

    @Nonnull
    public Generic gcd() {
        return null;
    }

    public Generic pow(int exponent) {
        return null;
    }

    public Generic negate() {
        return new Rational(numerator.negate(), denominator);
    }

    public int signum() {
        return numerator.signum();
    }

    public int degree() {
        return 0;
    }

    public Generic antiDerivative(@Nonnull Variable variable) throws NotIntegrableException {
        return multiply(variable.expressionValue());
    }

    public Generic derivative(@Nonnull Variable variable) {
        return JsclInteger.valueOf(0);
    }

    public Generic substitute(@Nonnull Variable variable, Generic generic) {
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

    public Generic valueOf(Generic generic) {
        if (generic instanceof Rational) {
            Rational r = (Rational) generic;
            return new Rational(r.numerator, r.denominator);
        } else if (generic instanceof Expression) {
            boolean sign = generic.signum() < 0;
            Generic g[] = ((Fraction) (sign ? generic.negate() : generic).variableValue()).getParameters();
            JsclInteger numerator = (JsclInteger) (sign ? g[0].negate() : g[0]);
            JsclInteger denominator = (JsclInteger) g[1];
            return new Rational(numerator.content(), denominator.content());
        } else {
            JsclInteger en = (JsclInteger) generic;
            return new Rational(en.content(), BigInteger.valueOf(1));
        }
    }

    public Generic[] sumValue() {
        try {
            if (integerValue().signum() == 0) return new Generic[0];
            else return new Generic[]{this};
        } catch (NotIntegerException e) {
            return new Generic[]{this};
        }
    }

    public Generic[] productValue() throws NotProductException {
        try {
            if (integerValue().compareTo(JsclInteger.valueOf(1)) == 0) return new Generic[0];
            else return new Generic[]{this};
        } catch (NotIntegerException e) {
            return new Generic[]{this};
        }
    }

    public Power powerValue() throws NotPowerException {
        return new Power(this, 1);
    }

    public Expression expressionValue() throws NotExpressionException {
        return Expression.valueOf(this);
    }

    public JsclInteger integerValue() throws NotIntegerException {
        if (denominator.compareTo(BigInteger.ONE) == 0) {
            return new JsclInteger(numerator);
        } else {
            throw NotIntegerException.get();
        }
    }

    @Override
    public double doubleValue() throws NotDoubleException {
        return numerator.doubleValue() / denominator.doubleValue();
    }

    @Override
    public boolean isInteger() {
        try {
            integerValue();
            return true;
        } catch (NotIntegerException e) {
            return false;
        }
    }

    public Variable variableValue() throws NotVariableException {
        try {
            integerValue();
            throw new NotVariableException();
        } catch (NotIntegerException e) {
            if (numerator.compareTo(BigInteger.valueOf(1)) == 0) return new Inverse(new JsclInteger(denominator));
            else return new Fraction(new JsclInteger(numerator), new JsclInteger(denominator));
        }
    }

    public Variable[] variables() {
        return new Variable[0];
    }

    public boolean isPolynomial(@Nonnull Variable variable) {
        return true;
    }

    public boolean isConstant(@Nonnull Variable variable) {
        return true;
    }

    public int compareTo(Rational rational) {
        int c = denominator.compareTo(rational.denominator);
        if (c < 0) return -1;
        else if (c > 0) return 1;
        else return numerator.compareTo(rational.numerator);
    }

    public int compareTo(Generic generic) {
        if (generic instanceof Rational) {
            return compareTo((Rational) generic);
        } else if (generic instanceof JsclInteger) {
            return compareTo(valueOf(generic));
        } else {
            return generic.valueOf(this).compareTo(generic);
        }
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();
        try {
            result.append(integerValue());
        } catch (NotIntegerException e) {
            result.append(numerator);
            result.append("/");
            result.append(denominator);
        }
        return result.toString();
    }

    public String toJava() {
        return "JsclDouble.valueOf(" + numerator + "/" + denominator + ")";
    }

    public void toMathML(MathML element, Object data) {
        int exponent = data instanceof Integer ? (Integer) data : 1;
        if (exponent == 1) bodyToMathML(element);
        else {
            MathML e1 = element.element("msup");
            bodyToMathML(e1);
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    @Nonnull
    @Override
    public Set<? extends Constant> getConstants() {
        return Collections.emptySet();
    }

    void bodyToMathML(MathML element) {
        try {
            MathML e1 = element.element("mn");
            e1.appendChild(element.text(String.valueOf(integerValue())));
            element.appendChild(e1);
        } catch (NotIntegerException e) {
            MathML e1 = element.element("mfrac");
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(numerator)));
            e1.appendChild(e2);
            e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(denominator)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    @Override
    public BigInteger toBigInteger() {
        try {
            return integerValue().toBigInteger();
        } catch (NotIntegerException e) {
            return null;
        }
    }
}
