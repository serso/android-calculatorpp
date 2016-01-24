package jscl.math.numeric;

import jscl.math.NotDivisibleException;
import jscl.util.ArrayComparator;

import javax.annotation.Nonnull;

public class Vector extends Numeric {

    protected final Numeric element[];
    protected final int n;

    public Vector(Numeric element[]) {
        this.element = element;
        n = element.length;
    }

    public static Vector unity(int dimension) {
        Vector v = new Vector(new Numeric[dimension]);
        for (int i = 0; i < v.n; i++) {
            if (i == 0) v.element[i] = Real.ONE;
            else v.element[i] = Real.ZERO;
        }
        return v;
    }

    public Numeric[] elements() {
        return element;
    }

    public Vector add(Vector vector) {
        Vector v = newInstance();
        for (int i = 0; i < n; i++) v.element[i] = element[i].add(vector.element[i]);
        return v;
    }

    @Nonnull
    public Numeric add(@Nonnull Numeric that) {
        if (that instanceof Vector) {
            return add((Vector) that);
        } else {
            return add(valueOf(that));
        }
    }

    public Vector subtract(Vector vector) {
        Vector v = newInstance();
        for (int i = 0; i < n; i++) {
            v.element[i] = element[i].subtract(vector.element[i]);
        }
        return v;
    }

    @Nonnull
    public Numeric subtract(@Nonnull Numeric that) {
        if (that instanceof Vector) {
            return subtract((Vector) that);
        } else {
            return subtract(valueOf(that));
        }
    }

    @Nonnull
    public Numeric multiply(@Nonnull Numeric that) {
        if (that instanceof Vector) {
            return scalarProduct((Vector) that);
        } else if (that instanceof Matrix) {
            return ((Matrix) that).transpose().multiply(this);
        } else {
            Vector v = newInstance();
            for (int i = 0; i < n; i++) v.element[i] = element[i].multiply(that);
            return v;
        }
    }

    @Nonnull
    public Numeric divide(@Nonnull Numeric that) throws NotDivisibleException {
        if (that instanceof Vector) {
            throw new ArithmeticException();
        } else if (that instanceof Matrix) {
            return multiply(that.inverse());
        } else {
            Vector v = newInstance();
            for (int i = 0; i < n; i++) {
                v.element[i] = element[i].divide(that);
            }
            return v;
        }
    }

    @Nonnull
    public Numeric negate() {
        Vector v = newInstance();
        for (int i = 0; i < n; i++) v.element[i] = element[i].negate();
        return v;
    }

    public int signum() {
        for (int i = 0; i < n; i++) {
            int c = element[i].signum();
            if (c < 0) {
                return -1;
            } else if (c > 0) {
                return 1;
            }
        }
        return 0;
    }

    @Nonnull
    public Numeric valueOf(@Nonnull Numeric numeric) {
        if (numeric instanceof Vector || numeric instanceof Matrix) {
            throw new ArithmeticException();
        } else {
            Vector v = (Vector) unity(n).multiply(numeric);
            return newInstance(v.element);
        }
    }

    public Numeric magnitude2() {
        return scalarProduct(this);
    }

    public Numeric scalarProduct(Vector vector) {
        Numeric a = Real.ZERO;
        for (int i = 0; i < n; i++) {
            a = a.add(element[i].multiply(vector.element[i]));
        }
        return a;
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
        Vector v = newInstance();
        for (int i = 0; i < n; i++) v.element[i] = element[i].conjugate();
        return v;
    }

    public int compareTo(Vector vector) {
        return ArrayComparator.comparator.compare(element, vector.element);
    }

    public int compareTo(Numeric numeric) {
        if (numeric instanceof Vector) {
            return compareTo((Vector) numeric);
        } else {
            return compareTo(valueOf(numeric));
        }
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();

        result.append("[");

        for (int i = 0; i < n; i++) {
            result.append(element[i]).append(i < n - 1 ? ", " : "");
        }

        result.append("]");

        return result.toString();
    }

    @Nonnull
    protected Vector newInstance() {
        return newInstance(new Numeric[n]);
    }

    @Nonnull
    protected Vector newInstance(@Nonnull Numeric element[]) {
        return new Vector(element);
    }
}
