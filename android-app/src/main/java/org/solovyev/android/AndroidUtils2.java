package org.solovyev.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 03.10.12
 * Time: 10:48
 */
public final class AndroidUtils2 {

    private AndroidUtils2() {
        throw new AssertionError();
    }

    public static void addFlags(@NotNull Intent intent, boolean detached, @NotNull Context context) {
        int flags = 0;

        if (!(context instanceof Activity)) {
            flags = flags | Intent.FLAG_ACTIVITY_NEW_TASK;
        }

        if (detached) {
            flags = flags | Intent.FLAG_ACTIVITY_NO_HISTORY;
        }

        intent.setFlags(flags);

    }
}
