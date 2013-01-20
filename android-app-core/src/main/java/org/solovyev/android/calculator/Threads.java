package org.solovyev.android.calculator;

import android.app.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;

/**
 * User: serso
 * Date: 1/19/13
 * Time: 5:04 PM
 */
public final class Threads {

    private Threads() {
        throw new AssertionError();
    }

    public static void tryRunOnUiThread(@Nullable final Activity activity, @NotNull final Runnable runnable) {
        if (activity != null && !activity.isFinishing()) {
            if (AndroidUtils.isUiThread()) {
                runnable.run();
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // some time may pass and activity might be closing
                        if (!activity.isFinishing()) {
                            runnable.run();
                        }
                    }
                });
            }
        }
    }
}
