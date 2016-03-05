package jscl.math.function;

import jscl.JsclMathEngine;
import jscl.math.*;
import jscl.mathml.MathML;
import jscl.util.ArrayComparator;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constant extends Variable {

    public static final int PRIME_CHARS = 3;
    private final int prime;
    private final Generic subscripts[];
    private Object[] hashArray;

    public Constant(String name) {
        this(name, 0, new Generic[0]);
    }

    public Constant(String name, int prime, Generic subscripts[]) {
        super(name);
        this.prime = prime;
        this.subscripts = subscripts;
    }

    static String primeChars(int n) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < n; i++) buffer.append("'");
        return buffer.toString();
    }

    static String underscores(int n) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < n; i++) buffer.append("_");
        return buffer.toString();
    }

    static void primeCharsToMathML(MathML element, int n) {
        MathML e1 = element.element("mo");
        for (int i = 0; i < n; i++) e1.appendChild(element.text("\u2032"));
        element.appendChild(e1);
    }

    public int prime() {
        return prime;
    }

    public Generic[] subscript() {
        return subscripts;
    }

    public Generic antiDerivative(Variable variable) throws NotIntegrableException {
        return null;
    }

    @Nonnull
    public Generic derivative(Variable variable) {
        if (isIdentity(variable)) {
            return JsclInteger.valueOf(1);
        } else {
            return JsclInteger.valueOf(0);
        }
    }

    public Generic substitute(Variable variable, Generic generic) {
        Constant v = (Constant) newInstance();
        for (int i = 0; i < subscripts.length; i++) {
            v.subscripts[i] = subscripts[i].substitute(variable, generic);
        }

        if (v.isIdentity(variable)) {
            return generic;
        } else {
            return v.expressionValue();
        }
    }

    public Generic expand() {
        Constant v = (Constant) newInstance();
        for (int i = 0; i < subscripts.length; i++) {
            v.subscripts[i] = subscripts[i].expand();
        }
        return v.expressionValue();
    }

    public Generic factorize() {
        Constant v = (Constant) newInstance();
        for (int i = 0; i < subscripts.length; i++) {
            v.subscripts[i] = subscripts[i].factorize();
        }
        return v.expressionValue();
    }

    public Generic elementary() {
        Constant v = (Constant) newInstance();
        for (int i = 0; i < subscripts.length; i++) {
            v.subscripts[i] = subscripts[i].elementary();
        }
        return v.expressionValue();
    }

    public Generic simplify() {
        Constant v = (Constant) newInstance();
        for (int i = 0; i < subscripts.length; i++) {
            v.subscripts[i] = subscripts[i].simplify();
        }
        return v.expressionValue();
    }

    public Generic numeric() {
        return new NumericWrapper(this);
    }

    public boolean isConstant(Variable variable) {
        return !isIdentity(variable);
    }

    public int compareTo(Variable variable) {
        if (this == variable) {
            return 0;
        }

        int c = comparator.compare(this, variable);
        if (c == 0) {
            final Constant that = (Constant) variable;
            c = name.compareTo(that.name);
            if (c == 0) {
                c = ArrayComparator.comparator.compare(this.subscripts, that.subscripts);
                if (c == 0) {
                    if (prime < that.prime) {
                        return -1;
                    } else if (prime > that.prime) {
                        return 1;
                    } else return 0;
                } else {
                    return c;
                }
            } else {
                return c;
            }
        } else {
            return c;
        }
    }

    @Override
    public int hashCode() {
        final Object[] hashArray = getHashArray();
        hashArray[0] = Constant.class;
        hashArray[1] = name;
        hashArray[2] = subscripts;
        hashArray[3] = prime;
        return Arrays.deepHashCode(this.hashArray);
    }

    @Nonnull
    private Object[] getHashArray() {
        if(hashArray == null) {
            hashArray = new Object[4];
        }
        return hashArray;
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(name);

        for (Generic subscript : subscripts) {
            result.append("[").append(subscript).append("]");
        }

        if (prime != 0) {
            if (prime <= PRIME_CHARS) result.append(primeChars(prime));
            else result.append("{").append(prime).append("}");
        }

        return result.toString();
    }

    public String toJava() {
        final IConstant constantFromRegistry = JsclMathEngine.getInstance().getConstantsRegistry().get(getName());

        if (constantFromRegistry != null) {
            return constantFromRegistry.toJava();
        }

        final StringBuilder result = new StringBuilder();
        result.append(name);

        if (prime != 0) {
            if (prime <= PRIME_CHARS) result.append(underscores(prime));
            else result.append("_").append(prime);
        }

        for (Generic subscript : subscripts) {
            result.append("[").append(subscript.integerValue().intValue()).append("]");
        }
        return result.toString();
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

    public void bodyToMathML(MathML element) {
        if (subscripts.length == 0) {
            if (prime == 0) {
                nameToMathML(element);
            } else {
                MathML e1 = element.element("msup");
                nameToMathML(e1);
                primeToMathML(e1);
                element.appendChild(e1);
            }
        } else {
            if (prime == 0) {
                MathML e1 = element.element("msub");
                nameToMathML(e1);
                MathML e2 = element.element("mrow");
                for (int i = 0; i < subscripts.length; i++) {
                    subscripts[i].toMathML(e2, null);
                }
                e1.appendChild(e2);
                element.appendChild(e1);
            } else {
                MathML e1 = element.element("msubsup");
                nameToMathML(e1);
                MathML e2 = element.element("mrow");
                for (int i = 0; i < subscripts.length; i++) {
                    subscripts[i].toMathML(e2, null);
                }
                e1.appendChild(e2);
                primeToMathML(e1);
                element.appendChild(e1);
            }
        }
    }

    void primeToMathML(MathML element) {
        if (prime <= PRIME_CHARS) {
            primeCharsToMathML(element, prime);
        } else {
            MathML e1 = element.element("mfenced");
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(prime)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    @Nonnull
    public Variable newInstance() {
        return new Constant(name, prime, new Generic[subscripts.length]);
    }

    @Nonnull
    @Override
    public Set<? extends Constant> getConstants() {
        final Set<Constant> result = new HashSet<Constant>();
        result.add(this);
        return result;
    }
}
