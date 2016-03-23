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

import org.solovyev.android.Check;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NumberMapper<N extends Number> implements Mapper<N> {

    private static final List<Class<? extends Number>> supportedClasses = NumberParser.supportedClasses;

    private static final Map<Class<?>, Mapper<?>> mappers = new HashMap<>(supportedClasses.size());

    static {
        for (Class<? extends Number> supportedClass : supportedClasses) {
            mappers.put(supportedClass, newInstance(supportedClass));
        }
    }

    @Nonnull
    private final Formatter<N> formatter;

    @Nonnull
    private final Parser<? extends N> parser;

    /**
     * Use org.solovyev.common.text.NumberMapper#getMapper(java.lang.Class<N>) instead
     *
     * @param clazz class representing parsed object
     */
    private NumberMapper(@Nonnull Class<? extends N> clazz) {
        this(NumberParser.of(clazz), ValueOfFormatter.<N>getNotNullFormatter());
    }

    private NumberMapper(@Nonnull Parser<? extends N> parser,
                         @Nonnull Formatter<N> formatter) {
        this.parser = parser;
        this.formatter = formatter;
    }

    @Nonnull
    public static <N extends Number> Mapper<N> newInstance(@Nonnull Parser<? extends N> parser,
                                                           @Nonnull Formatter<N> formatter) {
        return new NumberMapper<>(parser, formatter);
    }

    @Nonnull
    private static <N extends Number> Mapper<N> newInstance(@Nonnull Class<? extends N> clazz) {
        return new NumberMapper<>(clazz);
    }

    @Nonnull
    public static <N extends Number> Mapper<N> of(@Nonnull Class<? extends N> clazz) {
        Check.isTrue(supportedClasses.contains(clazz), "Class " + clazz + " is not supported by " + NumberMapper.class);
        return (Mapper<N>) mappers.get(clazz);
    }

    @Override
    public String formatValue(@Nullable N value) throws IllegalArgumentException {
        return formatter.formatValue(value);
    }

    @Override
    public N parseValue(@Nullable String value) throws IllegalArgumentException {
        return this.parser.parseValue(value);
    }
}
