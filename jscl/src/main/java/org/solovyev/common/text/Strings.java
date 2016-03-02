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
import java.util.Date;
import java.util.Random;

public class Strings {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];
    // random variable: must be synchronized in usage
    private static final Random RANDOM = new Random(new Date().getTime());

    // not intended for instantiation
    protected Strings() {
        throw new AssertionError();
    }

    public static boolean isEmpty(@Nullable CharSequence s) {
        return s == null || s.length() == 0;
    }

    @Nonnull
    public static String getNotEmpty(@Nullable CharSequence s, @Nonnull String defaultValue) {
        return isEmpty(s) ? defaultValue : s.toString();
    }

    @Nonnull
    public static Character[] toObjects(char[] array) {
        if (array == null || array.length == 0) {
            return EMPTY_CHARACTER_OBJECT_ARRAY;
        }

        final Character[] result = new Character[array.length];

        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    @Nonnull
    public static String generateRandomString(int length) {

        final StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            final char ch;

            synchronized (RANDOM) {
                // 'A' = 65
                ch = (char) (RANDOM.nextInt(52) + 65);
            }

            result.append(ch);
        }

        return result.toString();
    }

}
