package jscl.math;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.math.function.Constant;
import jscl.math.function.Fraction;
import jscl.math.function.Inverse;
import jscl.math.numeric.Real;
import jscl.math.polynomial.Polynomial;
import jscl.math.polynomial.UnivariatePolynomial;
import jscl.mathml.MathML;
import jscl.text.ExpressionParser;
import jscl.text.ParseException;
import jscl.text.Parser;
import jscl.text.ParserUtils;
import jscl.text.msg.Messages;
import jscl.util.ArrayUtils;

public class Expression extends Generic {

    protected static final com.google.common.base.Function<Variable, Generic> FACTORIZE_CONVERTER = new com.google.common.base.Function<Variable, Generic>() {
        @Nonnull
        public Generic apply(@Nonnull Variable variable) {
            return variable.factorize();
        }
    };
    protected static final com.google.common.base.Function<Variable, Generic> ELEMENTARY_CONVERTER = new com.google.common.base.Function<Variable, Generic>() {
        @Nonnull
        public Generic apply(@Nonnull Variable variable) {
            return variable.elementary();
        }
    };
    protected static final com.google.common.base.Function<Variable, Generic> EXPAND_CONVERTER = new com.google.common.base.Function<Variable, Generic>() {
        @Nonnull
        public Generic apply(@Nonnull Variable variable) {
            return variable.expand();
        }
    };
    protected static final com.google.common.base.Function<Variable, Generic> NUMERIC_CONVERTER = new com.google.common.base.Function<Variable, Generic>() {
        @Nonnull
        public Generic apply(@Nonnull Variable variable) {
            return variable.numeric();
        }
    };
    int size;
    private Literal literals[];
    private JsclInteger coefficients[];

    Expression() {
    }

    Expression(int size) {
        init(size);
    }

    public static Variable[] variables(Generic elements[]) {
        final List<Variable> result = new ArrayList<Variable>();

        for (Generic element : elements) {
            for (Variable variable : element.variables()) {
                if (!result.contains(variable)) {
                    result.add(variable);
                }
            }
        }

        return ArrayUtils.toArray(result, new Variable[result.size()]);
    }

    @Nonnull
    public static Expression valueOf(@Nonnull Variable variable) {
        return valueOf(Literal.valueOf(variable));
    }

    @Nonnull
    public static Expression valueOf(@Nonnull Literal literal) {
        return valueOf(literal, JsclInteger.valueOf(1));
    }

    @Nonnull
    public static Expression valueOf(@Nonnull JsclInteger integer) {
        return valueOf(Literal.newInstance(), integer);
    }

    @Nonnull
    public static Expression valueOf(@Nonnull Literal literal, @Nonnull JsclInteger integer) {
        final Expression result = new Expression();
        result.init(literal, integer);
        return result;
    }

    public static Expression valueOf(Rational rational) {
        Expression ex = new Expression();
        ex.init(rational);
        return ex;
    }

    public static Expression valueOf(@Nonnull Constant constant) {
        final Expression expression = new Expression(1);
        Literal literal = new Literal();
        literal.init(constant, 1);
        expression.init(literal, JsclInteger.ONE);
        return expression;
    }

    public static Expression valueOf(double value) {
        final Expression expression = new Expression(1);
        Literal literal = new Literal();
        literal.init(new DoubleVariable(new NumericWrapper(Real.valueOf(value))), 1);
        expression.init(literal, JsclInteger.ONE);
        return expression;
    }

    public static Expression valueOf(@Nonnull String expression) throws ParseException {
        final Parser.Parameters p = Parser.Parameters.get(expression);

        final Generic generic = ExpressionParser.parser.parse(p, null);

        ParserUtils.skipWhitespaces(p);

        int index = p.position.intValue();
        if (index < expression.length()) {
            throw new ParseException(index, expression, Messages.msg_1, index + 1);
        }

        return new Expression().init(generic);
    }

    public Expression init(@Nonnull NumericWrapper numericWrapper) {
        final Literal literal = new Literal();
        literal.init(new ExpressionVariable(numericWrapper), 1);
        init(literal, JsclInteger.ONE);
        return this;
    }

    public static void separateSign(MathML element, Generic generic) {
        if (generic.signum() < 0) {
            MathML e1 = element.element("mo");
            e1.appendChild(element.text("-"));
            element.appendChild(e1);
            generic.negate().toMathML(element, null);
        } else {
            generic.toMathML(element, null);
        }
    }

    public int size() {
        return size;
    }

    public Literal literal(int n) {
        return literals[n];
    }

    public JsclInteger coef(int n) {
        return coefficients[n];
    }

    void init(int size) {
        literals = new Literal[size];
        coefficients = new JsclInteger[size];
        this.size = size;
    }

    void resize(int size) {
        int length = literals.length;
        if (size < length) {
            Literal literal[] = new Literal[size];
            JsclInteger coef[] = new JsclInteger[size];
            System.arraycopy(this.literals, length - size, literal, 0, size);
            System.arraycopy(this.coefficients, length - size, coef, 0, size);
            this.literals = literal;
            this.coefficients = coef;
            this.size = size;
        }
    }

    public Expression add(@Nonnull Expression that) {
        final Expression result = newInstance(size + that.size);
        int i = result.size;

        int thisI = this.size;
        int thatI = that.size;

        Literal thisLiteral = thisI > 0 ? this.literals[--thisI] : null;
        Literal thatLiteral = thatI > 0 ? that.literals[--thatI] : null;

        while (thisLiteral != null || thatLiteral != null) {
            int c;

            if (thisLiteral == null) {
                c = 1;
            } else if (thatLiteral == null) {
                c = -1;
            } else {
                c = -thisLiteral.compareTo(thatLiteral);
            }

            if (c < 0) {
                final JsclInteger thisCoefficient = this.coefficients[thisI];
                --i;
                result.literals[i] = thisLiteral;
                result.coefficients[i] = thisCoefficient;
                thisLiteral = thisI > 0 ? literals[--thisI] : null;
            } else if (c > 0) {
                JsclInteger en = that.coefficients[thatI];
                --i;
                result.literals[i] = thatLiteral;
                result.coefficients[i] = en;
                thatLiteral = thatI > 0 ? that.literals[--thatI] : null;
            } else {
                JsclInteger sum = coefficients[thisI].add(that.coefficients[thatI]);
                if (sum.signum() != 0) {
                    --i;
                    result.literals[i] = thisLiteral;
                    result.coefficients[i] = sum;
                }

                thisLiteral = thisI > 0 ? literals[--thisI] : null;
                thatLiteral = thatI > 0 ? that.literals[--thatI] : null;
            }
        }

        result.resize(result.size - i);

        return result;
    }

    @Nonnull
    public Generic add(@Nonnull Generic that) {
        if (that instanceof Expression) {
            return add((Expression) that);
        } else if (that instanceof JsclInteger || that instanceof Rational || that instanceof NumericWrapper) {
            return add(valueOf(that));
        } else {
            return that.valueOf(this).add(that);
        }
    }

    public Expression subtract(Expression expression) {
        return multiplyAndAdd(Literal.newInstance(), JsclInteger.valueOf(-1), expression);
    }

    @Nonnull
    public Generic subtract(@Nonnull Generic that) {
        if (that instanceof Expression) {
            return subtract((Expression) that);
        } else if (that instanceof JsclInteger || that instanceof Rational || that instanceof NumericWrapper) {
            return subtract(valueOf(that));
        } else {
            return that.valueOf(this).subtract(that);
        }
    }

    Expression multiplyAndAdd(@Nonnull Literal literal, @Nonnull JsclInteger coefficient, @Nonnull Expression that) {
        if (coefficient.signum() == 0) return this;

        final Expression result = newInstance(size + that.size);
        int i = result.size;

        int thisI = this.size;
        int thatI = that.size;

        Literal thisLiteral = thisI > 0 ? literals[--thisI] : null;
        Literal thatLiteral = thatI > 0 ? that.literals[--thatI].multiply(literal) : null;

        while (thisLiteral != null || thatLiteral != null) {
            int c = thisLiteral == null ? 1 : (thatLiteral == null ? -1 : -thisLiteral.compareTo(thatLiteral));

            if (c < 0) {
                JsclInteger en = coefficients[thisI];
                --i;
                result.literals[i] = thisLiteral;
                result.coefficients[i] = en;
                thisLiteral = thisI > 0 ? literals[--thisI] : null;
            } else if (c > 0) {
                JsclInteger en = that.coefficients[thatI].multiply(coefficient);
                --i;
                result.literals[i] = thatLiteral;
                result.coefficients[i] = en;
                thatLiteral = thatI > 0 ? that.literals[--thatI].multiply(literal) : null;
            } else {
                JsclInteger en = coefficients[thisI].add(that.coefficients[thatI].multiply(coefficient));
                if (en.signum() != 0) {
                    --i;
                    result.literals[i] = thisLiteral;
                    result.coefficients[i] = en;
                }
                thisLiteral = thisI > 0 ? literals[--thisI] : null;
                thatLiteral = thatI > 0 ? that.literals[--thatI].multiply(literal) : null;
            }
        }

        result.resize(result.size - i);

        return result;
    }

    public Expression multiply(Expression expression) {
        Expression result = newInstance(0);

        for (int i = 0; i < size; i++) {
            result = result.multiplyAndAdd(literals[i], coefficients[i], expression);
        }

        return result;
    }

    @Nonnull
    public Generic multiply(@Nonnull Generic that) {
        if (that instanceof Expression) {
            return multiply((Expression) that);
        } else if (that instanceof JsclInteger || that instanceof Rational || that instanceof NumericWrapper) {
            return multiply(valueOf(that));
        } else {
            return that.multiply(this);
        }
    }

    @Nonnull
    public Generic divide(@Nonnull Generic that) throws NotDivisibleException {
        Generic a[] = divideAndRemainder(that);
        if (a[1].signum() == 0) return a[0];
        else throw new NotDivisibleException();
    }

    public Generic[] divideAndRemainder(Generic generic) throws ArithmeticException {
        if (generic instanceof Expression) {
            Expression ex = (Expression) generic;
            Literal l1 = literalScm();
            Literal l2 = ex.literalScm();
            Literal l = l1.gcd(l2);
            Variable va[] = l.variables();
            if (va.length == 0) {
                if (signum() == 0 && ex.signum() != 0) return new Generic[]{this, JsclInteger.valueOf(0)};
                else try {
                    return divideAndRemainder(ex.integerValue());
                } catch (NotIntegerException e) {
                    return new Generic[]{JsclInteger.valueOf(0), this};
                }
            } else {
                Polynomial fact = Polynomial.factory(va[0]);
                Polynomial p[] = fact.valueOf(this).divideAndRemainder(fact.valueOf(ex));
                return new Generic[]{p[0].genericValue(), p[1].genericValue()};
            }
        } else if (generic instanceof JsclInteger) {
            try {
                Expression ex = newInstance(size);
                for (int i = 0; i < size; i++) {
                    ex.literals[i] = literals[i];
                    ex.coefficients[i] = coefficients[i].divide((JsclInteger) generic);
                }
                return new Generic[]{ex, JsclInteger.valueOf(0)};
            } catch (NotDivisibleException e) {
                return new Generic[]{JsclInteger.valueOf(0), this};
            }
        } else if (generic instanceof Rational || generic instanceof NumericWrapper) {
            return divideAndRemainder(valueOf(generic));
        } else {
            return generic.valueOf(this).divideAndRemainder(generic);
        }
    }

    public Generic gcd(@Nonnull Generic generic) {
        if (generic instanceof Expression) {
            final Expression that = (Expression) generic;

            final Literal thisL = this.literalScm();
            final Literal thatL = that.literalScm();

            final Literal gcdL = thisL.gcd(thatL);

            final Variable vars[] = gcdL.variables();
            if (vars.length == 0) {
                if (signum() == 0) {
                    return that;
                } else {
                    return this.gcd(that.gcd());
                }
            } else {
                Polynomial p = Polynomial.factory(vars[0]);
                return p.valueOf(this).gcd(p.valueOf(that)).genericValue();
            }
        } else if (generic instanceof JsclInteger) {
            if (generic.signum() == 0) {
                return this;
            } else {
                return this.gcd().gcd(generic);
            }
        } else if (generic instanceof Rational || generic instanceof NumericWrapper) {
            return gcd(valueOf(generic));
        } else {
            return generic.valueOf(this).gcd(generic);
        }
    }

    @Nonnull
    public Generic gcd() {
        JsclInteger result = JsclInteger.valueOf(0);

        for (int i = size - 1; i >= 0; i--) {
            result = result.gcd(coefficients[i]);
        }

        return result;
    }

    @Nonnull
    public Literal literalScm() {
        Literal result = Literal.newInstance();
        for (int i = 0; i < size; i++) {
            result = result.scm(literals[i]);
        }
        return result;
    }

    public Generic negate() {
        return multiply(JsclInteger.valueOf(-1));
    }

    public int signum() {
        return size == 0 ? 0 : coefficients[0].signum();
    }

    public int degree() {
        return 0;
    }

    public Generic antiDerivative(@Nonnull Variable variable) throws NotIntegrableException {
        if (isPolynomial(variable)) {
            return ((UnivariatePolynomial) Polynomial.factory(variable).valueOf(this)).antiderivative().genericValue();
        } else {
            try {
                Variable v = variableValue();
                try {
                    return v.antiDerivative(variable);
                } catch (NotIntegrableException e) {
                    if (v instanceof Fraction) {
                        Generic g[] = ((Fraction) v).getParameters();
                        if (g[1].isConstant(variable)) {
                            return new Inverse(g[1]).selfExpand().multiply(g[0].antiDerivative(variable));
                        }
                    }
                }
            } catch (NotVariableException e) {
                Generic sumElements[] = sumValue();
                if (sumElements.length > 1) {

                    Generic result = JsclInteger.valueOf(0);
                    for (Generic sumElement : sumElements) {
                        result = result.add(sumElement.antiDerivative(variable));
                    }
                    return result;

                } else {
                    final Generic products[] = sumElements[0].productValue();
                    Generic constantProduct = JsclInteger.valueOf(1);
                    Generic notConstantProduct = JsclInteger.valueOf(1);
                    for (Generic product : products) {
                        if (product.isConstant(variable)) {
                            constantProduct = constantProduct.multiply(product);
                        } else {
                            notConstantProduct = notConstantProduct.multiply(product);
                        }
                    }
                    if (constantProduct.compareTo(JsclInteger.valueOf(1)) != 0) {
                        return constantProduct.multiply(notConstantProduct.antiDerivative(variable));
                    }
                }
            }
        }
        throw new NotIntegrableException(this);
    }

    public Generic derivative(@Nonnull Variable variable) {
        Generic s = JsclInteger.valueOf(0);
        Literal l = literalScm();
        int n = l.size();
        for (int i = 0; i < n; i++) {
            Variable v = l.getVariable(i);
            Generic a = ((UnivariatePolynomial) Polynomial.factory(v).valueOf(this)).derivative(variable).genericValue();
            s = s.add(a);
        }
        return s;
    }

    public Generic substitute(@Nonnull final Variable variable, final Generic generic) {
        final Map<Variable, Generic> content = literalScm().content(new com.google.common.base.Function<Variable, Generic>() {
            @Nonnull
            public Generic apply(@Nonnull Variable v) {
                return v.substitute(variable, generic);
            }
        });

        return substitute(content);
    }

    @Nonnull
    private Generic substitute(@Nonnull Map<Variable, Generic> content) {
        // sum = sumElement_0 + sumElement_1 + ... + sumElement_size
        Generic sum = JsclInteger.ZERO;

        for (int i = 0; i < size; i++) {
            final Literal literal = literals[i];

            // sumElement = variable_1 ^ power_1 * variable_2 ^ power_2 * ... * variable_size ^ power_size
            Generic sumElement = coefficients[i];

            for (int j = 0; j < literal.size(); j++) {
                final Variable variable = literal.getVariable(j);

                final int power = literal.getPower(j);
                final Generic contentVariable = content.get(variable);
                Generic b = pow(contentVariable, power);

                if (Matrix.isMatrixProduct(sumElement, b)) {
                    throw new ArithmeticException("Should not be matrix!");
                }

                sumElement = sumElement.multiply(b);
            }

            sum = sum.add(sumElement);
        }

        return sum;
    }

    @Nonnull
    private Generic pow(@Nonnull Generic g, int power) {
        switch (power) {
            case 0:
                return JsclInteger.valueOf(1);
            case 1:
                return g;
            default:
                return g.pow(power);
        }
    }

    public Generic expand() {
        return substitute(literalScm().content(EXPAND_CONVERTER));
    }

    public Generic factorize() {
        return Factorization.compute(substitute(literalScm().content(FACTORIZE_CONVERTER)));
    }

    public Generic elementary() {
        return substitute(literalScm().content(ELEMENTARY_CONVERTER));
    }

    public Generic simplify() {
        return Simplification.compute(this);
    }

    public Generic numeric() {
        try {
            return integerValue().numeric();
        } catch (NotIntegerException ex) {
            return substitute(literalScm().content(NUMERIC_CONVERTER));
        }
    }

    @Nonnull
    public Generic valueOf(@Nonnull Generic generic) {
        return newInstance(0).init(generic);
    }

    @Nonnull
    public Generic[] sumValue() {
        final Generic result[] = new Generic[size];

        for (int i = 0; i < result.length; i++) {
            result[i] = valueOf(literals[i], coefficients[i]);
        }

        return result;
    }

    @Nonnull
    public Generic[] productValue() throws NotProductException {
        if (size == 0) {
            return new Generic[]{JsclInteger.valueOf(0)};
        } else if (size == 1) {
            final Literal l = literals[0];
            final JsclInteger k = coefficients[0];

            Generic productElements[] = l.productValue();
            if (k.compareTo(JsclInteger.valueOf(1)) == 0) {
                return productElements;
            } else {
                final Generic result[] = new Generic[productElements.length + 1];
                System.arraycopy(productElements, 0, result, 1, productElements.length);
                result[0] = k;
                return result;
            }
        } else {
            throw new NotProductException();
        }
    }

    public Power powerValue() throws NotPowerException {
        if (size == 0) return new Power(JsclInteger.valueOf(0), 1);
        else if (size == 1) {
            Literal l = literals[0];
            JsclInteger en = coefficients[0];
            if (en.compareTo(JsclInteger.valueOf(1)) == 0) return l.powerValue();
            else if (l.degree() == 0) return en.powerValue();
            else throw new NotPowerException();
        } else throw new NotPowerException();
    }

    public Expression expressionValue() throws NotExpressionException {
        return this;
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

    public JsclInteger integerValue() throws NotIntegerException {
        if (size == 0) {
            return JsclInteger.valueOf(0);
        } else if (size == 1) {
            final Literal l = literals[0];
            final JsclInteger c = coefficients[0];

            if (l.degree() == 0) {
                return c;
            } else {
                throw NotIntegerException.get();
            }
        } else {
            throw NotIntegerException.get();
        }
    }

    @Override
    public double doubleValue() throws NotDoubleException {
        if (size == 0) {
            return 0f;
        } else if (size == 1) {
            final Literal l = literals[0];
            final JsclInteger c = coefficients[0];

            if (l.degree() == 0) {
                return c.doubleValue();
            } else {
                throw NotDoubleException.get();
            }
        } else {
            throw NotDoubleException.get();
        }
    }

    public Variable variableValue() throws NotVariableException {
        if (size == 0) {
            throw new NotVariableException();
        } else if (size == 1) {
            final Literal l = literals[0];
            final JsclInteger c = coefficients[0];
            if (c.compareTo(JsclInteger.valueOf(1)) == 0) {
                return l.variableValue();
            } else {
                throw new NotVariableException();
            }
        } else {
            throw new NotVariableException();
        }
    }

    public Variable[] variables() {
        return literalScm().variables();
    }

    public boolean isPolynomial(@Nonnull Variable variable) {
        boolean result = true;

        final Literal l = literalScm();
        for (int i = 0; i < l.size(); i++) {

            final Variable v = l.getVariable(i);
            if (!v.isConstant(variable) && !v.isIdentity(variable)) {
                result = false;
                break;
            }
        }

        return result;
    }

    public boolean isConstant(@Nonnull Variable variable) {

        Literal l = literalScm();
        for (int i = 0; i < l.size(); i++) {
            if (!l.getVariable(i).isConstant(variable)) {
                return false;
            }
        }

        return true;
    }

    public JsclVector grad(Variable variable[]) {
        Generic v[] = new Generic[variable.length];
        for (int i = 0; i < variable.length; i++) v[i] = derivative(variable[i]);
        return new JsclVector(v);
    }

    public Generic laplacian(Variable variable[]) {
        return grad(variable).divergence(variable);
    }

    public Generic dalembertian(Variable variable[]) {
        Generic a = derivative(variable[0]).derivative(variable[0]);
        for (int i = 1; i < 4; i++) a = a.subtract(derivative(variable[i]).derivative(variable[i]));
        return a;
    }

    public int compareTo(Expression expression) {
        int i1 = size;
        int i2 = expression.size;
        Literal l1 = i1 == 0 ? null : literals[--i1];
        Literal l2 = i2 == 0 ? null : expression.literals[--i2];
        while (l1 != null || l2 != null) {
            int c = l1 == null ? -1 : (l2 == null ? 1 : l1.compareTo(l2));
            if (c < 0) return -1;
            else if (c > 0) return 1;
            else {
                c = coefficients[i1].compareTo(expression.coefficients[i2]);
                if (c < 0) return -1;
                else if (c > 0) return 1;
                l1 = i1 == 0 ? null : literals[--i1];
                l2 = i2 == 0 ? null : expression.literals[--i2];
            }
        }
        return 0;
    }

    public int compareTo(@Nonnull Generic generic) {
        if (generic instanceof Expression) {
            return compareTo((Expression) generic);
        } else if (generic instanceof JsclInteger || generic instanceof Rational || generic instanceof NumericWrapper) {
            return compareTo(valueOf(generic));
        } else {
            return generic.valueOf(this).compareTo(generic);
        }
    }

    void init(Literal lit, JsclInteger integer) {
        if (integer.signum() != 0) {
            init(1);
            literals[0] = lit;
            coefficients[0] = integer;
        } else init(0);
    }

    void init(Expression expression) {
        init(expression.size);
        System.arraycopy(expression.literals, 0, literals, 0, size);
        System.arraycopy(expression.coefficients, 0, coefficients, 0, size);
    }

    void init(JsclInteger integer) {
        init(Literal.newInstance(), integer);
    }

    void init(Rational rational) {
        try {
            init(Literal.newInstance(), rational.integerValue());
        } catch (NotIntegerException e) {
            init(Literal.valueOf(rational.variableValue()), JsclInteger.valueOf(1));
        }
    }

    Expression init(@Nonnull Generic generic) {
        if (generic instanceof Expression) {
            init((Expression) generic);
        } else if (generic instanceof JsclInteger) {
            init((JsclInteger) generic);
        } else if (generic instanceof NumericWrapper) {
            init((NumericWrapper) generic);
        } else if (generic instanceof Rational) {
            init((Rational) generic);
        } else throw new ArithmeticException("Could not initialize expression with " + generic.getClass());

        return this;
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();

        if (signum() == 0) {
            result.append("0");
        }

        // result = coef[0] * literal[0] + coef[1] * literal[1] + ... +
        for (int i = 0; i < size; i++) {
            final Literal literal = literals[i];
            final JsclInteger coefficient = coefficients[i];

            if (coefficient.signum() > 0 && i > 0) {
                result.append("+");
            }

            if (literal.degree() == 0) {
                result.append(coefficient);
            } else {
                if (coefficient.abs().compareTo(JsclInteger.valueOf(1)) == 0) {
                    if (coefficient.signum() < 0) {
                        result.append("-");
                    }
                } else {
                    result.append(coefficient).append("*");
                }
                result.append(literal);
            }
        }

        return result.toString();
    }

    public String toJava() {
        final StringBuilder result = new StringBuilder();
        if (signum() == 0) {
            result.append("JsclDouble.valueOf(0)");
        }

        for (int i = 0; i < size; i++) {
            Literal l = literals[i];
            JsclInteger en = coefficients[i];
            if (i > 0) {
                if (en.signum() < 0) {
                    result.append(".subtract(");
                    en = (JsclInteger) en.negate();
                } else result.append(".add(");
            }
            if (l.degree() == 0) result.append(en.toJava());
            else {
                if (en.abs().compareTo(JsclInteger.valueOf(1)) == 0) {
                    if (en.signum() > 0) result.append(l.toJava());
                    else if (en.signum() < 0) result.append(l.toJava()).append(".negate()");
                } else result.append(en.toJava()).append(".multiply(").append(l.toJava()).append(")");
            }
            if (i > 0) result.append(")");
        }

        return result.toString();
    }

    public void toMathML(MathML element, @Nullable Object data) {
        MathML e1 = element.element("mrow");
        if (signum() == 0) {
            MathML e2 = element.element("mn");
            e2.appendChild(element.text("0"));
            e1.appendChild(e2);
        }
        for (int i = 0; i < size; i++) {
            Literal l = literals[i];
            JsclInteger en = coefficients[i];
            if (en.signum() > 0 && i > 0) {
                MathML e2 = element.element("mo");
                e2.appendChild(element.text("+"));
                e1.appendChild(e2);
            }
            if (l.degree() == 0) separateSign(e1, en);
            else {
                if (en.abs().compareTo(JsclInteger.valueOf(1)) == 0) {
                    if (en.signum() < 0) {
                        MathML e2 = element.element("mo");
                        e2.appendChild(element.text("-"));
                        e1.appendChild(e2);
                    }
                } else separateSign(e1, en);
                l.toMathML(e1, null);
            }
        }
        element.appendChild(e1);
    }

    @Nonnull
    @Override
    public Set<? extends Constant> getConstants() {
        final Set<Constant> result = new HashSet<Constant>();

        for (Literal literal : literals) {
            for (Variable variable : literal.variables()) {
                result.addAll(variable.getConstants());
            }
        }

        return result;
    }

    @Nonnull
    private Expression newInstance(int n) {
        return new Expression(n);
    }
}
