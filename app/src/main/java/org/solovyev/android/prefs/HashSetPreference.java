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

import org.solovyev.common.text.EnumMapper;
import org.solovyev.common.text.Mapper;
import org.solovyev.common.text.StringMapper;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class HashSetPreference<T> extends CollectionSetPreference<Set<T>, T> {

    private HashSetPreference(@Nonnull String id, @Nonnull Set<T> defaultValue, @Nonnull Mapper<T> mapper) {
        super(id, defaultValue, mapper);
    }

    @Nonnull
    public static HashSetPreference<String> ofStrings(@Nonnull String key, @Nonnull Set<String> defaultValue) {
        return new HashSetPreference<String>(key, defaultValue, StringMapper.getInstance());
    }

    @Nonnull
    public static <T> HashSetPreference<T> ofTypedValues(@Nonnull String key, @Nonnull Set<T> defaultValue, @Nonnull Mapper<T> parser) {
        return new HashSetPreference<T>(key, defaultValue, parser);
    }

    @Nonnull
    public static <T extends Enum> HashSetPreference<T> ofEnums(@Nonnull String id, @Nonnull Set<T> defaultValue, @Nonnull Class<T> enumType) {
        return new HashSetPreference<T>(id, defaultValue, EnumMapper.of(enumType));
    }

    @Nonnull
    @Override
    protected Set<T> createCollection(int size) {
        return new HashSet<T>(size);
    }
}
