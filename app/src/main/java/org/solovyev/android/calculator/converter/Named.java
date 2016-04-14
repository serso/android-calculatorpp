package org.solovyev.android.calculator.converter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import javax.annotation.Nonnull;

class Named<T> {
    @NonNull
    public final T item;
    @NonNull
    public final CharSequence name;

    private Named(@NonNull T item, @Nonnull String name) {
        this.item = item;
        this.name = name;
    }

    @NonNull
    static <T> Named<T> create(@NonNull T item, @Nonnull String name) {
        return new Named<T>(item, name);
    }

    @NonNull
    static <T> Named<T> create(@NonNull T item, @StringRes int name, @NonNull Context context) {
        return create(item, name == 0 ? item.toString() : context.getString(name));
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Named<?> that = (Named<?>) o;
        return item.equals(that.item);

    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }
}
