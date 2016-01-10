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

package org.solovyev.android.sherlock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 8/7/12
 * Time: 11:55 AM
 */
public class FragmentItemImpl implements FragmentItem {

    @Nonnull
    private final AppCompatActivity activity;

    // Fragment

    @Nonnull
    private final String tag;

    @Nonnull
    private final Class<? extends Fragment> fragmentClass;
    @Nullable
    private final Integer parentViewId;
    @Nullable
    private Bundle fragmentArgs;
    @Nullable
    private Fragment fragment;

    /**
     * Constructor used each time a new tab is created.
     *
     * @param activity      The host Activity, used to instantiate the fragment
     * @param tag           The identifier tag for the fragment
     * @param fragmentClass The fragment's Class, used to instantiate the fragment
     * @param fragmentArgs  arguments to be passed to fragment
     * @param parentViewId  parent view id
     */

    public FragmentItemImpl(@Nonnull AppCompatActivity activity,
                            @Nonnull String tag,
                            @Nonnull Class<? extends Fragment> fragmentClass,
                            @Nullable Bundle fragmentArgs,
                            @Nullable Integer parentViewId) {
        this.activity = activity;
        this.tag = tag;
        this.fragmentClass = fragmentClass;
        this.fragmentArgs = fragmentArgs;
        this.parentViewId = parentViewId;

        final FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
        this.fragment = supportFragmentManager.findFragmentByTag(tag);
    }


    @Override
    public void onSelected(@Nonnull FragmentTransaction ft) {
        if (fragment == null) {
            fragment = activity.getSupportFragmentManager().findFragmentByTag(this.tag);
        }

        // Check if the fragment is already initialized
        if (fragment == null) {
            // If not, instantiate and add it to the activity
            fragment = Fragment.instantiate(activity, fragmentClass.getName(), fragmentArgs);
            if (parentViewId != null) {
                ft.add(parentViewId, fragment, tag);
            } else {
                ft.add(fragment, tag);
            }
        } else {
            if (fragment.isDetached()) {
                // If it exists, simply attach it in order to show it
                ft.attach(fragment);
            }
        }
    }

    @Override
    public void onUnselected(@Nonnull FragmentTransaction ft) {
        if (fragment != null) {
            ft.detach(fragment);
        }
    }
}
