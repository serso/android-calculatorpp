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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface MathRegistry<T extends MathEntity> {

    @Nonnull
    List<T> getEntities();

    @Nonnull
    List<T> getSystemEntities();

    T add(@Nonnull JBuilder<? extends T> JBuilder);

    void remove(@Nonnull T var);

    @Nonnull
    List<String> getNames();

    boolean contains(@Nonnull final String name);

    @Nullable
    T get(@Nonnull String name);

    @Nullable
    T getById(@Nonnull Integer id);
}
