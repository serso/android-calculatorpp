package org.solovyev.android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * User: Solovyev_S
 * Date: 03.10.12
 * Time: 10:48
 */
public final class AndroidUtils2 {

    @NotNull
    private static final boolean AT_LEAST_API_5 = Build.VERSION.SDK_INT >= 5;

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

    public static boolean isComponentEnabled(@NotNull Context context,
                                             @NotNull Class<? extends Context> componentClass) {
        final PackageManager pm = context.getPackageManager();

        int componentEnabledSetting = pm.getComponentEnabledSetting(new ComponentName(context, componentClass));
        return componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED || componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
    }

    public static String saveBitmap(@NotNull Bitmap bitmap,
                                    @NotNull String path,
                                    @NotNull String fileName) {
        final File filePath = new File(path);
        filePath.mkdirs();

        final File file = new File(path, fileName);
        if (!file.exists()) {
            final String name = file.getAbsolutePath();

            FileOutputStream fos = null;
            try {
				fos = new FileOutputStream(name);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
            } catch (FileNotFoundException e) {
                Log.e("AndroidUtils", e.getMessage(), e);
            } catch (IOException e) {
                Log.e("AndroidUtils", e.getMessage(), e);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        Log.e("AndroidUtils", e.getMessage(), e);
                    }
                }
            }

            return name;
        }

        return null;
    }

    public static int getPointerCountFromMotionEvent(@NotNull MotionEvent event) {
        return AT_LEAST_API_5 ? event.getPointerCount() : 1;
    }

    public static float getXFromMotionEvent(@NotNull MotionEvent event, int pointer) {
        return AT_LEAST_API_5 ? event.getX(pointer) : 0;
    }

    public static float getYFromMotionEvent(@NotNull MotionEvent event, int pointer) {
        return AT_LEAST_API_5 ? event.getY(pointer) : 0;
    }
}
