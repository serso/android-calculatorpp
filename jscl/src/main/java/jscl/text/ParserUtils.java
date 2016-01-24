package jscl.text;

import jscl.math.Generic;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;

/**
 * User: serso
 * Date: 10/27/11
 * Time: 2:40 PM
 */
public class ParserUtils {

    public static void checkInterruption() {
        if (Thread.currentThread().isInterrupted()) {
            throw new ParseInterruptedException("Interrupted!");
        }
    }

    public static void skipWhitespaces(@Nonnull Parser.Parameters p) {
        final MutableInt position = p.position;
        final String expression = p.expression;

        while (position.intValue() < expression.length() && Character.isWhitespace(expression.charAt(position.intValue()))) {
            position.increment();
        }
    }

    public static void tryToParse(@Nonnull Parser.Parameters p,
                                  int pos0,
                                  char ch) throws ParseException {
        skipWhitespaces(p);

        if (p.position.intValue() < p.expression.length()) {
            char actual = p.expression.charAt(p.position.intValue());
            if (actual == ch) {
                p.position.increment();
            } else {
                throwParseException(p, pos0, Messages.msg_12, ch);
            }
        } else {
            throwParseException(p, pos0, Messages.msg_12, ch);
        }
    }

    public static void tryToParse(@Nonnull Parser.Parameters p,
                                  int pos0,
                                  @Nonnull String s) throws ParseException {
        skipWhitespaces(p);

        if (p.position.intValue() < p.expression.length()) {
            if (p.expression.startsWith(s, p.position.intValue())) {
                p.position.add(s.length());
            } else {
                throwParseException(p, pos0, Messages.msg_11, s);
            }
        } else {
            throwParseException(p, pos0, Messages.msg_11, s);
        }
    }

    public static void throwParseException(@Nonnull Parser.Parameters p,
                                           int pos0,
                                           @Nonnull String messageId,
                                           Object... parameters) throws ParseException {
        final MutableInt position = p.position;
        final ParseException parseException = new ParseException(messageId, position.intValue(), p.expression, parameters);
        position.setValue(pos0);
        throw parseException;
    }


    @Nonnull
    static <T> T parseWithRollback(@Nonnull Parser<T> parser,
                                   int initialPosition,
                                   @Nullable final Generic previousSumParser,
                                   @Nonnull final Parser.Parameters p) throws ParseException {
        T result;

        try {
            result = parser.parse(p, previousSumParser);
        } catch (ParseException e) {
            p.position.setValue(initialPosition);
            throw e;
        }

        return result;
    }

    public static <T> T[] copyOf(@Nonnull T[] array, int newLength) {
        return (T[]) copyOf(array, newLength, array.getClass());
    }

    public static <T> T[] copyOf(@Nonnull T[] array) {
        return (T[]) copyOf(array, array.length, array.getClass());
    }

    public static <T, U> T[] copyOf(U[] array, int newLength, Class<? extends T[]> newType) {
        T[] copy = ((Object) newType == (Object) Object[].class)
                ? (T[]) new Object[newLength]
                : (T[]) Array.newInstance(newType.getComponentType(), newLength);

        System.arraycopy(array, 0, copy, 0, Math.min(array.length, newLength));

        return copy;
    }
}
