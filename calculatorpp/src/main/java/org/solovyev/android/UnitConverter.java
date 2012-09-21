package org.solovyev.android;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 4/21/12
 * Time: 7:53 PM
 */
public interface UnitConverter<T> {

    boolean isSupported(@NotNull UnitType<?> from, @NotNull UnitType<T> to);

    @NotNull
    Unit<T> convert(@NotNull Unit<?> from, @NotNull UnitType<T> toType);

    public static class Dummy implements UnitConverter<Object> {

        @NotNull
        private static final Dummy instance = new Dummy();

        @NotNull
        public static <T> UnitConverter<T> getInstance() {
            return (UnitConverter<T>)instance;
        }

        private Dummy() {
        }

        @Override
        public boolean isSupported(@NotNull UnitType<?> from, @NotNull UnitType<Object> to) {
            return false;
        }

        @NotNull
        @Override
        public Unit<Object> convert(@NotNull Unit<?> from, @NotNull UnitType<Object> toType) {
            throw new IllegalArgumentException();
        }
    }

}
