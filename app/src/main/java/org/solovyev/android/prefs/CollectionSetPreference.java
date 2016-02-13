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
import org.solovyev.common.text.Mapper;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class CollectionSetPreference<C extends Collection<T>, T> extends AbstractPreference<C> {

    @Nonnull
    private final Mapper<T> mapper;

    protected CollectionSetPreference(@Nonnull String id, @Nonnull C defaultValue, @Nonnull Mapper<T> mapper) {
        super(id, defaultValue);
        this.mapper = mapper;
    }

    @Override
    protected C getPersistedValue(@Nonnull SharedPreferences preferences) {
        final Set<String> stringValues = preferences.getStringSet(getKey(), null);

        final C result = createCollection(stringValues.size());
        for (String stringValue : stringValues) {
            result.add(mapper.parseValue(stringValue));
        }

        return result;
    }

    @Nonnull
    protected abstract C createCollection(int size);

    @Override
    protected void putPersistedValue(@Nonnull SharedPreferences.Editor editor, @Nonnull C values) {

        final Set<String> result = new HashSet<String>(values.size());
        for (T value : values) {
            result.add(mapper.formatValue(value));
        }

        editor.putStringSet(getKey(), result);
    }
}
