package org.solovyev.android;

import android.os.Handler;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.DelayedExecutor;

import java.util.concurrent.TimeUnit;

/**
 * User: serso
 * Date: 12/1/12
 * Time: 4:11 PM
 */
public class UiThreadExecutor implements DelayedExecutor {

    @NotNull
    private final Handler uiHandler;

    public UiThreadExecutor() {
        assert AndroidUtils.isUiThread() : "Must be called on UI thread!";

        this.uiHandler = new Handler();
    }

    @Override
    public void execute(@NotNull Runnable command, long delay, @NotNull TimeUnit delayUnit) {
        this.uiHandler.postDelayed(command, delayUnit.toMillis(delay));
    }

    @Override
    public void execute(@NotNull Runnable command) {
        this.uiHandler.post(command);
    }
}
