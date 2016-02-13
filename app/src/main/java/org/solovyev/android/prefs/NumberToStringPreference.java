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
import org.solovyev.common.text.NumberMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NumberToStringPreference<N extends Number> extends AbstractPreference<N> {

    @Nonnull
    private final Mapper<N> mapper;

    private NumberToStringPreference(@Nonnull String key, @Nullable N defaultValue, @Nonnull Class<N> clazz) {
        super(key, defaultValue);

        this.mapper = NumberMapper.of(clazz);
    }

    @Nonnull
    public static <N extends Number> NumberToStringPreference<N> of(@Nonnull String key, @Nullable N defaultValue, @Nonnull Class<N> clazz) {
        return new NumberToStringPreference<N>(key, defaultValue, clazz);
    }

    @Override
    protected N getPersistedValue(@Nonnull SharedPreferences preferences) {
        return mapper.parseValue(preferences.getString(getKey(), "0"));
    }

    @Override
    protected void putPersistedValue(@Nonnull SharedPreferences.Editor editor, @Nonnull N value) {
        editor.putString(getKey(), mapper.formatValue(value));
    }

}
