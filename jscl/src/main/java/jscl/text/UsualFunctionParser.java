package jscl.text;

import jscl.math.Generic;
import jscl.math.function.Function;
import jscl.math.function.FunctionsRegistry;
import jscl.text.msg.Messages;
import org.solovyev.common.math.MathRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 10/29/11
 * Time: 1:05 PM
 */
class UsualFunctionParser implements Parser<Function> {

    public static final Parser<Function> parser = new UsualFunctionParser();

    private MathRegistry<Function> functionsRegistry = FunctionsRegistry.getInstance();

    private UsualFunctionParser() {
    }

    static boolean valid(@Nullable String name) {
        return name != null && FunctionsRegistry.getInstance().getNames().contains(name);
    }

    public Function parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        final String name = Identifier.parser.parse(p, previousSumElement);

        if (!valid(name)) {
            ParserUtils.throwParseException(p, pos0, Messages.msg_13);
        }

        final Function result = functionsRegistry.get(name);

        if (result != null) {
            final Generic parameters[] = ParserUtils.parseWithRollback(new ParameterListParser(result.getMinParameters()), pos0, previousSumElement, p);

            if (result.getMinParameters() <= parameters.length && result.getMaxParameters() >= parameters.length) {
                result.setParameters(parameters);
            } else {
                ParserUtils.throwParseException(p, pos0, Messages.msg_14, parameters.length);
            }
        } else {
            ParserUtils.throwParseException(p, pos0, Messages.msg_13);
        }

        return result;
    }
}
