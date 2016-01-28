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

package org.solovyev.android.calculator;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.squareup.otto.Bus;

import org.json.JSONArray;
import org.json.JSONException;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.json.Json;
import org.solovyev.android.calculator.json.Jsonable;
import org.solovyev.android.io.FileSaver;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.math.MathRegistry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

public abstract class BaseEntitiesRegistry<T extends MathEntity> implements EntitiesRegistry<T> {

    @Nonnull
    protected final Object lock = this;
    @Nonnull
    private final MathRegistry<T> mathRegistry;
    @Nonnull
    private final String prefix;
    @NonNull
    private final WriteTask writeTask = new WriteTask();
    @Inject
    Handler handler;
    @Inject
    SharedPreferences preferences;
    @Inject
    Application application;
    @Inject
    Bus bus;
    @Inject
    ErrorReporter errorReporter;
    @Inject
    @Named(AppModule.THREAD_BACKGROUND)
    Executor backgroundThread;

    // synchronized on lock
    private boolean initialized;

    protected BaseEntitiesRegistry(@Nonnull MathRegistry<T> mathRegistry,
                                   @Nonnull String prefix) {
        this.mathRegistry = mathRegistry;
        this.prefix = prefix;
    }


    @Nonnull
    protected abstract Map<String, String> getSubstitutes();

    @Nullable
    @Override
    public String getDescription(@Nonnull String name) {
        final String stringName;

        final Map<String, String> substitutes = getSubstitutes();
        final String substitute = substitutes.get(name);
        if (substitute == null) {
            stringName = prefix + name;
        } else {
            stringName = prefix + substitute;
        }

        return getDescription(App.getApplication(), stringName);
    }

    @Override
    public void init() {
        setInitialized();
    }

    @Nullable
    public String getDescription(@Nonnull Context context, @Nonnull String descriptionId) {
        final Resources resources = context.getResources();

        final int stringId = resources.getIdentifier(descriptionId, "string", CalculatorApplication.class.getPackage().getName());
        try {
            return resources.getString(stringId);
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }

    @NonNull
    protected final <E> List<E> loadEntities(@NonNull Json.Creator<E> creator) {
        final File file = getEntitiesFile();
        if (file == null) {
            return Collections.emptyList();
        }
        try {
            return Json.load(file, creator);
        } catch (IOException | JSONException e) {
            errorReporter.onException(e);
        }
        return Collections.emptyList();
    }

    protected final void setInitialized() {
        synchronized (lock) {
            Check.isTrue(!initialized);
            initialized = true;
        }
    }

    public boolean isInitialized() {
        synchronized (lock) {
            return initialized;
        }
    }

    @Override
    public void save() {
        handler.removeCallbacks(writeTask);
        handler.postDelayed(writeTask, 500);
    }

    @Nonnull
    @Override
    public List<T> getEntities() {
        return mathRegistry.getEntities();
    }

    @Nonnull
    @Override
    public List<T> getSystemEntities() {
        return mathRegistry.getSystemEntities();
    }

    @Override
    public T add(@Nonnull JBuilder<? extends T> builder) {
        final T entity = mathRegistry.add(builder);
        if (!entity.isSystem() && isInitialized()) {
            save();
        }
        return entity;
    }

    @Nullable
    protected T addSafely(@Nonnull JBuilder<? extends T> builder) {
        try {
            return add(builder);
        } catch (Exception e) {
            errorReporter.onException(e);
        }
        return null;
    }

    @Override
    public void remove(@Nonnull T var) {
        mathRegistry.remove(var);
        save();
    }

    @Nonnull
    @Override
    public List<String> getNames() {
        return mathRegistry.getNames();
    }

    @Override
    public boolean contains(@Nonnull String name) {
        return mathRegistry.contains(name);
    }

    @Override
    public T get(@Nonnull String name) {
        return mathRegistry.get(name);
    }

    @Override
    public T getById(@Nonnull Integer id) {
        return mathRegistry.getById(id);
    }

    @Nullable
    protected abstract Jsonable toJsonable(@NonNull T entity);

    @Nullable
    protected abstract File getEntitiesFile();

    private class WriteTask implements Runnable {

        @Override
        public void run() {
            Check.isMainThread();
            final File file = getEntitiesFile();
            if (file == null) {
                return;
            }
            final List<Jsonable> entities = new ArrayList<>();
            for (T entity : getEntities()) {
                if (entity.isSystem()) {
                    continue;
                }
                final Jsonable jsonable = toJsonable(entity);
                if (jsonable != null) {
                    entities.add(jsonable);
                }
            }
            backgroundThread.execute(new Runnable() {
                @Override
                public void run() {
                    final JSONArray array = Json.toJson(entities);
                    try {
                        FileSaver.save(file, array.toString());
                    } catch (IOException e) {
                        errorReporter.onException(e);
                    }
                }
            });
        }
    }
}
