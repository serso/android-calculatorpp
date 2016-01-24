package jscl.text;

import jscl.math.Generic;
import jscl.math.function.Function;
import jscl.math.function.Root;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;

public class RootParser implements Parser<Function> {
    public static final Parser<Function> parser = new RootParser();

    private RootParser() {
    }

    public Function parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        final String name = Identifier.parser.parse(p, previousSumElement);
        if (name.compareTo("root") != 0) {
            ParserUtils.throwParseException(p, pos0, Messages.msg_11, "root");
        }

        final Generic subscript = ParserUtils.parseWithRollback(Subscript.parser, pos0, previousSumElement, p);
        final Generic parameters[] = ParserUtils.parseWithRollback(ParameterListParser.parser1, pos0, previousSumElement, p);

        return new Root(parameters, subscript);
    }
}
