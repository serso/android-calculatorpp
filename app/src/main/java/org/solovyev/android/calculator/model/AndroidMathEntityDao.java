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

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.solovyev.android.calculator.CalculatorApplication;
import org.solovyev.android.calculator.MathEntityDao;
import org.solovyev.android.calculator.MathEntityPersistenceContainer;
import org.solovyev.android.calculator.MathPersistenceEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.StringWriter;

public class AndroidMathEntityDao<T extends MathPersistenceEntity> implements MathEntityDao<T> {

    @Nonnull
    private static final String TAG = AndroidMathEntityDao.class.getSimpleName();

    @Nullable
    private final String preferenceString;

    @Nonnull
    private final Context context;

    @Nullable
    private final Class<? extends MathEntityPersistenceContainer<T>> persistenceContainerClass;

    public AndroidMathEntityDao(@Nullable String preferenceString,
                                @Nonnull Application application,
                                @Nullable Class<? extends MathEntityPersistenceContainer<T>> persistenceContainerClass) {
        this.preferenceString = preferenceString;
        this.context = application;
        this.persistenceContainerClass = persistenceContainerClass;
    }

    @Override
    public void save(@Nonnull MathEntityPersistenceContainer<T> container) {
        if (preferenceString != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();

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
    }

    @Nullable
    @Override
    public MathEntityPersistenceContainer<T> load() {
        if (persistenceContainerClass != null && preferenceString != null) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

            if (preferences != null) {
                final String value = preferences.getString(preferenceString, null);
                if (value != null) {
                    final Serializer serializer = new Persister();
                    try {
                        return serializer.read(persistenceContainerClass, value);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public String getDescription(@Nonnull String descriptionId) {
        final Resources resources = context.getResources();

        final int stringId = resources.getIdentifier(descriptionId, "string", CalculatorApplication.class.getPackage().getName());
        try {
            return resources.getString(stringId);
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }
}
