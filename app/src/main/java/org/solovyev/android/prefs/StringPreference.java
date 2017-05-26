/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.prefs;

import android.content.SharedPreferences;
import org.solovyev.common.text.EnumMapper;
import org.solovyev.common.text.Mapper;
import org.solovyev.common.text.StringMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link Preference} implementation which uses {@link String} way of storing object in persistence.
 * This class provides methods for mapping real java objects to String and vice versa.
 *
 * @param <T>
 */
public final class StringPreference<T> extends AbstractPreference<T> {

    @Nonnull
    private final Mapper<T> mapper;

    public StringPreference(@Nonnull String key, @Nullable T defaultValue, @Nonnull Mapper<T> mapper) {
        super(key, defaultValue);
        this.mapper = CachingMapper.of(mapper);
    }

    @Nonnull
    public static StringPreference<String> of(@Nonnull String key, @Nullable String defaultValue) {
        return new StringPreference<String>(key, defaultValue, StringMapper.getInstance());
    }

    @Nonnull
    public static <T> StringPreference<T> ofTypedValue(@Nonnull String key, @Nullable String defaultValue, @Nonnull Mapper<T> mapper) {
        return new StringPreference<T>(key, mapper.parseValue(defaultValue), mapper);
    }

    @Nonnull
    public static <T> StringPreference<T> ofTypedValue(@Nonnull String key, @Nullable T defaultValue, @Nonnull Mapper<T> mapper) {
        return new StringPreference<T>(key, defaultValue, mapper);
    }

    @Nonnull
    public static <T extends Enum> StringPreference<T> ofEnum(@Nonnull String key, @Nullable T defaultValue, @Nonnull Class<T> enumType) {
        return new StringPreference<T>(key, defaultValue, EnumMapper.of(enumType));
    }

    @Override
    protected T getPersistedValue(@Nonnull SharedPreferences preferences) {
        return mapper.parseValue(preferences.getString(getKey(), null));
    }

    @Override
    protected void putPersistedValue(@Nonnull SharedPreferences.Editor editor, @Nonnull T value) {
        editor.putString(getKey(), mapper.formatValue(value));
    }
}
