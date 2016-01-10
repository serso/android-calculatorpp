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

package org.solovyev.android.sherlock.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import org.solovyev.android.sherlock.FragmentItem;
import org.solovyev.android.sherlock.FragmentItemImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 8/4/12
 * Time: 12:42 PM
 */
public class ActionBarFragmentTabListener implements ActionBar.TabListener {

    private final FragmentItem fragmentItem;

    /**
     * Constructor used each time a new tab is created.
     *
     * @param activity      The host Activity, used to instantiate the fragment
     * @param tag           The identifier tag for the fragment
     * @param fragmentClass The fragment's Class, used to instantiate the fragment
     * @param fragmentArgs  arguments to be passed to fragment
     * @param parentViewId  parent view id
     */

    public ActionBarFragmentTabListener(@Nonnull AppCompatActivity activity,
                                        @Nonnull String tag,
                                        @Nonnull Class<? extends Fragment> fragmentClass,
                                        @Nullable Bundle fragmentArgs,
                                        @Nullable Integer parentViewId) {
        this.fragmentItem = new FragmentItemImpl(activity, tag, fragmentClass, fragmentArgs, parentViewId);
    }


    /* The following are each of the ActionBar.TabListener callbacks */

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        this.fragmentItem.onSelected(ft);
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        this.fragmentItem.onUnselected(ft);
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }
}
