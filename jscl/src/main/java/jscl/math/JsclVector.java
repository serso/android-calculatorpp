package jscl.math;

import jscl.math.function.Conjugate;
import jscl.math.function.Constant;
import jscl.math.function.Fraction;
import jscl.mathml.MathML;
import jscl.util.ArrayComparator;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class JsclVector extends Generic {

    @Nonnull
    protected final Generic elements[];

    protected final int rows;

    public JsclVector(@Nonnull Generic elements[]) {
        this.elements = elements;
        this.rows = elements.length;
    }

    @Nonnull
    public static JsclVector unity(int dimension) {
        final JsclVector result = new JsclVector(new Generic[dimension]);

        for (int i = 0; i < result.rows; i++) {
            if (i == 0) {
                result.elements[i] = JsclInteger.valueOf(1);
            } else {
                result.elements[i] = JsclInteger.valueOf(0);
            }
        }

        return result;
    }

    @Nonnull
    public Generic[] elements() {
        return elements;
    }

    public JsclVector add(@Nonnull JsclVector vector) {
        final JsclVector result = (JsclVector) newInstance();

        for (int i = 0; i < rows; i++) {
            result.elements[i] = elements[i].add(vector.elements[i]);
        }

        return result;
    }

    @Nonnull
    public Generic add(@Nonnull Generic that) {
        if (that instanceof JsclVector) {
            return add((JsclVector) that);
        } else {
            return add(valueOf(that));
        }
    }

    public JsclVector subtract(@Nonnull JsclVector vector) {
        final JsclVector result = (JsclVector) newInstance();

        for (int i = 0; i < rows; i++) {
            result.elements[i] = elements[i].subtract(vector.elements[i]);
        }

        return result;
    }

    @Nonnull
    public Generic subtract(@Nonnull Generic that) {
        if (that instanceof JsclVector) {
            return subtract((JsclVector) that);
        } else {
            return subtract(valueOf(that));
        }
    }

    @Nonnull
    public Generic multiply(@Nonnull Generic that) {
        if (that instanceof JsclVector) {
            return scalarProduct((JsclVector) that);
        } else if (that instanceof Matrix) {
            return ((Matrix) that).transpose().multiply(this);
        } else {
            final JsclVector result = (JsclVector) newInstance();

            for (int i = 0; i < rows; i++) {
                result.elements[i] = elements[i].multiply(that);
            }

            return result;
        }
    }

    @Nonnull
    public Generic divide(@Nonnull Generic that) throws NotDivisibleException {
        if (that instanceof JsclVector) {
            throw new ArithmeticException("Unable to divide vector by vector!");
        } else if (that instanceof Matrix) {
            return multiply(that.inverse());
        } else {
            final JsclVector result = (JsclVector) newInstance();
            for (int i = 0; i < rows; i++) {
                try {
                    result.elements[i] = elements[i].divide(that);
                } catch (NotDivisibleException e) {
                    result.elements[i] = new Fraction(elements[i], that).selfExpand();
                }
            }
            return result;
        }
    }

    public Generic gcd(@Nonnull Generic generic) {
        return null;
    }

    @Nonnull
    public Generic gcd() {
        return null;
    }

    public Generic negate() {
        final JsclVector result = (JsclVector) newInstance();

        for (int i = 0; i < rows; i++) {
            result.elements[i] = elements[i].negate();
        }

        return result;
    }

    public int signum() {
        for (int i = 0; i < rows; i++) {
            int c = elements[i].signum();
            if (c < 0) {
                return -1;
            } else if (c > 0) {
                return 1;
            }
        }
        return 0;
    }

    public int degree() {
        return 0;
    }

    public Generic antiDerivative(@Nonnull Variable variable) throws NotIntegrableException {
        final JsclVector result = (JsclVector) newInstance();

        for (int i = 0; i < rows; i++) {
            result.elements[i] = elements[i].antiDerivative(variable);
        }

        return result;
    }

    public Generic derivative(@Nonnull Variable variable) {
        final JsclVector result = (JsclVector) newInstance();

        for (int i = 0; i < rows; i++) {
            result.elements[i] = elements[i].derivative(variable);
        }

        return result;
    }

    public Generic substitute(@Nonnull Variable variable, Generic generic) {
        final JsclVector result = (JsclVector) newInstance();

        for (int i = 0; i < rows; i++) {
            result.elements[i] = elements[i].substitute(variable, generic);
        }

        return result;
    }

    public Generic expand() {
        JsclVector v = (JsclVector) newInstance();
        for (int i = 0; i < rows; i++) v.elements[i] = elements[i].expand();
        return v;
    }

    public Generic factorize() {
        final JsclVector result = (JsclVector) newInstance();

        for (int i = 0; i < rows; i++) {
            result.elements[i] = elements[i].factorize();
        }

        return result;
    }

    public Generic elementary() {
        final JsclVector result = (JsclVector) newInstance();

        for (int i = 0; i < rows; i++) {
            result.elements[i] = elements[i].elementary();
        }

        return result;
    }

    public Generic simplify() {
        final JsclVector result = (JsclVector) newInstance();

        for (int i = 0; i < rows; i++) {
            result.elements[i] = elements[i].simplify();
        }

        return result;
    }

    public Generic numeric() {
        return new NumericWrapper(this);
    }

    public Generic valueOf(@Nonnull Generic generic) {
        if (generic instanceof JsclVector || generic instanceof Matrix) {
            throw new ArithmeticException("Unable to create vector: vector of vectors or vector of matrices are forbidden!");
        } else {
            JsclVector v = (JsclVector) unity(rows).multiply(generic);
            return newInstance(v.elements);
        }
    }

    public Generic[] sumValue() {
        return new Generic[]{this};
    }

    public Generic[] productValue() throws NotProductException {
        return new Generic[]{this};
    }

    public Power powerValue() throws NotPowerException {
        return new Power(this, 1);
    }

    public Expression expressionValue() throws NotExpressionException {
        throw new NotExpressionException();
    }

    public JsclInteger integerValue() throws NotIntegerException {
        throw new NotIntegerException();
    }

    @Override
    public boolean isInteger() {
        return false;
    }

    public Variable variableValue() throws NotVariableException {
        throw new NotVariableException();
    }

    public Variable[] variables() {
        return null;
    }

    public boolean isPolynomial(@Nonnull Variable variable) {
        return false;
    }

    public boolean isConstant(@Nonnull Variable variable) {
        return false;
    }

    public Generic magnitude2() {
        return scalarProduct(this);
    }

    public Generic scalarProduct(@Nonnull JsclVector vector) {
        Generic result = JsclInteger.valueOf(0);

        for (int i = 0; i < rows; i++) {
            result = result.add(elements[i].multiply(vector.elements[i]));
        }

        return result;
    }

    public JsclVector vectorProduct(@Nonnull JsclVector vector) {
        final JsclVector result = (JsclVector) newInstance();
        Generic m[][] = {
                {JsclInteger.valueOf(0), elements[2].negate(), elements[1]},
                {elements[2], JsclInteger.valueOf(0), elements[0].negate()},
                {elements[1].negate(), elements[0], JsclInteger.valueOf(0)}
        };

        JsclVector v2 = (JsclVector) new Matrix(m).multiply(vector);

        for (int i = 0; i < rows; i++) {
            result.elements[i] = i < v2.rows ? v2.elements[i] : JsclInteger.valueOf(0);
        }

        return result;
    }

    public JsclVector complexProduct(JsclVector vector) {
        return product(new Clifford(0, 1).operator(), vector);
    }

    public JsclVector quaternionProduct(JsclVector vector) {
        return product(new Clifford(0, 2).operator(), vector);
    }

    public JsclVector geometricProduct(JsclVector vector, int algebra[]) {
        return product(new Clifford(algebra == null ? new int[]{Clifford.log2e(rows), 0} : algebra).operator(), vector);
    }

    JsclVector product(int product[][], JsclVector vector) {
        JsclVector v = (JsclVector) newInstance();
        for (int i = 0; i < rows; i++) v.elements[i] = JsclInteger.valueOf(0);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                Generic a = elements[i].multiply(vector.elements[j]);
                int k = Math.abs(product[i][j]) - 1;
                v.elements[k] = v.elements[k].add(product[i][j] < 0 ? a.negate() : a);
            }
        }
        return v;
    }

    public Generic divergence(Variable variable[]) {
        Generic a = JsclInteger.valueOf(0);
        for (int i = 0; i < rows; i++) a = a.add(elements[i].derivative(variable[i]));
        return a;
    }

    public JsclVector curl(Variable variable[]) {
        JsclVector v = (JsclVector) newInstance();
        v.elements[0] = elements[2].derivative(variable[1]).subtract(elements[1].derivative(variable[2]));
        v.elements[1] = elements[0].derivative(variable[2]).subtract(elements[2].derivative(variable[0]));
        v.elements[2] = elements[1].derivative(variable[0]).subtract(elements[0].derivative(variable[1]));
        for (int i = 3; i < rows; i++) v.elements[i] = elements[i];
        return v;
    }

    public Matrix jacobian(Variable variable[]) {
        Matrix m = new Matrix(new Generic[rows][variable.length]);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < variable.length; j++) {
                m.elements[i][j] = elements[i].derivative(variable[j]);
            }
        }
        return m;
    }

    public Generic del(Variable variable[], int algebra[]) {
        return differential(new Clifford(algebra == null ? new int[]{Clifford.log2e(rows), 0} : algebra).operator(), variable);
    }

    JsclVector differential(int product[][], Variable variable[]) {
        JsclVector v = (JsclVector) newInstance();
        for (int i = 0; i < rows; i++) v.elements[i] = JsclInteger.valueOf(0);
        int l = Clifford.log2e(rows);
        for (int i = 1; i <= l; i++) {
            for (int j = 0; j < rows; j++) {
                Generic a = elements[j].derivative(variable[i - 1]);
                int k = Math.abs(product[i][j]) - 1;
                v.elements[k] = v.elements[k].add(product[i][j] < 0 ? a.negate() : a);
            }
        }
        return v;
    }

    public Generic conjugate() {
        JsclVector v = (JsclVector) newInstance();
        for (int i = 0; i < rows; i++) {
            v.elements[i] = new Conjugate(elements[i]).selfExpand();
        }
        return v;
    }

    public int compareTo(JsclVector vector) {
        return ArrayComparator.comparator.compare(elements, vector.elements);
    }

    public int compareTo(Generic generic) {
        if (generic instanceof JsclVector) {
            return compareTo((JsclVector) generic);
        } else {
            return compareTo(valueOf(generic));
        }
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();

        result.append("[");

        for (int i = 0; i < rows; i++) {
            result.append(elements[i]).append(i < rows - 1 ? ", " : "");
        }

        result.append("]");

        return result.toString();
    }

    public String toJava() {
        final StringBuilder result = new StringBuilder();
        result.append("new Vector(new Numeric[] {");
        for (int i = 0; i < rows; i++) {
            result.append(elements[i].toJava()).append(i < rows - 1 ? ", " : "");
        }
        result.append("})");
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

    @Nonnull
    @Override
    public Set<? extends Constant> getConstants() {
        final Set<Constant> result = new HashSet<Constant>(elements.length);

        for (Generic element : elements) {
            result.addAll(element.getConstants());
        }

        return result;
    }

    protected void bodyToMathML(MathML e0) {
        MathML e1 = e0.element("mfenced");
        MathML e2 = e0.element("mtable");
        for (int i = 0; i < rows; i++) {
            MathML e3 = e0.element("mtr");
            MathML e4 = e0.element("mtd");
            elements[i].toMathML(e4, null);
            e3.appendChild(e4);
            e2.appendChild(e3);
        }
        e1.appendChild(e2);
        e0.appendChild(e1);
    }

    @Nonnull
    protected Generic newInstance() {
        return newInstance(new Generic[rows]);
    }

    @Nonnull
    protected Generic newInstance(@Nonnull Generic element[]) {
        return new JsclVector(element);
    }
}

