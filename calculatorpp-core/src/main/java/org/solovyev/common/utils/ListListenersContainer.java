package org.solovyev.common.utils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:43
 */
// todo serso: move to common
public class ListListenersContainer<T> {

    @NotNull
    private final List<WeakReference<T>> listeners = new ArrayList<WeakReference<T>>();

    public void addListener(@NotNull final T listener) {
        synchronized (listeners) {
            boolean contains = Iterables.any(listeners, new WeakReferencePredicate<T>(listener));

            if (!contains) {
                listeners.add(new WeakReference<T>(listener));
            }
        }
    }

    public void removeListener(@NotNull T listener) {
        synchronized (listeners) {
            Iterables.removeIf(listeners, new WeakReferencePredicate<T>(listener));
        }
    }

    @NotNull
    public List<T> getListeners() {
        final List<T> localListeners;

        synchronized (listeners) {
            localListeners = new ArrayList<T>(listeners.size());

            // copy listeners and remove garbage collected references
            for ( Iterator<WeakReference<T>> it = listeners.iterator(); it.hasNext();  ) {
                final WeakReference<T> r = it.next();
                final T t = r.get();
                if ( t == null ) {
                    it.remove();
                } else {
                    localListeners.add(t);
                }
            }
        }

        return localListeners;
    }

    private static class WeakReferencePredicate<T> implements Predicate<WeakReference<T>> {

        @NotNull
        private final T t;

        public WeakReferencePredicate(T t) {
            this.t = t;
        }

        @Override
        public boolean apply(@Nullable WeakReference<T> r) {
            final T t = r != null ? r.get() : null;
            return this.t.equals(t);
        }
    }
}
