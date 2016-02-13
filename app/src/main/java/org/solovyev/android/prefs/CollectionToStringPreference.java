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
import org.solovyev.common.text.ListMapper;
import org.solovyev.common.text.Mapper;
import org.solovyev.common.text.StringMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class CollectionToStringPreference<C extends Collection<T>, T> extends AbstractPreference<C> {

    @Nonnull
    private final Mapper<C> mapper;

    private CollectionToStringPreference(@Nonnull String key, @Nullable C defaultValue, @Nonnull Mapper<C> mapper) {
        super(key, defaultValue);
        this.mapper = mapper;
    }

    @Nonnull
    public static <T> CollectionToStringPreference<List<T>, T> forList(@Nonnull String key, @Nullable List<T> defaultValue, @Nonnull Mapper<List<T>> mapper) {
        return new CollectionToStringPreference<List<T>, T>(key, defaultValue, mapper);
    }

    @Nonnull
    public static <T> CollectionToStringPreference<List<T>, T> forTypedList(@Nonnull String key, @Nullable List<T> defaultValue, @Nonnull Mapper<T> mapper) {
        return new CollectionToStringPreference<List<T>, T>(key, defaultValue, ListMapper.newInstance(mapper));
    }

    @Nonnull
    public static CollectionToStringPreference<List<String>, String> forStringList(@Nonnull String key, @Nullable List<String> defaultValue) {
        return new CollectionToStringPreference<List<String>, String>(key, defaultValue, ListMapper.newInstance(StringMapper.getInstance()));
    }

   @Override
    protected C getPersistedValue(@Nonnull SharedPreferences preferences) {
        return mapper.parseValue(preferences.getString(getKey(), null));
    }

    @Override
    protected void putPersistedValue(@Nonnull SharedPreferences.Editor editor, @Nonnull C values) {
        editor.putString(getKey(), mapper.formatValue(values));
    }
}
