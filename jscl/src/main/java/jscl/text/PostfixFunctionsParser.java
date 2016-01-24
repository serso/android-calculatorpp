package jscl.text;

import jscl.math.Generic;
import jscl.math.function.PostfixFunctionsRegistry;
import jscl.math.operator.Operator;
import jscl.math.operator.TripleFactorial;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: serso
 * Date: 10/31/11
 * Time: 11:20 PM
 */
public class PostfixFunctionsParser implements Parser<Generic> {

    @Nonnull
    private final Generic content;

    public PostfixFunctionsParser(@Nonnull Generic content) {
        this.content = content;
    }

    private static Generic parsePostfix(@Nonnull List<PostfixFunctionParser> parsers,
                                        Generic content,
                                        @Nullable final Generic previousSumElement,
                                        @Nonnull final Parameters p) throws ParseException {
        Generic result = content;

        for (PostfixFunctionParser parser : parsers) {
            final PostfixFunctionParser.Result postfixResult = parser.parse(p, previousSumElement);
            if (postfixResult.isPostfixFunction()) {
                final Operator postfixFunction;

                if (previousSumElement == null) {
                    postfixFunction = PostfixFunctionsRegistry.getInstance().get(postfixResult.getPostfixFunctionName(), new Generic[]{content});
                } else {
                    postfixFunction = PostfixFunctionsRegistry.getInstance().get(postfixResult.getPostfixFunctionName(), new Generic[]{content, previousSumElement});
                }

                if (postfixFunction == null) {
                    if (TripleFactorial.NAME.equals(postfixResult.getPostfixFunctionName())) {
                        throw p.exceptionsPool.obtain(p.position.intValue(), p.expression, Messages.msg_18);
                    } else {
                        throw p.exceptionsPool.obtain(p.position.intValue(), p.expression, Messages.msg_4, Collections.singletonList(postfixResult.getPostfixFunctionName()));
                    }
                }

                result = parsePostfix(parsers, postfixFunction.expressionValue(), previousSumElement, p);
            }
        }

        return result;
    }

    public Generic parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {

        final List<String> postfixFunctionNames = PostfixFunctionsRegistry.getInstance().getNames();

        final List<PostfixFunctionParser> parsers = new ArrayList<PostfixFunctionParser>(postfixFunctionNames.size());
        parsers.add(new PostfixFunctionParser(TripleFactorial.NAME));
        for (String postfixFunctionName : postfixFunctionNames) {
            parsers.add(new PostfixFunctionParser(postfixFunctionName));
        }

        return parsePostfix(parsers, content, previousSumElement, p);
    }
}
