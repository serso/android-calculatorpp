package jscl.math;

import jscl.JsclMathEngine;
import jscl.math.function.Constant;
import jscl.mathml.MathML;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;

public final class JsclInteger extends Generic {

    public static final JsclInteger factory = new JsclInteger(BigInteger.valueOf(0));
    public static final JsclInteger ZERO = new JsclInteger(BigInteger.valueOf(0));
    public static final JsclInteger ONE = new JsclInteger(BigInteger.valueOf(1));
    private final BigInteger content;

    public JsclInteger(BigInteger content) {
        this.content = content;
    }

    public static JsclInteger valueOf(long val) {
        switch ((int) val) {
            case 0:
                return ZERO;
            case 1:
                return ONE;
            default:
                return new JsclInteger(BigInteger.valueOf(val));
        }
    }

    public static JsclInteger valueOf(String str) {
        return new JsclInteger(new BigInteger(str));
    }

    public BigInteger content() {
        return content;
    }

    public JsclInteger add(JsclInteger integer) {
        return new JsclInteger(content.add(integer.content));
    }

    @Nonnull
    public Generic add(@Nonnull Generic that) {
        if (isZero()) {
            return that;
        }
        if (that instanceof JsclInteger) {
            return add((JsclInteger) that);
        } else {
            return that.valueOf(this).add(that);
        }
    }

    private boolean isZero() {
        return content.equals(ZERO.content);
    }

    public JsclInteger subtract(JsclInteger that) {
        if(isZero()) {
            return that.negate();
        }
        return new JsclInteger(content.subtract(that.content));
    }

    @Nonnull
    public Generic subtract(@Nonnull Generic that) {
        if (that instanceof JsclInteger) {
            return subtract((JsclInteger) that);
        } else {
            return that.valueOf(this).subtract(that);
        }
    }

    public JsclInteger multiply(JsclInteger integer) {
        return new JsclInteger(content.multiply(integer.content));
    }

    @Nonnull
    public Generic multiply(@Nonnull Generic that) {
        if (isOne()) {
            return that;
        }
        if (that instanceof JsclInteger) {
            return multiply((JsclInteger) that);
        } else {
            return that.multiply(this);
        }
    }

    private boolean isOne() {
        return content.equals(ONE.content);
    }

    public JsclInteger divide(@Nonnull JsclInteger that) {
        if (isZero()) {
            return ZERO;
        }
        JsclInteger e[] = divideAndRemainder(that);
        if (e[1].signum() == 0) {
            return e[0];
        } else {
            throw new NotDivisibleException();
        }
    }

    @Nonnull
    public Generic divide(@Nonnull Generic that) throws NotDivisibleException {
        if (that instanceof JsclInteger) {
            return divide((JsclInteger) that);
        } else {
            return that.valueOf(this).divide(that);
        }
    }

    @Nonnull
    private JsclInteger[] divideAndRemainder(@Nonnull JsclInteger that) {
        try {
            final BigInteger result[] = content.divideAndRemainder(that.content);
            return new JsclInteger[]{new JsclInteger(result[0]), new JsclInteger(result[1])};
        } catch (ArithmeticException e) {
            throw new NotDivisibleException();
        }
    }

    public Generic[] divideAndRemainder(@Nonnull Generic that) {
        if (that instanceof JsclInteger) {
            return divideAndRemainder((JsclInteger) that);
        } else {
            return that.valueOf(this).divideAndRemainder(that);
        }
    }

    public JsclInteger remainder(JsclInteger integer) throws ArithmeticException {
        return new JsclInteger(content.remainder(integer.content));
    }

    public Generic remainder(Generic generic) throws ArithmeticException {
        if (generic instanceof JsclInteger) {
            return remainder((JsclInteger) generic);
        } else {
            return generic.valueOf(this).remainder(generic);
        }
    }

    @Nonnull
    public JsclInteger gcd(@Nonnull JsclInteger integer) {
        return new JsclInteger(content.gcd(integer.content));
    }

    public Generic gcd(@Nonnull Generic generic) {
        if (generic instanceof JsclInteger) {
            return gcd((JsclInteger) generic);
        } else {
            return generic.valueOf(this).gcd(generic);
        }
    }

    @Nonnull
    public Generic gcd() {
        return new JsclInteger(BigInteger.valueOf(signum()));
    }

    public Generic pow(int exponent) {
        return new JsclInteger(content.pow(exponent));
    }

    public JsclInteger negate() {
        return new JsclInteger(content.negate());
    }

    public int signum() {
        return content.signum();
    }

    public int degree() {
        return 0;
    }

    public JsclInteger mod(JsclInteger that) {
        return new JsclInteger(content.mod(that.content));
    }

    public JsclInteger modPow(JsclInteger exponent, JsclInteger integer) {
        return new JsclInteger(content.modPow(exponent.content, integer.content));
    }

    public JsclInteger modInverse(JsclInteger integer) {
        return new JsclInteger(content.modInverse(integer.content));
    }

    public JsclInteger phi() {
        if (signum() == 0) return this;
        Generic a = factorize();
        Generic p[] = a.productValue();
        Generic s = JsclInteger.valueOf(1);
        for (int i = 0; i < p.length; i++) {
            Power o = p[i].powerValue();
            Generic q = o.value(true);
            int c = o.exponent();
            s = s.multiply(q.subtract(JsclInteger.valueOf(1)).multiply(q.pow(c - 1)));
        }
        return s.integerValue();
    }

    public JsclInteger[] primitiveRoots() {
        JsclInteger phi = phi();
        Generic a = phi.factorize();
        Generic p[] = a.productValue();
        JsclInteger d[] = new JsclInteger[p.length];
        for (int i = 0; i < p.length; i++) {
            d[i] = phi.divide(p[i].powerValue().value(true).integerValue());
        }
        int k = 0;
        JsclInteger n = this;
        JsclInteger m = JsclInteger.valueOf(1);
        JsclInteger r[] = new JsclInteger[phi.phi().intValue()];
        while (m.compareTo(n) < 0) {
            boolean b = m.gcd(n).compareTo(JsclInteger.valueOf(1)) == 0;
            for (int i = 0; i < d.length; i++) {
                b = b && m.modPow(d[i], n).compareTo(JsclInteger.valueOf(1)) > 0;
            }
            if (b) r[k++] = m;
            m = m.add(JsclInteger.valueOf(1));
        }
        return k > 0 ? r : new JsclInteger[0];
    }

    public JsclInteger sqrt() {
        return nthrt(2);
    }

    public JsclInteger nthrt(int n) {
//      return JsclInteger.valueOf((int)Math.pow((double)intValue(),1./n));
        if (signum() == 0) {
            return JsclInteger.valueOf(0);
        } else if (signum() < 0) {
            if (n % 2 == 0) {
                throw new ArithmeticException("Could not calculate root of negative argument: " + this + " of odd order: " + n);
            } else {
                return (JsclInteger) ((JsclInteger) negate()).nthrt(n).negate();
            }
        } else {
            Generic x0;
            Generic x = this;
            do {
                x0 = x;
                x = divideAndRemainder(x.pow(n - 1))[0].add(x.multiply(JsclInteger.valueOf(n - 1))).divideAndRemainder(JsclInteger.valueOf(n))[0];
            } while (x.compareTo(x0) < 0);
            return x0.integerValue();
        }
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

    public Generic valueOf(Generic generic) {
        return new JsclInteger(((JsclInteger) generic).content);
    }

    public Generic[] sumValue() {
        if (content.signum() == 0) return new Generic[0];
        else return new Generic[]{this};
    }

    public Generic[] productValue() throws NotProductException {
        if (content.compareTo(BigInteger.valueOf(1)) == 0) return new Generic[0];
        else return new Generic[]{this};
    }

    public Power powerValue() throws NotPowerException {
        if (content.signum() < 0) throw new NotPowerException();
        else return new Power(this, 1);
    }

    public Expression expressionValue() throws NotExpressionException {
        return Expression.valueOf(this);
    }

    public JsclInteger integerValue() throws NotIntegerException {
        return this;
    }

    @Override
    public boolean isInteger() {
        return true;
    }

    public Variable variableValue() throws NotVariableException {
        throw new NotVariableException();
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

    public int intValue() {
        return content.intValue();
    }

    public int compareTo(JsclInteger integer) {
        return content.compareTo(integer.content);
    }

    public int compareTo(Generic generic) {
        if (generic instanceof JsclInteger) {
            return compareTo((JsclInteger) generic);
        } else {
            return generic.valueOf(this).compareTo(generic);
        }
    }

    public String toString() {
        // todo serso: actually better way is to provide custom format() method for integers and not to convert integer to double
        return JsclMathEngine.getInstance().format(this.content.doubleValue());
    }

    public String toJava() {
        return "JsclDouble.valueOf(" + content + ")";
    }

    public void toMathML(MathML element, @Nullable Object data) {
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
        MathML e1 = element.element("mn");
        e1.appendChild(element.text(String.valueOf(content)));
        element.appendChild(e1);
    }

    @Override
    public BigInteger toBigInteger() {
        return content;
    }

    @Override
    public double doubleValue() throws NotDoubleException {
        return content.doubleValue();
    }
}
