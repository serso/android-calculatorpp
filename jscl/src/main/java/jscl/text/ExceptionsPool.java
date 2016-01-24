package jscl.text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExceptionsPool {

    @Nonnull
    private final List<ParseException> list = new ArrayList<>();

    @Nonnull
    public ParseException obtain(int position, @Nonnull String expression, @Nonnull String messageCode) {
        return obtain(position, expression, messageCode, Collections.emptyList());
    }

    @Nonnull
    public ParseException obtain(int position, @Nonnull String expression, @Nonnull String messageCode, @Nullable Object[] messageArgs) {
        return obtain(position, expression, messageCode, messageArgs == null || messageArgs.length == 0 ? java.util.Collections.emptyList() : Arrays.asList(messageArgs));
    }

    @Nonnull
    public ParseException obtain(int position, @Nonnull String expression, @Nonnull String messageCode, @Nonnull List<?> messagesArgs) {
        final ParseException exception = !list.isEmpty() ? list.remove(list.size() - 1) : new ParseException();
        exception.set(position, expression, messageCode, messagesArgs);
        return exception;
    }

    public void release(@Nonnull ParseException e) {
        list.add(e);
    }
}
