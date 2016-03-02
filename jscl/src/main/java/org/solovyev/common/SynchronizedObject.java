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

package org.solovyev.common;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;

public abstract class SynchronizedObject<D> {

    @GuardedBy("mutex")
    @Nonnull
    protected final D delegate;

    @Nonnull
    protected final Object mutex;

    protected SynchronizedObject(@Nonnull D delegate) {
        this.delegate = delegate;
        this.mutex = this;
    }

    protected SynchronizedObject(@Nonnull D delegate, @Nonnull Object mutex) {
        this.delegate = delegate;
        this.mutex = mutex;
    }

    // for manually synchronization it is allows to use mutex
    @Nonnull
    public Object getMutex() {
        return mutex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SynchronizedObject)) {
            return false;
        }

        final SynchronizedObject that = (SynchronizedObject) o;

        synchronized (mutex) {
            if (!delegate.equals(that.delegate)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        synchronized (mutex) {
            return delegate.hashCode();
        }
    }

    @Override
    public String toString() {
        synchronized (mutex) {
            return delegate.toString();
        }
    }
}
