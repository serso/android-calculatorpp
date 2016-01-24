package jscl.math.polynomial;

import jscl.math.Generic;
import jscl.math.Rational;

class ArrayPolynomialRational extends ArrayPolynomialGeneric {
    Rational coef[];

    ArrayPolynomialRational(Monomial monomialFactory) {
        super(monomialFactory, Rational.factory);
    }

    ArrayPolynomialRational(int size, Monomial monomialFactory) {
        this(monomialFactory);
        init(size);
    }

    void init(int size) {
        monomial = new Monomial[size];
        coef = new Rational[size];
        this.size = size;
    }

    void resize(int size) {
        int length = monomial.length;
        if (size < length) {
            Monomial monomial[] = new Monomial[size];
            Rational coef[] = new Rational[size];
            System.arraycopy(this.monomial, length - size, monomial, 0, size);
            System.arraycopy(this.coef, length - size, coef, 0, size);
            this.monomial = monomial;
            this.coef = coef;
            this.size = size;
        }
    }

    protected Generic coefficient(Generic generic) {
        return coefFactory.valueOf(generic);
    }

    protected Generic getCoef(int n) {
        return coef[n];
    }

    protected void setCoef(int n, Generic generic) {
        coef[n] = (Rational) generic;
    }

    protected ArrayPolynomialGeneric newinstance(int n) {
        return new ArrayPolynomialRational(n, monomialFactory);
    }
}
