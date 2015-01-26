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

package org.solovyev.android.calculator.wizard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;

import javax.annotation.Nullable;

public class OnScreenCalculatorWizardStep extends Fragment {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	static final String ONSCREEN_CALCULATOR_ENABLED = "onscreen_calculator_enabled";

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

	@Nullable
	private CheckBox onscreenCalculatorCheckbox;

	private Boolean onscreenCalculatorEnabled;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.containsKey(ONSCREEN_CALCULATOR_ENABLED)) {
			onscreenCalculatorEnabled = savedInstanceState.getBoolean(ONSCREEN_CALCULATOR_ENABLED);
		}

		if (onscreenCalculatorEnabled == null) {
			onscreenCalculatorEnabled = getArguments().getBoolean(ONSCREEN_CALCULATOR_ENABLED, Preferences.OnscreenCalculator.showAppIcon.getDefaultValue());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cpp_wizard_step_onscreen, null);
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		onscreenCalculatorCheckbox = (CheckBox) root.findViewById(R.id.wizard_onscreen_app_enabled_checkbox);
		onscreenCalculatorCheckbox.setChecked(onscreenCalculatorEnabled);
	}

	public Boolean isOnscreenCalculatorEnabled() {
		boolean enabled = Preferences.OnscreenCalculator.showAppIcon.getDefaultValue();

		if (onscreenCalculatorCheckbox != null) {
			enabled = onscreenCalculatorCheckbox.isChecked();
		}

		return enabled;
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean(ONSCREEN_CALCULATOR_ENABLED, onscreenCalculatorEnabled);
	}

	@Nullable
	CheckBox getOnscreenCalculatorCheckbox() {
		return onscreenCalculatorCheckbox;
	}
}

