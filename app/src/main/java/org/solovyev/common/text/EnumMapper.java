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
import java.util.HashMap;
import java.util.Map;

public class EnumMapper<T extends Enum> implements Mapper<T> {

    @Nonnull
    private final static Map<Class<? extends Enum>, Mapper<?>> cachedMappers = new HashMap<Class<? extends Enum>, Mapper<?>>();

    private final Class<T> enumClass;

    private EnumMapper(@Nonnull Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Nonnull
    public static <T extends Enum> Mapper<T> of(@Nonnull Class<T> enumClass) {
        Mapper<T> result = (Mapper<T>) cachedMappers.get(enumClass);
        if (result == null) {
            // do not care about synchronization
            result = new EnumMapper<T>(enumClass);
            cachedMappers.put(enumClass, result);
        }

        return result;
    }

    @Override
    public String formatValue(@Nullable T value) throws IllegalArgumentException {
        return value == null ? null : value.name();
    }

    @Override
    public T parseValue(@Nullable String value) throws IllegalArgumentException {
        return value == null ? null : (T) Enum.valueOf(enumClass, value);
    }
}
