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

import com.squareup.otto.Bus;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.model.EntityDao;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.math.MathRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public abstract class BaseEntitiesRegistry<T extends MathEntity, P extends PersistedEntity> implements EntitiesRegistry<T> {

    @Nullable
    protected final EntityDao<P> entityDao;
    @Nonnull
    protected final Object lock = this;
    @Nonnull
    private final MathRegistry<T> mathRegistry;
    @Nonnull
    private final String prefix;
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

    // synchronized on lock
    private boolean initialized;

    protected BaseEntitiesRegistry(@Nonnull MathRegistry<T> mathRegistry,
                                   @Nonnull String prefix,
                                   @Nullable EntityDao<P> entityDao) {
        this.mathRegistry = mathRegistry;
        this.prefix = prefix;
        this.entityDao = entityDao;
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

    public synchronized void init() {
        Check.isNotMainThread();

        if (entityDao == null) {
            return;
        }
        final PersistedEntitiesContainer<P> persistenceContainer = entityDao.load();

        final List<P> notCreatedEntities = new ArrayList<P>();

        if (persistenceContainer != null) {
            for (P entity : persistenceContainer.getEntities()) {
                if (!contains(entity.getName())) {
                    try {
                        final JBuilder<? extends T> builder = createBuilder(entity);
                        add(builder);
                    } catch (RuntimeException e) {
                        Locator.getInstance().getErrorReporter().onException(e);
                        notCreatedEntities.add(entity);
                    }
                }
            }
        }

        try {
            if (!notCreatedEntities.isEmpty()) {
                final StringBuilder errorMessage = new StringBuilder(notCreatedEntities.size() * 100);
                for (P notCreatedEntity : notCreatedEntities) {
                    errorMessage.append(notCreatedEntity).append("\n\n");
                }

                Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_message_dialog, MessageDialogData.newInstance(CalculatorMessages.newErrorMessage(CalculatorMessages.msg_007, errorMessage.toString()), null));
            }
        } catch (RuntimeException e) {
            // just in case
            Locator.getInstance().getErrorReporter().onException(e);
        }
        setInitialized();
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

    @Nonnull
    protected abstract JBuilder<? extends T> createBuilder(@Nonnull P entity);

    @Override
    public synchronized void save() {
        if (entityDao == null) {
            return;
        }
        final PersistedEntitiesContainer<P> container = createPersistenceContainer();

        for (T entity : this.getEntities()) {
            if (!entity.isSystem()) {
                final P persistenceEntity = transform(entity);
                if (persistenceEntity != null) {
                    container.getEntities().add(persistenceEntity);
                }
            }
        }

        entityDao.save(container);
    }

    @Nullable
    protected abstract P transform(@Nonnull T entity);

    @Nonnull
    protected abstract PersistedEntitiesContainer<P> createPersistenceContainer();

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
    public T add(@Nonnull JBuilder<? extends T> JBuilder) {
        return mathRegistry.add(JBuilder);
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
}
