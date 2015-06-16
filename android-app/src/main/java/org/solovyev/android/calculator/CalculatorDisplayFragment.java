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

package org.solovyev.android.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;

import javax.annotation.Nonnull;

/**
 * User: Solovyev_S
 * Date: 25.09.12
 * Time: 12:03
 */
public class CalculatorDisplayFragment extends Fragment {

	@Nonnull
	private FragmentUi fragmentHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		final Preferences.Gui.Layout layout = Preferences.Gui.getLayout(prefs);
		if (!layout.isOptimized()) {
			fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(R.layout.cpp_app_display_mobile, R.string.result);
		} else {
			fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(R.layout.cpp_app_display, R.string.result);
		}

		fragmentHelper.onCreate(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return fragmentHelper.onCreateView(this, inflater, container);
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		((AndroidCalculator) Locator.getInstance().getCalculator()).setDisplay(getActivity());

		fragmentHelper.onViewCreated(this, root);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

		fragmentHelper.onResume(this);
	}

	@Override
	public void onPause() {
		fragmentHelper.onPause(this);

		super.onPause();
	}

	@Override
	public void onDestroyView() {
		((AndroidCalculator) Locator.getInstance().getCalculator()).clearDisplay();
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		fragmentHelper.onDestroy(this);

		super.onDestroy();
	}
}
