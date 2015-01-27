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
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.NumeralBaseButtons.toggleNumericDigits;
import static org.solovyev.android.calculator.Preferences.Gui.hideNumeralBaseDigits;
import static org.solovyev.android.calculator.Preferences.Gui.showEqualsButton;
import static org.solovyev.android.calculator.model.AndroidCalculatorEngine.Preferences.multiplicationSign;
import static org.solovyev.android.calculator.model.AndroidCalculatorEngine.Preferences.numeralBase;

/**
 * User: Solovyev_S
 * Date: 25.09.12
 * Time: 12:25
 */
public class CalculatorKeyboardFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	@Nonnull
	private Preferences.Gui.Theme theme;

	@Nonnull
	private FragmentUi fragmentHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

		final Preferences.Gui.Layout layout = Preferences.Gui.getLayout(preferences);
		if (!layout.isOptimized()) {
			fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(R.layout.cpp_app_keyboard_mobile);
		} else {
			fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(R.layout.cpp_app_keyboard);
		}

		fragmentHelper.onCreate(this);

		preferences.registerOnSharedPreferenceChangeListener(this);

		theme = Preferences.Gui.theme.getPreferenceNoError(preferences);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return fragmentHelper.onCreateView(this, inflater, container);
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		fragmentHelper.onViewCreated(this, root);
	}


	@Override
	public void onResume() {
		super.onResume();

		this.fragmentHelper.onResume(this);
	}

	@Override
	public void onPause() {
		this.fragmentHelper.onPause(this);

		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		fragmentHelper.onDestroy(this);

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		preferences.unregisterOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		if (numeralBase.isSameKey(key) || hideNumeralBaseDigits.isSameKey(key)) {
			toggleNumericDigits(this.getActivity(), preferences);
		}

		if (showEqualsButton.isSameKey(key)) {
			CalculatorButtons.toggleEqualsButton(preferences, this.getActivity());
		}

		if (multiplicationSign.isSameKey(key)) {
			CalculatorButtons.initMultiplicationButton(getView());
		}
	}


	@Nullable
	private static AndroidCalculatorDisplayView getCalculatorDisplayView() {
		return (AndroidCalculatorDisplayView) Locator.getInstance().getDisplay().getView();
	}

	@Nonnull
	private Calculator getCalculator() {
		return Locator.getInstance().getCalculator();
	}

	@Nonnull
	private static CalculatorKeyboard getKeyboard() {
		return Locator.getInstance().getKeyboard();
	}
}

