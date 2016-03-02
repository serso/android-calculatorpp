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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NumberParser<N extends Number> implements Parser<N> {

    static final List<Class<? extends Number>> supportedClasses = Arrays.<Class<? extends Number>>asList(Integer.class, Float.class, Long.class, Double.class);

    private static final Map<Class<?>, Parser<?>> parsers = new HashMap<>(supportedClasses.size());

    static {
        for (Class<? extends Number> supportedClass : supportedClasses) {
            parsers.put(supportedClass, new NumberParser<>(supportedClass));
        }
    }

    @Nonnull
    private final Class<? extends N> clazz;

    private NumberParser(@Nonnull Class<? extends N> clazz) {
        this.clazz = clazz;
    }

    @Nonnull
    public static <N extends Number> Parser<N> of(@Nonnull Class<N> clazz) {
        assert supportedClasses.contains(clazz) : "Class " + clazz + " is not supported by " + NumberParser.class;
        return (Parser<N>) parsers.get(clazz);
    }

    @Override
    public N parseValue(@Nullable String value) throws IllegalArgumentException {
        N result;

        if (value != null) {
            if (this.clazz.equals(Integer.class)) {
                result = (N) Integer.valueOf(value);
            } else if (this.clazz.equals(Float.class)) {
                result = (N) Float.valueOf(value);
            } else if (this.clazz.equals(Long.class)) {
                result = (N) Long.valueOf(value);
            } else if (this.clazz.equals(Double.class)) {
                result = (N) Double.valueOf(value);
            } else {
                throw new UnsupportedOperationException(this.clazz + " is not supported!");
            }
        } else {
            result = null;
        }

        return result;
    }
}
