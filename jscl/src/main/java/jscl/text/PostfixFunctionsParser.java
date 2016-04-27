package jscl.text;

import jscl.math.Generic;
import jscl.math.function.PostfixFunctionsRegistry;
import jscl.math.operator.Operator;
import jscl.math.operator.TripleFactorial;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static java.util.Collections.singletonList;

public class PostfixFunctionsParser implements Parser<Generic> {

    private static final PostfixFunctionsRegistry registry = PostfixFunctionsRegistry.getInstance();
    private static final PostfixFunctionParser tripleFactorialParser = new PostfixFunctionParser(TripleFactorial.NAME);

    @Nonnull
    private final Generic content;

    public PostfixFunctionsParser(@Nonnull Generic content) {
        this.content = content;
    }

    private static Generic parsePostfix(@Nonnull List<String> names,
                                        final Generic content,
                                        @Nullable final Generic previousSumElement,
                                        @Nonnull final Parameters p) throws ParseException {
        checkTripleFactorial(previousSumElement, p);

        for (int i = 0; i < names.size(); i++) {
            final PostfixFunctionParser parser = new PostfixFunctionParser(names.get(i));
            final String functionName = parser.parse(p, previousSumElement);
            if (functionName == null) {
                continue;
            }
            final Generic[] parameters = previousSumElement == null ? new Generic[]{content} : new Generic[]{content, previousSumElement};
            final Operator function = registry.get(functionName, parameters);

            if (function != null) {
                return parsePostfix(names, function.expressionValue(), previousSumElement, p);
            }

            throw p.exceptionsPool.obtain(p.position.intValue(), p.expression, Messages.msg_4, singletonList(functionName));
        }
        return content;
    }

    private static void checkTripleFactorial(@Nullable Generic previousSumElement, @Nonnull Parameters p) throws ParseException {
        if (tripleFactorialParser.parse(p, previousSumElement) != null) {
            throw p.exceptionsPool.obtain(p.position.intValue(), p.expression, Messages.msg_18);
        }
    }

    public Generic parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        return parsePostfix(registry.getNames(), content, previousSumElement, p);
    }
}
