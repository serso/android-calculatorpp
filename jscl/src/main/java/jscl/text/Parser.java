package jscl.text;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.JsclMathEngine;
import jscl.MathContext;
import jscl.math.Generic;

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
        private static final ThreadLocal<Parameters> instance = new ThreadLocal<Parameters>() {
            @Override
            protected Parameters initialValue() {
                return new Parameters("", JsclMathEngine.getInstance());
            }
        };

        @Nonnull
        public String expression;

        @Nonnull
        public final MutableInt position = new MutableInt(0);

        @Nonnull
        public final List<ParseException> exceptions = new ArrayList<ParseException>();

        @Nonnull
        public final MathContext context;

        @Nonnull
        public final ExceptionsPool exceptionsPool = new ExceptionsPool();

        /**
         * @param expression  expression to be parsed
         * @param context math engine to be used in parsing
         */
        Parameters(@Nonnull String expression, @Nonnull MathContext context) {
            this.expression = expression;
            this.context = context;
        }

        @Nonnull
        public static Parameters get(@Nonnull String expression) {
            final Parameters parameters = instance.get();
            parameters.expression = expression;
            parameters.reset();
            return parameters;
        }

        public void reset() {
            position.setValue(0);
            exceptions.clear();
        }

        public void addException(@Nonnull ParseException e) {
            if (!exceptions.contains(e)) {
                exceptions.add(e);
            }
        }
    }
}
