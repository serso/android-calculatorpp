package jscl.text;

import javax.annotation.Nonnull;

abstract class AbstractConverter<T, K> implements Parser<K> {
    @Nonnull
    protected final Parser<T> parser;

    AbstractConverter(@Nonnull Parser<T> parser) {
        this.parser = parser;
    }

}
