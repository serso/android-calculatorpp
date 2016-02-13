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

/**
 * Class for working with android preferences: can save and load preferences, convert them to custom java objects
 * and use default value;
 *
 * @param <T> type of java object preference
 */
public interface Preference<T> {

    /**
     * Method returns key of preference used in android: the key with which current preference is saved in persistence
     *
     * @return android preference key
     */
    @Nonnull
    String getKey();

    /**
     * @return default preference value, may be null
     */
    T getDefaultValue();

    /**
     * NOTE: this method can throw runtime exceptions if errors occurred while extracting preferences values
     *
     * @param preferences application preferences
     * @return value from preference, default value if no value in preference was found
     */
    T getPreference(@Nonnull SharedPreferences preferences);

    /**
     * NOTE: this method SHOULD not throw any runtime exceptions BUT return default value if any error occurred
     *
     * @param preferences application preferences
     * @return value from preference, default value if no value in preference was found or error occurred
     */
    T getPreferenceNoError(@Nonnull SharedPreferences preferences);

    /**
     * Method puts (saves) preference represented by <code>value</code> in <code>preferences</code> container
     *
     * @param preferences preferences container
     * @param value       value to be saved
     */
    void putPreference(@Nonnull SharedPreferences preferences, @Nullable T value);

    /**
     * Method saves default value in <code>preferences</code> container.
     * Should behave exactly as <code>p.putPreference(preferences, p.getDefaultValue())</code>
     *
     * @param preferences preferences container
     */
    void putDefault(@Nonnull SharedPreferences preferences);

    /**
     * @param preferences preferences container
     * @return true if any value is saved in preferences container, false - otherwise
     */
    boolean isSet(@Nonnull SharedPreferences preferences);

    /**
     * Method applies default value to preference only if explicit value is not set
     *
     * @param preferences preferences container
     * @return true if default values have been applied, false otherwise
     */
    boolean tryPutDefault(@Nonnull SharedPreferences preferences);

    /**
     * @param key preference key
     * @return true if current preferences has the same key
     */
    boolean isSameKey(@Nonnull String key);
}
