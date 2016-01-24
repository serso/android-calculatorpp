package jscl.text;

import jscl.math.Generic;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public class MultiTryParser<T> implements Parser<T> {

    @Nonnull
    private final List<Parser<? extends T>> parsers;

    public MultiTryParser(@Nonnull List<Parser<? extends T>> parsers) {
        this.parsers = parsers;
    }

    public T parse(@Nonnull Parameters p, Generic previousSumElement) throws ParseException {
        T result = null;

        for (final Iterator<Parser<? extends T>> it = parsers.iterator(); it.hasNext(); ) {
            try {
                final Parser<? extends T> parser = it.next();
                result = parser.parse(p, previousSumElement);
            } catch (ParseException e) {

                p.addException(e);

                if (!it.hasNext()) {
                    throw e;
                }
            }

            if (result != null) {
                break;
            }
        }

        return result;
    }
}
