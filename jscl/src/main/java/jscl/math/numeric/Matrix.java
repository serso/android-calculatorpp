package jscl.math.numeric;

import jscl.math.NotDivisibleException;
import jscl.math.NotDoubleException;
import jscl.util.ArrayComparator;

import javax.annotation.Nonnull;

public class Matrix extends Numeric {

    @Nonnull
    private final Numeric m[][];

    private final int rows, cols;

    public Matrix(@Nonnull Numeric m[][]) {
        this.m = m;
        rows = m.length;
        cols = m.length > 0 ? m[0].length : 0;
    }

    public static Matrix identity(int dimension) {
        return identity(dimension, dimension);
    }

    public static Matrix identity(int n, int p) {
        Matrix m = new Matrix(new Numeric[n][p]);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < p; j++) {
                if (i == j) {
                    m.m[i][j] = Real.ONE;
                } else {
                    m.m[i][j] = Real.ZERO;
                }
            }
        }
        return m;
    }

    public Numeric[][] elements() {
        return m;
    }

    public Matrix add(Matrix matrix) {
        Matrix m = newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.m[i][j] = this.m[i][j].add(matrix.m[i][j]);
            }
        }
        return m;
    }

    @Nonnull
    public Numeric add(@Nonnull Numeric that) {
        if (that instanceof Matrix) {
            return add((Matrix) that);
        } else {
            return add(valueOf(that));
        }
    }

    public Matrix subtract(Matrix matrix) {
        Matrix m = newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.m[i][j] = this.m[i][j].subtract(matrix.m[i][j]);
            }
        }
        return m;
    }

    @Nonnull
    public Numeric subtract(@Nonnull Numeric that) {
        if (that instanceof Matrix) {
            return subtract((Matrix) that);
        } else {
            return subtract(valueOf(that));
        }
    }

    public Matrix multiply(Matrix matrix) {
        if (cols != matrix.rows) throw new ArithmeticException();
        Matrix m = newInstance(new Numeric[rows][matrix.cols]);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < matrix.cols; j++) {
                m.m[i][j] = Real.ZERO;
                for (int k = 0; k < cols; k++) {
                    m.m[i][j] = m.m[i][j].add(this.m[i][k].multiply(matrix.m[k][j]));
                }
            }
        }
        return m;
    }

    @Nonnull
    public Numeric multiply(@Nonnull Numeric that) {
        if (that instanceof Matrix) {
            return multiply((Matrix) that);
        } else if (that instanceof Vector) {
            Vector v = ((Vector) that).newInstance(new Numeric[rows]);
            Vector v2 = (Vector) that;
            if (cols != v2.n) throw new ArithmeticException();
            for (int i = 0; i < rows; i++) {
                v.element[i] = Real.ZERO;
                for (int k = 0; k < cols; k++) {
                    v.element[i] = v.element[i].add(m[i][k].multiply(v2.element[k]));
                }
            }
            return v;
        } else {
            Matrix m = newInstance();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    m.m[i][j] = this.m[i][j].multiply(that);
                }
            }
            return m;
        }
    }

    @Nonnull
    public Numeric divide(@Nonnull Numeric that) throws NotDivisibleException {

        if (that instanceof Matrix) {
            return multiply(that.inverse());
        } else if (that instanceof Vector) {
            throw new ArithmeticException();
        } else {
            Matrix m = newInstance();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    m.m[i][j] = this.m[i][j].divide(that);
                }
            }
            return m;
        }

    }

    @Nonnull
    public Numeric negate() {
        Matrix m = newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.m[i][j] = this.m[i][j].negate();
            }
        }
        return m;
    }

    public int signum() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int c = m[i][j].signum();
                if (c < 0) {
                    return -1;
                } else if (c > 0) {
                    return 1;
                }
            }
        }

        return 0;
    }

    @Nonnull
    public Numeric valueOf(@Nonnull Numeric numeric) {
        if (numeric instanceof Matrix || numeric instanceof Vector) {
            throw new ArithmeticException();
        } else {
            Matrix m = (Matrix) identity(rows, cols).multiply(numeric);
            return newInstance(m.m);
        }
    }

    public Numeric[] vectors() {
        Vector v[] = new Vector[rows];
        for (int i = 0; i < rows; i++) {
            v[i] = new Vector(new Numeric[cols]);
            for (int j = 0; j < cols; j++) {
                v[i].element[j] = m[i][j];
            }
        }
        return v;
    }

    public Numeric transpose() {
        Matrix m = newInstance(new Numeric[cols][rows]);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.m[j][i] = this.m[i][j];
            }
        }
        return m;
    }

    public Numeric trace() {
        Numeric s = Real.ZERO;
        for (int i = 0; i < rows; i++) {
            s = s.add(m[i][i]);
        }
        return s;
    }

    @Nonnull
    public Numeric inverse() {
        Matrix m = newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                m.m[i][j] = inverseElement(i, j);
            }
        }
        return m.transpose().divide(determinant());
    }

    Numeric inverseElement(int k, int l) {
        final Matrix result = newInstance();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                if (i == k) {
                    result.m[i][j] = Real.valueOf(j == l ? 1 : 0);
                } else {
                    result.m[i][j] = this.m[i][j];
                }
            }
        }

        return result.determinant();
    }

    public Numeric determinant() {
        if (rows > 1) {
            Numeric a = Real.ZERO;
            for (int i = 0; i < rows; i++) {
                if (m[i][0].signum() != 0) {
                    Matrix m = newInstance(new Numeric[rows - 1][rows - 1]);
                    for (int j = 0; j < rows - 1; j++) {
                        for (int k = 0; k < rows - 1; k++) m.m[j][k] = this.m[j < i ? j : j + 1][k + 1];
                    }
                    if (i % 2 == 0) {
                        a = a.add(this.m[i][0].multiply(m.determinant()));
                    } else {
                        a = a.subtract(this.m[i][0].multiply(m.determinant()));
                    }
                }
            }
            return a;
        } else if (rows > 0) return m[0][0];
        else return Real.ZERO;
    }

    @Nonnull
    public Numeric ln() {
        throw new ArithmeticException();
    }

    @Nonnull
    @Override
    public Numeric lg() {
        throw new ArithmeticException();
    }

    @Nonnull
    public Numeric exp() {
        throw new ArithmeticException();
    }

    public Numeric conjugate() {
        Matrix m = newInstance();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.m[i][j] = this.m[i][j].conjugate();
            }
        }
        return m;
    }

    public int compareTo(Matrix matrix) {
        return ArrayComparator.comparator.compare(vectors(), matrix.vectors());
    }

    public int compareTo(Numeric numeric) {
        if (numeric instanceof Matrix) {
            return compareTo((Matrix) numeric);
        } else {
            return compareTo(valueOf(numeric));
        }
    }

    @Override
    public double doubleValue() {
        throw NotDoubleException.get();
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("{");
        for (int i = 0; i < rows; i++) {
            result.append("{");
            for (int j = 0; j < cols; j++) {
                result.append(m[i][j]).append(j < cols - 1 ? ", " : "");
            }
            result.append("}").append(i < rows - 1 ? ",\n" : "");
        }
        result.append("}");
        return result.toString();
    }

    protected Matrix newInstance() {
        return newInstance(new Numeric[rows][cols]);
    }

    protected Matrix newInstance(Numeric element[][]) {
        return new Matrix(element);
    }
}
