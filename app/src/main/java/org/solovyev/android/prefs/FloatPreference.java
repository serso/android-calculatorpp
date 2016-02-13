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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FloatPreference extends AbstractPreference<Float> {

    private FloatPreference(@Nonnull String key, @Nullable Float defaultValue) {
        super(key, defaultValue);
    }

    @Nonnull
    public static FloatPreference of(@Nonnull String key, @Nullable Float defaultValue) {
        return new FloatPreference(key, defaultValue);
    }

    @Override
    protected Float getPersistedValue(@Nonnull SharedPreferences preferences) {
        return preferences.getFloat(getKey(), -1f);
    }

    @Override
    protected void putPersistedValue(@Nonnull SharedPreferences.Editor editor, @Nonnull Float value) {
        editor.putFloat(getKey(), value);
    }
}
