package org.solovyev.android.checkout;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * App-wide {@link Checkout} which counts how many times it has been started.
 */
@Singleton
public class CppCheckout extends Checkout {
    private int started = 0;

    @Inject
    public CppCheckout(@NonNull Billing billing) {
        super(null, billing);
    }

    @Override
    public void stop() {
        Check.isMainThread();
        Check.isTrue(started > 0, "Must be started first");
        started--;
        if (started == 0) {
            super.stop();
        }
        started = Math.max(0, started);
    }

    @Override
    public void start(Listener listener) {
        Check.isMainThread();
        started++;
        if (started == 1) {
            super.start(listener);
        }
    }
}
