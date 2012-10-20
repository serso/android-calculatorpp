package org.solovyev.android;

import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

    public static void showDialog(@NotNull DialogFragment dialogFragment,
                                  @NotNull String fragmentTag,
                                  @NotNull FragmentManager fm) {
        final FragmentTransaction ft = fm.beginTransaction();

        Fragment prev = fm.findFragmentByTag(fragmentTag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        dialogFragment.show(ft, fragmentTag);

    }

    public static boolean isUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
