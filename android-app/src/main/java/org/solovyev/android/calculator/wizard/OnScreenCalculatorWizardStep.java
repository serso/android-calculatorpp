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
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;

import javax.annotation.Nullable;

import static org.solovyev.android.calculator.CalculatorApplication.getPreferences;

public class OnScreenCalculatorWizardStep extends WizardFragment implements CompoundButton.OnCheckedChangeListener {

	@Nullable
	private CheckBox checkbox;

	@Override
	protected int getViewResId() {
		return R.layout.cpp_wizard_step_onscreen;
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		final Boolean enabled = Preferences.OnscreenCalculator.showAppIcon.getPreference(getPreferences());
		checkbox = (CheckBox) root.findViewById(R.id.wizard_onscreen_app_enabled_checkbox);
		checkbox.setChecked(enabled);
		checkbox.setOnCheckedChangeListener(this);
	}

	@Nullable
	CheckBox getCheckbox() {
		return checkbox;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
		Preferences.OnscreenCalculator.showAppIcon.putPreference(getPreferences(), checked);
	}
}

