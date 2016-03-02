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
 * ---------------------------------------------------------------------
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.common.math;

import org.solovyev.common.JBuilder;
import org.solovyev.common.collections.SortedList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * User: serso
 * Date: 9/29/11
 * Time: 4:57 PM
 */
public abstract class AbstractMathRegistry<T extends MathEntity> implements MathRegistry<T> {

    private static final MathEntityComparator<MathEntity> MATH_ENTITY_COMPARATOR = new MathEntityComparator<MathEntity>();
    @GuardedBy("this")
    @Nonnull
    private static volatile Integer counter = 0;
    @GuardedBy("this")
    @Nonnull
    protected final SortedList<T> entities = SortedList.newInstance(new ArrayList<T>(30), MATH_ENTITY_COMPARATOR);
    @GuardedBy("this")
    @Nonnull
    protected final List<String> entityNames = new ArrayList<>();
    @GuardedBy("this")
    @Nonnull
    protected final SortedList<T> systemEntities = SortedList.newInstance(new ArrayList<T>(30), MATH_ENTITY_COMPARATOR);

    protected AbstractMathRegistry() {
    }

    @Nonnull
    private static synchronized Integer count() {
        final Integer result = counter;
        counter++;
        return result;
    }

    @Nullable
    private static <E extends MathEntity> E removeByName(@Nonnull List<E> entities, @Nonnull String name) {
        for (int i = 0; i < entities.size(); i++) {
            final E entity = entities.get(i);
            if (entity.getName().equals(name)) {
                entities.remove(i);
                return entity;
            }
        }
        return null;
    }

    private static boolean areEqual(@Nullable Object l, @Nullable Object r) {
        return l != null ? l.equals(r) : r == null;
    }

    @Nonnull
    public List<T> getEntities() {
        synchronized (this) {
            return java.util.Collections.unmodifiableList(new ArrayList<T>(entities));
        }
    }

    @Nonnull
    public List<T> getSystemEntities() {
        synchronized (this) {
            return java.util.Collections.unmodifiableList(new ArrayList<T>(systemEntities));
        }
    }

    protected void add(@Nonnull T entity) {
        synchronized (this) {
            if (entity.isSystem()) {
                if (contains(entity.getName(), this.systemEntities)) {
                    throw new IllegalArgumentException("Trying to add two system entities with same name: " + entity.getName());
                }

                this.systemEntities.add(entity);
            }

            if (!contains(entity.getName(), this.entities)) {
                addEntity(entity, this.entities);
                this.entityNames.clear();
            }
        }
    }

    private void addEntity(@Nonnull T entity, @Nonnull List<T> list) {
        assert Thread.holdsLock(this);

        entity.setId(count());
        list.add(entity);
    }

    public T add(@Nonnull JBuilder<? extends T> builder) {
        synchronized (this) {
            final T entity = builder.create();

            T varFromRegister;

            if (entity.isIdDefined()) {
                varFromRegister = getById(entity.getId());
            } else {
                varFromRegister = get(entity.getName());
            }

            if (varFromRegister == null) {
                varFromRegister = entity;

                addEntity(entity, this.entities);
                this.entityNames.clear();
                if (entity.isSystem()) {
                    this.systemEntities.add(entity);
                }
            } else {
                varFromRegister.copy(entity);
                this.entities.sort();
                this.entityNames.clear();
                this.systemEntities.sort();
            }

            return varFromRegister;
        }
    }

    public void remove(@Nonnull T entity) {
        synchronized (this) {
            if (!entity.isSystem()) {
                final T removed = removeByName(entities, entity.getName());
                if (removed != null) {
                    this.entityNames.clear();
                }
            }
        }
    }

    @Nonnull
    public List<String> getNames() {
        synchronized (this) {
            if (entityNames.isEmpty()) {
                for (T entity : entities) {
                    entityNames.add(entity.getName());
                }
            }
            return entityNames;
        }
    }

    @Nullable
    public T get(@Nonnull final String name) {
        synchronized (this) {
            return get(name, entities);
        }
    }

    @Nullable
    private T get(@Nonnull String name, @Nonnull List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            final T entity = list.get(i);
            if (areEqual(entity.getName(), name)) {
                return entity;
            }
        }
        return null;
    }

    public T getById(@Nonnull final Integer id) {
        synchronized (this) {
            for (T entity : entities) {
                if (areEqual(entity.getId(), id)) {
                    return entity;
                }
            }
            return null;
        }
    }

    public boolean contains(@Nonnull final String name) {
        synchronized (this) {
            return contains(name, this.entities);
        }
    }

    private boolean contains(final String name, @Nonnull List<T> entities) {
        return get(name, entities) != null;
    }

    static class MathEntityComparator<T extends MathEntity> implements Comparator<T> {

        MathEntityComparator() {
        }

        public int compare(T l, T r) {
            int result = r.getName().length() - l.getName().length();
            if (result == 0) {
                result = l.getName().compareTo(r.getName());
            }
            return result;
        }
    }
}
