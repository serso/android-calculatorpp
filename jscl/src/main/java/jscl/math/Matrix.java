package jscl.math;

import jscl.math.function.Conjugate;
import jscl.math.function.Constant;
import jscl.math.function.Fraction;
import jscl.math.function.trigonometric.Cos;
import jscl.math.function.trigonometric.Sin;
import jscl.mathml.MathML;
import jscl.util.ArrayComparator;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class Matrix extends Generic {

    protected final Generic elements[][];
    protected final int rows, cols;

    public Matrix(Generic elements[][]) {
        this.elements = elements;
        rows = elements.length;
        cols = elements.length > 0 ? elements[0].length : 0;
    }

    public static boolean isMatrixProduct(@Nonnull Generic a, @Nonnull Generic b) {
        return (a instanceof Matrix && b instanceof Matrix) ||
                (a instanceof Matrix && b instanceof JsclVector) ||
                (a instanceof JsclVector && b instanceof Matrix);
    }

    public static Matrix identity(int dimension) {
        return identity(dimension, dimension);
    }

    public static Matrix identity(int n, int p) {
        Matrix m = new Matrix(new Generic[n][p]);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < p; j++) {
                if (i == j) {
                    m.elements[i][j] = JsclInteger.valueOf(1);
                } else {
                    m.elements[i][j] = JsclInteger.valueOf(0);
                }
            }
        }
        return m;
    }

    public static Matrix frame(JsclVector vector[]) {
        Matrix m = new Matrix(new Generic[vector.length > 0 ? vector[0].rows : 0][vector.length]);
        for (int i = 0; i < m.rows; i++) {
            for (int j = 0; j < m.cols; j++) {
                m.elements[i][j] = vector[j].elements[i];
            }
        }
        return m;
    }

    public static Matrix rotation(int dimension, int plane, Generic angle) {
        return rotation(dimension, plane, 2, angle);
    }

    public static Matrix rotation(int dimension, int axis1, int axis2, Generic angle) {
        Matrix m = new Matrix(new Generic[dimension][dimension]);
        for (int i = 0; i < m.rows; i++) {
            for (int j = 0; j < m.cols; j++) {
                if (i == axis1 && j == axis1) {
                    m.elements[i][j] = new Cos(angle).selfExpand();
                } else if (i == axis1 && j == axis2) {
                    m.elements[i][j] = new Sin(angle).selfExpand().negate();
                } else if (i == axis2 && j == axis1) {
                    m.elements[i][j] = new Sin(angle).selfExpand();
                } else if (i == axis2 && j == axis2) {
                    m.elements[i][j] = new Cos(angle).selfExpand();
                } else if (i == j) {
                    m.elements[i][j] = JsclInteger.valueOf(1);
                } else {
                    m.elements[i][j] = JsclInteger.valueOf(0);
                }
            }
        }
        return m;
    }

    @Nonnull
    protected static Generic newInstance(Generic element[][]) {
        return new Matrix(element);
    }

    public Generic[][] elements() {
        return elements;
    }

    public Matrix add(Matrix matrix) {
        Matrix m = (Matrix) newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.elements[i][j] = elements[i][j].add(matrix.elements[i][j]);
            }
        }
        return m;
    }

    @Nonnull
    public Generic add(@Nonnull Generic that) {
        if (that instanceof Matrix) {
            return add((Matrix) that);
        } else {
            return add(valueOf(that));
        }
    }

    public Matrix subtract(Matrix matrix) {
        Matrix m = (Matrix) newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.elements[i][j] = elements[i][j].subtract(matrix.elements[i][j]);
            }
        }
        return m;
    }

    @Nonnull
    public Generic subtract(@Nonnull Generic that) {
        if (that instanceof Matrix) {
            return subtract((Matrix) that);
        } else {
            return subtract(valueOf(that));
        }
    }

    public Matrix multiply(Matrix matrix) {
        if (cols != matrix.rows) {
            throw new ArithmeticException("Unable to multiply matrix by matrix: number of columns of left matrix doesn't match number of rows of right matrix!");
        }
        Matrix m = (Matrix) newInstance(new Generic[rows][matrix.cols]);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < matrix.cols; j++) {
                m.elements[i][j] = JsclInteger.valueOf(0);
                for (int k = 0; k < cols; k++) {
                    m.elements[i][j] = m.elements[i][j].add(elements[i][k].multiply(matrix.elements[k][j]));
                }
            }
        }
        return m;
    }

    @Nonnull
    public Generic multiply(@Nonnull Generic that) {
        if (that instanceof Matrix) {
            return multiply((Matrix) that);
        } else if (that instanceof JsclVector) {
            JsclVector v = (JsclVector) ((JsclVector) that).newInstance(new Generic[rows]);
            JsclVector v2 = (JsclVector) that;
            if (cols != v2.rows) {
                throw new ArithmeticException("Unable to multiply matrix by vector: number of matrix columns doesn't match number of vector rows!");
            }
            for (int i = 0; i < rows; i++) {
                v.elements[i] = JsclInteger.valueOf(0);
                for (int k = 0; k < cols; k++) {
                    v.elements[i] = v.elements[i].add(elements[i][k].multiply(v2.elements[k]));
                }
            }
            return v;
        } else {
            Matrix m = (Matrix) newInstance();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    m.elements[i][j] = elements[i][j].multiply(that);
                }
            }
            return m;
        }
    }

    @Nonnull
    public Generic divide(@Nonnull Generic that) throws NotDivisibleException {
        if (that instanceof Matrix) {
            return multiply(that.inverse());
        } else if (that instanceof JsclVector) {
            throw new ArithmeticException("Unable to divide matrix by vector: matrix could not be divided by vector!");
        } else {
            Matrix m = (Matrix) newInstance();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    try {
                        m.elements[i][j] = elements[i][j].divide(that);
                    } catch (NotDivisibleException e) {
                        m.elements[i][j] = new Fraction(elements[i][j], that).selfExpand();
                    }
                }
            }
            return m;
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
        Matrix m = (Matrix) newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.elements[i][j] = elements[i][j].negate();
            }
        }
        return m;
    }

    public int signum() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int c = elements[i][j].signum();
                if (c < 0) return -1;
                else if (c > 0) return 1;
            }
        }
        return 0;
    }

    public int degree() {
        return 0;
    }

    public Generic antiDerivative(@Nonnull Variable variable) throws NotIntegrableException {
        Matrix m = (Matrix) newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.elements[i][j] = elements[i][j].antiDerivative(variable);
            }
        }
        return m;
    }

    public Generic derivative(@Nonnull Variable variable) {
        Matrix m = (Matrix) newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.elements[i][j] = elements[i][j].derivative(variable);
            }
        }
        return m;
    }

    public Generic substitute(@Nonnull Variable variable, Generic generic) {
        Matrix m = (Matrix) newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.elements[i][j] = elements[i][j].substitute(variable, generic);
            }
        }
        return m;
    }

    public Generic expand() {
        Matrix m = (Matrix) newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.elements[i][j] = elements[i][j].expand();
            }
        }
        return m;
    }

    public Generic factorize() {
        Matrix m = (Matrix) newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.elements[i][j] = elements[i][j].factorize();
            }
        }
        return m;
    }

    public Generic elementary() {
        Matrix m = (Matrix) newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.elements[i][j] = elements[i][j].elementary();
            }
        }
        return m;
    }

    public Generic simplify() {
        Matrix m = (Matrix) newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.elements[i][j] = elements[i][j].simplify();
            }
        }
        return m;
    }

    public Generic numeric() {
        return new NumericWrapper(this);
    }

    public Generic valueOf(Generic generic) {
        if (generic instanceof Matrix || generic instanceof JsclVector) {
            throw new ArithmeticException("Unable to create matrix: matrix of vectors and matrix of matrices are forbidden");
        } else {
            Matrix m = (Matrix) identity(rows, cols).multiply(generic);
            return newInstance(m.elements);
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
        throw NotIntegerException.get();
    }

    @Override
    public double doubleValue() throws NotDoubleException {
        throw NotDoubleException.get();
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

    public Generic[] vectors() {
        JsclVector v[] = new JsclVector[rows];
        for (int i = 0; i < rows; i++) {
            v[i] = new JsclVector(new Generic[cols]);
            for (int j = 0; j < cols; j++) {
                v[i].elements[j] = elements[i][j];
            }
        }
        return v;
    }

    public Generic tensorProduct(Matrix matrix) {
        Matrix m = (Matrix) newInstance(new Generic[rows * matrix.rows][cols * matrix.cols]);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < matrix.rows; k++) {
                    for (int l = 0; l < matrix.cols; l++) {
                        m.elements[i * matrix.rows + k][j * matrix.cols + l] = elements[i][j].multiply(matrix.elements[k][l]);
                    }
                }
            }
        }
        return m;
    }

    public Matrix transpose() {
        Matrix m = (Matrix) newInstance(new Generic[cols][rows]);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.elements[j][i] = elements[i][j];
            }
        }
        return m;
    }

    public Generic trace() {
        Generic s = JsclInteger.valueOf(0);
        for (int i = 0; i < rows; i++) {
            s = s.add(elements[i][i]);
        }
        return s;
    }

    public Generic inverse() {
        Matrix m = (Matrix) newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                m.elements[i][j] = inverseElement(i, j);
            }
        }
        return m.transpose().divide(determinant());
    }

    Generic inverseElement(int k, int l) {
        Matrix m = (Matrix) newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                m.elements[i][j] = i == k ? JsclInteger.valueOf(j == l ? 1 : 0) : elements[i][j];
            }
        }
        return m.determinant();
    }

    public Generic determinant() {
        if (rows > 1) {
            Generic a = JsclInteger.valueOf(0);
            for (int i = 0; i < rows; i++) {
                if (elements[i][0].signum() == 0) ;
                else {
                    Matrix m = (Matrix) newInstance(new Generic[rows - 1][rows - 1]);
                    for (int j = 0; j < rows - 1; j++) {
                        for (int k = 0; k < rows - 1; k++) m.elements[j][k] = elements[j < i ? j : j + 1][k + 1];
                    }
                    if (i % 2 == 0) a = a.add(elements[i][0].multiply(m.determinant()));
                    else a = a.subtract(elements[i][0].multiply(m.determinant()));
                }
            }
            return a;
        } else if (rows > 0) return elements[0][0];
        else return JsclInteger.valueOf(0);
    }

    public Generic conjugate() {
        Matrix m = (Matrix) newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.elements[i][j] = new Conjugate(elements[i][j]).selfExpand();
            }
        }
        return m;
    }

    public int compareTo(Matrix matrix) {
        return ArrayComparator.comparator.compare(vectors(), matrix.vectors());
    }

    public int compareTo(Generic generic) {
        if (generic instanceof Matrix) {
            return compareTo((Matrix) generic);
        } else {
            return compareTo(valueOf(generic));
        }
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();

        result.append("[");
        for (int i = 0; i < rows; i++) {
            result.append("[");

            for (int j = 0; j < cols; j++) {
                result.append(elements[i][j]).append(j < cols - 1 ? ", " : "");
            }

            result.append("]").append(i < rows - 1 ? ",\n" : "");
        }

        result.append("]");

        return result.toString();
    }

    public String toJava() {
        final StringBuilder result = new StringBuilder();

        result.append("new Matrix(new Numeric[][] {");

        for (int i = 0; i < rows; i++) {
            result.append("{");
            for (int j = 0; j < cols; j++) {
                result.append(elements[i][j].toJava()).append(j < cols - 1 ? ", " : "");
            }
            result.append("}").append(i < rows - 1 ? ", " : "");
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
        final Set<Constant> result = new HashSet<Constant>();

        for (Generic[] element : elements) {
            for (Generic generic : element) {
                result.addAll(generic.getConstants());
            }
        }

        return result;
    }

    protected void bodyToMathML(MathML e0) {
        MathML e1 = e0.element("mfenced");
        MathML e2 = e0.element("mtable");
        for (int i = 0; i < rows; i++) {
            MathML e3 = e0.element("mtr");
            for (int j = 0; j < cols; j++) {
                MathML e4 = e0.element("mtd");
                elements[i][j].toMathML(e4, null);
                e3.appendChild(e4);
            }
            e2.appendChild(e3);
        }
        e1.appendChild(e2);
        e0.appendChild(e1);
    }

    @Nonnull
    protected Generic newInstance() {
        return newInstance(new Generic[rows][cols]);
    }
}
