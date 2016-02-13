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
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.StringTokenizer;

public class LocalePreference implements Preference<Locale> {

    @Nonnull
    private final StringPreference<Locale> stringPreference;

    private LocalePreference(@Nonnull String id, @Nullable Locale defaultValue, @Nonnull Mapper<Locale> localeMapper) {
        this.stringPreference = new StringPreference<Locale>(id, defaultValue, localeMapper);
    }

    private LocalePreference(@Nonnull String id, @Nullable Locale defaultValue) {
        this.stringPreference = new StringPreference<Locale>(id, defaultValue, DefaultLocaleMapper.getInstance());
    }

    @Nonnull
    public static LocalePreference of(@Nonnull String id, @Nullable Locale defaultValue, @Nonnull Mapper<Locale> localeMapper) {
        return new LocalePreference(id, defaultValue, localeMapper);
    }

    @Nonnull
    public static LocalePreference of(@Nonnull String id, @Nullable Locale defaultValue) {
        return new LocalePreference(id, defaultValue);
    }

    @Override
    @Nonnull
    public String getKey() {
        return stringPreference.getKey();
    }

    @Override
    public Locale getDefaultValue() {
        return stringPreference.getDefaultValue();
    }

    @Override
    public Locale getPreference(@Nonnull SharedPreferences preferences) {
        return stringPreference.getPreference(preferences);
    }

    @Override
    public Locale getPreferenceNoError(@Nonnull SharedPreferences preferences) {
        return stringPreference.getPreferenceNoError(preferences);
    }

    @Override
    public void putDefault(@Nonnull SharedPreferences preferences) {
        stringPreference.putDefault(preferences);
    }

    @Override
    public void putPreference(@Nonnull SharedPreferences preferences, @Nullable Locale value) {
        stringPreference.putPreference(preferences, value);
    }

    @Override
    public boolean isSet(@Nonnull SharedPreferences preferences) {
        return stringPreference.isSet(preferences);
    }

    @Override
    public boolean tryPutDefault(@Nonnull SharedPreferences preferences) {
        return stringPreference.tryPutDefault(preferences);
    }

    @Override
    public boolean isSameKey(@Nonnull String key) {
        return stringPreference.isSameKey(key);
    }

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

    private static final class DefaultLocaleMapper implements Mapper<Locale> {

        @Nonnull
        private static final String delimiter = ";";

        @Nonnull
        private static Mapper<Locale> instance = new DefaultLocaleMapper();

        private DefaultLocaleMapper() {
        }

        @Nonnull
        public static Mapper<Locale> getInstance() {
            return instance;
        }

        @Override
        public String formatValue(@Nullable Locale locale) throws IllegalArgumentException {
            assert locale != null;
            return locale.getLanguage() + delimiter + locale.getCountry() + delimiter + locale.getVariant();
        }

        @Override
        public Locale parseValue(@Nullable String s) throws IllegalArgumentException {
            final StringTokenizer st = new StringTokenizer(s, delimiter, false);

            final String language = st.nextToken();

            final String country;
            if (st.hasMoreTokens()) {
                country = st.nextToken();
            } else {
                country = "";
            }

            final String variant;
            if (st.hasMoreTokens()) {
                variant = st.nextToken();
            } else {
                variant = "";
            }

            return new Locale(language, country, variant);
        }
    }
}
