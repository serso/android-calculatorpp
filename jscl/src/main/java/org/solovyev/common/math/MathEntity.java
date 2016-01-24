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

import org.solovyev.common.JPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MathEntity {

    @Nonnull
    String getName();

    boolean isSystem();

    @Nonnull
    Integer getId();

    void setId(@Nonnull Integer id);

    boolean isIdDefined();

    void copy(@Nonnull MathEntity that);

    class Finder<T extends MathEntity> implements JPredicate<T> {

        @Nonnull
        private final String name;

        public Finder(@Nonnull String name) {
            this.name = name;
        }

        public boolean apply(@Nullable T entity) {
            return entity != null && name.equals(entity.getName());
        }
    }
}
