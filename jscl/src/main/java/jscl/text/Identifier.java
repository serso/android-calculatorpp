package jscl.text;

import jscl.math.Generic;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class Identifier implements Parser<String> {

    public static final Parser<String> parser = new Identifier();
    private final static List<Character> allowedCharacters = Arrays.asList('√', '∞', 'π', '∂', '∏', 'Σ', '∫');

    private Identifier() {
    }

    private static boolean isValidFirstCharacter(char ch) {
        return Character.isLetter(ch) || allowedCharacters.contains(ch);
    }

    private static boolean isValidNotFirstCharacter(@Nonnull String string, @Nonnull MutableInt position) {
        final char ch = string.charAt(position.intValue());
        return Character.isLetter(ch) || Character.isDigit(ch) || ch == '_';
    }

    // returns getVariable/constant getName
    @Nonnull
    public String parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) throws ParseException {
        int pos0 = p.position.intValue();

        final StringBuilder result = new StringBuilder();

        ParserUtils.skipWhitespaces(p);

        if (p.position.intValue() < p.expression.length() && isValidFirstCharacter(p.expression.charAt(p.position.intValue()))) {
            result.append(p.expression.charAt(p.position.intValue()));
            p.position.increment();
        } else {
            ParserUtils.throwParseException(p, pos0, Messages.msg_5);
        }

        while (p.position.intValue() < p.expression.length() && isValidNotFirstCharacter(p.expression, p.position)) {
            result.append(p.expression.charAt(p.position.intValue()));
            p.position.increment();
        }

        return result.toString();
    }


}
