package jscl.text;

import jscl.MathContext;
import jscl.math.Generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Main parser interface.
 * <p/>
 * Aim of parser is to convert input string expression into java objects
 *
 * @param <T> type of result object of parser
 */
public interface Parser<T> {

    /**
     * @param p                  parse parameters
     * @param previousSumElement sum element to the left of last + sign
     * @return parsed object of type T
     * @throws ParseException occurs if object could not be parsed from the string
     */
    T parse(@Nonnull Parameters p, @Nullable Generic previousSumElement) throws ParseException;

    class Parameters {

        @Nonnull
        public final String expression;

        @Nonnull
        public final MutableInt position;

        @Nonnull
        public final List<ParseException> exceptions = new ArrayList<ParseException>();

        @Nonnull
        public final MathContext context;

        /**
         * @param expression  expression to be parsed
         * @param position    current position of expression. Side effect: if parsing is successful this parameter should be increased on the number of parsed letters (incl whitespaces etc)
         * @param context math engine to be used in parsing
         */
        Parameters(@Nonnull String expression, @Nonnull MutableInt position, @Nonnull MathContext context) {
            this.expression = expression;
            this.position = position;
            this.context = context;
        }

        @Nonnull
        public static Parameters newInstance(@Nonnull String expression, @Nonnull MutableInt position, @Nonnull final MathContext mathEngine) {
            return new Parameters(expression, position, mathEngine);
        }

        public void addException(@Nonnull ParseException e) {
            if (!exceptions.contains(e)) {
                exceptions.add(e);
            }
        }
    }
}
