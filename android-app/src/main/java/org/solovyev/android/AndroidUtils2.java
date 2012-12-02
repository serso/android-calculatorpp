package org.solovyev.android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

    public static void toggleComponent(@NotNull Context context,
                                       @NotNull Class<? extends Context> componentClass,
                                       boolean enable) {
        final PackageManager pm = context.getPackageManager();

        final int componentState;
        if (enable) {
            componentState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        } else {
            componentState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }

        pm.setComponentEnabledSetting(new ComponentName(context, componentClass), componentState, PackageManager.DONT_KILL_APP);
    }
}
