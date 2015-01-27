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

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuInflater;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 8/13/12
 * Time: 2:04 AM
 */
public final class AndroidSherlockUtils {

	private AndroidSherlockUtils() {
		throw new AssertionError("Not intended for instantiation!");
	}

	@Nonnull
	public static ActionBar getSupportActionBar(@Nonnull Activity activity) {
		if (activity instanceof ActionBarActivity) {
			return ((ActionBarActivity) activity).getSupportActionBar();
		}

		throw new IllegalArgumentException(activity.getClass() + " is not supported!");

	}

	public static ActionBar getSupportActionBar(@Nonnull Fragment fragment) {
		return ((ActionBarActivity) fragment.getActivity()).getSupportActionBar();
	}


	@Nonnull
	public static MenuInflater getSupportMenuInflater(@Nonnull Activity activity) {
		return activity.getMenuInflater();
	}

	public static void showDialog(@Nonnull DialogFragment dialogFragment,
								  @Nonnull String fragmentTag,
								  @Nonnull FragmentManager fm) {
		final FragmentTransaction ft = fm.beginTransaction();

		Fragment prev = fm.findFragmentByTag(fragmentTag);
		if (prev != null) {
			ft.remove(prev);
		}

		// Create and show the dialog.
		dialogFragment.show(ft, fragmentTag);
	}
}
