package jscl.math;

import javax.annotation.Nonnull;

public interface Arithmetic<T extends Arithmetic<T>> {

    @Nonnull
    T add(@Nonnull T that);

    @Nonnull
    T subtract(@Nonnull T that);

    @Nonnull
    T multiply(@Nonnull T that);

    @Nonnull
    T divide(@Nonnull T that) throws NotDivisibleException;

}
