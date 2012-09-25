package org.solovyev.android.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 9/25/12
 * Time: 9:29 PM
 */
public class FragmentUtils {

    public static void createFragment(@NotNull FragmentActivity activity,
                                @NotNull Class<? extends Fragment> fragmentClass,
                                int parentViewId,
                                @NotNull String tag) {
        final FragmentManager fm = activity.getSupportFragmentManager();

        Fragment messagesFragment = fm.findFragmentByTag(tag);

        final FragmentTransaction ft = fm.beginTransaction();
        try {
            if (messagesFragment == null) {
                messagesFragment = Fragment.instantiate(activity, fragmentClass.getName(), null);
                ft.add(parentViewId, messagesFragment, tag);
            } else {
                if (messagesFragment.isDetached()) {
                    ft.attach(messagesFragment);
                }
            }
        } finally {
            ft.commit();
        }
    }
}
