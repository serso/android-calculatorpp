package jscl.text;

import jscl.NumeralBase;
import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;

public class JsclIntegerParser implements Parser<JsclInteger> {

    public static final Parser<JsclInteger> parser = new JsclIntegerParser();

    private JsclIntegerParser() {
    }

    public JsclInteger parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        final NumeralBase nb = NumeralBaseParser.parser.parse(p, previousSumElement);

        final StringBuilder result = new StringBuilder();

        try {
            result.append(new Digits(nb).parse(p, previousSumElement));
        } catch (ParseException e) {
            p.position.setValue(pos0);
            throw e;
        }

        final String number = result.toString();
        try {
            return nb.toJsclInteger(number);
        } catch (NumberFormatException e) {
            throw p.exceptionsPool.obtain(p.position.intValue(), p.expression, Messages.msg_8, Collections.singletonList(number));
        }
    }
}
