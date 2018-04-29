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

package org.solovyev.common.text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ValueOfFormatter<T> implements Formatter<T> {

    @Nonnull
    private static final ValueOfFormatter<Object> notNullFormatter = new ValueOfFormatter<Object>(false);

    @Nonnull
    private static final ValueOfFormatter<Object> nullableFormatter = new ValueOfFormatter<Object>(true);
    private final boolean processNulls;

    private ValueOfFormatter() {
        this(false);
    }

    private ValueOfFormatter(boolean processNulls) {
        this.processNulls = processNulls;
    }

    @Nonnull
    public static <T> ValueOfFormatter<T> getNotNullFormatter() {
        return (ValueOfFormatter<T>) notNullFormatter;
    }

    @Nonnull
    public static <T> ValueOfFormatter<T> getNullableFormatter() {
        return (ValueOfFormatter<T>) nullableFormatter;
    }

    @Override
    public String formatValue(@Nullable T t) throws IllegalArgumentException {
        if (t == null) {
            if (processNulls) {
                return String.valueOf(t);
            } else {
                return null;
            }
        } else {
            return String.valueOf(t);
        }
    }
}
