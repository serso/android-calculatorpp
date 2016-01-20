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

package org.solovyev.android.calculator.model;

import android.content.SharedPreferences;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.solovyev.android.calculator.PersistedEntitiesContainer;
import org.solovyev.android.calculator.PersistedEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.StringWriter;

public class EntityDao<T extends PersistedEntity> {

    @Nonnull
    private final String preferenceString;

    @Nonnull
    private final Class<? extends PersistedEntitiesContainer<T>> persistenceContainerClass;
    @Nonnull
    private final SharedPreferences preferences;

    public EntityDao(@Nonnull String preferenceString,
                     @Nonnull Class<? extends PersistedEntitiesContainer<T>> persistenceContainerClass, @Nonnull SharedPreferences preferences) {
        this.preferenceString = preferenceString;
        this.persistenceContainerClass = persistenceContainerClass;
        this.preferences = preferences;
    }

    public void save(@Nonnull PersistedEntitiesContainer<T> container) {
        final SharedPreferences.Editor editor = preferences.edit();

        final StringWriter sw = new StringWriter();
        final Serializer serializer = new Persister();
        try {
            serializer.write(container, sw);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        editor.putString(preferenceString, sw.toString());
        editor.apply();
    }

    @Nullable
    public PersistedEntitiesContainer<T> load() {
        final String value = preferences.getString(preferenceString, null);
        if (value != null) {
            final Serializer serializer = new Persister();
            try {
                return serializer.read(persistenceContainerClass, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}
