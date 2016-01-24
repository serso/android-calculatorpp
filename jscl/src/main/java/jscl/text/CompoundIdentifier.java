package jscl.text;

import jscl.math.Generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CompoundIdentifier implements Parser<String> {

    public static final Parser<String> parser = new CompoundIdentifier();

    private CompoundIdentifier() {
    }

    @Nonnull
    public String parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) throws ParseException {
        int pos0 = p.getPosition().intValue();

        StringBuilder result = new StringBuilder();

        ParserUtils.skipWhitespaces(p);
        try {
            String s = Identifier.parser.parse(p, previousSumElement);
            result.append(s);
        } catch (ParseException e) {
            p.getPosition().setValue(pos0);
            throw e;
        }

        while (true) {
            try {
                final String dotAndId = DotAndIdentifier.parser.parse(p, previousSumElement);
                // NOTE: '.' must be appended after parsing
                result.append(".").append(dotAndId);
            } catch (ParseException e) {
                break;
            }
        }

        return result.toString();
    }
}

class DotAndIdentifier implements Parser<String> {

    public static final Parser<String> parser = new DotAndIdentifier();

    private DotAndIdentifier() {
    }

    public String parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.getPosition().intValue();

        ParserUtils.tryToParse(p, pos0, '.');

        String result;
        try {
            result = Identifier.parser.parse(p, previousSumElement);
        } catch (ParseException e) {
            p.getPosition().setValue(pos0);
            throw e;
        }

        return result;
    }
}
