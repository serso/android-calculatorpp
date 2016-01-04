/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import org.solovyev.common.collections.Collections;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 9/25/12
 * Time: 9:29 PM
 */
public class FragmentUtils {

    public static void createFragment(@Nonnull FragmentActivity activity,
                                      @Nonnull Class<? extends Fragment> fragmentClass,
                                      int parentViewId,
                                      @Nonnull String tag) {
        createFragment(activity, fragmentClass, parentViewId, tag, null);
    }

    public static void createFragment(@Nonnull FragmentActivity activity,
                                      @Nonnull Class<? extends Fragment> fragmentClass,
                                      int parentViewId,
                                      @Nonnull String tag,
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

    public static void removeFragments(@Nonnull ActionBarActivity activity, @Nonnull String... fragmentTags) {
        removeFragments(activity, Collections.asList(fragmentTags));
    }

    public static void removeFragments(@Nonnull ActionBarActivity activity, @Nonnull List<String> fragmentTags) {
        for (String fragmentTag : fragmentTags) {
            removeFragment(activity, fragmentTag);
        }
    }

    public static void detachFragments(@Nonnull ActionBarActivity activity, @Nonnull String... fragmentTags) {
        detachFragments(activity, Collections.asList(fragmentTags));
    }

    public static void detachFragments(@Nonnull ActionBarActivity activity, @Nonnull List<String> fragmentTags) {
        for (String fragmentTag : fragmentTags) {
            detachFragment(activity, fragmentTag);
        }
    }

    public static void detachFragment(@Nonnull ActionBarActivity activity, @Nonnull String fragmentTag) {
        final Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment != null) {
            if (!fragment.isDetached()) {
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.detach(fragment);
                ft.commit();
            }
        }
    }

    public static void removeFragment(@Nonnull ActionBarActivity activity, @Nonnull String fragmentTag) {
        final Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment != null) {
            if (fragment.isAdded()) {
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.remove(fragment);
                ft.commit();
            }
        }
    }
}
