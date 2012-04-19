package org.solovyev.android.calculator;

import android.os.Binder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

/**
 * A generic implementation of Binder to be used for local services
 *
 * @param <S> The type of the service being bound
 * @author Geoff Bruckner  12th December 2009
 */

public class LocalBinder<S> extends Binder {

    @NotNull
    private static String TAG = "LocalBinder";

    @NotNull
    private WeakReference<S> serviceReference;


    public LocalBinder(@NotNull S service) {
        serviceReference = new WeakReference<S>(service);
    }

    @Nullable
    public S getService() {
        return serviceReference.get();
    }
}