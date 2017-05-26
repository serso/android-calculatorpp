package org.solovyev.android.prefs;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.solovyev.common.text.Mapper;

import java.util.Objects;

import javax.annotation.Nullable;

public final class CachingMapper<T> implements Mapper<T> {

    private static class CachedEntry<T> {
        @Nullable
        private String value;
        @Nullable
        private T object;
    }

    @NonNull
    private final Mapper<T> mapper;
    @Nullable
    private CachedEntry<T> cachedEntry = null;

    private CachingMapper(@NonNull Mapper<T> mapper) {
        this.mapper = mapper;
    }

    @NonNull
    public static <T> Mapper<T> of(@NonNull Mapper<T> mapper) {
        if (mapper.getClass().equals(CachingMapper.class)) {
            return mapper;
        }
        return new CachingMapper<>(mapper);
    }

    @Nullable
    @Override
    public synchronized T parseValue(@Nullable String value) throws IllegalArgumentException {
        if (cachedEntry == null) {
            cachedEntry = new CachedEntry<>();
        } else if (TextUtils.equals(cachedEntry.value, value)) {
            return cachedEntry.object;
        }
        cachedEntry.value = value;
        cachedEntry.object = mapper.parseValue(value);
        return cachedEntry.object;
    }

    @Nullable
    @Override
    public synchronized String formatValue(@Nullable T object) throws IllegalArgumentException {
        if (cachedEntry == null) {
            cachedEntry = new CachedEntry<>();
        } else if (Objects.equals(cachedEntry.object, object)) {
            return cachedEntry.value;
        }
        cachedEntry.object = object;
        cachedEntry.value = mapper.formatValue(object);
        return cachedEntry.value;
    }
}
