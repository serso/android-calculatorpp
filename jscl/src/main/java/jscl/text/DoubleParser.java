package jscl.text;

import jscl.NumeralBase;
import jscl.math.Generic;
import jscl.math.NumericWrapper;
import jscl.math.numeric.Real;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DoubleParser implements Parser<NumericWrapper> {

    public static final Parser<NumericWrapper> parser = new DoubleParser();

    private static final List<Parser<Double>> parsers = Arrays.asList(
            Singularity.parser,
            FloatingPointLiteral.parser);

    private DoubleParser() {
    }

    @Nonnull
    public NumericWrapper parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        final Parser<Double> multiTryParser = new MultiTryParser<Double>(new ArrayList<Parser<? extends Double>>(parsers));
        return new NumericWrapper(Real.valueOf(multiTryParser.parse(p, previousSumElement)));
    }
}

class Singularity implements Parser<Double> {

    public static final Parser<Double> parser = new Singularity();

    private Singularity() {
    }

    @Nonnull
    public Double parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        double result = 0d;

        String s = Identifier.parser.parse(p, previousSumElement);
        if (s.equals("NaN")) {
            result = Double.NaN;
        } else if (s.equals("Infinity") || s.equals("∞")) {
            result = Double.POSITIVE_INFINITY;
        } else {
            ParserUtils.throwParseException(p, pos0, Messages.msg_10, "NaN", "∞");
        }

        return result;
    }
}

class FloatingPointLiteral implements Parser<Double> {

    public static final Parser<Double> parser = new FloatingPointLiteral();

    private FloatingPointLiteral() {
    }

    public Double parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        final NumeralBase nb = NumeralBaseParser.parser.parse(p, previousSumElement);

        final StringBuilder result = new StringBuilder();

        boolean digits = false;
        boolean point = false;
        boolean exponent = false;

        final Digits digitsParser = new Digits(nb);

        try {
            result.append(digitsParser.parse(p, previousSumElement));
            digits = true;
        } catch (ParseException e) {
            p.exceptionsPool.release(e);
        }

        try {
            DecimalPoint.parser.parse(p, previousSumElement);
            result.append(".");
            point = true;
        } catch (ParseException e) {
            if (!digits) {
                p.position.setValue(pos0);
                throw e;
            } else {
                p.exceptionsPool.release(e);
            }
        }

        if (point && nb != NumeralBase.dec) {
            ParserUtils.throwParseException(p, pos0, Messages.msg_15);
        }

        try {
            result.append(digitsParser.parse(p, previousSumElement));
        } catch (ParseException e) {
            if (!digits) {
                p.position.setValue(pos0);
                throw e;
            } else {
                p.exceptionsPool.release(e);
            }
        }

        try {
            result.append(ExponentPart.parser.parse(p, previousSumElement));
            exponent = true;
        } catch (ParseException e) {
            if (!point) {
                p.position.setValue(pos0);
                throw e;
            } else {
                p.exceptionsPool.release(e);
            }
        }

        if (exponent && nb != NumeralBase.dec) {
            ParserUtils.throwParseException(p, pos0, Messages.msg_15);
        }

        final String doubleString = result.toString();
        try {
            return nb.toDouble(doubleString);
        } catch (NumberFormatException e) {
            throw p.exceptionsPool.obtain(p.position.intValue(), p.expression, Messages.msg_8, Collections.singletonList(doubleString));
        }
    }
}

class DecimalPoint implements Parser<Void> {

    public static final Parser<Void> parser = new DecimalPoint();

    private DecimalPoint() {
    }

    @Nullable
    public Void parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        ParserUtils.skipWhitespaces(p);

        ParserUtils.tryToParse(p, pos0, '.');

        return null;
    }
}

class ExponentPart implements Parser<String> {

    public static final Parser<String> parser = new ExponentPart();

    private ExponentPart() {
    }

    @Nonnull
    public String parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        final StringBuilder result = new StringBuilder();

        ParserUtils.skipWhitespaces(p);

        if (p.position.intValue() < p.expression.length() && (p.expression.charAt(p.position.intValue()) == 'e' || p.expression.charAt(p.position.intValue()) == 'E')) {
            char c = p.expression.charAt(p.position.intValue());
            p.position.increment();
            result.append(c);
        } else {
            ParserUtils.throwParseException(p, pos0, Messages.msg_10, 'e', 'E');
        }

        try {
            result.append(SignedInteger.parser.parse(p, previousSumElement));
        } catch (ParseException e) {
            p.position.setValue(pos0);
            throw e;
        }

        return result.toString();
    }
}

class SignedInteger implements Parser<String> {

    public static final Parser<String> parser = new SignedInteger();

    private SignedInteger() {
    }

    @Nonnull
    public String parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        final int pos0 = p.position.intValue();

        final StringBuilder result = new StringBuilder();

        ParserUtils.skipWhitespaces(p);

        final int pos1 = p.position.intValue();
        if (pos1 < p.expression.length() && (p.expression.charAt(pos1) == '+' || MinusParser.isMinus(p.expression.charAt(pos1)))) {
            final char c = p.expression.charAt(pos1);
            p.position.increment();
            result.append(c);
        }

        try {
            result.append(IntegerParser.parser.parse(p, previousSumElement).intValue());
        } catch (ParseException e) {
            p.position.setValue(pos0);
            throw e;
        }

        return result.toString();
    }
}
