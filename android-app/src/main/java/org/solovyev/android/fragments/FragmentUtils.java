package org.solovyev.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.collections.Collections;

import java.util.List;

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
		createFragment(activity, fragmentClass, parentViewId, tag, null);
	}

    public static void createFragment(@NotNull FragmentActivity activity,
									   @NotNull Class<? extends Fragment> fragmentClass,
									   int parentViewId,
									   @NotNull String tag,
									   @Nullable Bundle args) {
        final FragmentManager fm = activity.getSupportFragmentManager();

        Fragment messagesFragment = fm.findFragmentByTag(tag);

        final FragmentTransaction ft = fm.beginTransaction();
        try {
            if (messagesFragment == null) {
                messagesFragment = Fragment.instantiate(activity, fragmentClass.getName(), args);
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

    public static void removeFragments(@NotNull SherlockFragmentActivity activity, @NotNull String... fragmentTags) {
        removeFragments(activity, Collections.asList(fragmentTags));
    }

    public static void removeFragments(@NotNull SherlockFragmentActivity activity, @NotNull List<String> fragmentTags) {
        for (String fragmentTag : fragmentTags) {
            removeFragment(activity, fragmentTag);
        }
    }

    public static void detachFragments(@NotNull SherlockFragmentActivity activity, @NotNull String... fragmentTags) {
        detachFragments(activity, Collections.asList(fragmentTags));
    }

    public static void detachFragments(@NotNull SherlockFragmentActivity activity, @NotNull List<String> fragmentTags) {
        for (String fragmentTag : fragmentTags) {
            detachFragment(activity, fragmentTag);
        }
    }

    public static void detachFragment(@NotNull SherlockFragmentActivity activity, @NotNull String fragmentTag) {
        final Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if ( fragment != null ) {
            if ( !fragment.isDetached() ) {
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.detach(fragment);
                ft.commit();
            }
        }
    }

    public static void removeFragment(@NotNull SherlockFragmentActivity activity, @NotNull String fragmentTag) {
        final Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if ( fragment != null ) {
            if ( fragment.isAdded()) {
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.remove(fragment);
                ft.commit();
            }
        }
    }
}
