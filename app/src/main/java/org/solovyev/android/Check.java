/*
 * Copyright 2014 serso aka se.solovyev
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

package org.solovyev.android;

import android.os.Looper;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.lang.Thread.currentThread;

public final class Check {

    private static final boolean junit = isJunit();

    private Check() {
        throw new AssertionError();
    }

    private static boolean isJunit() {
        final StackTraceElement[] stackTrace = currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true;
            }
        }
        return false;
    }

    public static void isMainThread() {
        if (!junit && Looper.getMainLooper() != Looper.myLooper()) {
            throw new AssertionException("Should be called on the main thread");
        }
    }

    public static void isNotNull(@Nullable Object o) {
        isNotNull(o, "Object should not be null");
    }

    public static void isNotNull(@Nullable Object o, @Nonnull String message) {
        if (o == null) {
            throw new AssertionException(message);
        }
    }

    public static void notEquals(int expected, int actual) {
        if (expected == actual) {
            throw new AssertionException("Should not be equal");
        }
    }

    public static void equals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionException("Should be equal");
        }
    }

    public static void equals(@Nullable Object expected, @Nullable Object actual) {
        equals(expected, actual, "Should be equal");
    }

    public static void equals(@Nullable Object expected, @Nullable Object actual, @Nonnull String message) {
        if (expected == actual) {
            // both nulls or same
            return;
        }

        if (expected != null && actual != null && expected.equals(actual)) {
            // equals
            return;
        }

        throw new AssertionException(message);
    }

    public static void isTrue(boolean expression) {
        if (!expression) {
            throw new AssertionException("");
        }
    }

    public static void isTrue(boolean expression, @Nonnull String message) {
        if (!expression) {
            throw new AssertionException(message);
        }
    }

    public static void isFalse(boolean expression, @Nonnull String message) {
        if (expression) {
            throw new AssertionException(message);
        }
    }

    public static void isNull(@Nullable Object o) {
        isNull(o, "Object should be null");
    }

    public static void isNull(@Nullable Object o, @Nonnull String message) {
        if (o != null) {
            throw new AssertionException(message);
        }
    }

    public static void isNotEmpty(@Nullable String s) {
        if (s == null || s.length() == 0) {
            throw new AssertionException("String should not be empty");
        }
    }

    public static void isNotEmpty(@Nullable String[] array) {
        if (array == null || array.length == 0) {
            throw new AssertionException("Array should not be empty");
        }
    }

    public static void isNotEmpty(@Nullable Collection<?> c) {
        if (c == null || c.size() == 0) {
            throw new AssertionException("Collection should not be empty");
        }
    }

    public static void isNotEmpty(@Nullable Map<?, ?> c) {
        if (c == null || c.size() == 0) {
            throw new AssertionException("Map should not be empty");
        }
    }

    public static void same(Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionException("Objects should be the same");
        }
    }

    public static void isEmpty(@Nullable Collection<?> c) {
        if (c != null && !c.isEmpty()) {
            throw new AssertionException("Collection should be empty");
        }
    }

    public static void shouldNotHappen() {
        throw new AssertionException("Should not happen");
    }

    private static final class AssertionException extends RuntimeException {
        private AssertionException(@Nonnull String message) {
            super(message);
        }
    }
}
