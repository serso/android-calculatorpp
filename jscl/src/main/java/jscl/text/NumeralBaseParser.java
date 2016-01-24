package jscl.text;

import jscl.NumeralBase;
import jscl.math.Generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NumeralBaseParser implements Parser<NumeralBase> {

    public static final Parser<NumeralBase> parser = new NumeralBaseParser();

    private NumeralBaseParser() {
    }

    public NumeralBase parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) {
        int pos0 = p.position.intValue();

        NumeralBase result = p.context.getNumeralBase();

        ParserUtils.skipWhitespaces(p);

        for (NumeralBase numeralBase : NumeralBase.values()) {
            try {
                final String jsclPrefix = numeralBase.getJsclPrefix();
                ParserUtils.tryToParse(p, pos0, jsclPrefix);
                result = numeralBase;
                break;
            } catch (ParseException e) {
            }
        }


        return result;
    }
}
