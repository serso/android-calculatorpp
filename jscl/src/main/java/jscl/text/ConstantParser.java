package jscl.text;

import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.util.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConstantParser implements Parser<Constant> {

    public static final Parser<Constant> parser = new ConstantParser();

    private ConstantParser() {
    }

    public Constant parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {

        final String name = CompoundIdentifier.parser.parse(p, previousSumElement);

        List<Generic> l = new ArrayList<Generic>();
        while (true) {
            try {
                l.add(Subscript.parser.parse(p, previousSumElement));
            } catch (ParseException e) {
                break;
            }
        }

        Integer prime = 0;
        try {
            prime = Prime.parser.parse(p, previousSumElement);
        } catch (ParseException e) {
        }

        return new Constant(name, prime, ArrayUtils.toArray(l, new Generic[l.size()]));
    }
}

class Prime implements Parser<Integer> {

    public static final Parser<Integer> parser = new Prime();

    private static final ArrayList<Parser<? extends Integer>> parsers = new ArrayList<Parser<? extends Integer>>(Arrays.asList(
            PrimeCharacters.parser,
            Superscript.parser));

    private static final Parser<Integer> internalParser = new MultiTryParser<Integer>(parsers);

    private Prime() {
    }

    public Integer parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        return internalParser.parse(p, previousSumElement);
    }
}

class Superscript implements Parser<Integer> {
    public static final Parser<Integer> parser = new Superscript();

    private Superscript() {
    }

    public Integer parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.getPosition().intValue();


        ParserUtils.tryToParse(p, pos0, '{');

        int result;
        try {
            result = IntegerParser.parser.parse(p, previousSumElement);
        } catch (ParseException e) {
            p.getPosition().setValue(pos0);
            throw e;
        }

        ParserUtils.tryToParse(p, pos0, '}');

        return result;
    }
}
